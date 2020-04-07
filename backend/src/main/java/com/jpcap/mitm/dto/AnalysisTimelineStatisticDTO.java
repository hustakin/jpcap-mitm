/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.dto;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class AnalysisTimelineStatisticDTO implements Serializable {
    //[
    //    ['product', '2012-10-01', '2013-02-02', '2014-03-03', '2015-10-04', '2016-09-01', '2017-02-02'],
    //    ['Matcha Latte', 41.1, 30.4, 65.1, 53.3, 83.8, 98.7],
    //    ['Milk Tea', 86.5, 92.1, 85.7, 83.1, 73.4, 55.1],
    //    ['Cheese Cocoa', 24.1, 67.2, 79.5, 86.4, 65.2, 82.5],
    //    ['Walnut Brownie', 55.2, 67.1, 69.2, 72.4, 53.9, 39.1]
    //]
    private String timeTitle = "times";
    private String upFirstMinute;
    private String downFirstMinute;
    private Object[] upstreamData;
    private Object[] downstreamData;

    public String getUpFirstMinute() {
        return upFirstMinute;
    }

    public void setUpFirstMinute(String upFirstMinute) {
        this.upFirstMinute = upFirstMinute;
    }

    public String getDownFirstMinute() {
        return downFirstMinute;
    }

    public void setDownFirstMinute(String downFirstMinute) {
        this.downFirstMinute = downFirstMinute;
    }

    public String getTimeTitle() {
        return timeTitle;
    }

    public void setTimeTitle(String timeTitle) {
        this.timeTitle = timeTitle;
    }

    public Object[] getUpstreamData() {
        return upstreamData;
    }

    public void setUpstreamData(Object[] upstreamData) {
        this.upstreamData = upstreamData;
    }

    public Object[] getDownstreamData() {
        return downstreamData;
    }

    public void setDownstreamData(Object[] downstreamData) {
        this.downstreamData = downstreamData;
    }
}
