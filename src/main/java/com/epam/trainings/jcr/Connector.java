package com.epam.trainings.jcr;


import org.apache.jackrabbit.core.TransientRepository;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.File;
import java.util.Arrays;

public class Connector {

    public static final String WORKSPACE_NAME = "myWorkspace";
    private static final String JCR_DEFAULT_DIRECTORY = "C:/jcr_dir";

    private static Repository repository;
    
    public static Repository getRepository() {
        if (repository == null) {
            repository = new TransientRepository(getRepositoryDir());
        }
        
        return repository;
    }

    public static Session getSession(Repository repository) throws RepositoryException {
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
}
