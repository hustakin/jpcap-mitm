/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.entity;

import com.jpcap.mitm.model.AbsAnalyzedPacket;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * @author Frankie
 */
@Document(collection = "AnalyzedHttpPackets")
public class AnalyzedHttpPacket extends AbsAnalyzedPacket {

    @Id
    private String id;

    @Indexed
    private Long batchId;

    private boolean compressed;

    @Indexed
    private String contentType;

    private Map<String, String> httpHeaders;

    private Boolean httpReq;

    private String method;

    public Boolean isHttpReq() {
        return httpReq;
    }

    public void setHttpReq(Boolean httpReq) {
        this.httpReq = httpReq;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

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

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
}
