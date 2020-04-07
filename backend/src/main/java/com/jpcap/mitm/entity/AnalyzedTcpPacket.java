/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.entity;

import com.jpcap.mitm.model.AbsAnalyzedPacket;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Frankie
 */
@Document(collection = "AnalyzedTcpPackets")
public class AnalyzedTcpPacket extends AbsAnalyzedPacket {

    @Id
    private String id;

    @Indexed
    private Long batchId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }
}
