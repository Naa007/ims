package com.stepup.ims.modelmapper;

import com.stepup.ims.model.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EmployeeModelMapper {

    @Autowired
    private ModelMapper modelMapper;

    public com.stepup.ims.entity.Employee toEntity(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Input EmployeeModel cannot be null");
        }
        return modelMapper.map(employee, com.stepup.ims.entity.Employee.class);
    }

    public Employee toModel(com.stepup.ims.entity.Employee employeeEntity) {
        if (employeeEntity == null) {
            throw new IllegalArgumentException("Input EmployeeEntity cannot be null");
        }
        return modelMapper.map(employeeEntity, Employee.class);
    }

    public List<Employee> toModelList(List<com.stepup.ims.entity.Employee> employeeEntities) {
        if (employeeEntities == null) {
            throw new IllegalArgumentException("Input EmployeeEntity list cannot be null");
        }
        return employeeEntities.stream().map(this::toModel).toList();
    }

    public List<com.stepup.ims.entity.Employee> toEntityList(List<Employee> employees) {
        if (employees == null) {
            throw new IllegalArgumentException("Input EmployeeModel list cannot be null");
        }
        return employees.stream().map(this::toEntity).toList();
    }

    public Optional<Employee> toOptionalModel(Optional<com.stepup.ims.entity.Employee> employeeEntityOptional) {
        return employeeEntityOptional.map(this::toModel);
    }


}