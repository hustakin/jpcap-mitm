/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class DistinctTimelinePacketCount implements Serializable {
    private String minuteTimeStr;
    private String propertyName;
    private Object propertyValue;
    private Boolean upstream;
    private Integer count;

    public String getMinuteTimeStr() {
        return minuteTimeStr;
    }

    public void setMinuteTimeStr(String minuteTimeStr) {
        this.minuteTimeStr = minuteTimeStr;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Object propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Boolean getUpstream() {
        return upstream;
    }

    public void setUpstream(Boolean upstream) {
        this.upstream = upstream;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
