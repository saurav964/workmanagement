package com.ralise.workforcemgmt.repository;

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


    public InMemoryTaskRepository() {
        // Seed data
        createSeedTask(101L, ReferenceType.ORDER, Task.CREATE_INVOICE, 1L, TaskStatus.ASSIGNED, Priority.HIGH);
        createSeedTask(101L, ReferenceType.ORDER, Task.ARRANGE_PICKUP, 1L, TaskStatus.COMPLETED, Priority.HIGH);
        createSeedTask(102L, ReferenceType.ORDER, Task.CREATE_INVOICE, 2L, TaskStatus.ASSIGNED, Priority.MEDIUM);
        createSeedTask(201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, 2L, TaskStatus.ASSIGNED, Priority.LOW);
        createSeedTask(201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, 3L, TaskStatus.ASSIGNED, Priority.LOW); // Duplicate for Bug #1
        createSeedTask(103L, ReferenceType.ORDER, Task.COLLECT_PAYMENT, 1L, TaskStatus.CANCELLED, Priority.MEDIUM); // For Bug #2
    }


    private void createSeedTask(Long refId, ReferenceType refType, Task task, Long assigneeId, TaskStatus status, Priority priority) {
        long newId = idCounter.incrementAndGet();
        TaskManagement newTask = new TaskManagement();
        newTask.setId(newId);
        newTask.setReferenceId(refId);
        newTask.setReferenceType(refType);
        newTask.setTask(task);
        newTask.setAssigneeId(assigneeId);
        newTask.setStatus(status);
        newTask.setPriority(priority);
        newTask.setDescription("This is a seed task.");
        newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000); // 1 day from now
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
