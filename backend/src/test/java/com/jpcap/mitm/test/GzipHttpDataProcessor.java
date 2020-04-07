/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import com.jpcap.mitm.dao.GenericDao;
import com.jpcap.mitm.entity.AnalyzedHttpPacket;
import com.jpcap.mitm.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author Frankie
 */
public class GzipHttpDataProcessor {
    //image/webp, video/mp4, image/jpeg, video/x-flv, text/plain,
    private static String CONTENT_TYPE_HTML = "text/html";
    private static String HEADER_CONTENT_TYPE = "content-type";
    private GenericDao genericDao;

    public GzipHttpDataProcessor(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    public void process(Long ackNum, Map<String, String> httpHeaders, byte[] data) {
        String hex = NetworkUtils.bytesToHexString(data);
        byte[] contentBytes = NetworkUtils.toBytes(hex);
        byte[] unGzipContentBytes = NetworkUtils.unGzip(contentBytes);
        try {
            String content = new String(unGzipContentBytes, "GBK");

            //process html packet
            if (httpHeaders.get(HEADER_CONTENT_TYPE) != null && httpHeaders.get(HEADER_CONTENT_TYPE).startsWith(CONTENT_TYPE_HTML)) {
                AnalyzedHttpPacket analyzedHttpPacket = new AnalyzedHttpPacket();
                analyzedHttpPacket.setAckNum(ackNum);
                analyzedHttpPacket.setHttpHeaders(httpHeaders);
                analyzedHttpPacket.setData(data);
                analyzedHttpPacket.setContent(content);
                genericDao.save(analyzedHttpPacket);
                System.out.println("---------save gzip http packet: " + ackNum);
            } else {
                System.out.println("---------find other gzip content type: " + httpHeaders.get(HEADER_CONTENT_TYPE));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
