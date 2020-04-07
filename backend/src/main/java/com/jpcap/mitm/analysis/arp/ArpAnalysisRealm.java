/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.arp;

import com.jpcap.mitm.analysis.ISingleAnalysisRealm;
import com.jpcap.mitm.analysis.ProtocolEnum;
import com.jpcap.mitm.entity.AnalyzedArpPacket;
import com.jpcap.mitm.model.ARPPacketModel;
import com.jpcap.mitm.model.AbsAnalyzedPacket;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.utils.Helper;
import com.jpcap.mitm.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
public class ArpAnalysisRealm implements ISingleAnalysisRealm {
    protected Date time;
    protected boolean upstream;
    protected Long batchId;
    protected List<String> capturedPacketIds = new ArrayList<>();
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;

    @Override
    public String protocol() {
        return ProtocolEnum.ARP.name();
    }

    @Override
    public void initPacket(Long batchId, String capturePacketId, boolean upstream, PacketModel packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        ARPPacketModel arpPacketModel = (ARPPacketModel) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.contentBytes = arpPacketModel.getData();
        this.srcMac = NetworkUtils.bytesToMac(arpPacketModel.getSender_hardaddr());
        this.destMac = NetworkUtils.bytesToMac(arpPacketModel.getTarget_hardaddr());
        this.srcIp = NetworkUtils.bytesToIp(arpPacketModel.getSender_protoaddr());
        this.destIp = NetworkUtils.bytesToIp(arpPacketModel.getTarget_protoaddr());
    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        String realContent = null;
        try {
            realContent = new String(this.contentBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedArpPacket analyzedUdpPacket = new AnalyzedArpPacket();
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
        analyzedUdpPacket.setSrcMac(this.srcMac);
        analyzedUdpPacket.setDestMac(this.destMac);
        System.out.println("++++++save arp packet");
        return analyzedUdpPacket;
    }

}
