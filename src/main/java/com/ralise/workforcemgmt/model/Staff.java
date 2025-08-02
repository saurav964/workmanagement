package com.ralise.workforcemgmt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {
    private Long id;
    private String name;
    private String role;
    private String department;
}
