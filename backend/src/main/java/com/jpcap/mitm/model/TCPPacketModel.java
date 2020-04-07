/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import jpcap.packet.TCPPacket;

/**
 * @author Frankie
 */
public class TCPPacketModel extends IPPacketModel {
    private int src_port;
    private int dst_port;
    private long sequence;
    private long ack_num;
    private boolean urg;
    private boolean ack;
    private boolean psh;
    private boolean rst;
    private boolean syn;
    private boolean fin;
    private boolean rsv1;
    private boolean rsv2;
    private int window;
    private short urgent_pointer;

    public static TCPPacketModel readFrom(TCPPacket packet) {
        TCPPacketModel model = new TCPPacketModel();
        model.setSec(packet.sec);
        model.setUsec(packet.usec);
        model.setCaplen(packet.caplen);
        model.setLen(packet.len);
        model.setHeader(packet.header);
        model.setData(packet.data);
        model.setDatalink(EthernetPacketModel.readFrom(packet.datalink));

        model.setVersion(packet.version);
        model.setPriority(packet.priority);
        model.setD_flag(packet.d_flag);
        model.setT_flag(packet.t_flag);
        model.setR_flag(packet.r_flag);
        model.setRsv_tos(packet.rsv_tos);
        model.setLength(packet.length);
        model.setRsv_frag(packet.rsv_frag);
        model.setDont_frag(packet.dont_frag);
        model.setMore_frag(packet.more_frag);
        model.setOffset(packet.offset);
        model.setHop_limit(packet.hop_limit);
        model.setProtocol(packet.protocol);
        model.setIdent(packet.ident);
        model.setFlow_label(packet.flow_label);
        model.setSrc_ip(packet.src_ip.getHostAddress());
        model.setDst_ip(packet.dst_ip.getHostAddress());
        model.setOption(packet.option);
        model.setOptions(packet.options);

        model.setSrc_port(packet.src_port);
        model.setDst_port(packet.dst_port);
        model.setSequence(packet.sequence);
        model.setAck_num(packet.ack_num);
        model.setUrg(packet.urg);
        model.setAck(packet.ack);
        model.setPsh(packet.psh);
        model.setRst(packet.rst);
        model.setSyn(packet.syn);
        model.setFin(packet.fin);
        model.setRsv1(packet.rsv1);
        model.setRsv2(packet.rsv2);
        model.setWindow(packet.window);
        model.setUrgent_pointer(packet.urgent_pointer);
        return model;
    }

    public int getSrc_port() {
        return src_port;
    }

    public void setSrc_port(int src_port) {
        this.src_port = src_port;
    }

    public int getDst_port() {
        return dst_port;
    }

    public void setDst_port(int dst_port) {
        this.dst_port = dst_port;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getAck_num() {
        return ack_num;
    }

    public void setAck_num(long ack_num) {
        this.ack_num = ack_num;
    }

    public boolean isUrg() {
        return urg;
    }

    public void setUrg(boolean urg) {
        this.urg = urg;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public boolean isPsh() {
        return psh;
    }

    public void setPsh(boolean psh) {
        this.psh = psh;
    }

    public boolean isRst() {
        return rst;
    }

    public void setRst(boolean rst) {
        this.rst = rst;
    }

    public boolean isSyn() {
        return syn;
    }

    public void setSyn(boolean syn) {
        this.syn = syn;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public boolean isRsv1() {
        return rsv1;
    }

    public void setRsv1(boolean rsv1) {
        this.rsv1 = rsv1;
    }

    public boolean isRsv2() {
        return rsv2;
    }

    public void setRsv2(boolean rsv2) {
        this.rsv2 = rsv2;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public short getUrgent_pointer() {
        return urgent_pointer;
    }

    public void setUrgent_pointer(short urgent_pointer) {
        this.urgent_pointer = urgent_pointer;
    }
}
