/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.dto;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class ResultDTO implements Serializable {
    private Serializable result;

    public ResultDTO() {
    }

    public ResultDTO(Serializable result) {
        this.result = result;
    }

    public Serializable getResult() {
        return result;
    }

    public void setResult(Serializable result) {
        this.result = result;
    }
}
