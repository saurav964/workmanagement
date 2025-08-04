package com.ralise.workforcemgmt;

import com.ralise.workforcemgmt.TaskManagementServiceimpl;
import com.ralise.workforcemgmt.dto.*;
import com.ralise.workforcemgmt.exception.ResourceNotFoundException;
import com.ralise.workforcemgmt.mapper.ITaskManagementMapper;
import com.ralise.workforcemgmt.mapper.StaffMapper;
import com.ralise.workforcemgmt.model.Staff;
import com.ralise.workforcemgmt.model.TaskManagement;
import com.ralise.workforcemgmt.model.enums.Task;
import com.ralise.workforcemgmt.model.enums.TaskStatus;
import com.ralise.workforcemgmt.repository.InMemoryStaffRepository;
import com.ralise.workforcemgmt.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskManagementService implements TaskManagementServiceimpl {

    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;
    private final StaffMapper staffMapper;
    private final InMemoryStaffRepository staffRepository;

    public TaskManagementService(TaskRepository taskRepository,
                                 ITaskManagementMapper taskMapper,
                                 StaffMapper staffMapper,
                                 InMemoryStaffRepository staffRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.staffMapper = staffMapper;
        this.staffRepository = staffRepository;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        TaskManagementDto dto = taskMapper.modelToDto(task);

        if (task.getAssignee() != null) {
            staffRepository.findById(task.getAssignee().getId())
                    .ifPresent(staff -> dto.setStaff(staffMapper.modelToDto(staff)));
        }

        return dto;
    }

    @Override
    public TaskManagementDto createTask(TaskCreateRequest request) {
        return null;
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest request) {
        if (request.getRequests() == null || request.getRequests().isEmpty()) {
            throw new IllegalArgumentException("Request list must not be null or empty");
        }

        List<TaskManagement> taskList = new ArrayList<>();

        for (TaskCreateRequest.RequestItem item : request.getRequests()) {
            Staff staff = taskMapper.toModel(item.getAssignee());
            staffRepository.save(staff);  // Save staff in-memory

            TaskManagement task = new TaskManagement();
            task.setReferenceId(item.getReferenceId());
            task.setReferenceType(item.getReferenceType());
            task.setTask(item.getTask());
            task.setAssignee(staff);  // Set the Staff object
            task.setPriority(item.getPriority());
            task.setTaskDeadlineTime(item.getTaskDeadlineTime());
            task.setStatus(TaskStatus.ASSIGNED);
            task.setDescription("Auto-generated description");

            taskList.add(taskRepository.save(task));
        }

        // Map and attach staff to each DTO explicitly
        List<TaskManagementDto> dtoList = new ArrayList<>();
        for (TaskManagement task : taskList) {
            TaskManagementDto dto = taskMapper.modelToDto(task);
            if (task.getAssignee() != null) {
                staffRepository.findById(task.getAssignee().getId())
                        .ifPresent(staff -> dto.setStaff(staffMapper.modelToDto(staff)));
            }
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest request) {
        List<TaskManagementDto> updatedTasks = new ArrayList<>();

        for (UpdateTaskRequest.RequestItem item : request.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + item.getTaskId()));

            if (item.getDescription() != null) task.setDescription(item.getDescription());
            if (item.getTaskStatus() != null) task.setStatus(item.getTaskStatus());
            if(item.getPriority() != null) task.setPriority(item.getPriority());
            if(item.getTaskDeadlineTime() != null) task.setTaskDeadlineTime(item.getTaskDeadlineTime());

            if (item.getAssignee() != null) {
                Staff staff = taskMapper.toModel(item.getAssignee());
                staffRepository.save(staff);
                task.setAssignee(staff);
            }

            taskRepository.save(task);

            TaskManagementDto dto = taskMapper.modelToDto(task);
            if (task.getAssignee() != null) {
                staffRepository.findById(task.getAssignee().getId())
                        .ifPresent(staff -> dto.setStaff(staffMapper.modelToDto(staff)));
            }

            updatedTasks.add(dto);
        }

        return updatedTasks;
    }


    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            Optional<TaskManagement> alreadyAssignedTask = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .findFirst();

            if (alreadyAssignedTask.isPresent()) {
                TaskManagement taskToUpdate = alreadyAssignedTask.get();
                taskToUpdate.setAssigneeId(request.getAssigneeId());
                taskRepository.save(taskToUpdate);

                existingTasks.stream()
                        .filter(t -> t.getTask() == taskType && t != taskToUpdate && t.getStatus() != TaskStatus.COMPLETED)
                        .forEach(t -> {
                            t.setStatus(TaskStatus.CANCELLED);
                            taskRepository.save(t);
                        });
            } else {
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                taskRepository.save(newTask);
            }
        }
        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        Long start = request.getStartDate();
        Long end = request.getEndDate();

        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                .filter(task -> {
                    Long deadline = task.getTaskDeadlineTime();
                    return deadline != null && deadline >= start && deadline <= end;
                })
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }
}