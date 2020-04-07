/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.analysis;

import com.jpcap.mitm.model.AbsAnalyzedPacket;
import com.jpcap.mitm.model.PacketModel;

/**
 * @author Frankie
 */
public interface ISingleAnalysisRealm {
    String protocol();

    void initPacket(Long batchId, String capturePacketId, boolean upstream, PacketModel packet);

    AbsAnalyzedPacket makePacket4Save();
}
