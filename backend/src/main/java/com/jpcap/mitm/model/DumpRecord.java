/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Frankie
 */
public class DumpRecord implements Serializable {
    private Long batchId;
    private Date startAttackTime;
    private long upstreamPackets = 0;
    private long downstreamPackets = 0;

    public void addPacket(boolean upstream) {
        if (upstream) upstreamPackets++;
        else downstreamPackets++;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Date getStartAttackTime() {
        return startAttackTime;
    }

    public void setStartAttackTime(Date startAttackTime) {
        this.startAttackTime = startAttackTime;
    }

    public long getUpstreamPackets() {
        return upstreamPackets;
    }

    public void setUpstreamPackets(long upstreamPackets) {
        this.upstreamPackets = upstreamPackets;
    }

    public long getDownstreamPackets() {
        return downstreamPackets;
    }

    public void setDownstreamPackets(long downstreamPackets) {
        this.downstreamPackets = downstreamPackets;
    }
}
