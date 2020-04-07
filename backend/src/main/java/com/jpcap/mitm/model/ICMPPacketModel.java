/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;

import java.net.InetAddress;

/**
 * @author Frankie
 */
public class ICMPPacketModel extends IPPacketModel {
    private byte type;
    private byte code;
    private short checksum;
    private short id;
    private short seq;
    private int subnetmask;
    private int orig_timestamp;
    private int recv_timestamp;
    private int trans_timestamp;
    private short mtu;
    private IPPacket ippacket;
    private InetAddress redir_ip;
    private byte addr_num;
    private byte addr_entry_size;
    private short alive_time;
    private InetAddress[] router_ip;
    private int[] preference;

    public static ICMPPacketModel readFrom(ICMPPacket packet) {
        ICMPPacketModel model = new ICMPPacketModel();
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

        model.setType(packet.type);
        model.setCode(packet.code);
        model.setChecksum(packet.checksum);
        model.setId(packet.id);
        model.setSeq(packet.seq);
        model.setSubnetmask(packet.subnetmask);
        model.setOrig_timestamp(packet.orig_timestamp);
        model.setRecv_timestamp(packet.recv_timestamp);
        model.setTrans_timestamp(packet.trans_timestamp);
        model.setMtu(packet.mtu);
        model.setIppacket(packet.ippacket);
        model.setRedir_ip(packet.redir_ip);
        model.setAddr_num(packet.addr_num);
        model.setAddr_entry_size(packet.addr_entry_size);
        model.setAlive_time(packet.alive_time);
        model.setRouter_ip(packet.router_ip);
        model.setPreference(packet.preference);
        return model;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public short getChecksum() {
        return checksum;
    }

    public void setChecksum(short checksum) {
        this.checksum = checksum;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public int getSubnetmask() {
        return subnetmask;
    }

    public void setSubnetmask(int subnetmask) {
        this.subnetmask = subnetmask;
    }

    public int getOrig_timestamp() {
        return orig_timestamp;
    }

    public void setOrig_timestamp(int orig_timestamp) {
        this.orig_timestamp = orig_timestamp;
    }

    public int getRecv_timestamp() {
        return recv_timestamp;
    }

    public void setRecv_timestamp(int recv_timestamp) {
        this.recv_timestamp = recv_timestamp;
    }

    public int getTrans_timestamp() {
        return trans_timestamp;
    }

    public void setTrans_timestamp(int trans_timestamp) {
        this.trans_timestamp = trans_timestamp;
    }

    public short getMtu() {
        return mtu;
    }

    public void setMtu(short mtu) {
        this.mtu = mtu;
    }

    public IPPacket getIppacket() {
        return ippacket;
    }

    public void setIppacket(IPPacket ippacket) {
        this.ippacket = ippacket;
    }

    public InetAddress getRedir_ip() {
        return redir_ip;
    }

    public void setRedir_ip(InetAddress redir_ip) {
        this.redir_ip = redir_ip;
    }

    public byte getAddr_num() {
        return addr_num;
    }

    public void setAddr_num(byte addr_num) {
        this.addr_num = addr_num;
    }

    public byte getAddr_entry_size() {
        return addr_entry_size;
    }

    public void setAddr_entry_size(byte addr_entry_size) {
        this.addr_entry_size = addr_entry_size;
    }

    public short getAlive_time() {
        return alive_time;
    }

    public void setAlive_time(short alive_time) {
        this.alive_time = alive_time;
    }

    public InetAddress[] getRouter_ip() {
        return router_ip;
    }

    public void setRouter_ip(InetAddress[] router_ip) {
        this.router_ip = router_ip;
    }

    public int[] getPreference() {
        return preference;
    }

    public void setPreference(int[] preference) {
        this.preference = preference;
    }
}
