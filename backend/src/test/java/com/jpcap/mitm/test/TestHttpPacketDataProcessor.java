/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import com.jpcap.mitm.utils.Helper;
import com.jpcap.mitm.utils.NetworkUtils;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

import java.io.*;
import java.util.*;

/**
 * @author Frankie
 */
@SuppressWarnings("Duplicates")
public class TestHttpPacketDataProcessor implements IPacketProcessor {

    private static String HTTP_HEADER_MARK = "HTTP/1.1";
    private static String HEADER_CONTENT_TYPE = "Content-Type";
    private static String HEADER_ENCODING = "Content-Encoding";

    private static List<String> TRY_CHARSETS;
    private TCPPacket tcpPacket;
    private byte[] data;
    private String tryDecodedData;
    private Map<String, String> httpHeaders;

    private static long COUNT = 0;
    private Map<Long, byte[]> packagedHttpDatas = new HashMap<>();

    static {
        TRY_CHARSETS = new ArrayList<>();
        TRY_CHARSETS.add("ASCII");
        TRY_CHARSETS.add("UTF-8");
        TRY_CHARSETS.add("GB2312");
        TRY_CHARSETS.add("GBK");
        TRY_CHARSETS.add("Unicode");
    }

    @Override
    public void process(Packet packet) {
        this.tcpPacket = (TCPPacket) packet;
        this.data = tcpPacket.data;
        //tryParseHttpHeader();
        COUNT++;
        parseHttpContent();
        //printPackagedHttpDatas();
    }

    public void tryParseHttpHeader() {
        String tryDecodedData = null;
        try {
            String parsedStr = new String(data);
            if (parsedStr.contains(HTTP_HEADER_MARK)) {
                System.out.println("-------- Default decode --------");
                tryDecodedData = parsedStr;
            } else {
                for (String charset : TRY_CHARSETS) {
                    parsedStr = new String(data, charset);
                    if (parsedStr.contains(HTTP_HEADER_MARK)) {
                        System.out.println("-------- " + charset + " decode --------");
                        tryDecodedData = parsedStr;
                        break;
                    }
                }
            }

            System.out.println("-------- Decoded --------");
            Helper.println(tryDecodedData);
            this.tryDecodedData = tryDecodedData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void parseHttpContent() {
        this.httpHeaders = new HashMap<>();

        try {
            BufferedReader in = new BufferedReader(new StringReader(new String(this.data)));
            String method = in.readLine();
            if (method == null || method.indexOf("HTTP") == -1) {
                System.out.println("Not HTTP Header: " + method);
                return;
            }

            String l;
            //read headers
            while ((l = in.readLine()).length() > 0) {
                String[] header = l.split(":");
                if (header.length > 1) {
                    this.httpHeaders.put(header[0], header[1].trim());
                    System.out.println("------------- header: " + header[0] + " : " + header[1].trim());
                } else {
                    System.out.println("------------- unknown header: " + l);
                }
            }

            StringBuffer sb = new StringBuffer();
            //read data
            while ((l = in.readLine()) != null) {
                sb.append(l);
                //sb.append("\r\n");
            }
            if (sb.toString().length() > 0) {
                byte[] data = NetworkUtils.strToByteArray(sb.toString());
                if (!packagedHttpDatas.containsKey(this.tcpPacket.ack_num)) {
                    packagedHttpDatas.put(this.tcpPacket.ack_num, data);
                } else {
                    byte[] existed = packagedHttpDatas.get(this.tcpPacket.ack_num);
                    byte[] newBytes = NetworkUtils.concatBytes(existed, data);
                    packagedHttpDatas.put(this.tcpPacket.ack_num, newBytes);
                }
            }

            System.out.println("======================");
            System.out.println(NetworkUtils.bytesToHexString(this.data));
            System.out.println("======================");
        } catch (IOException e) {
        }
    }

    private void printPackagedHttpDatas() {
        if (COUNT >= 6) {
            System.out.println("======================");
            Iterator<Map.Entry<Long, byte[]>> it = packagedHttpDatas.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, byte[]> entry = it.next();
                Long ack = entry.getKey();
                byte[] data = entry.getValue();
                byte[] unGziped = NetworkUtils.unGzip(data);
                //System.out.println(ack + ": " + new String(unGziped));
                System.out.println(NetworkUtils.bytesToHexString(data));
            }
            System.out.println("======================");
        }
    }

}
