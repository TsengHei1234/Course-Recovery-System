package com.crs.model;

import java.io.Serializable;

public class ReferenceOption implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String label;

    public ReferenceOption() {
    }

    public ReferenceOption(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
