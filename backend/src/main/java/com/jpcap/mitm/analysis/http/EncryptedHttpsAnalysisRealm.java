/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.http;

import com.jpcap.mitm.analysis.IAnalysisRealm;
import com.jpcap.mitm.analysis.ProtocolEnum;
import com.jpcap.mitm.entity.AnalyzedHttpsPacket;
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
public class EncryptedHttpsAnalysisRealm implements IAnalysisRealm {
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

    @Override
    public String protocol() {
        return ProtocolEnum.HTTPS.name();
    }

    @Override
    public void initPacket(Long batchId, String capturePacketId, boolean upstream, PacketModel packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        TCPPacketModel ipPacketModel = (TCPPacketModel) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.ackNum = ipPacketModel.getAck_num();
        this.contentBytes = ipPacketModel.getData();
        this.srcIp = ipPacketModel.getSrc_ip();
        this.destIp = ipPacketModel.getDst_ip();
        if (ipPacketModel.getDatalink() != null) {
            this.srcMac = ipPacketModel.getDatalink().getSourceAddress();
            this.destMac = ipPacketModel.getDatalink().getDestinationAddress();
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
            realContent = new String(this.contentBytes, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedHttpsPacket analyzedHttpsPacket = new AnalyzedHttpsPacket();
        analyzedHttpsPacket.setTime(this.time);
        if (this.time != null) {
            analyzedHttpsPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedHttpsPacket.setProtocol(this.protocol());
        analyzedHttpsPacket.setUpstream(this.upstream);
        analyzedHttpsPacket.setBatchId(this.batchId);
        analyzedHttpsPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedHttpsPacket.setAckNum(this.ackNum);
        analyzedHttpsPacket.setData(this.contentBytes);
        analyzedHttpsPacket.setContent(realContent);
        analyzedHttpsPacket.setSrcIp(this.srcIp);
        analyzedHttpsPacket.setDestIp(this.destIp);
        analyzedHttpsPacket.setSrcMac(this.srcMac);
        analyzedHttpsPacket.setDestMac(this.destMac);
        System.out.println("++++++save https packet: " + this.ackNum);
        return analyzedHttpsPacket;
    }
}
