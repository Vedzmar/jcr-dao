import com.epam.trainings.jcr.dao.EmployeeDAO;
import com.epam.trainings.jcr.dao.impl.EmployeeDAOImpl;
import com.epam.trainings.jcr.entities.Employee;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.TransientRepository;

import javax.jcr.*;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static final String JCR_DEFAULT_DIRECTORY = "C:/jcr_dir";
    public static final String WORKSPACE_NAME = "myWorkspace";

    public static void main(String[] args) throws RepositoryException {
        Repository repository = getRepository();

        Session session = null;
        try{
            session = getSession(repository);


        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isLive()) session.logout();
        }
    }

    private static Repository getRepository() {
        return new TransientRepository(getRepositoryDir());
    }
/*
    private static Repository getRepository() throws RepositoryException {
        return JcrUtils.getRepository("http://localhost:4502/crx/repository");
    }
*/
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
