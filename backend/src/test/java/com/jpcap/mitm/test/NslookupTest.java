/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import com.jpcap.mitm.entity.Domain;
import com.jpcap.mitm.utils.Helper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
public class NslookupTest {

    @Test
    public void test() throws Exception {
        try {
            String domainsStr = "www.immomo.com,api.immomo.com,api-log.immomo.com,download.immomo.com,httpdns.immomo.com,api-alpha.immomo.com,passport.immomo.com,live-log.immomo.com,live-api.immomo.com,mk.immomo.com,mms.immomo.com,mvip.immomo.com,m.immomo.com,referee.immomo.com,www.momocdn.com,dl-ali.momocdn.com,et.momocdn.com,img.momocdn.com,s.momocdn.com,g.momocdn.com,cdnst.momocdn.com";
            String[] domains = domainsStr.split(",");
            for (String domain : domains) {
                InetAddress[] inetAddresses = InetAddress.getAllByName(domain);
                for (InetAddress inetAddress : inetAddresses) {
                    Domain domainObj = new Domain();
                    System.err.println(inetAddress.getHostName());
                    System.err.println(inetAddress.getHostAddress());
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIpv6() {
        int[] ip = parseIpv6ToIpv4("fe80:0:0:0:1826:a37e:59b0:8226");
        Helper.println(ip);
    }

    public int[] parseIpv6ToIpv4(String ipv6Str) {
        int[] ipv6parts = new int[8];
        for (int i = 0; i < 8; i++) {
            ipv6parts[i] = 0;
        }
        //check for trivial IPs
        if (ipv6Str.equals("::"))
            return ipv6parts;
        //parse
        String[] parts = ipv6Str.split(":");
        int slen = parts.length;
        if (slen > 8) slen = 8;
        int j = 0;
        for (int i = 0; i < slen; i++) {
            //this is a "::", switch to end-run mode
            if (parts[i].equals("")) {
                j = 9 - slen + i;
                continue;
            }
            ipv6parts[j] = Integer.parseInt(parts[i], 16);
            j++;
        }
        return ipv6parts;
    }
}
