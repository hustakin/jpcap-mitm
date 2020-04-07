/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.http;

import com.jpcap.mitm.analysis.IPacketModelFilter;
import com.jpcap.mitm.model.IPPacketModel;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.TCPPacketModel;

/**
 * 过滤出HTTPS包
 *
 * @author Frankie
 */
public class HttpsPacketModelFilter implements IPacketModelFilter {
    @Override
    public boolean filter(PacketModel packet) {
        if (packet instanceof IPPacketModel) {        //分析IP
            IPPacketModel iPacketModel = (IPPacketModel) packet;
            if (iPacketModel instanceof TCPPacketModel) {      //分析TCP协议
                TCPPacketModel tcpPacketModel = (TCPPacketModel) iPacketModel;
                if (tcpPacketModel.getSrc_port() == 443 || tcpPacketModel.getDst_port() == 443) {     //分析HTTPS协议
                    return false;
                }
            }
        }
        return true;
    }
}
