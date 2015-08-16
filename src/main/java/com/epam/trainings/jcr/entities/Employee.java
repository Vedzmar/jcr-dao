package com.epam.trainings.jcr.entities;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Employee {
    private long id;
    private String name;
    private long age;
    private Calendar hiringDate;
    private List<Child> children;

    public Employee(String name, long age, Calendar hiringDate) {
        this.id = System.currentTimeMillis();
        this.name = name;
        this.age = age;
        this.hiringDate = hiringDate;
        this.children = new ArrayList<Child>();
    }
    public Employee(long id, String name, long age, Calendar hiringDate, List<Child> children) {
        this(name,age,hiringDate,children);
        this.id = id;
    }

    public Employee(String name, long age, Calendar hiringDate, List<Child> children) {
        this(name, age, hiringDate);
        this.children = children;
    }
    
    public Employee(long id, String name, long age, Calendar hiringDate) {
        this(name, age, hiringDate);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Calendar getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(Calendar hiringDate) {
        this.hiringDate = hiringDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hiringDate=" + new SimpleDateFormat("MM/dd/yyyy").format(hiringDate.getTime()) +
                ", children=" + children +
                '}';
    }
}
