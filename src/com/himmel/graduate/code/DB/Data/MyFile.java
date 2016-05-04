package com.himmel.graduate.code.DB.Data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lyaro on 11.04.2016.
 */
public class MyFile implements Serializable, Comparable {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private int id;
    private String path;
    private Date dateLastChanges;
    private Date dateHash;

    public MyFile (String name) {
        id = 0;
        this.path = name;
        this.dateLastChanges = new Date();
        this.dateHash = new Date();
    }

    public MyFile (int id, String name, String dateLastChanges, String dataHash) {
        this.id = id;
        this.path = name;
        try {
            this.dateLastChanges = SIMPLE_DATE_FORMAT.parse(dateLastChanges);
            this.dateHash = SIMPLE_DATE_FORMAT.parse(dataHash);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public MyFile (MyFile file) {
        this.id = file.id;
        this.path = new String(file.getPath());
        this.dateLastChanges = new Date(file.dateLastChanges.getTime());
        this.dateHash = new Date(file.dateHash.getTime());
    }

    public int getId() {
        return id;
    }

    public Date getDateLastChanges() {
        return dateLastChanges;
    }

    public void setDateLastChanges(Date dateLastChanges) {
        this.dateLastChanges = dateLastChanges;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getDateHash() {
        return dateHash;
    }

    public void setDateHash(Date dateHash) {
        this.dateHash = dateHash;
    }

    public StringProperty pathProperty() {
        return new SimpleStringProperty(path);
    }

    @Override
    public int compareTo(Object o) {
        MyFile file = (MyFile) o;
        if (this.id > file.id) {
            return 1;
        } else {
            if (this.id < file.id) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals (Object o) {
        return this.path.equals(((MyFile) o).path);
    }
}
