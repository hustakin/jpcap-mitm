/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.req;

import java.util.List;

/**
 * @author Frankie
 */
public class PacketFilterParamsReq {
    private List<Long> batchIds;
    private List<Boolean> directions;
    private List<String> protocols;
    private List<String> contentTypes; //only for http packets

    public List<Long> getBatchIds() {
        return batchIds;
    }

    public void setBatchIds(List<Long> batchIds) {
        this.batchIds = batchIds;
    }

    public List<Boolean> getDirections() {
        return directions;
    }

    public void setDirections(List<Boolean> directions) {
        this.directions = directions;
    }

    public List<String> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<String> protocols) {
        this.protocols = protocols;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }
}
