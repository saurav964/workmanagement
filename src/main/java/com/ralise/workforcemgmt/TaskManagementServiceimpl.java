package com.ralise.workforcemgmt;

import com.ralise.workforcemgmt.dto.*;

import java.util.List;

public interface TaskManagementServiceimpl {
    TaskManagementDto createTask(TaskCreateRequest request);

    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);

}
