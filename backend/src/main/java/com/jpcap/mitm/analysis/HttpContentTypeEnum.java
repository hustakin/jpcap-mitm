/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis;

/**
 * @author Frankie
 */
public enum HttpContentTypeEnum {

    HTML("text/html"),
    PLAIN("text/plain"),
    XML("text/xml"),
    JSON("application/json"),
    ATTACHMENT("application/octet-stream"),

    WEBP("image/webp"),
    JPEG("image/jpeg"),
    MP4("video/mp4"),
    FLV("video/x-flv"),

    OTHER("other");

    private String contentType;

    HttpContentTypeEnum(String contentType) {
        this.contentType = contentType;
    }

    public static HttpContentTypeEnum extract(String contentType) {
        if (contentType == null)
            return null;
        if (contentType.startsWith(HTML.value()))
            return HTML;
        else if (contentType.startsWith(PLAIN.value()))
            return PLAIN;
        else if (contentType.startsWith(XML.value()))
            return XML;
        else if (contentType.startsWith(JSON.value()))
            return JSON;
        else if (contentType.startsWith(ATTACHMENT.value()))
            return ATTACHMENT;
        else if (contentType.startsWith(WEBP.value()))
            return WEBP;
        else if (contentType.startsWith(JPEG.value()))
            return JPEG;
        else if (contentType.startsWith(MP4.value()))
            return MP4;
        else if (contentType.startsWith(FLV.value()))
            return FLV;
        else
            return null;
    }

    public static boolean isSupport(String contentType) {
        HttpContentTypeEnum contentTypeEnum = extract(contentType);
        if (contentTypeEnum != null) {
            return true;
        } else {
            return false;
        }
    }

    public String value() {
        return this.contentType;
    }
}
