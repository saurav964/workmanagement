package com.ralise.workforcemgmt.model;

import com.ralise.workforcemgmt.model.enums.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskManagement {
    private Long id;
    private Long referenceId;
    private ReferenceType referenceType;
    private Task task;
    private String description;
    private TaskStatus status;
    private Staff assignee;
    private Long taskDeadlineTime;
    private Priority priority;


    public void setAssigneeId(Long assigneeId) {
    }
}
