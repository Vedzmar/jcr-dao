package com.epam.trainings.jcr.dao.impl;

import com.epam.trainings.jcr.IterableNodeIterator;
import com.epam.trainings.jcr.dao.EmployeeDAO;
import com.epam.trainings.jcr.entities.Child;
import com.epam.trainings.jcr.entities.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.lang.String.format;

/**
 * Created by Dzianis on 06.08.2015.
 */
public class EmployeeDAOImpl implements EmployeeDAO {

    private static final Logger log = LoggerFactory.getLogger(EmployeeDAOImpl.class);
    
    public static final String EMPLOYEE_NODE_TYPE = "epm:employee";
    public static final String EMPLOYEE_FOLDER = "employeeFolder";
    public static final String EMPLOYEE_NAME = "epm:name";
    public static final String EMPLOYEE_AGE = "epm:age";
    public static final String EMPLOYEE_HIRING_DATE = "epm:hiringDate";
    public static final String EMPLOYEE_NODE_NAME = "employee";
    private static final String CHILD_NODE_NAME = "child";
    private static final String CHILD_NAME = "epm:name";
    private static final String CHILD_BIRTH_DATE = "epm:birthDate";
    private static final String CHILD_NODE_TYPE = "epm:child";
    public static final String ID = "id";

    private final Session session;

    public EmployeeDAOImpl(Session session) {
        this.session = session;
    }

    @Override
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<Employee>();
        for(Node employeeNode : getEmployeeNodeList()){
            try {
                Employee employee = createEmployeeFromNode(employeeNode);
                employees.add(employee);
            } catch (RepositoryException e) {
                log.error("Error during creation employee from node : " + e.getMessage());
            }
        }        
        
        return employees;
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        Node employeeNode = null;
        try {
            Node folderNode = getEmployeeFolderNode();

            employeeNode = folderNode.addNode(EMPLOYEE_NODE_NAME, EMPLOYEE_NODE_TYPE);

            copyEmployeeToNode(employee, employeeNode);
            
            session.save();
        } catch (RepositoryException e) {
            log.error("Error during saving an employee " + employee + " " + e.getMessage() );
            return null;
        }
        
