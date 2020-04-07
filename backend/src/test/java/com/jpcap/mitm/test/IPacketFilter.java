/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import jpcap.packet.Packet;

/**
 * @author Frankie
 */
public interface IPacketFilter {

    boolean filter(Packet packet);

}
