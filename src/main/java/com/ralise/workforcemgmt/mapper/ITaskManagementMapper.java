package com.ralise.workforcemgmt.mapper;

import com.ralise.workforcemgmt.dto.StaffDto;
import com.ralise.workforcemgmt.dto.TaskManagementDto;
import com.ralise.workforcemgmt.model.Staff;
import com.ralise.workforcemgmt.model.TaskManagement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StaffMapper.class})
public interface ITaskManagementMapper {

    TaskManagementDto modelToDto(TaskManagement model);

    TaskManagement dtoToModel(TaskManagementDto dto);

    List<TaskManagementDto> modelListToDtoList(List<TaskManagement> models);

    StaffDto toDto(Staff model);
    Staff toModel(StaffDto dto);
}
