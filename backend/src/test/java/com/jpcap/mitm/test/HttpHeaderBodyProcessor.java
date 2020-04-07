/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import com.jpcap.mitm.analysis.IPacketModelProcessor;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.TCPPacketModel;
import com.jpcap.mitm.utils.NetworkUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Frankie
 */
public class HttpHeaderBodyProcessor implements IPacketModelProcessor {
    private static final String HEX_NEW_LINE = "0D0A0D0A";

    private Map<String, String> httpHeaders = new HashMap<>();
    private String contentHex;

    @Override
    public void process(PacketModel packet) {
        TCPPacketModel tcpPacketModel = (TCPPacketModel) packet;
        String hex = NetworkUtils.bytesToHexString(tcpPacketModel.getData());
        int indexOfNewLine = hex.indexOf(HEX_NEW_LINE);
        if (indexOfNewLine > 0) {
            String headerStr = hex.substring(0, indexOfNewLine);
            this.httpHeaders = NetworkUtils.readHttpHeadersByStr(new String(NetworkUtils.toBytes(headerStr)));
            this.contentHex = hex.substring(indexOfNewLine + 8);
        }
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public String getContentHex() {
        return contentHex;
    }
}
