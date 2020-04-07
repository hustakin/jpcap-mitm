/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import jpcap.packet.UDPPacket;

/**
 * @author Frankie
 */
public class UDPPacketModel extends IPPacketModel {
    private int src_port;
    private int dst_port;
    private int iLength;

    public static UDPPacketModel readFrom(UDPPacket packet) {
        UDPPacketModel model = new UDPPacketModel();
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
        model.setiLength(packet.length);
        return model;
    }

    public int getiLength() {
        return iLength;
    }

    public void setiLength(int iLength) {
        this.iLength = iLength;
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
}
