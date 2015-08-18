import com.epam.trainings.jcr.Connector;
import com.epam.trainings.jcr.dao.EmployeeDAO;
import com.epam.trainings.jcr.dao.impl.EmployeeDAOImpl;
import com.epam.trainings.jcr.entities.Employee;
import com.epam.trainings.jcr.eventlistner.GrouppedEventListener;
import com.epam.trainings.jcr.helpers.JcrHelper;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.observation.Event;
import java.io.*;
import java.util.Calendar;

public class Runner{
    private static final String NODE_TYPE_DEFINITION_FILE = "employee.cnd";

    private static final Logger log = LoggerFactory.getLogger(Runner.class);
    private static final String DATA_FILE = "data.xml";


    public static void main(String[] args) throws IOException {
        Repository repository = Connector.getRepository();

        Session session = null;
        try{
            session = Connector.getSession(repository);
            init(session);
            EmployeeDAO employeeDAO = new EmployeeDAOImpl(session);
            importContent(session);
            employeeDAO.saveEmployee(new Employee("name", 24, Calendar.getInstance()));
//            JcrHelper.printNodeTreeRecursive(session.getNode("/employeeFolder"));
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isLive()) session.logout();
        }
    }

    private static void init(Session session) throws RepositoryException {
        if (!session.getWorkspace().getNodeTypeManager().hasNodeType(EmployeeDAOImpl.EMPLOYEE_NODE_TYPE)) {
            try {
                registerNodeTypeFromFile(session);
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }

        initListeners(session);
    }

    private static void initListeners(Session session) throws RepositoryException {
        session.getWorkspace().getObservationManager().addEventListener(
                GrouppedEventListener.getInstance(),
                Event.NODE_ADDED ,
                "/employeeFolder",
                false,
                null,
                new String[] {"nt:unstructured"},
                false
            );
    }

    private static void registerNodeTypeFromFile(Session session) throws IOException, RepositoryException, ParseException {
        CndImporter.registerNodeTypes(
                new InputStreamReader( ClassLoader.getSystemResourceAsStream(NODE_TYPE_DEFINITION_FILE) ),session
        );

    }

    public static void importContent(Session session) throws RepositoryException, IOException {
        Node rootNode = session.getRootNode();
        if(rootNode.hasNode("employeeFolder"))
            rootNode.getNode("employeeFolder").remove();

        session.importXML("/",
                ClassLoader.getSystemResourceAsStream(DATA_FILE),
                ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW
        );
        
        session.save();
    }
}
