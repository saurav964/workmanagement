package com.ralise.workforcemgmt.repository;

import com.ralise.workforcemgmt.model.Staff;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class InMemoryStaffRepository {
    private final Map<Long, Staff> staffStore = new ConcurrentHashMap<>();

    public Optional<Staff> findById(Long id) {
        return Optional.ofNullable(staffStore.get(id));
    }

    public Staff save(Staff staff) {
        staffStore.put(staff.getId(), staff);
        return staff;
    }

    public boolean existsById(Long id) {
        return staffStore.containsKey(id);
    }
}
