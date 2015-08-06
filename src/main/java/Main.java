import com.epam.trainings.jcr.dao.EmployeeDAO;
import com.epam.trainings.jcr.dao.impl.EmployeeDAOImpl;
import com.epam.trainings.jcr.entities.Employee;
import org.apache.jackrabbit.core.TransientRepository;

import javax.jcr.*;
import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static final String JCR_DEFAULT_DIRECTORY = "C:/jcr_dir";

    public static void main(String[] args)  {
        Repository repository = new TransientRepository(getRepositoryDir());

        Session session = null;
        try{
            session = repository.login(
                    new SimpleCredentials("admin","admin".toCharArray()));
            printNodeTreeRecursive(session.getRootNode(), 0);
            EmployeeDAO dao = new EmployeeDAOImpl(session);
            
            p("Number of employees : " + dao.getEmployees().size());
            
            for (Employee employee : dao.getEmployees()){
                if (dao.deleteEmployee(employee)){
                    
                 p("An employee have been deleted");
                }
            }

            dao.saveEmployee(new Employee("Dzanis", 25, Calendar.getInstance()));

            List<Employee> employees = dao.getEmployees();
            
            p(employees);
            
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isLive()) session.logout();
        }
    }

    public static void printNodeTreeRecursive(Node node, int depth) throws RepositoryException {
        Iterator<Node> iter = node.getNodes();
        for (int i = 0; i < depth; i++) {
            System.out.print("   ");
        }
     
        p(node.getName());
        while (iter.hasNext()) printNodeTreeRecursive(iter.next(), depth + 1);
    }
    
    private static File getRepositoryDir(){
        File file = new File(JCR_DEFAULT_DIRECTORY);
        
        if (!file.exists()) file.mkdir();
        
        return file;
    }
    
    public static void p(Object o){
        System.out.println(o);
    }
}
