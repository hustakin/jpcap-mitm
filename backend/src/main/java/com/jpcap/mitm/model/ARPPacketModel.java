/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import jpcap.packet.ARPPacket;

/**
 * @author Frankie
 */
public class ARPPacketModel extends PacketModel {
    private short hardtype;
    private short prototype;
    private short hlen;
    private short plen;
    private short operation;
    private byte[] sender_hardaddr;
    private byte[] sender_protoaddr;
    private byte[] target_hardaddr;
    private byte[] target_protoaddr;

    public static ARPPacketModel readFrom(ARPPacket packet) {
        ARPPacketModel model = new ARPPacketModel();
        model.setSec(packet.sec);
        model.setUsec(packet.usec);
        model.setCaplen(packet.caplen);
        model.setLen(packet.len);
        model.setHeader(packet.header);
        model.setData(packet.data);
        model.setDatalink(EthernetPacketModel.readFrom(packet.datalink));

        model.setHardtype(packet.hardtype);
        model.setPrototype(packet.prototype);
        model.setHlen(packet.hlen);
        model.setPlen(packet.plen);
        model.setOperation(packet.operation);
        model.setSender_hardaddr(packet.sender_hardaddr);
        model.setSender_protoaddr(packet.sender_protoaddr);
        model.setTarget_hardaddr(packet.target_hardaddr);
        model.setTarget_protoaddr(packet.target_protoaddr);
        return model;
    }

    public short getHardtype() {
        return hardtype;
    }

    public void setHardtype(short hardtype) {
        this.hardtype = hardtype;
    }

    public short getPrototype() {
        return prototype;
    }

    public void setPrototype(short prototype) {
        this.prototype = prototype;
    }

    public short getHlen() {
        return hlen;
    }

    public void setHlen(short hlen) {
        this.hlen = hlen;
    }

    public short getPlen() {
        return plen;
    }

    public void setPlen(short plen) {
        this.plen = plen;
    }

    public short getOperation() {
        return operation;
    }

    public void setOperation(short operation) {
        this.operation = operation;
    }

    public byte[] getSender_hardaddr() {
        return sender_hardaddr;
    }

    public void setSender_hardaddr(byte[] sender_hardaddr) {
        this.sender_hardaddr = sender_hardaddr;
    }

    public byte[] getSender_protoaddr() {
        return sender_protoaddr;
    }

    public void setSender_protoaddr(byte[] sender_protoaddr) {
        this.sender_protoaddr = sender_protoaddr;
    }

    public byte[] getTarget_hardaddr() {
        return target_hardaddr;
    }

    public void setTarget_hardaddr(byte[] target_hardaddr) {
        this.target_hardaddr = target_hardaddr;
    }

    public byte[] getTarget_protoaddr() {
        return target_protoaddr;
    }

    public void setTarget_protoaddr(byte[] target_protoaddr) {
        this.target_protoaddr = target_protoaddr;
    }
}
