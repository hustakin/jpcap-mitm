/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.udp;

import com.jpcap.mitm.analysis.IPacketModelFilter;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.UDPPacketModel;

/**
 * 过滤出UDP包
 *
 * @author Frankie
 */
public class UdpPacketModelFilter implements IPacketModelFilter {
    @Override
    public boolean filter(PacketModel packet) {
        if (packet instanceof UDPPacketModel) {
            return false;
        }
        return true;
    }
}
