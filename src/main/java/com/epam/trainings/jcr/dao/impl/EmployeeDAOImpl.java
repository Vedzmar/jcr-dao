package com.epam.trainings.jcr.dao.impl;

import com.epam.trainings.jcr.dao.EmployeeDAO;
import com.epam.trainings.jcr.entities.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

/**
 * Created by Dzianis on 06.08.2015.
 */
public class EmployeeDAOImpl implements EmployeeDAO {

    private static final Logger log = LoggerFactory.getLogger(EmployeeDAOImpl.class);
    public static final String NAME = "epm:name";
    public static final String AGE = "epm:age";
    public static final String HIRING_DATE = "epm:hiringDate";
    public static final String EMPLOYEE_NODE_NAME = "employee";
    public static final String EMPLOYEE_FOLDER = "employeeFolder";

    private final Session session;

    public EmployeeDAOImpl(Session session) {
        this.session = session;
    }

    @Override
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<Employee>();
        for(Node employeeNode : getEmployeeNodeList()){
            try {
                employees.add(
                    new Employee(
                        employeeNode.getPath(),
                        employeeNode.getProperty(NAME).getString(),
                        employeeNode.getProperty(AGE).getLong(),
                        employeeNode.getProperty(HIRING_DATE).getDate()
                    )
                );
            } catch (RepositoryException e) {
                log.error("Error during creation employee from node : " + e.getMessage());
            }
        }        
        
        return employees;
    }

    private List<Node> getEmployeeNodeList() {
        List<Node> nodeList = new ArrayList<Node>();

        try {
            Node employeeFolder = getEmployeeFolderNode();
            Iterator<Node> i = employeeFolder.getNodes((String[]) Arrays.asList(EMPLOYEE_NODE_NAME + "*").toArray());
            while (i.hasNext()){
                nodeList.add(i.next());
            }
        } catch (RepositoryException e) {
            log.error("Can't get employee node list " + e.getMessage());
        }

        return nodeList; 
    }


    @Override
    public Employee saveEmployee(Employee employee) {
        Node employeeNode = null;
        try {
            Node folderNode = getEmployeeFolderNode();


            employeeNode = folderNode.addNode(EMPLOYEE_NODE_NAME + System.currentTimeMillis());

            copyEmployeeToNode(employee, employeeNode);
                    
            session.save();
            employee.setId(employeeNode.getPath());
        } catch (RepositoryException e) {
            log.error("Error during saving an employee " + employee + " " + e.getMessage() );
            return null;
        }
        
        return employee;
    }

    private Node getEmployeeFolderNode() throws RepositoryException {
        Node rootNode = session.getRootNode();
        return rootNode.hasNode(EMPLOYEE_FOLDER) ? rootNode.getNode(EMPLOYEE_FOLDER) : rootNode.addNode(EMPLOYEE_FOLDER);
    }

    private void copyEmployeeToNode(Employee employee, Node employeeNode) throws RepositoryException {
        employeeNode.setProperty(NAME, employee.getName());
        employeeNode.setProperty(AGE, employee.getAge());
        employeeNode.setProperty(HIRING_DATE, employee.getHiringDate());
    }

    @Override
    public boolean editEmployee(Employee employee) {
        try {
            Node employeeNode = session.getNode(employee.getId());
            copyEmployeeToNode(employee, employeeNode);
            
            session.save();
        } catch (RepositoryException e) {
            log.error("can't edit employee " + employee + " " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        try {
            Node employeeNode = session.getNode(employee.getId());
            employeeNode.remove();
            session.save();
        } catch (RepositoryException e) {
            log.error("can't delete employee " + employee + " " + e.getMessage());
            return false;
        }
        return true;
    }
}
