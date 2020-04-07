/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import java.io.Serializable;

/**
 *
 * @author Frankie
 */
public class DataTableRow implements Serializable {

    private String[][] data;

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }
}
