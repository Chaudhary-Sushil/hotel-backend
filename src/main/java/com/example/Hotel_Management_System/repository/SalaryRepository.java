package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.*;
import org.aspectj.weaver.ast.And;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    // all salaries for a staff member
    List<Salary> findByStaff(Staff staff);

    // all salaries by type
    List<Salary> findBySalaryType(SalaryType salaryType);

    // salaries for a staff by type
    List<Salary> findByStaffAndSalaryType(Staff staff, SalaryType salaryType);
}
