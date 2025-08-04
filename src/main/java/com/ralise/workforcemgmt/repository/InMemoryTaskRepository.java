package com.ralise.workforcemgmt.repository;

import com.ralise.workforcemgmt.model.Staff;
import com.ralise.workforcemgmt.model.TaskManagement;
import com.ralise.workforcemgmt.model.enums.Priority;
import com.ralise.workforcemgmt.model.enums.ReferenceType;
import com.ralise.workforcemgmt.model.enums.Task;
import com.ralise.workforcemgmt.model.enums.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskRepository  implements TaskRepository {
    private final Map<Long, TaskManagement> taskStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);
    private final InMemoryStaffRepository staffRepository;

    public InMemoryTaskRepository(InMemoryStaffRepository staffRepository) {
        this.staffRepository = staffRepository;
        staffRepository.save(new Staff(1L, "Saurav Gupta", "Engineer", "Operations"));
        staffRepository.save(new Staff(2L, "Anjali Mehta", "Manager", "Finance"));
        staffRepository.save(new Staff(3L, "Rahul Sharma", "Sales Executive", "Sales"));
        staffRepository.save(new Staff(4L, "Rahul Sharma", "Sales Executive", "Sales"));
        createSeedTask(102L, ReferenceType.ORDER, Task.CREATE_INVOICE, 1L, TaskStatus.ASSIGNED, Priority.MEDIUM);
        createSeedTask(201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, 3L, TaskStatus.ASSIGNED, Priority.LOW); // For Bug #2
    }


    private void createSeedTask(Long refId, ReferenceType refType, Task task, Long assigneeId, TaskStatus status, Priority priority) {
        long newId = idCounter.incrementAndGet();

        Staff staff = null;
        if (assigneeId != null) {
            staff = staffRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Staff with ID " + assigneeId + " not found"));
        }

        TaskManagement newTask = new TaskManagement();
        newTask.setId(newId);
        newTask.setReferenceId(refId);
        newTask.setReferenceType(refType);
        newTask.setTask(task);
        newTask.setAssignee(staff); 
        newTask.setStatus(status);
        newTask.setPriority(priority);
        newTask.setDescription("This is a seed task.");
        newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000);

        taskStore.put(newId, newTask);
    }

    @Override
    public Optional<TaskManagement> findById(Long id) {
        return Optional.ofNullable(taskStore.get(id));
    }


    @Override
    public TaskManagement save(TaskManagement task) {
        if (task.getId() == null) {
            task.setId(idCounter.incrementAndGet());
        }
        taskStore.put(task.getId(), task);
        return task;
    }


    @Override
    public List<TaskManagement> findAll() {
        return List.copyOf(taskStore.values());
    }


    @Override
    public List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType) {
        return taskStore.values().stream()
                .filter(task ->
                        referenceId != null &&
                                referenceType != null &&
                                referenceId.equals(task.getReferenceId()) &&
                                referenceType.equals(task.getReferenceType())
                )
                .collect(Collectors.toList());
    }



    @Override
    public List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds) {
            return taskStore.values().stream()
                    .filter(task -> task.getAssignee() != null && assigneeIds.contains(task.getAssignee()))
                    .collect(Collectors.toList());
        }

}
