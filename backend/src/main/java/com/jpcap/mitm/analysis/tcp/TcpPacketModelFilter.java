/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.tcp;

import com.jpcap.mitm.analysis.IPacketModelFilter;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.TCPPacketModel;

/**
 * 过滤出非HTTP/HTTPS的TCP包
 *
 * @author Frankie
 */
public class TcpPacketModelFilter implements IPacketModelFilter {
    @Override
    public boolean filter(PacketModel packet) {
        //分析TCP协议
        if (packet instanceof TCPPacketModel) {
            TCPPacketModel tcpPacketModel = (TCPPacketModel) packet;
            if (tcpPacketModel.getSrc_port() != 80 &&
                    tcpPacketModel.getDst_port() != 80 &&
                    tcpPacketModel.getSrc_port() != 443 &&
                    tcpPacketModel.getDst_port() != 443) {
                return false;
            }
        }
        return true;
    }
}
