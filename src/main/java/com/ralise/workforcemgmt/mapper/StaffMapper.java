package com.ralise.workforcemgmt.mapper;

import com.ralise.workforcemgmt.dto.StaffDto;
import com.ralise.workforcemgmt.model.Staff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StaffMapper {
    StaffDto modelToDto(Staff model);
    Staff dtoToModel(StaffDto dto);
}
