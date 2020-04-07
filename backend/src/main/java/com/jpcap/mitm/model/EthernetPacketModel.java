/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jpcap.mitm.model;

import jpcap.packet.DatalinkPacket;
import jpcap.packet.EthernetPacket;

public class EthernetPacketModel {
    public byte[] dst_mac;
    public byte[] src_mac;
    public short frametype;

    public static EthernetPacketModel readFrom(DatalinkPacket packet) {
        if (packet == null) return null;
        if (packet instanceof EthernetPacket) {
            EthernetPacket ethernetPacket = (EthernetPacket) packet;
            EthernetPacketModel model = new EthernetPacketModel();
            model.setDst_mac(ethernetPacket.dst_mac);
            model.setSrc_mac(ethernetPacket.src_mac);
            model.setFrametype(ethernetPacket.frametype);
            return model;
        } else {
            return null;
        }
    }

    public String getSourceAddress() {
        char[] src = new char[17];

        for (int i = 0; i < 5; ++i) {
            src[i * 3] = this.hexUpperChar(this.src_mac[i]);
            src[i * 3 + 1] = this.hexLowerChar(this.src_mac[i]);
            src[i * 3 + 2] = ':';
        }

        src[15] = this.hexUpperChar(this.src_mac[5]);
        src[16] = this.hexLowerChar(this.src_mac[5]);
        return new String(src);
    }

    public String getDestinationAddress() {
        char[] dst = new char[17];

        for (int i = 0; i < 5; ++i) {
            dst[i * 3] = this.hexUpperChar(this.dst_mac[i]);
            dst[i * 3 + 1] = this.hexLowerChar(this.dst_mac[i]);
            dst[i * 3 + 2] = ':';
        }

        dst[15] = this.hexUpperChar(this.dst_mac[5]);
        dst[16] = this.hexLowerChar(this.dst_mac[5]);
        return new String(dst);
    }

    public String toString() {
        return super.toString() + " " + this.getSourceAddress() + "->" + this.getDestinationAddress() + " (" + this.frametype + ")";
    }

    private char hexUpperChar(byte b) {
        b = (byte) (b >> 4 & 15);
        if (b == 0) {
            return '0';
        } else {
            return b < 10 ? (char) (48 + b) : (char) (97 + b - 10);
        }
    }

    private char hexLowerChar(byte b) {
        b = (byte) (b & 15);
        if (b == 0) {
            return '0';
        } else {
            return b < 10 ? (char) (48 + b) : (char) (97 + b - 10);
        }
    }

    public byte[] getDst_mac() {
        return dst_mac;
    }

    public void setDst_mac(byte[] dst_mac) {
        this.dst_mac = dst_mac;
    }

    public byte[] getSrc_mac() {
        return src_mac;
    }

    public void setSrc_mac(byte[] src_mac) {
        this.src_mac = src_mac;
    }

    public short getFrametype() {
        return frametype;
    }

    public void setFrametype(short frametype) {
        this.frametype = frametype;
    }
}
