package com.epam.trainings.jcr.eventlistner;

import com.epam.trainings.jcr.helpers.JcrHelper;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;


public class GrouppedEventListener implements EventListener {
    
    private static GrouppedEventListener me;
    
    private GrouppedEventListener() {}

    public static EventListener getInstance(){
        if (me == null) {
            me = new GrouppedEventListener();
        }
        
        return me;
    }
    
    @Override
    public void onEvent(EventIterator events) {
        while (events.hasNext()){
            Event event = (Event) events.next();
            try {
                JcrHelper.pl(event.getPath());
            } catch (RepositoryException e) {
                e.printStackTrace();
            }

        }

    }
    
}
