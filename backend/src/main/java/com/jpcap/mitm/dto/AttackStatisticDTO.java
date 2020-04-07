/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.dto;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class AttackStatisticDTO implements Serializable {
    private long upStreamNum;
    private long downStreamNum;
    private long upTcpNum;
    private long upUdpNum;
    private long upIcmpNum;
    private long upArpNum;
    private long downTcpNum;
    private long downUdpNum;
    private long downIcmpNum;
    private long downArpNum;

    public long getUpStreamNum() {
        return upStreamNum;
    }

    public void setUpStreamNum(long upStreamNum) {
        this.upStreamNum = upStreamNum;
    }

    public long getDownStreamNum() {
        return downStreamNum;
    }

    public void setDownStreamNum(long downStreamNum) {
        this.downStreamNum = downStreamNum;
    }

    public long getUpTcpNum() {
        return upTcpNum;
    }

    public void setUpTcpNum(long upTcpNum) {
        this.upTcpNum = upTcpNum;
    }

    public long getUpUdpNum() {
        return upUdpNum;
    }

    public void setUpUdpNum(long upUdpNum) {
        this.upUdpNum = upUdpNum;
    }

    public long getUpIcmpNum() {
        return upIcmpNum;
    }

    public void setUpIcmpNum(long upIcmpNum) {
        this.upIcmpNum = upIcmpNum;
    }

    public long getUpArpNum() {
        return upArpNum;
    }

    public void setUpArpNum(long upArpNum) {
        this.upArpNum = upArpNum;
    }

    public long getDownTcpNum() {
        return downTcpNum;
    }

    public void setDownTcpNum(long downTcpNum) {
        this.downTcpNum = downTcpNum;
    }

    public long getDownUdpNum() {
        return downUdpNum;
    }

    public void setDownUdpNum(long downUdpNum) {
        this.downUdpNum = downUdpNum;
    }

    public long getDownIcmpNum() {
        return downIcmpNum;
    }

    public void setDownIcmpNum(long downIcmpNum) {
        this.downIcmpNum = downIcmpNum;
    }

    public long getDownArpNum() {
        return downArpNum;
    }

    public void setDownArpNum(long downArpNum) {
        this.downArpNum = downArpNum;
    }
}
