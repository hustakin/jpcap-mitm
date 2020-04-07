/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Create and update time
 *
 * @author Frankie
 */
public class Timestamp implements Serializable {

    private Date created = new Date();
    private Date updated = new Date();

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
