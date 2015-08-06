package com.epam.trainings.jcr.entities;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Employee {
    private String id;
    private String name;
    private long age;
    private Calendar hiringDate;

    public Employee(String name, long age, Calendar hiringDate) {
        this.name = name;
        this.age = age;
        this.hiringDate = hiringDate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hiringDate=" +new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(hiringDate.getTime()) +
                '}';
    }

    public Employee(String id, String name, long age, Calendar hiringDate) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
