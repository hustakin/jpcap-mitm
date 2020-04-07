/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class PageSort implements Serializable {
    private String prop; //The sort column
    private String dir; //The sort column direction

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
