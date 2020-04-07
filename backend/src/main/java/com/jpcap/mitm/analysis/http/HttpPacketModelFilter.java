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
 * 过滤出HTTP包
 *
 * @author Frankie
 */
public class HttpPacketModelFilter implements IPacketModelFilter {
    @Override
    public boolean filter(PacketModel packet) {
        if (packet instanceof IPPacketModel) {        //分析IP
            IPPacketModel iPacketModel = (IPPacketModel) packet;
            if (iPacketModel instanceof TCPPacketModel) {      //分析TCP协议
                TCPPacketModel tcpPacketModel = (TCPPacketModel) iPacketModel;
                if (tcpPacketModel.getSrc_port() == 80 || tcpPacketModel.getDst_port() == 80) {     //分析HTTP协议
                    return false;
                }
            }
        }
        return true;
    }
}
