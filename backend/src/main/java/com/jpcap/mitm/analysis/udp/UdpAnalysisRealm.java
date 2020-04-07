/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.udp;

import com.jpcap.mitm.analysis.ISingleAnalysisRealm;
import com.jpcap.mitm.analysis.ProtocolEnum;
import com.jpcap.mitm.entity.AnalyzedUdpPacket;
import com.jpcap.mitm.model.AbsAnalyzedPacket;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.UDPPacketModel;
import com.jpcap.mitm.utils.Helper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
public class UdpAnalysisRealm implements ISingleAnalysisRealm {
    protected Date time;
    protected boolean upstream;
    protected Long batchId;
    protected List<String> capturedPacketIds = new ArrayList<>();
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;
    protected int srcPort;
    protected int destPort;

    @Override
    public String protocol() {
        return ProtocolEnum.UDP.name();
    }

    @Override
    public void initPacket(Long batchId, String capturePacketId, boolean upstream, PacketModel packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        UDPPacketModel tcpPacketModel = (UDPPacketModel) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.contentBytes = tcpPacketModel.getData();
        this.srcIp = tcpPacketModel.getSrc_ip();
        this.destIp = tcpPacketModel.getDst_ip();
        this.srcPort = tcpPacketModel.getSrc_port();
        this.destPort = tcpPacketModel.getDst_port();
        if (tcpPacketModel.getDatalink() != null) {
            this.srcMac = tcpPacketModel.getDatalink().getSourceAddress();
            this.destMac = tcpPacketModel.getDatalink().getDestinationAddress();
        }
    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        String realContent = null;
        try {
            realContent = new String(this.contentBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedUdpPacket analyzedUdpPacket = new AnalyzedUdpPacket();
        analyzedUdpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedUdpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedUdpPacket.setProtocol(this.protocol());
        analyzedUdpPacket.setUpstream(this.upstream);
        analyzedUdpPacket.setBatchId(this.batchId);
        analyzedUdpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedUdpPacket.setData(this.contentBytes);
        analyzedUdpPacket.setContent(realContent);
        analyzedUdpPacket.setSrcIp(this.srcIp);
        analyzedUdpPacket.setDestIp(this.destIp);
        analyzedUdpPacket.setSrcPort(this.srcPort);
        analyzedUdpPacket.setDestPort(this.destPort);
        analyzedUdpPacket.setSrcMac(this.srcMac);
        analyzedUdpPacket.setDestMac(this.destMac);
        System.out.println("++++++save udp packet");
        return analyzedUdpPacket;
    }
}
