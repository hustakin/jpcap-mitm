/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

/**
 * 过滤出待测试的HTTP包
 *
 * @author Frankie
 */
@SuppressWarnings("Duplicates")
public class TestHttpPacketFilter implements IPacketFilter {
    @Override
    public boolean filter(Packet packet) {
        if (packet instanceof IPPacket) {        //分析IP
            IPPacket iPacket = (IPPacket) packet;
            if (iPacket instanceof TCPPacket) {      //分析TCP协议
                TCPPacket tPacket = (TCPPacket) iPacket;
                if (tPacket.src_port == 80 || tPacket.dst_port == 80) {     //分析HTTP协议
                    String testIP = "/162.210.102.213";
                    //String testUrl = "51.qiusuo.me";
                    if (iPacket.src_ip.toString().equals(testIP)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