        return employee;
    }


    @Override
    public boolean editEmployee(Employee employee) {
        try {
            Node employeeNode = getEmployeeNode(employee.getId());
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
            Node employeeNode = getEmployeeNode(employee.getId());
            employeeNode.remove();
            session.save();
        } catch (RepositoryException e) {
            log.error("can't delete employee " + employee + " " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<Employee> getEmployeeWhoLivesWithCats() {
        try {
            return getEmployeeByXPath("//element(*, epm:employee)[not(jcr:contains(child,'*')) and @epm:age > 40]");
        } catch (RepositoryException e) {
            log.error("Error during searching persons who lives with cats " + e.getMessage());
        }
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getEmployeeWhoHaveAdultKids() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        
        try {
            return getEmployeeByXPath(
                    format("//element(*,epm:employee)[child/@epm:birthDate < xs:dateTime('%s')]",
                            session.getValueFactory().createValue(calendar).getString()
                    )
                );
        } catch (RepositoryException e) {
            log.error("Error during getting employees who have adult kids  " + e.getMessage());
        }
        
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getEmployeesWhosNameStartsWith(String name) {

        try {
            return getEmployeeByXPath(
                    format("//element(*,epm:employee)[jcr:like(@epm:name,'%s%%')]", name)
            );
        } catch (RepositoryException e) {
            log.error("Error during getting employees whos name starts with (" + name + ") " + e.getMessage());
        }
        
        return new ArrayList<Employee>();
    }

    private Employee createEmployeeFromNode(Node employeeNode) throws RepositoryException {
        return new Employee(
                employeeNode.getProperty(ID).getLong(),
                employeeNode.getProperty(EMPLOYEE_NAME).getString(),
                employeeNode.getProperty(EMPLOYEE_AGE).getLong(),
                employeeNode.getProperty(EMPLOYEE_HIRING_DATE).getDate(),
                getChildrenFromEmployeeNode(employeeNode)
        );
    }

    private List<Child> getChildrenFromEmployeeNode(Node employeeNode) throws RepositoryException {
        List<Child> children = new ArrayList<Child>();

        for (Node childNode : getChildrenNodeFromEmployeeNode(employeeNode)){
            children.add(createChildFromNode(childNode));
        }
        
        return children;
    }

    private Child createChildFromNode(Node childNode) throws RepositoryException {
        return new Child(
                childNode.getProperty(CHILD_NAME).getString(),
                childNode.getProperty(CHILD_BIRTH_DATE).getDate()
        );
    }

    private List<Node> getEmployeeNodeList() {
        List<Node> nodeList = new ArrayList<Node>();

        try {
            Node employeeFolder = getEmployeeFolderNode();
            for (Node node : makeIterable(employeeFolder.getNodes(EMPLOYEE_NODE_NAME) ) ){
                nodeList.add(node);
            }
        } catch (RepositoryException e) {
            log.error("Can't get employee node list " + e.getMessage());
        }

        return nodeList;
    }

    private Node getEmployeeFolderNode() throws RepositoryException {
        Node rootNode = session.getRootNode();
        return rootNode.hasNode(EMPLOYEE_FOLDER) ? rootNode.getNode(EMPLOYEE_FOLDER) : rootNode.addNode(EMPLOYEE_FOLDER);
    }

    private void copyEmployeeToNode(Employee employee, Node employeeNode) throws RepositoryException {
        employeeNode.setProperty(ID, employee.getId());
        employeeNode.setProperty(EMPLOYEE_NAME, employee.getName());
        employeeNode.setProperty(EMPLOYEE_AGE, employee.getAge());
        employeeNode.setProperty(EMPLOYEE_HIRING_DATE, employee.getHiringDate());
        
        removeChildNodesFromEmployeeNode(employeeNode);
        
        for(Child child : employee.getChildren()){
            Node childNode = employeeNode.addNode(CHILD_NODE_NAME, CHILD_NODE_TYPE);
            copyChildToNode(child, childNode);
        }
    }

    private void removeChildNodesFromEmployeeNode(Node employeeNode) throws RepositoryException {
        for(Node childNode : getChildrenNodeFromEmployeeNode(employeeNode)){
            childNode.remove();
        }
    }

    private List<Node> getChildrenNodeFromEmployeeNode(Node employeeNode) throws RepositoryException {
        List<Node> childNodeList = new ArrayList<Node>();

        for (Node node : makeIterable(employeeNode.getNodes(CHILD_NODE_NAME ))){
            childNodeList.add(node);
        }

        return childNodeList;
    }

    private void copyChildToNode(Child child, Node childNode) throws RepositoryException {
        childNode.setProperty(CHILD_NAME, child.getName());
        childNode.setProperty(CHILD_BIRTH_DATE, child.getBirthDate());
    }

    private Node getEmployeeNode(long id) throws RepositoryException {
        for(Node employeeNode : getEmployeeNodeList()){
            if (employeeNode.getProperty(ID).getLong() == id){
                return employeeNode;
            }
        }
        return null;
    }

    private IterableNodeIterator makeIterable(NodeIterator iterator) throws RepositoryException {
        return new IterableNodeIterator(iterator);
    }

    private List<Employee> getEmployeeByXPath(String xpath) throws RepositoryException {
        List<Employee> employees = new ArrayList<Employee>();

        QueryManager manager = this.session.getWorkspace().getQueryManager();
        Query query =  manager.createQuery(xpath , Query.XPATH);

        for(Node node : makeIterable(query.execute().getNodes())){
            employees.add( createEmployeeFromNode(node) );
        }

        return employees;
    }

}
