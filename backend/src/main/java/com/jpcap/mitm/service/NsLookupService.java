/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.service;

import com.jpcap.mitm.dao.GenericDao;
import com.jpcap.mitm.entity.Domain;
import com.jpcap.mitm.utils.PingUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class NsLookupService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NsLookupService.class);
    @Autowired
    private GenericDao genericDao;

    public void nsLookup(String filterDomain) {
        if (StringUtils.isEmpty(filterDomain))
            return;
        genericDao.clean(Domain.class);
        String[] domains = filterDomain.split(",");
        for (String domain : domains) {
            List<Domain> lookupedDomains = PingUtils.lookup(domain);
            genericDao.insertAll(lookupedDomains);
        }
    }

    public List<String> getAllIps() {
        List<Domain> allDomains = genericDao.findAll(Domain.class);
        List<String> ips = new ArrayList<>();
        for (Domain domain : allDomains) {
            ips.add(domain.getIp());
        }
        return ips;
    }
}
