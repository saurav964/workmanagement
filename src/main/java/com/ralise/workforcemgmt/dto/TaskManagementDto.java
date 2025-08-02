
package com.ralise.workforcemgmt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ralise.workforcemgmt.model.enums.ReferenceType;
import com.ralise.workforcemgmt.model.enums.Task;
import com.ralise.workforcemgmt.model.enums.TaskStatus;
import com.ralise.workforcemgmt.model.enums.Priority;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskManagementDto {
    private Long id;
    private Long referenceId;
    private ReferenceType referenceType;
    private Task task;
    private String description;
    private TaskStatus status;
    private Long taskDeadlineTime;
    private Priority priority;
    private StaffDto staff;
}
