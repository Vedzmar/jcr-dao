package com.epam.trainings.jcr.entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Dzianis on 15.08.2015.
 */
public class Child {
    private String name;
    private Calendar birthDate;

    public Child(String name, Calendar birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Calendar birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Child{" +
                "name='" + name + '\'' +
                ", birthDate=" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(birthDate.getTime()) +
                '}';
    }
}
