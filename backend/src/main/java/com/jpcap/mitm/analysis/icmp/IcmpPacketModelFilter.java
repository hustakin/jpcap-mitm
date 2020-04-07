/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.icmp;

import com.jpcap.mitm.analysis.IPacketModelFilter;
import com.jpcap.mitm.model.ICMPPacketModel;
import com.jpcap.mitm.model.PacketModel;

/**
 * 过滤出ICMP包
 *
 * @author Frankie
 */
public class IcmpPacketModelFilter implements IPacketModelFilter {
    @Override
    public boolean filter(PacketModel packet) {
        if (packet instanceof ICMPPacketModel) {
            return false;
        }
        return true;
    }
}
