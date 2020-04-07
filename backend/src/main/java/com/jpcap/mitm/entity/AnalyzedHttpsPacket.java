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
@Document(collection = "AnalyzedHttpsPackets")
public class AnalyzedHttpsPacket extends AbsAnalyzedPacket {

    @Id
    private String id;

    @Indexed
    private Long batchId;

    private boolean decrypted;

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

    public boolean isDecrypted() {
        return decrypted;
    }

    public void setDecrypted(boolean decrypted) {
        this.decrypted = decrypted;
    }
}
