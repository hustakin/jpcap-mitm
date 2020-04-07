/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import com.jpcap.mitm.analysis.HttpContentTypeEnum;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class DistinctTimelinePacketItem implements Serializable, Comparable {
    private String minuteTimeStr;
    private long upHtmlNum = 0;
    private long upPlainNum = 0;
    private long upXmlNum = 0;
    private long upJsonNum = 0;
    private long upAttachmentNum = 0;
    private long upWebpNum = 0;
    private long upJpegNum = 0;
    private long upMp4Num = 0;
    private long upFlvNum = 0;
    private long upOtherNum = 0;
    private long downHtmlNum = 0;
    private long downPlainNum = 0;
    private long downXmlNum = 0;
    private long downJsonNum = 0;
    private long downAttachmentNum = 0;
    private long downWebpNum = 0;
    private long downJpegNum = 0;
    private long downMp4Num = 0;
    private long downFlvNum = 0;
    private long downOtherNum = 0;

    public void accumulate(boolean upstream, String contentType, long count) {
        if (HttpContentTypeEnum.HTML.name().equals(contentType)) {
            if (upstream)
                upHtmlNum += count;
            else
                downHtmlNum += count;
        } else if (HttpContentTypeEnum.PLAIN.name().equals(contentType)) {
            if (upstream)
                upPlainNum += count;
            else
                downPlainNum += count;
        } else if (HttpContentTypeEnum.XML.name().equals(contentType)) {
            if (upstream)
                upXmlNum += count;
            else
                downXmlNum += count;
        } else if (HttpContentTypeEnum.JSON.name().equals(contentType)) {
            if (upstream)
                upJsonNum += count;
            else
                downJsonNum += count;
        } else if (HttpContentTypeEnum.ATTACHMENT.name().equals(contentType)) {
            if (upstream)
                upAttachmentNum += count;
            else
                downAttachmentNum += count;
        } else if (HttpContentTypeEnum.WEBP.name().equals(contentType)) {
            if (upstream)
                upWebpNum += count;
            else
                downWebpNum += count;
        } else if (HttpContentTypeEnum.JPEG.name().equals(contentType)) {
            if (upstream)
                upJpegNum += count;
            else
                downJpegNum += count;
        } else if (HttpContentTypeEnum.MP4.name().equals(contentType)) {
            if (upstream)
                upMp4Num += count;
            else
                downMp4Num += count;
        } else if (HttpContentTypeEnum.FLV.name().equals(contentType)) {
            if (upstream)
                upFlvNum += count;
            else
                downFlvNum += count;
        } else if (HttpContentTypeEnum.OTHER.name().equals(contentType)) {
            if (upstream)
                upOtherNum += count;
            else
                downOtherNum += count;
        }
    }

    @Override
    public int compareTo(Object o) {
        DistinctTimelinePacketItem other = (DistinctTimelinePacketItem) o;
        return this.minuteTimeStr.compareTo(other.getMinuteTimeStr());
    }

    public String getMinuteTimeStr() {
        return minuteTimeStr;
    }

    public void setMinuteTimeStr(String minuteTimeStr) {
        this.minuteTimeStr = minuteTimeStr;
    }

    public long getUpHtmlNum() {
        return upHtmlNum;
    }

    public void setUpHtmlNum(long upHtmlNum) {
        this.upHtmlNum = upHtmlNum;
    }

    public long getUpPlainNum() {
        return upPlainNum;
    }

    public void setUpPlainNum(long upPlainNum) {
        this.upPlainNum = upPlainNum;
    }

    public long getUpXmlNum() {
        return upXmlNum;
    }

    public void setUpXmlNum(long upXmlNum) {
        this.upXmlNum = upXmlNum;
    }

    public long getUpJsonNum() {
        return upJsonNum;
    }

    public void setUpJsonNum(long upJsonNum) {
        this.upJsonNum = upJsonNum;
    }

    public long getUpAttachmentNum() {
        return upAttachmentNum;
    }

    public void setUpAttachmentNum(long upAttachmentNum) {
        this.upAttachmentNum = upAttachmentNum;
    }

    public long getUpWebpNum() {
        return upWebpNum;
    }

    public void setUpWebpNum(long upWebpNum) {
        this.upWebpNum = upWebpNum;
    }

    public long getUpJpegNum() {
        return upJpegNum;
    }

    public void setUpJpegNum(long upJpegNum) {
        this.upJpegNum = upJpegNum;
    }

    public long getUpMp4Num() {
        return upMp4Num;
    }

    public void setUpMp4Num(long upMp4Num) {
        this.upMp4Num = upMp4Num;
    }

    public long getUpFlvNum() {
        return upFlvNum;
    }

    public void setUpFlvNum(long upFlvNum) {
        this.upFlvNum = upFlvNum;
    }

    public long getUpOtherNum() {
        return upOtherNum;
    }

    public void setUpOtherNum(long upOtherNum) {
        this.upOtherNum = upOtherNum;
    }

    public long getDownHtmlNum() {
        return downHtmlNum;
    }

    public void setDownHtmlNum(long downHtmlNum) {
        this.downHtmlNum = downHtmlNum;
    }

    public long getDownPlainNum() {
        return downPlainNum;
    }

    public void setDownPlainNum(long downPlainNum) {
        this.downPlainNum = downPlainNum;
    }

    public long getDownXmlNum() {
        return downXmlNum;
    }

    public void setDownXmlNum(long downXmlNum) {
        this.downXmlNum = downXmlNum;
    }

    public long getDownJsonNum() {
        return downJsonNum;
    }

    public void setDownJsonNum(long downJsonNum) {
        this.downJsonNum = downJsonNum;
    }

    public long getDownAttachmentNum() {
        return downAttachmentNum;
    }

    public void setDownAttachmentNum(long downAttachmentNum) {
        this.downAttachmentNum = downAttachmentNum;
    }

    public long getDownWebpNum() {
        return downWebpNum;
    }

    public void setDownWebpNum(long downWebpNum) {
        this.downWebpNum = downWebpNum;
    }

    public long getDownJpegNum() {
        return downJpegNum;
    }

    public void setDownJpegNum(long downJpegNum) {
        this.downJpegNum = downJpegNum;
    }

    public long getDownMp4Num() {
        return downMp4Num;
    }

    public void setDownMp4Num(long downMp4Num) {
        this.downMp4Num = downMp4Num;
    }

    public long getDownFlvNum() {
        return downFlvNum;
    }

    public void setDownFlvNum(long downFlvNum) {
        this.downFlvNum = downFlvNum;
    }

    public long getDownOtherNum() {
        return downOtherNum;
    }

    public void setDownOtherNum(long downOtherNum) {
        this.downOtherNum = downOtherNum;
    }
}
