/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.entity;

import com.jpcap.mitm.model.PacketModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
@Document(collection = "CapturedPackets")
@CompoundIndexes(value = {
        @CompoundIndex(name = "time_upstream_idx", def = "{'startAttackTime': -1, 'upstream': -1}")
})
public class CapturedPacket implements Serializable {

    @Id
    private String id;

    private Date created = new Date();

    //upstream means packaets from dest to gateway
    private boolean upstream;

    @Indexed
    private Long batchId;

    @Indexed
    private Date startAttackTime;

    private PacketModel packet;

    @Indexed
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUpstream() {
        return upstream;
    }

    public void setUpstream(boolean upstream) {
        this.upstream = upstream;
    }

    public Date getStartAttackTime() {
        return startAttackTime;
    }

    public void setStartAttackTime(Date startAttackTime) {
        this.startAttackTime = startAttackTime;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public PacketModel getPacket() {
        return packet;
    }

    public void setPacket(PacketModel packet) {
        this.packet = packet;
    }
}
