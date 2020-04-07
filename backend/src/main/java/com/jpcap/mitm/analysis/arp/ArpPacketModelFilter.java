/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis.arp;

import com.jpcap.mitm.analysis.IPacketModelFilter;
import com.jpcap.mitm.model.ARPPacketModel;
import com.jpcap.mitm.model.PacketModel;

/**
 * 过滤出ARP包
 *
 * @author Frankie
 */
public class ArpPacketModelFilter implements IPacketModelFilter {
    @Override
    public boolean filter(PacketModel packet) {
        if (packet instanceof ARPPacketModel) {
            return false;
        }
        return true;
    }
}
