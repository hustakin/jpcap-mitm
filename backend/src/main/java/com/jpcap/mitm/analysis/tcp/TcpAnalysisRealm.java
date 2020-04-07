/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.tcp;

import com.jpcap.mitm.analysis.IAnalysisRealm;
import com.jpcap.mitm.analysis.ProtocolEnum;
import com.jpcap.mitm.entity.AnalyzedTcpPacket;
import com.jpcap.mitm.model.AbsAnalyzedPacket;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.TCPPacketModel;
import com.jpcap.mitm.utils.Helper;
import com.jpcap.mitm.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
@SuppressWarnings("Duplicates")
public class TcpAnalysisRealm implements IAnalysisRealm {
    protected Date time;
    protected boolean upstream;
    protected Long batchId;
    protected List<String> capturedPacketIds = new ArrayList<>();
    protected Long ackNum;
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;
    protected int srcPort;
    protected int destPort;

    @Override
    public String protocol() {
        return ProtocolEnum.TCP.name();
    }

    @Override
    public void initPacket(Long batchId, String capturePacketId, boolean upstream, PacketModel packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        TCPPacketModel tcpPacketModel = (TCPPacketModel) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.ackNum = tcpPacketModel.getAck_num();
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
    public void appendPacket(String capturePacketId, PacketModel packet) {
        this.capturedPacketIds.add(capturePacketId);

        TCPPacketModel tcpPacketModel = (TCPPacketModel) packet;
        byte[] newBytes = NetworkUtils.concatBytes(this.contentBytes, tcpPacketModel.getData());
        this.contentBytes = newBytes;
    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        String realContent = null;
        try {
            realContent = new String(this.contentBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedTcpPacket analyzedTcpPacket = new AnalyzedTcpPacket();
        analyzedTcpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedTcpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedTcpPacket.setProtocol(this.protocol());
        analyzedTcpPacket.setUpstream(this.upstream);
        analyzedTcpPacket.setBatchId(this.batchId);
        analyzedTcpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedTcpPacket.setAckNum(this.ackNum);
        analyzedTcpPacket.setData(this.contentBytes);
        analyzedTcpPacket.setContent(realContent);
        analyzedTcpPacket.setSrcIp(this.srcIp);
        analyzedTcpPacket.setDestIp(this.destIp);
        analyzedTcpPacket.setSrcPort(this.srcPort);
        analyzedTcpPacket.setDestPort(this.destPort);
        analyzedTcpPacket.setSrcMac(this.srcMac);
        analyzedTcpPacket.setDestMac(this.destMac);
        System.out.println("++++++save tcp packet: " + this.ackNum);
        return analyzedTcpPacket;
    }
}
