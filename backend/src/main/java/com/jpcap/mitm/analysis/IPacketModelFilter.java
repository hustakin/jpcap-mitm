/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis;

import com.jpcap.mitm.model.PacketModel;

/**
 * @author Frankie
 */
public interface IPacketModelFilter {

    boolean filter(PacketModel packet);

}
