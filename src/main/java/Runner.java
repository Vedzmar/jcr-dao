import com.epam.trainings.jcr.dao.EmployeeDAO;
import com.epam.trainings.jcr.dao.impl.EmployeeDAOImpl;
import com.epam.trainings.jcr.helpers.JcrHelper;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.*;
import java.util.Arrays;

public class Runner{
    private static final String JCR_DEFAULT_DIRECTORY = "C:/jcr_dir";
    public static final String WORKSPACE_NAME = "myWorkspace";
    private static final String NODE_TYPE_DEFINITION_FILE = "employee.cnd";

    private static final Logger log = LoggerFactory.getLogger(Runner.class);
    private static final String DATA_FILE = "data.xml";


    public static void main(String[] args) throws IOException {
        Repository repository = getRepository();

        Session session = null;
        try{
            session = getSession(repository);
            init(session);
            
            EmployeeDAO employeeDAO = new EmployeeDAOImpl(session);
            
            importContent(session);
            
            JcrHelper.pl(employeeDAO.getEmployeeWhoLivesWithCats());

            //com.epam.trainings.jcr.helpers.JcrHelper.printNodeTreeRecursive(session.getNode("/employeeFolder"));
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
    }

    private static void registerNodeTypeFromFile(Session session) throws IOException, RepositoryException, ParseException {
        CndImporter.registerNodeTypes(
                new InputStreamReader( ClassLoader.getSystemResourceAsStream(NODE_TYPE_DEFINITION_FILE) ),session
        );

    }

    private static Repository getRepository() {
        return new TransientRepository(getRepositoryDir());
    }

    private static Session getSession(Repository repository) throws RepositoryException {
        Session session = null;

        try {
            session = repository.login(
                    new SimpleCredentials("admin","admin".toCharArray()));

            if (Arrays.asList(session.getWorkspace().getAccessibleWorkspaceNames()).indexOf(WORKSPACE_NAME) == -1 ) {
                session.getWorkspace().createWorkspace(WORKSPACE_NAME);
            }

        } finally {
            if (session != null && session.isLive()) session.logout();
        }

        return repository.login(new SimpleCredentials("admin","admin".toCharArray()), WORKSPACE_NAME);
    }

    private static File getRepositoryDir(){
        File file = new File(JCR_DEFAULT_DIRECTORY);

        if (!file.exists()) file.mkdir();

        return file;
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
