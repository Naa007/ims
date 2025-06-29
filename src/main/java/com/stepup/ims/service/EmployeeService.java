package com.stepup.ims.service;

import com.stepup.ims.model.Employee;
import com.stepup.ims.modelmapper.EmployeeModelMapper;
import com.stepup.ims.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.stepup.ims.constants.ApplicationConstants.*;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeModelMapper employeeModelMapper;

    // Get all employees
    public List<Employee> getAllEmployees() {
        logger.debug("Fetching all employees");
        return employeeModelMapper.toModelList(employeeRepository.findAll());
    }

    public List<Employee> getAllCoordinateEmployees() {
        logger.debug("Fetching all active coordinators");
        return employeeModelMapper.toModelList(employeeRepository.findAll().stream()
                .filter(employee -> COORDINATOR.equalsIgnoreCase(employee.getRole()))
                .filter(employee -> YES.equalsIgnoreCase(employee.getActive()))
                .toList());
    }

    public List<Employee> getAllTechnicalCoordinateEmployees() {
        logger.debug("Fetching all active technical coordinators");
        return employeeModelMapper.toModelList(employeeRepository.findAll().stream()
                .filter(employee -> TECHNICAL_COORDINATOR.equalsIgnoreCase(employee.getRole()))
                .filter(employee -> YES.equalsIgnoreCase(employee.getActive()))
                .toList());
    }

    // Get employee by ID
    public Optional<Employee> getEmployeeById(Long id) {
        logger.debug("Fetching employee by ID: {}", id);
        return employeeRepository.findById(id).map(employeeModelMapper::toModel);
    }

    // Add or update employee
    @Transactional
    public Employee saveEmployee(Employee employee) {
        logger.info("Saving employee with ID: {}", employee.getEmpId());
        return employeeModelMapper.toModel(employeeRepository.save(employeeModelMapper.toEntity(employee)));
    }

    // Delete employee
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }


    public String getRoleByEmail(String email) {
        logger.debug("Fetching role by email: {}", email);
        return employeeRepository.findRoleByEmail(email);
    }

    public Employee getEmployeeByEmail(String email) {
        logger.debug("Fetching employee by email: {}", email);
        return Optional.ofNullable(employeeRepository.findByEmail(email))
                .map(employeeModelMapper::toModel)
                .orElse(null);
    }

    public com.stepup.ims.entity.Employee getEmployeeEntityByEmail(String email) {
        logger.debug("Fetching employee entity by email: {}", email);
        return employeeRepository.findByEmail(email);
    }

    public String getEmployeeIdByEmail(String email) {
        logger.debug("Fetching employee ID by email: {}", email);
        return employeeRepository.findEmpIdByEmail(email);
    }

    public String getEmployeeNameByEmail(String email) {
        logger.debug("Fetching employee name by email :{}", email);
        return employeeRepository.findEmpNameByEmail(email);
    }

    public String getEmployeeNameByEmpId(String id) {
        return employeeRepository.findEmpNameByEmpId(id);
    }

    public void validateEmployee(String id, String email, String flow) {
        logger.info("Validating employee with ID: {}, Email: {}, Flow: {}", id, email, flow);
        if ("update".equalsIgnoreCase(flow)) {
            Employee existingEmployee = getEmployeeByEmail(email);
            if (existingEmployee != null && !existingEmployee.getEmpId().equals(id)) {
                logger.error("Updated email already exists for another employee: {}", email);
                throw new IllegalArgumentException("Updated email already exists for another employee");
            }
        } else {
            if (getEmployeeById(Long.valueOf(id)).isPresent()) {
                logger.error("Employee ID already exists: {}", id);
                throw new IllegalArgumentException("Employee ID already exists");
            }
            if (getEmployeeByEmail(email) != null) {
                logger.error("Employee email already exists: {}", email);
                throw new IllegalArgumentException("Employee email already exists");
            }
        }
    }

}