/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.req;

import java.util.List;

/**
 * @author Frankie
 */
public class PacketStatisticFilterParamsReq {
    private List<Long> batchIds;

    public List<Long> getBatchIds() {
        return batchIds;
    }

    public void setBatchIds(List<Long> batchIds) {
        this.batchIds = batchIds;
    }
}
