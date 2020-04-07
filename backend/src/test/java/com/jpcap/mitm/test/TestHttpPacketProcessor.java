/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import com.jpcap.mitm.dao.GenericDao;
import com.jpcap.mitm.entity.TestCapturedPacket;
import com.jpcap.mitm.model.TCPPacketModel;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

/**
 * @author Frankie
 */
public class TestHttpPacketProcessor implements IPacketProcessor {
    private GenericDao genericDao;

    public TestHttpPacketProcessor(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public void process(Packet packet) {
        IPPacket iPacket = (IPPacket) packet;
        String testIP = "/162.210.102.213";
        if (iPacket.src_ip.toString().equals(testIP)) {
            TestCapturedPacket capturedPacket = new TestCapturedPacket();
            capturedPacket.setUpstream(false);
            if (packet instanceof TCPPacket) {
                capturedPacket.setPacket(TCPPacketModel.readFrom((TCPPacket) packet));
                genericDao.insert(capturedPacket);
            }
        } else if (iPacket.dst_ip.toString().equals(testIP)) {
        }
    }
}
