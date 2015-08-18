package com.epam.trainings.jcr.eventlistner;

import com.epam.trainings.jcr.Connector;
import com.epam.trainings.jcr.helpers.JcrHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;


public class GrouppedEventListener implements EventListener {

    private static final Logger log = LoggerFactory.getLogger(GrouppedEventListener.class);

    private static GrouppedEventListener me;

    private Session session;
    
    
    private GrouppedEventListener() throws RepositoryException {
        this.session = Connector.getSession(Connector.getRepository());
    }

    public static GrouppedEventListener getInstance() throws RepositoryException {
        if (me == null){
            me = new GrouppedEventListener();
        }
        
        return me;
    }
    
    @Override
    public void onEvent(EventIterator events) {
        while (events.hasNext()){
            Event event = events.nextEvent();
            try {
                JcrHelper.pl(event.getPath());
                createSiblingFolder(event.getIdentifier());
            } catch (RepositoryException e) {
                log.error("Exception during moving node under group folder " + e.getMessage());
            }
        }
    }
    
    public void createSiblingFolder(String id) throws RepositoryException {
        Node node = session.getNodeByIdentifier(id);
        Node parentNode = node.getParent();
        Node folderNode = null;
        if (!parentNode.hasNode(getGroupName(node))) {
            folderNode = parentNode.addNode(getGroupName(node), "epm:folder");
        } else {
            folderNode = parentNode.getNode(getGroupName(node));
        }
        
        session.move(node.getPath(), folderNode.getPath() + "/" +  node.getName());
        
        log.error("Node " + node.getPath() + " have been moved under " + folderNode.getPath());
    }

    private String getGroupName(Node node) throws RepositoryException {
        return node.getProperty("epm:age").getLong() / 10 + "";
    }
}
