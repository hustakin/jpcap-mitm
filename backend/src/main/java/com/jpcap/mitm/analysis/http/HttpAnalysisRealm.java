/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.http;

import com.jpcap.mitm.analysis.HttpContentTypeEnum;
import com.jpcap.mitm.analysis.IAnalysisRealm;
import com.jpcap.mitm.analysis.ProtocolEnum;
import com.jpcap.mitm.entity.AnalyzedHttpPacket;
import com.jpcap.mitm.model.AbsAnalyzedPacket;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.TCPPacketModel;
import com.jpcap.mitm.utils.Helper;
import com.jpcap.mitm.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Frankie
 */
public class HttpAnalysisRealm implements IAnalysisRealm {
    private static final String HEX_NEW_LINE = "0D0A0D0A";

    private static String HEADER_CONTENT_TYPE = "content-type";
    private static String HEADER_ENCODING = "content-encoding";
    private static String HEADER_ENCODING_GZIP = "gzip";

    protected Date time;
    protected boolean upstream;
    protected Long batchId;
    protected List<String> capturedPacketIds = new ArrayList<>();
    protected Long ackNum;
    protected boolean isGzip = false;
    protected String contentType;
    protected boolean hasHeader = false;
    protected Map<String, String> httpHeaders;
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;

    @Override
    public String protocol() {
        return ProtocolEnum.HTTP.name();
    }

    @Override
    public void initPacket(Long batchId, String capturePacketId, boolean upstream, PacketModel packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        TCPPacketModel tcpPacketModel = (TCPPacketModel) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.ackNum = tcpPacketModel.getAck_num();
        this.srcIp = tcpPacketModel.getSrc_ip();
        this.destIp = tcpPacketModel.getDst_ip();
        String hex = NetworkUtils.bytesToHexString(tcpPacketModel.getData());
        if (tcpPacketModel.getDatalink() != null) {
            this.srcMac = tcpPacketModel.getDatalink().getSourceAddress();
            this.destMac = tcpPacketModel.getDatalink().getDestinationAddress();
        }

        String contentHex = null;
        int indexOfNewLine = hex.indexOf(HEX_NEW_LINE);
        if (indexOfNewLine > 0) {
            this.hasHeader = true;
            String headerStr = hex.substring(0, indexOfNewLine);
            this.httpHeaders = NetworkUtils.readHttpHeadersByStr(new String(NetworkUtils.toBytes(headerStr)));
            if (this.httpHeaders != null) {
                this.contentType = this.httpHeaders.get(HEADER_CONTENT_TYPE);
            }
            contentHex = hex.substring(indexOfNewLine + 8);
        }

        //unGzip content for gzip data
        if (this.hasHeader && HEADER_ENCODING_GZIP.equals(httpHeaders.get(HEADER_ENCODING))) {
            this.isGzip = true;
            String gzipContentHex = NetworkUtils.parseGzipContent(contentHex);
            byte[] contentBytes = NetworkUtils.toBytes(gzipContentHex);
            this.contentBytes = contentBytes;
        } else {
            this.isGzip = false;
            this.contentBytes = NetworkUtils.toBytes(contentHex);
        }
    }

    @Override
    public void appendPacket(String capturePacketId, PacketModel packet) {
        this.capturedPacketIds.add(capturePacketId);

        TCPPacketModel tcpPacketModel = (TCPPacketModel) packet;
        String hex = NetworkUtils.bytesToHexString(tcpPacketModel.getData());

        String contentHex;
        int indexOfNewLine = hex.indexOf(HEX_NEW_LINE);
        if (indexOfNewLine > 0) {
            contentHex = hex.substring(indexOfNewLine + 8);
        } else {
            contentHex = hex;
        }

        if (this.isGzip) {
            String gzipContentHex = NetworkUtils.parseGzipContent(contentHex);
            byte[] newContentBytes = NetworkUtils.toBytes(gzipContentHex);

            byte[] newBytes = NetworkUtils.concatBytes(this.contentBytes, newContentBytes);
            this.contentBytes = newBytes;
        } else {
            byte[] newBytes = NetworkUtils.concatBytes(this.contentBytes, NetworkUtils.toBytes(contentHex));
            this.contentBytes = newBytes;
        }
    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        byte[] realContentBytes;
        String realContent = null;
        try {
            realContentBytes = this.contentBytes;
            if (this.isGzip && this.contentBytes.length > 0) {
                realContentBytes = NetworkUtils.unGzip(this.contentBytes);
            }
            realContent = new String(realContentBytes, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Boolean httpReq = null;
        String method = null;
        if (NetworkUtils.isHttpHeader(this.httpHeaders) && NetworkUtils.isRequestHttpHeader(this.httpHeaders)) {
            httpReq = true;
            method = this.httpHeaders.get("METHOD");
        } else if (NetworkUtils.isHttpHeader(this.httpHeaders) && NetworkUtils.isResponseHttpHeader(this.httpHeaders)) {
            httpReq = false;
            method = this.httpHeaders.get("METHOD");
        }

        //process supported packet: html/plain/xml
        AnalyzedHttpPacket analyzedHttpPacket = new AnalyzedHttpPacket();
        analyzedHttpPacket.setHttpReq(httpReq);
        analyzedHttpPacket.setMethod(method);
        analyzedHttpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedHttpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedHttpPacket.setProtocol(this.protocol());
        analyzedHttpPacket.setUpstream(this.upstream);
        analyzedHttpPacket.setBatchId(this.batchId);
        analyzedHttpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedHttpPacket.setAckNum(this.ackNum);
        analyzedHttpPacket.setHttpHeaders(this.httpHeaders);
        analyzedHttpPacket.setData(this.contentBytes);
        analyzedHttpPacket.setContent(realContent);
        analyzedHttpPacket.setCompressed(this.isGzip);
        analyzedHttpPacket.setSrcIp(this.srcIp);
        analyzedHttpPacket.setDestIp(this.destIp);
        analyzedHttpPacket.setSrcMac(this.srcMac);
        analyzedHttpPacket.setDestMac(this.destMac);
        HttpContentTypeEnum contentTypeEnum = HttpContentTypeEnum.extract(this.contentType);
        if (contentTypeEnum != null)
            analyzedHttpPacket.setContentType(contentTypeEnum.name());
        else {
            //if no content type means it's a request, so use html
            //if no method means this body cannot be parsed, so ignore it
            if (httpReq == null && method == null && this.httpHeaders == null) {
                System.out.println("------ignore http packet due to it's not complete: " + this.ackNum);
                return null;
            }
            analyzedHttpPacket.setContentType(HttpContentTypeEnum.HTML.name());
        }
        System.out.println("++++++save http packet: " + this.ackNum + ", " + analyzedHttpPacket.getContentType() + ", " + this.isGzip);
        return analyzedHttpPacket;
    }
}
