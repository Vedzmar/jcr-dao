package com.epam.trainings.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Iterator;

/**
 * Created by Dzianis on 16.08.2015.
 */
public class IterableNodeIterator implements Iterable<Node> {
    private final Iterator<Node> iterator;

    public IterableNodeIterator(NodeIterator iter) {
        this.iterator = iter;
    }

    @Override
    public Iterator<Node> iterator() {
        return iterator;
    }
}
