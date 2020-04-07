/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import jpcap.packet.IPPacket;

import java.util.List;

/**
 * @author Frankie
 */
public class IPPacketModel extends PacketModel {
    private byte version;
    private byte priority;
    private boolean d_flag;
    private boolean t_flag;
    private boolean r_flag;
    private byte rsv_tos;
    private int length;
    private boolean rsv_frag;
    private boolean dont_frag;
    private boolean more_frag;
    private short offset;
    private short hop_limit;
    private short protocol;
    private int ident;
    private int flow_label;
    private String src_ip;
    private String dst_ip;
    private byte[] option;
    private List options = null;

    public static IPPacketModel readFrom(IPPacket packet) {
        IPPacketModel model = new IPPacketModel();
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
        return model;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public boolean isD_flag() {
        return d_flag;
    }

    public void setD_flag(boolean d_flag) {
        this.d_flag = d_flag;
    }

    public boolean isT_flag() {
        return t_flag;
    }

    public void setT_flag(boolean t_flag) {
        this.t_flag = t_flag;
    }

    public boolean isR_flag() {
        return r_flag;
    }

    public void setR_flag(boolean r_flag) {
        this.r_flag = r_flag;
    }

    public byte getRsv_tos() {
        return rsv_tos;
    }

    public void setRsv_tos(byte rsv_tos) {
        this.rsv_tos = rsv_tos;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isRsv_frag() {
        return rsv_frag;
    }

    public void setRsv_frag(boolean rsv_frag) {
        this.rsv_frag = rsv_frag;
    }

    public boolean isDont_frag() {
        return dont_frag;
    }

    public void setDont_frag(boolean dont_frag) {
        this.dont_frag = dont_frag;
    }

    public boolean isMore_frag() {
        return more_frag;
    }

    public void setMore_frag(boolean more_frag) {
        this.more_frag = more_frag;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public short getHop_limit() {
        return hop_limit;
    }

    public void setHop_limit(short hop_limit) {
        this.hop_limit = hop_limit;
    }

    public short getProtocol() {
        return protocol;
    }

    public void setProtocol(short protocol) {
        this.protocol = protocol;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public int getFlow_label() {
        return flow_label;
    }

    public void setFlow_label(int flow_label) {
        this.flow_label = flow_label;
    }

    public String getSrc_ip() {
        return src_ip;
    }

    public void setSrc_ip(String src_ip) {
        this.src_ip = src_ip;
    }

    public String getDst_ip() {
        return dst_ip;
    }

    public void setDst_ip(String dst_ip) {
        this.dst_ip = dst_ip;
    }

    public byte[] getOption() {
        return option;
    }

    public void setOption(byte[] option) {
        this.option = option;
    }

    public List getOptions() {
        return options;
    }

    public void setOptions(List options) {
        this.options = options;
    }
}
