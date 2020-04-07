/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.icmp;

import com.jpcap.mitm.analysis.ISingleAnalysisRealm;
import com.jpcap.mitm.analysis.ProtocolEnum;
import com.jpcap.mitm.entity.AnalyzedIcmpPacket;
import com.jpcap.mitm.model.AbsAnalyzedPacket;
import com.jpcap.mitm.model.ICMPPacketModel;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.utils.Helper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
public class IcmpAnalysisRealm implements ISingleAnalysisRealm {
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
        return ProtocolEnum.ICMP.name();
    }

    @Override
    public void initPacket(Long batchId, String capturePacketId, boolean upstream, PacketModel packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        ICMPPacketModel icmpPacketModel = (ICMPPacketModel) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.contentBytes = icmpPacketModel.getData();
        this.srcIp = icmpPacketModel.getSrc_ip();
        this.destIp = icmpPacketModel.getDst_ip();
        if (icmpPacketModel.getDatalink() != null) {
            this.srcMac = icmpPacketModel.getDatalink().getSourceAddress();
            this.destMac = icmpPacketModel.getDatalink().getDestinationAddress();
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

        AnalyzedIcmpPacket analyzedIcmpPacket = new AnalyzedIcmpPacket();
        analyzedIcmpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedIcmpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedIcmpPacket.setProtocol(this.protocol());
        analyzedIcmpPacket.setUpstream(this.upstream);
        analyzedIcmpPacket.setBatchId(this.batchId);
        analyzedIcmpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedIcmpPacket.setData(this.contentBytes);
        analyzedIcmpPacket.setContent(realContent);
        analyzedIcmpPacket.setSrcIp(this.srcIp);
        analyzedIcmpPacket.setDestIp(this.destIp);
        analyzedIcmpPacket.setSrcMac(this.srcMac);
        analyzedIcmpPacket.setDestMac(this.destMac);
        System.out.println("++++++save icmp packet");
        return analyzedIcmpPacket;
    }
}
