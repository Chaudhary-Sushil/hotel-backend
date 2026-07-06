package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.dto.SalaryRequest;
import com.example.Hotel_Management_System.dto.SalaryResponse;
import com.example.Hotel_Management_System.entity.Salary;
import com.example.Hotel_Management_System.entity.SalaryType;
import com.example.Hotel_Management_System.exception.SalaryNotFoundException;
import com.example.Hotel_Management_System.exception.StaffNotFoundException;
import com.example.Hotel_Management_System.repository.SalaryRepository;
import com.example.Hotel_Management_System.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final StaffRepository staffRepository;

    // ─── ADD SALARY ───────────────────────────────────────────────
    public SalaryResponse addSalary(SalaryRequest request) {

        // 1. find staff
        var staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new StaffNotFoundException(
                        "Staff not found: " + request.getStaffId()
                ));

        // 2. create salary
        var salary = Salary.builder()
                .staff(staff)
                .amount(request.getAmount())
                .salaryType(request.getSalaryType())
                .effectiveDate(request.getEffectiveDate())
                .build();

        salaryRepository.save(salary);

        return buildSalaryResponse(salary);
    }

    // ─── UPDATE SALARY ────────────────────────────────────────────
    public SalaryResponse updateSalary(Long salaryId, SalaryRequest request) {

        // 1. find existing salary
        var salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new SalaryNotFoundException(
                        "Salary not found: " + salaryId
                ));

        // 2. update fields
        salary.setAmount(request.getAmount());
        salary.setSalaryType(request.getSalaryType());
        salary.setEffectiveDate(request.getEffectiveDate());

        salaryRepository.save(salary);

        return buildSalaryResponse(salary);
    }

    // ─── DELETE SALARY ────────────────────────────────────────────
    public String deleteSalary(Long salaryId) {

        var salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new SalaryNotFoundException(
                        "Salary not found: " + salaryId
                ));

        salaryRepository.delete(salary);

        return "Salary record deleted successfully ";
    }

    // ─── GET ALL SALARIES ─────────────────────────────────────────
    public List<SalaryResponse> getAllSalaries() {
        return salaryRepository.findAll()
                .stream()
                .map(this::buildSalaryResponse)
                .collect(Collectors.toList());
    }

    // ─── GET SALARIES BY STAFF ────────────────────────────────────
    public List<SalaryResponse> getSalariesByStaff(Long staffId) {

        var staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffNotFoundException(
                        "Staff not found: " + staffId
                ));

        return salaryRepository.findByStaff(staff)
                .stream()
                .map(this::buildSalaryResponse)
                .collect(Collectors.toList());
    }

    // ─── GET SALARIES BY TYPE ─────────────────────────────────────
    public List<SalaryResponse> getSalariesByType(SalaryType salaryType) {
        return salaryRepository.findBySalaryType(salaryType)
                .stream()
                .map(this::buildSalaryResponse)
                .collect(Collectors.toList());
    }

    // ─── HELPER METHOD ────────────────────────────────────────────
    private SalaryResponse buildSalaryResponse(Salary salary) {
        return SalaryResponse.builder()
                .salaryId(salary.getSalaryId())
                .staffId(salary.getStaff().getId())
                .staffName(salary.getStaff().getFirstName()
                        + " " + salary.getStaff().getLastName())
                .amount(salary.getAmount())
                .salaryType(salary.getSalaryType())
                .effectiveDate(salary.getEffectiveDate())
                .createdAt(salary.getCreatedAt())
                .build();
    }
}