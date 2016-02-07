package com.himmel.graduate.code.DB.Data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Lyaro on 20.12.2015.
 */
public class MyFile {
    private final IntegerProperty id;
    private final StringProperty path;
    private final StringProperty md5;

    public MyFile(int id, String path, String md5){
        this.id = new SimpleIntegerProperty(id);
        this.path = new SimpleStringProperty(path);
        this.md5 = new SimpleStringProperty(md5);
    }
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getPath() {
        return path.get();
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public StringProperty pathProperty() {
        return path;
    }

    public String getMd5() {
        return md5.get();
    }

    public void setMd5(String md5) {
        this.md5.set(md5);
    }

    public StringProperty md5Property() {
        return md5;
    }
}
