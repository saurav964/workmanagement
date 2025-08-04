package com.ralise.workforcemgmt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ralise.workforcemgmt.model.enums.ReferenceType;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignByReferenceRequest {
    private Long referenceId;
    private ReferenceType referenceType;
    private Long assigneeId;



}
