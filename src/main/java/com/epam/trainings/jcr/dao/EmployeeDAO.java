package com.epam.trainings.jcr.dao;

import com.epam.trainings.jcr.entities.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dzianis on 06.08.2015.
 */
public interface EmployeeDAO {
    
    public List<Employee> getEmployees();
    
    public Employee saveEmployee(Employee employee);
    
    public boolean editEmployee(Employee employee);
    
    public boolean deleteEmployee(Employee employee);
    
    public List<Employee> getEmployeeWhoLivesWithCats();

    public List<Employee> getEmployeeWhoHaveAdultKids();
    
    public List<Employee> getEmployeesWhosNameStartsWith(String name);
}
