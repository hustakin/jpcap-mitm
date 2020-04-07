/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.entity;

import com.jpcap.mitm.model.PacketModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Frankie
 */
@Document(collection = "TestCapturedPackets")
public class TestCapturedPacket implements Serializable {

    @Id
    private String id;

    private Date created = new Date();

    //upstream means packaets from dest to gateway
    private boolean upstream;

    private PacketModel packet;

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
