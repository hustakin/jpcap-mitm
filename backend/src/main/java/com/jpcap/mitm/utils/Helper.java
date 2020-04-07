/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.utils;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Helper {
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    public static final String ALL_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final SimpleDateFormat HOUR_MINUTE_SECOND_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static final SimpleDateFormat SIMPLE_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat MINUTE_HOUR_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH");

    public static final SimpleDateFormat MINUTE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private static final SimpleDateFormat sd2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");

    private static final SimpleDateFormat sd3 = new SimpleDateFormat("dd-MMM-yyyy");

    private static final SimpleDateFormat sd4 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static final DecimalFormat df = new DecimalFormat("#.00");

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getTempRandomFilePath(String postFix) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = generateString(8) + postFix;
        return tempDir + File.separator + fileName;
    }

    public static String fingerprintByMD5(String str) {
        return fingerprintByMD5(str.getBytes());
    }

    public static String fingerprintByMD5(byte[] bytes) {
        md5.update(bytes);
        byte[] md = md5.digest();
        int j = md.length;
        char cstr[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            cstr[k++] = hexDigits[byte0 >>> 4 & 0xf];
            cstr[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(cstr);
    }

    public static void println(Object... objs) {
        logger.info("=====================================");
        if (objs != null)
            for (Object obj : objs) {
                if (obj instanceof String || obj instanceof Number) {
                    logger.info(obj.toString());
                } else if (obj instanceof Map) {
                    Map mapObj = (Map) obj;
                    Set<Map.Entry> entrySet = mapObj.entrySet();
                    logger.info("map begin------------------");
                    for (Map.Entry entry : entrySet) {
                        logger.info(entry.getKey() + "=" + entry.getValue());
                    }
                } else {
                    String str = ToStringBuilder.reflectionToString(obj, ToStringStyle.MULTI_LINE_STYLE);
                    logger.info(str);
                }
            }
        logger.info("=====================================");
    }

    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        return sb.toString();
    }
}
