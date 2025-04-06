package com.stepup.ims.repository;

import com.stepup.ims.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);

    @Query("SELECT e.empId FROM Employee e WHERE e.email = :email")
    String findEmpIdByEmail(@Param("email") String email);
}

