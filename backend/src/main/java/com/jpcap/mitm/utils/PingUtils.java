/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.utils;

import com.jpcap.mitm.entity.Domain;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PingUtils {

    public static Domain ping(String domain) {
        try {
            InetAddress address = InetAddress.getByName(domain);
            Domain domainObj = new Domain();
            domainObj.setHost(address.getHostName());
            domainObj.setIp(address.getHostAddress());
            return domainObj;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Domain> lookup(String domain) {
        try {
            List<Domain> domainObjs = new ArrayList<>();
            InetAddress[] inetAddresses = InetAddress.getAllByName(domain);
            for (InetAddress inetAddress : inetAddresses) {
                Domain domainObj = new Domain();
                domainObj.setHost(inetAddress.getHostName());
                domainObj.setIp(inetAddress.getHostAddress());
                domainObjs.add(domainObj);
            }
            return domainObjs;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
