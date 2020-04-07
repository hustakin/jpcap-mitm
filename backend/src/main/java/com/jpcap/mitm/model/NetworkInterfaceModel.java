/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import java.io.Serializable;

public class NetworkInterfaceModel implements Serializable {
    private String name;
    private String description;
    private String datalinkName;
    private String datalinkDescription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatalinkName() {
        return datalinkName;
    }

    public void setDatalinkName(String datalinkName) {
        this.datalinkName = datalinkName;
    }

    public String getDatalinkDescription() {
        return datalinkDescription;
    }

    public void setDatalinkDescription(String datalinkDescription) {
        this.datalinkDescription = datalinkDescription;
    }
}
