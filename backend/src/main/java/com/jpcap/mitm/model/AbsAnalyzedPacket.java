/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
public class AbsAnalyzedPacket implements Serializable {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbsAnalyzedPacket.class);
    private static final int THRESHOLD_BYTE_SIZE = 16000000;

    protected String protocol;

    protected Long ackNum;
    protected List<String> capturedPacketIds;
    @Indexed
    protected boolean upstream;
    protected boolean minimized = false;
    protected byte[] data;
    protected String content;
    protected Date created = new Date();
    protected Date time; //the real time in the packet
    @Indexed
    protected String minuteTimeStr;

    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;
    protected int srcPort;
    protected int destPort;

    public void minimizeContent() {
        logger.info("the packet is too large, so minimize it.");
        this.minimized = true;
        this.setContent("--Too large to display--");
        if (this.data.length >= THRESHOLD_BYTE_SIZE) {
            this.setData(new byte[0]);
        }
    }

    public String getMinuteTimeStr() {
        return minuteTimeStr;
    }

    public void setMinuteTimeStr(String minuteTimeStr) {
        this.minuteTimeStr = minuteTimeStr;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public String getDestMac() {
        return destMac;
    }

    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }

    public Long getAckNum() {
        return ackNum;
    }

    public void setAckNum(Long ackNum) {
        this.ackNum = ackNum;
    }

    public boolean isUpstream() {
        return upstream;
    }

    public void setUpstream(boolean upstream) {
        this.upstream = upstream;
    }

    public boolean isMinimized() {
        return minimized;
    }

    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getCapturedPacketIds() {
        return capturedPacketIds;
    }

    public void setCapturedPacketIds(List<String> capturedPacketIds) {
        this.capturedPacketIds = capturedPacketIds;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
