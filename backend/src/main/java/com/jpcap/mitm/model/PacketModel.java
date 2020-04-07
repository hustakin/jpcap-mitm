
/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import jpcap.packet.Packet;

import java.io.Serializable;

public class PacketModel implements Serializable {
    private long sec;
    private long usec;
    private int caplen;
    private int len;
    public EthernetPacketModel datalink;
    private byte[] header;
    private byte[] data;

    public static PacketModel readFrom(Packet packet) {
        PacketModel model = new PacketModel();
        model.setSec(packet.sec);
        model.setUsec(packet.usec);
        model.setCaplen(packet.caplen);
        model.setLen(packet.len);
        model.setHeader(packet.header);
        model.setData(packet.data);
        model.setDatalink(EthernetPacketModel.readFrom(packet.datalink));
        return model;
    }

    public long getSec() {
        return sec;
    }

    public void setSec(long sec) {
        this.sec = sec;
    }

    public long getUsec() {
        return usec;
    }

    public void setUsec(long usec) {
        this.usec = usec;
    }

    public int getCaplen() {
        return caplen;
    }

    public void setCaplen(int caplen) {
        this.caplen = caplen;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public EthernetPacketModel getDatalink() {
        return datalink;
    }

    public void setDatalink(EthernetPacketModel datalink) {
        this.datalink = datalink;
    }
}
