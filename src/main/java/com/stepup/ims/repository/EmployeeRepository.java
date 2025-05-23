package com.stepup.ims.repository;

import com.stepup.ims.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);

    Employee findByEmpId(String empId);

    @Query("SELECT e.empId FROM Employee e WHERE e.email = :email")
    String findEmpIdByEmail(@Param("email") String email);

    @Query("SELECT e.empName FROM Employee e WHERE e.email = :email")
    String findEmpNameByEmail(@Param("email") String email);

    @Query("SELECT e.role FROM Employee e WHERE e.email = :email")
    String findRoleByEmail(@Param("email") String email);

    @Query("SELECT e.empName FROM Employee e WHERE e.empId = :empId")
    String findEmpNameByEmpId(@Param("empId") String empId);
}

