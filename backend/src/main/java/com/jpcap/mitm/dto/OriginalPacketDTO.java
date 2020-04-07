/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.dto;

import com.jpcap.mitm.model.*;
import com.jpcap.mitm.utils.NetworkUtils;
import com.jpcap.mitm.model.*;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class OriginalPacketDTO implements Serializable {

    //Packet
    private Integer capLen;
    private Integer len;
    private String hexHeader;
    private Integer headerLen;
    private String hexData;
    private Integer dataLen;

    //datalink
    private String srcMac;
    private String dstMac;
    private Short frameType;

    //ARP
    private Short hardwareType;
    private Short prototype;
    private Short hardwareAddressLen;
    private Short protocolAddressLen;
    private Short operation;
    private String senderHardwareAddress;
    private String senderProtocolAddress;
    private String targetHardwareAddress;
    private String targetProtocolAddress;

    //IP
    private Byte version;
    private Byte rsvTos;
    private String srcIp;
    private String dstIp;
    private byte[] option;
    private Byte priority;
    private Integer length;
    private Integer ident;
    private Boolean dontFrag;
    private Boolean moreFrag;
    private Short offset;
    private Short hopLimit;
    private Short protocol; //(TCP = 6; UDP = 17)

    //ICMP
    private Byte type;
    private Short aliveTime;

    //TCP
    private Long sequence;
    private Long ackNum;
    private Boolean urg;
    private Boolean ack;
    private Boolean psh;
    private Boolean rst;
    private Boolean syn;
    private Boolean fin;
    private Integer window;
    private Short urgentPointer;
    //TCP or UDP
    private Integer srcPort;
    private Integer dstPort;

    public static OriginalPacketDTO readFrom(PacketModel packet) {
        OriginalPacketDTO dto = new OriginalPacketDTO();
        dto.setCapLen(packet.getCaplen());
        dto.setLen(packet.getLen());
        dto.setHexHeader(NetworkUtils.bytesToHexString(packet.getHeader()));
        dto.setHeaderLen(packet.getHeader() != null ? packet.getHeader().length : 0);
        dto.setHexData(NetworkUtils.bytesToHexString(packet.getData()));
        dto.setDataLen(packet.getData() != null ? packet.getData().length : 0);
        if (packet.getDatalink() != null) {
            dto.setSrcMac(packet.getDatalink().getSourceAddress());
            dto.setDstMac(packet.getDatalink().getDestinationAddress());
            dto.setFrameType(packet.getDatalink().getFrametype());
        }
        if (packet instanceof ARPPacketModel) {
            ARPPacketModel arpPacketModel = (ARPPacketModel) packet;
            dto.setHardwareType(arpPacketModel.getHardtype());
            dto.setPrototype(arpPacketModel.getPrototype());
            dto.setHardwareAddressLen(arpPacketModel.getHlen());
            dto.setProtocolAddressLen(arpPacketModel.getPlen());
            dto.setOperation(arpPacketModel.getOperation());
            dto.setSenderHardwareAddress(NetworkUtils.bytesToMac(arpPacketModel.getSender_hardaddr()));
            dto.setSenderProtocolAddress(NetworkUtils.bytesToIp(arpPacketModel.getSender_protoaddr()));
            dto.setTargetHardwareAddress(NetworkUtils.bytesToMac(arpPacketModel.getTarget_hardaddr()));
            dto.setTargetProtocolAddress(NetworkUtils.bytesToIp(arpPacketModel.getTarget_protoaddr()));
        }
        if (packet instanceof IPPacketModel) {
            IPPacketModel ipPacketModel = (IPPacketModel) packet;
            dto.setVersion(ipPacketModel.getVersion());
            dto.setRsvTos(ipPacketModel.getRsv_tos());
            dto.setSrcIp(ipPacketModel.getSrc_ip());
            dto.setDstIp(ipPacketModel.getDst_ip());
            dto.setOption(ipPacketModel.getOption());
            dto.setPriority(ipPacketModel.getPriority());
            dto.setLength(ipPacketModel.getLength());
            dto.setIdent(ipPacketModel.getIdent());
            dto.setDontFrag(ipPacketModel.isDont_frag());
            dto.setMoreFrag(ipPacketModel.isMore_frag());
            dto.setOffset(ipPacketModel.getOffset());
            dto.setHopLimit(ipPacketModel.getHop_limit());
            dto.setProtocol(ipPacketModel.getProtocol());

            if (ipPacketModel instanceof ICMPPacketModel) {
                ICMPPacketModel icmpPacketModel = (ICMPPacketModel) ipPacketModel;
                dto.setType(icmpPacketModel.getType());
                dto.setAliveTime(icmpPacketModel.getAlive_time());
            } else if (ipPacketModel instanceof TCPPacketModel) {
                TCPPacketModel tcpPacketModel = (TCPPacketModel) ipPacketModel;
                dto.setSequence(tcpPacketModel.getSequence());
                dto.setAckNum(tcpPacketModel.getAck_num());
                dto.setUrg(tcpPacketModel.isUrg());
                dto.setAck(tcpPacketModel.isAck());
                dto.setPsh(tcpPacketModel.isPsh());
                dto.setRst(tcpPacketModel.isRst());
                dto.setSyn(tcpPacketModel.isSyn());
                dto.setFin(tcpPacketModel.isFin());
                dto.setWindow(tcpPacketModel.getWindow());
                dto.setUrgentPointer(tcpPacketModel.getUrgent_pointer());

                dto.setSrcPort(tcpPacketModel.getSrc_port());
                dto.setDstPort(tcpPacketModel.getDst_port());
            } else if (ipPacketModel instanceof UDPPacketModel) {
                UDPPacketModel udpPacketModel = (UDPPacketModel) ipPacketModel;
                dto.setSrcPort(udpPacketModel.getSrc_port());
                dto.setDstPort(udpPacketModel.getDst_port());
            }
        }

        return dto;
    }

    public Integer getCapLen() {
        return capLen;
    }

    public void setCapLen(Integer capLen) {
        this.capLen = capLen;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public String getHexHeader() {
        return hexHeader;
    }

    public void setHexHeader(String hexHeader) {
        this.hexHeader = hexHeader;
    }

    public Integer getHeaderLen() {
        return headerLen;
    }

    public void setHeaderLen(Integer headerLen) {
        this.headerLen = headerLen;
    }

    public String getHexData() {
        return hexData;
    }

    public void setHexData(String hexData) {
        this.hexData = hexData;
    }

    public Integer getDataLen() {
        return dataLen;
    }

    public void setDataLen(Integer dataLen) {
        this.dataLen = dataLen;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public String getDstMac() {
        return dstMac;
    }

    public void setDstMac(String dstMac) {
        this.dstMac = dstMac;
    }

    public Short getFrameType() {
        return frameType;
    }

    public void setFrameType(Short frameType) {
        this.frameType = frameType;
    }

    public Short getHardwareType() {
        return hardwareType;
    }

    public void setHardwareType(Short hardwareType) {
        this.hardwareType = hardwareType;
    }

    public Byte getRsvTos() {
        return rsvTos;
    }

    public void setRsvTos(Byte rsvTos) {
        this.rsvTos = rsvTos;
    }

    public Short getPrototype() {
        return prototype;
    }

    public void setPrototype(Short prototype) {
        this.prototype = prototype;
    }

    public Short getHardwareAddressLen() {
        return hardwareAddressLen;
    }

    public void setHardwareAddressLen(Short hardwareAddressLen) {
        this.hardwareAddressLen = hardwareAddressLen;
    }

    public Short getProtocolAddressLen() {
        return protocolAddressLen;
    }

    public void setProtocolAddressLen(Short protocolAddressLen) {
        this.protocolAddressLen = protocolAddressLen;
    }

    public Short getOperation() {
        return operation;
    }

    public void setOperation(Short operation) {
        this.operation = operation;
    }

    public String getSenderHardwareAddress() {
        return senderHardwareAddress;
    }

    public void setSenderHardwareAddress(String senderHardwareAddress) {
        this.senderHardwareAddress = senderHardwareAddress;
    }

    public String getSenderProtocolAddress() {
        return senderProtocolAddress;
    }

    public void setSenderProtocolAddress(String senderProtocolAddress) {
        this.senderProtocolAddress = senderProtocolAddress;
    }

    public String getTargetHardwareAddress() {
        return targetHardwareAddress;
    }

    public void setTargetHardwareAddress(String targetHardwareAddress) {
        this.targetHardwareAddress = targetHardwareAddress;
    }

    public String getTargetProtocolAddress() {
        return targetProtocolAddress;
    }

    public void setTargetProtocolAddress(String targetProtocolAddress) {
        this.targetProtocolAddress = targetProtocolAddress;
    }

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public byte[] getOption() {
        return option;
    }

    public void setOption(byte[] option) {
        this.option = option;
    }

    public Byte getPriority() {
        return priority;
    }

    public void setPriority(Byte priority) {
        this.priority = priority;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getIdent() {
        return ident;
    }

    public void setIdent(Integer ident) {
        this.ident = ident;
    }

    public Boolean getDontFrag() {
        return dontFrag;
    }

    public void setDontFrag(Boolean dontFrag) {
        this.dontFrag = dontFrag;
    }

    public Boolean getMoreFrag() {
        return moreFrag;
    }

    public void setMoreFrag(Boolean moreFrag) {
        this.moreFrag = moreFrag;
    }

    public Short getOffset() {
        return offset;
    }

    public void setOffset(Short offset) {
        this.offset = offset;
    }

    public Short getHopLimit() {
        return hopLimit;
    }

    public void setHopLimit(Short hopLimit) {
        this.hopLimit = hopLimit;
    }

    public Short getProtocol() {
        return protocol;
    }

    public void setProtocol(Short protocol) {
        this.protocol = protocol;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Short getAliveTime() {
        return aliveTime;
    }

    public void setAliveTime(Short aliveTime) {
        this.aliveTime = aliveTime;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Long getAckNum() {
        return ackNum;
    }

    public void setAckNum(Long ackNum) {
        this.ackNum = ackNum;
    }

    public Boolean getUrg() {
        return urg;
    }

    public void setUrg(Boolean urg) {
        this.urg = urg;
    }

    public Boolean getAck() {
        return ack;
    }

    public void setAck(Boolean ack) {
        this.ack = ack;
    }

    public Boolean getPsh() {
        return psh;
    }

    public void setPsh(Boolean psh) {
        this.psh = psh;
    }

    public Boolean getRst() {
        return rst;
    }

    public void setRst(Boolean rst) {
        this.rst = rst;
    }

    public Boolean getSyn() {
        return syn;
    }

    public void setSyn(Boolean syn) {
        this.syn = syn;
    }

    public Boolean getFin() {
        return fin;
    }

    public void setFin(Boolean fin) {
        this.fin = fin;
    }

    public Integer getWindow() {
        return window;
    }

    public void setWindow(Integer window) {
        this.window = window;
    }

    public Short getUrgentPointer() {
        return urgentPointer;
    }

    public void setUrgentPointer(Short urgentPointer) {
        this.urgentPointer = urgentPointer;
    }

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    public Integer getDstPort() {
        return dstPort;
    }

    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }
}
