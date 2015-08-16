package com.epam.trainings.jcr.helpers;

import javax.jcr.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static java.lang.String.format;


public class JcrHelper {
    private static final String PROPERTY_SIGN = "-";
    private static final String NODE_SIGN = "+";
    private static DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    public static void pl(Object o){
        System.out.println(o);
    }


    public static void printNodeTreeRecursive(Node node) throws RepositoryException {
        printNodeTreeRecursive(node, 0);
    }

    private static void printNodeTreeRecursive(Node node, int depth) throws RepositoryException {
        String spaces = new String(new char[depth * 2]).replace('\0', ' ');

        for ( String line : formatNode(node)){
            pl(spaces + line);
        }

        Iterator<Node> iter = node.getNodes();
        while (iter.hasNext()) printNodeTreeRecursive(iter.next(), depth + 1);
    }
    
    private static List<String> formatNode(Node node) throws RepositoryException {
        List<String> formattedNode = new ArrayList<String>();
        
        formattedNode.add(format("%s %s ", NODE_SIGN, node.getName()));
        formattedNode.addAll(formatNodeProperties(node));
        
        return formattedNode;
    }
    
    private static List<String> formatNodeProperties(Node node) throws RepositoryException {
        List<String> formattedNodeProperties = new ArrayList<String>();

        Iterator<Property> i = node.getProperties();
        while (i.hasNext()){
            Property property = i.next();
            formattedNodeProperties.add(
                    format("%s %s = '%s'", PROPERTY_SIGN, property.getName(), formatProperty(property) ) 
            );
        }
        
        return formattedNodeProperties;
    }
    
    private static String formatProperty(Property property) throws RepositoryException {
        if (property.isMultiple()){
            return formatMultipleValue(property.getValues());
        } else {
            return formatValue(property.getValue());            
        }
    }

    private static String formatMultipleValue(Value[] values) throws RepositoryException {
        List<String> formattedValues = new ArrayList<String>(); 
        
        for (Value value: values){
            formattedValues.add(formatValue(value));
        }
        
        return formattedValues.toString();
    }

    private static String formatValue(Value value) throws RepositoryException {
        switch (value.getType()){
            case PropertyType.DATE : 
                return sdf.format(value.getDate().getTime());
            default:
                return value.getString();
            
        }
    }
}
