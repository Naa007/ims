package com.stepup.ims.service;

import com.stepup.ims.model.Employee;
import com.stepup.ims.modelmapper.EmployeeModelMapper;
import com.stepup.ims.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.stepup.ims.constants.ApplicationConstants.*;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeModelMapper employeeModelMapper;

    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeModelMapper.toModelList(employeeRepository.findAll());
    }

    public List<Employee> getAllCoordinateEmployees() {
        return employeeModelMapper.toModelList(employeeRepository.findAll().stream()
                .filter(employee -> COORDINATOR.equalsIgnoreCase(employee.getRole()))
                .filter(employee -> YES.equalsIgnoreCase(employee.getActive()))
                .toList());
    }

    public List<Employee> getAllTechnicalCoordinateEmployees() {
        return employeeModelMapper.toModelList(employeeRepository.findAll().stream()
                .filter(employee -> TECHNICAL_COORDINATOR.equalsIgnoreCase(employee.getRole()))
                .filter(employee -> YES.equalsIgnoreCase(employee.getActive()))
                .toList());
    }

    // Get employee by ID
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(employeeModelMapper::toModel);
    }

    // Add or update employee
    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeModelMapper.toModel(employeeRepository.save(employeeModelMapper.toEntity(employee)));
    }

    // Delete employee
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }


    public String getRoleByEmail(String email) {
        return employeeRepository.findRoleByEmail(email);
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeModelMapper.toModel(employeeRepository.findByEmail(email));
    }

    public String getEmployeeIdByEmail(String email) {
        return employeeRepository.findEmpIdByEmail(email);
    }

    public String getEmployeeNameByEmail(String email) {
        return employeeRepository.findEmpNameByEmail(email);
    }

    public String getEmployeeNameByEmpId(String id) {
        return employeeRepository.findEmpNameByEmpId(id);
    }

    public void validateEmployee(String id, String email) {
        if (getEmployeeById(Long.valueOf(id)).isPresent()) {
            throw new IllegalArgumentException("Employee ID already exists");
        }
        if (getEmployeeByEmail(email) != null) {
            throw new IllegalArgumentException("Employee email already exists");
        }
    }

}