/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.service;

import com.jpcap.mitm.analysis.HttpContentTypeEnum;
import com.jpcap.mitm.analysis.IAnalysisRealm;
import com.jpcap.mitm.analysis.arp.ArpAnalysisRealm;
import com.jpcap.mitm.analysis.arp.ArpPacketModelFilter;
import com.jpcap.mitm.analysis.http.EncryptedHttpsAnalysisRealm;
import com.jpcap.mitm.analysis.http.HttpAnalysisRealm;
import com.jpcap.mitm.analysis.http.HttpPacketModelFilter;
import com.jpcap.mitm.analysis.http.HttpsPacketModelFilter;
import com.jpcap.mitm.analysis.icmp.IcmpAnalysisRealm;
import com.jpcap.mitm.analysis.icmp.IcmpPacketModelFilter;
import com.jpcap.mitm.analysis.tcp.TcpAnalysisRealm;
import com.jpcap.mitm.analysis.tcp.TcpPacketModelFilter;
import com.jpcap.mitm.analysis.udp.UdpAnalysisRealm;
import com.jpcap.mitm.analysis.udp.UdpPacketModelFilter;
import com.jpcap.mitm.dao.GenericDao;
import com.jpcap.mitm.dto.AnalysisTimelineStatisticDTO;
import com.jpcap.mitm.entity.*;
import com.jpcap.mitm.model.*;
import com.jpcap.mitm.utils.Helper;
import com.jpcap.mitm.utils.NetworkUtils;
import com.jpcap.mitm.entity.*;
import com.jpcap.mitm.model.*;
import org.bson.Document;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * @author Frankie Fan
 */
@SuppressWarnings("Duplicates")
@Service
public class AnalysisService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private static String TAG_HTTP = "HTTP";

    @Autowired
    private GenericDao genericDao;

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;

    private boolean analyzing = false;

    public void cleanAnalysisHttpHis() {
        genericDao.clean(AnalyzedHttpPacket.class);
    }

    public void cleanAnalysisHttpsHis() {
        genericDao.clean(AnalyzedHttpsPacket.class);
    }

    public void cleanAnalysisTcpHis() {
        genericDao.clean(AnalyzedTcpPacket.class);
    }

    public void cleanAnalysisUdpHis() {
        genericDao.clean(AnalyzedUdpPacket.class);
    }

    public void cleanAnalysisIcmpHis() {
        genericDao.clean(AnalyzedIcmpPacket.class);
    }

    public void cleanAnalysisArpHis() {
        genericDao.clean(AnalyzedArpPacket.class);
    }

    public void cleanByBatchId(Long batchId) {
        Query query = new Query(Criteria.where("batchId").is(batchId));
        genericDao.delete(query, AnalyzedHttpPacket.class);
        genericDao.delete(query, AnalyzedHttpsPacket.class);
        genericDao.delete(query, AnalyzedTcpPacket.class);
        genericDao.delete(query, AnalyzedUdpPacket.class);
        genericDao.delete(query, AnalyzedIcmpPacket.class);
        genericDao.delete(query, AnalyzedArpPacket.class);
    }

    public boolean isAnalyzing() {
        return this.analyzing;
    }

    public long analysisAll() {
        this.analyzing = true;
        long size = 0;
        List<CapturedPacket> capturedPackets = genericDao.findAll(CapturedPacket.class);
        size += this.analysisHttp(capturedPackets);
        size += this.analysisHttps(capturedPackets);
        size += this.analysisTcp(capturedPackets);
        size += this.analysisUdp(capturedPackets);
        size += this.analysisIcmp(capturedPackets);
        size += this.analysisArp(capturedPackets);
        this.analyzing = false;
        return size;
    }

    public long analysisByBatchId(Long batchId) {
        this.analyzing = true;
        long size = 0;
        Query query = new Query(Criteria.where("batchId").is(batchId));
        List<CapturedPacket> capturedPackets = genericDao.find(query, CapturedPacket.class);
        size += this.analysisHttp(capturedPackets);
        size += this.analysisHttps(capturedPackets);
        size += this.analysisTcp(capturedPackets);
        size += this.analysisUdp(capturedPackets);
        size += this.analysisIcmp(capturedPackets);
        size += this.analysisArp(capturedPackets);
        this.analyzing = false;
        return size;
    }

    public long analysisHttp(List<CapturedPacket> capturedPackets) {
        this.analyzing = true;
        Map<Long, IAnalysisRealm> analysisRealms = new HashMap<>();
        HttpPacketModelFilter httpPacketModelFilter = new HttpPacketModelFilter();
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            //filter http packet and process it
            if (!httpPacketModelFilter.filter(packetModel)) {
                TCPPacketModel tcpPacketModel = (TCPPacketModel) packetModel;
                if (tcpPacketModel.getData().length > 0) {
                    if (!analysisRealms.containsKey(tcpPacketModel.getAck_num())) {
                        HttpAnalysisRealm httpAnalysisRealm = new HttpAnalysisRealm();
                        httpAnalysisRealm.initPacket(packet.getBatchId(), packet.getId(), packet.isUpstream(), tcpPacketModel);
                        analysisRealms.put(tcpPacketModel.getAck_num(), httpAnalysisRealm);
                    } else {
                        analysisRealms.get(tcpPacketModel.getAck_num()).appendPacket(packet.getId(), tcpPacketModel);
                    }
                }
            }
        }

        this.saveRealmPackets(analysisRealms);
        this.analyzing = false;
        return analysisRealms.size();
    }

    public long analysisHttps(List<CapturedPacket> capturedPackets) {
        this.analyzing = true;
        Map<Long, IAnalysisRealm> analysisRealms = new HashMap<>();
        HttpsPacketModelFilter httpsPacketModelFilter = new HttpsPacketModelFilter();
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            //filter https packet and process it
            if (!httpsPacketModelFilter.filter(packetModel)) {
                TCPPacketModel tcpPacketModel = (TCPPacketModel) packetModel;
                if (tcpPacketModel.getData().length > 0) {
                    if (!analysisRealms.containsKey(tcpPacketModel.getAck_num())) {
                        EncryptedHttpsAnalysisRealm httpAnalysisRealm = new EncryptedHttpsAnalysisRealm();
                        httpAnalysisRealm.initPacket(packet.getBatchId(), packet.getId(), packet.isUpstream(), tcpPacketModel);
                        analysisRealms.put(tcpPacketModel.getAck_num(), httpAnalysisRealm);
                    } else {
                        analysisRealms.get(tcpPacketModel.getAck_num()).appendPacket(packet.getId(), tcpPacketModel);
                    }
                }
            }
        }

        this.saveRealmPackets(analysisRealms);
        this.analyzing = false;
        return analysisRealms.size();
    }

    public long analysisTcp(List<CapturedPacket> capturedPackets) {
        this.analyzing = true;
        Map<Long, IAnalysisRealm> analysisRealms = new HashMap<>();
        TcpPacketModelFilter tcpPacketModelFilter = new TcpPacketModelFilter();
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (!tcpPacketModelFilter.filter(packetModel)) {
                TCPPacketModel tcpPacketModel = (TCPPacketModel) packetModel;
                if (tcpPacketModel.getData().length > 0) {
                    if (!analysisRealms.containsKey(tcpPacketModel.getAck_num())) {
                        TcpAnalysisRealm tcpAnalysisRealm = new TcpAnalysisRealm();
                        tcpAnalysisRealm.initPacket(packet.getBatchId(), packet.getId(), packet.isUpstream(), tcpPacketModel);
                        analysisRealms.put(tcpPacketModel.getAck_num(), tcpAnalysisRealm);
                    } else {
                        analysisRealms.get(tcpPacketModel.getAck_num()).appendPacket(packet.getId(), tcpPacketModel);
                    }
                }
            }
        }

        this.saveRealmPackets(analysisRealms);
        this.analyzing = false;
        return analysisRealms.size();
    }

    public long analysisIcmp(List<CapturedPacket> capturedPackets) {
        this.analyzing = true;
        IcmpPacketModelFilter icmpPacketModelFilter = new IcmpPacketModelFilter();
        long size = 0;
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (!icmpPacketModelFilter.filter(packetModel)) {
                ICMPPacketModel icmpPacketModel = (ICMPPacketModel) packetModel;
                IcmpAnalysisRealm icmpAnalysisRealm = new IcmpAnalysisRealm();
                icmpAnalysisRealm.initPacket(packet.getBatchId(), packet.getId(), packet.isUpstream(), icmpPacketModel);
                Serializable toSavePacket = icmpAnalysisRealm.makePacket4Save();
                if (toSavePacket != null) {
                    genericDao.save(toSavePacket);
                }
                size++;
            }
        }
        this.analyzing = false;
        return size;
    }

    public long analysisUdp(List<CapturedPacket> capturedPackets) {
        this.analyzing = true;
        UdpPacketModelFilter udpPacketModelFilter = new UdpPacketModelFilter();
        long size = 0;
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (!udpPacketModelFilter.filter(packetModel)) {
                UDPPacketModel udpPacketModel = (UDPPacketModel) packetModel;
                UdpAnalysisRealm udpAnalysisRealm = new UdpAnalysisRealm();
                udpAnalysisRealm.initPacket(packet.getBatchId(), packet.getId(), packet.isUpstream(), udpPacketModel);
                Serializable toSavePacket = udpAnalysisRealm.makePacket4Save();
                if (toSavePacket != null) {
                    genericDao.save(toSavePacket);
                }
                size++;
            }
        }
        this.analyzing = false;
        return size;
    }

    public long analysisArp(List<CapturedPacket> capturedPackets) {
        this.analyzing = true;
        ArpPacketModelFilter arpPacketModelFilter = new ArpPacketModelFilter();
        long size = 0;
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (!arpPacketModelFilter.filter(packetModel)) {
                ARPPacketModel arpPacketModel = (ARPPacketModel) packetModel;
                ArpAnalysisRealm arpAnalysisRealm = new ArpAnalysisRealm();
                arpAnalysisRealm.initPacket(packet.getBatchId(), packet.getId(), packet.isUpstream(), arpPacketModel);
                Serializable toSavePacket = arpAnalysisRealm.makePacket4Save();
                if (toSavePacket != null) {
                    genericDao.save(toSavePacket);
                }
                size++;
            }
        }
        this.analyzing = false;
        return size;
    }

    public long analysisOthers(List<CapturedPacket> capturedPackets) {
        this.analyzing = true;
        HttpPacketModelFilter httpPacketModelFilter = new HttpPacketModelFilter();
        HttpsPacketModelFilter httpsPacketModelFilter = new HttpsPacketModelFilter();
        TcpPacketModelFilter tcpPacketModelFilter = new TcpPacketModelFilter();
        IcmpPacketModelFilter icmpPacketModelFilter = new IcmpPacketModelFilter();
        UdpPacketModelFilter udpPacketModelFilter = new UdpPacketModelFilter();
        ArpPacketModelFilter arpPacketModelFilter = new ArpPacketModelFilter();
        long size = 0;
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (httpPacketModelFilter.filter(packetModel) &&
                    httpsPacketModelFilter.filter(packetModel) &&
                    tcpPacketModelFilter.filter(packetModel) &&
                    icmpPacketModelFilter.filter(packetModel) &&
                    udpPacketModelFilter.filter(packetModel) &&
                    arpPacketModelFilter.filter(packetModel)) {
                size++;
            }
        }
        this.analyzing = false;
        return size;
    }

    public List<DistinctPacketCount> aggregatePacketsByProperty(List<Long> batchIds, String property, Class clazz) {
        try {
            Criteria criteria = new Criteria();
            if (!CollectionUtils.isEmpty(batchIds)) {
                criteria = criteria.and("batchId").in(batchIds);
            }
            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria)
                    , Aggregation.group(Fields.fields("upstream", property)).count().as("count")
                    , Aggregation.project("upstream", property, "count")
            );

            AggregationResults<Document> groupResults
                    = mongoTemplate.aggregate(agg, clazz, Document.class);
            List<Document> result = groupResults.getMappedResults();
            List<DistinctPacketCount> counts = new ArrayList<>();
            for (Document doc : result) {
                Boolean upstream = doc.getBoolean("upstream");
                Object propertyValue = doc.get(property);
                Integer count = doc.getInteger("count");

                DistinctPacketCount distinctPacketCount = new DistinctPacketCount();
                distinctPacketCount.setUpstream(upstream);
                distinctPacketCount.setPropertyName(property);
                distinctPacketCount.setPropertyValue(propertyValue);
                distinctPacketCount.setCount(count);
                counts.add(distinctPacketCount);
            }
            return counts;
        } catch (PropertyReferenceException e) {
            logger.warn(e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<DistinctTimelinePacketCount> aggregatePacketsTimeline(List<Boolean> upstreams, List<Long> batchIds, String property, Class clazz) {
        try {
            Criteria criteria = new Criteria();
            if (!CollectionUtils.isEmpty(batchIds)) {
                criteria = criteria.and("batchId").in(batchIds);
            }
            if (!CollectionUtils.isEmpty(upstreams)) {
                criteria = criteria.and("upstream").in(upstreams);
            }
            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria)
                    , Aggregation.group(Fields.fields("minuteTimeStr", "upstream", property)).count().as("count")
                    , Aggregation.project("minuteTimeStr", "upstream", property, "count")
                    , Aggregation.sort(Sort.Direction.ASC, "minuteTimeStr")
            );

            AggregationResults<Document> groupResults
                    = mongoTemplate.aggregate(agg, clazz, Document.class);
            List<Document> result = groupResults.getMappedResults();
            List<DistinctTimelinePacketCount> counts = new ArrayList<>();
            for (Document doc : result) {
                String minuteTimeStr = doc.getString("minuteTimeStr");
                Boolean upstream = doc.getBoolean("upstream");
                Object propertyValue = doc.get(property);
                Integer count = doc.getInteger("count");

                DistinctTimelinePacketCount distinctPacketCount = new DistinctTimelinePacketCount();
                distinctPacketCount.setMinuteTimeStr(minuteTimeStr);
                distinctPacketCount.setUpstream(upstream);
                distinctPacketCount.setPropertyName(property);
                distinctPacketCount.setPropertyValue(propertyValue);
                distinctPacketCount.setCount(count);
                counts.add(distinctPacketCount);
            }
            return counts;
        } catch (PropertyReferenceException e) {
            logger.warn(e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> distinctPacketsMinutes(List<Boolean> upstreams, List<Long> batchIds, Class clazz) {
        try {
            Criteria criteria = new Criteria();
            if (!CollectionUtils.isEmpty(batchIds)) {
                criteria = criteria.and("batchId").in(batchIds);
            }
            if (!CollectionUtils.isEmpty(upstreams)) {
                criteria = criteria.and("upstream").in(upstreams);
            }
            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria)
                    , Aggregation.group(Fields.fields("minuteTimeStr"))
                    , Aggregation.sort(Sort.Direction.ASC, "_id")
            );

            AggregationResults<Document> groupResults
                    = mongoTemplate.aggregate(agg, clazz, Document.class);
            List<Document> result = groupResults.getMappedResults();
            List<String> minutes = new ArrayList<>();
            for (Document doc : result) {
                String minuteTimeStr = doc.getString("_id");
                minutes.add(minuteTimeStr);
            }
            return minutes;
        } catch (PropertyReferenceException e) {
            logger.warn(e.getMessage());
            return new ArrayList<>();
        }
    }

    public AnalysisTimelineStatisticDTO calculateTimelineData(List<Long> batchIds) {
        AnalysisTimelineStatisticDTO dto = new AnalysisTimelineStatisticDTO();
        try {
            this.calculateUpstreamTimelineData(batchIds, dto);
            this.calculateDownstreamTimelineData(batchIds, dto);
            return dto;
        } catch (Exception e) {
            logger.error("Error to calculate timeline data: " + e.getMessage());
            return null;
        }
    }

    public void calculateUpstreamTimelineData(List<Long> batchIds, AnalysisTimelineStatisticDTO dto) {
        HashMap<String, DistinctTimelinePacketItem> time2ItemMap = new HashMap<>();
        this.accumulateByUpstream(true, batchIds, time2ItemMap);

        List<String> minutesList = new ArrayList<>(time2ItemMap.keySet());
        Collections.sort(minutesList);
        String[] timeArray = minutesList.toArray(new String[0]);
        String[] timeTitle = new String[]{"times"};
        String[] timesData = NetworkUtils.concatStrings(timeTitle, timeArray);
        int length = timesData.length;

        Object[] htmlData = new Object[length];
        Object[] plainData = new Object[length];
        Object[] xmlData = new Object[length];
        Object[] jsonData = new Object[length];
        Object[] attachmentData = new Object[length];
        Object[] webpData = new Object[length];
        Object[] jpegData = new Object[length];
        Object[] mp4Data = new Object[length];
        Object[] flvData = new Object[length];
        Object[] otherData = new Object[length];
        htmlData[0] = HttpContentTypeEnum.HTML.name();
        plainData[0] = HttpContentTypeEnum.PLAIN.name();
        xmlData[0] = HttpContentTypeEnum.XML.name();
        jsonData[0] = HttpContentTypeEnum.JSON.name();
        attachmentData[0] = HttpContentTypeEnum.ATTACHMENT.name();
        webpData[0] = HttpContentTypeEnum.WEBP.name();
        jpegData[0] = HttpContentTypeEnum.JPEG.name();
        mp4Data[0] = HttpContentTypeEnum.MP4.name();
        flvData[0] = HttpContentTypeEnum.FLV.name();
        otherData[0] = HttpContentTypeEnum.OTHER.name();
        for (int i = 1; i < length; i++) {
            htmlData[i] = time2ItemMap.get(timesData[i]).getUpHtmlNum();
            plainData[i] = time2ItemMap.get(timesData[i]).getUpPlainNum();
            xmlData[i] = time2ItemMap.get(timesData[i]).getUpXmlNum();
            jsonData[i] = time2ItemMap.get(timesData[i]).getUpJsonNum();
            attachmentData[i] = time2ItemMap.get(timesData[i]).getUpAttachmentNum();
            webpData[i] = time2ItemMap.get(timesData[i]).getUpWebpNum();
            jpegData[i] = time2ItemMap.get(timesData[i]).getUpJpegNum();
            mp4Data[i] = time2ItemMap.get(timesData[i]).getUpMp4Num();
            flvData[i] = time2ItemMap.get(timesData[i]).getUpFlvNum();
            otherData[i] = time2ItemMap.get(timesData[i]).getUpOtherNum();
        }
        Object[] upData = new Object[]{timesData, htmlData, plainData, xmlData, jsonData, attachmentData, webpData, jpegData, mp4Data, flvData, otherData};
        dto.setUpstreamData(upData);
        dto.setUpFirstMinute(timeArray[0]);
    }

    public void calculateDownstreamTimelineData(List<Long> batchIds, AnalysisTimelineStatisticDTO dto) {
        HashMap<String, DistinctTimelinePacketItem> time2ItemMap = new HashMap<>();
        this.accumulateByUpstream(false, batchIds, time2ItemMap);

        List<String> minutesList = new ArrayList<>(time2ItemMap.keySet());
        Collections.sort(minutesList);
        String[] timeArray = minutesList.toArray(new String[0]);
        String[] timeTitle = new String[]{"times"};
        String[] timesData = NetworkUtils.concatStrings(timeTitle, timeArray);
        int length = timesData.length;

        Object[] htmlData = new Object[length];
        Object[] plainData = new Object[length];
        Object[] xmlData = new Object[length];
        Object[] jsonData = new Object[length];
        Object[] attachmentData = new Object[length];
        Object[] webpData = new Object[length];
        Object[] jpegData = new Object[length];
        Object[] mp4Data = new Object[length];
        Object[] flvData = new Object[length];
        Object[] otherData = new Object[length];
        htmlData[0] = HttpContentTypeEnum.HTML.name();
        plainData[0] = HttpContentTypeEnum.PLAIN.name();
        xmlData[0] = HttpContentTypeEnum.XML.name();
        jsonData[0] = HttpContentTypeEnum.JSON.name();
        attachmentData[0] = HttpContentTypeEnum.ATTACHMENT.name();
        webpData[0] = HttpContentTypeEnum.WEBP.name();
        jpegData[0] = HttpContentTypeEnum.JPEG.name();
        mp4Data[0] = HttpContentTypeEnum.MP4.name();
        flvData[0] = HttpContentTypeEnum.FLV.name();
        otherData[0] = HttpContentTypeEnum.OTHER.name();
        for (int i = 1; i < length; i++) {
            htmlData[i] = time2ItemMap.get(timesData[i]).getDownHtmlNum();
            plainData[i] = time2ItemMap.get(timesData[i]).getDownPlainNum();
            xmlData[i] = time2ItemMap.get(timesData[i]).getDownXmlNum();
            jsonData[i] = time2ItemMap.get(timesData[i]).getDownJsonNum();
            attachmentData[i] = time2ItemMap.get(timesData[i]).getDownAttachmentNum();
            webpData[i] = time2ItemMap.get(timesData[i]).getDownWebpNum();
            jpegData[i] = time2ItemMap.get(timesData[i]).getDownJpegNum();
            mp4Data[i] = time2ItemMap.get(timesData[i]).getDownMp4Num();
            flvData[i] = time2ItemMap.get(timesData[i]).getDownFlvNum();
            otherData[i] = time2ItemMap.get(timesData[i]).getDownOtherNum();
        }
        Object[] downData = new Object[]{timesData, htmlData, plainData, xmlData, jsonData, attachmentData, webpData, jpegData, mp4Data, flvData, otherData};
        dto.setDownstreamData(downData);
        dto.setDownFirstMinute(timeArray[0]);
    }

    private void accumulateByUpstream(boolean upstream, List<Long> batchIds, HashMap<String, DistinctTimelinePacketItem> time2ItemMap) {
        List<DistinctTimelinePacketCount> httpDistincts = this.aggregatePacketsTimeline(Arrays.asList(upstream), batchIds, "contentType", AnalyzedHttpPacket.class);
        for (DistinctTimelinePacketCount httpDistinct : httpDistincts) {
            this.accumulateDistinctTimelineItem(time2ItemMap, httpDistinct, null);
        }

        List<DistinctTimelinePacketCount> httpsDistincts = this.aggregatePacketsTimeline(Arrays.asList(upstream), batchIds, "protocol", AnalyzedHttpsPacket.class);
        for (DistinctTimelinePacketCount httpsDistinct : httpsDistincts) {
            this.accumulateDistinctTimelineItem(time2ItemMap, httpsDistinct, HttpContentTypeEnum.OTHER.name());
        }
        List<DistinctTimelinePacketCount> tcpDistincts = this.aggregatePacketsTimeline(Arrays.asList(upstream), batchIds, "protocol", AnalyzedTcpPacket.class);
        for (DistinctTimelinePacketCount tcpDistinct : tcpDistincts) {
            this.accumulateDistinctTimelineItem(time2ItemMap, tcpDistinct, HttpContentTypeEnum.OTHER.name());
        }
        List<DistinctTimelinePacketCount> udpDistincts = this.aggregatePacketsTimeline(Arrays.asList(upstream), batchIds, "protocol", AnalyzedUdpPacket.class);
        for (DistinctTimelinePacketCount udpDistinct : udpDistincts) {
            this.accumulateDistinctTimelineItem(time2ItemMap, udpDistinct, HttpContentTypeEnum.OTHER.name());
        }
        List<DistinctTimelinePacketCount> icmpDistincts = this.aggregatePacketsTimeline(Arrays.asList(upstream), batchIds, "protocol", AnalyzedIcmpPacket.class);
        for (DistinctTimelinePacketCount icmpDistinct : icmpDistincts) {
            this.accumulateDistinctTimelineItem(time2ItemMap, icmpDistinct, HttpContentTypeEnum.OTHER.name());
        }
        List<DistinctTimelinePacketCount> arpDistincts = this.aggregatePacketsTimeline(Arrays.asList(upstream), batchIds, "protocol", AnalyzedArpPacket.class);
        for (DistinctTimelinePacketCount arpDistinct : arpDistincts) {
            this.accumulateDistinctTimelineItem(time2ItemMap, arpDistinct, HttpContentTypeEnum.OTHER.name());
        }
    }

    private void accumulateDistinctTimelineItem(HashMap<String, DistinctTimelinePacketItem> time2ItemMap, DistinctTimelinePacketCount httpsDistinct, String propertyValue) {
        if (time2ItemMap.get(httpsDistinct.getMinuteTimeStr()) == null) {
            DistinctTimelinePacketItem item = new DistinctTimelinePacketItem();
            item.setMinuteTimeStr(httpsDistinct.getMinuteTimeStr());
            time2ItemMap.put(httpsDistinct.getMinuteTimeStr(), item);
        }
        if (propertyValue == null)
            time2ItemMap.get(httpsDistinct.getMinuteTimeStr()).accumulate(httpsDistinct.getUpstream(), (String) httpsDistinct.getPropertyValue(), httpsDistinct.getCount());
        else
            time2ItemMap.get(httpsDistinct.getMinuteTimeStr()).accumulate(httpsDistinct.getUpstream(), propertyValue, httpsDistinct.getCount());
    }

    public List<String> distinctPacketsMinutesForAll(List<Boolean> upstreams, List<Long> batchIds) {
        List<String> httpMinutes = this.distinctPacketsMinutes(upstreams, batchIds, AnalyzedHttpPacket.class);
        List<String> httpsMinutes = this.distinctPacketsMinutes(upstreams, batchIds, AnalyzedHttpsPacket.class);
        List<String> tcpMinutes = this.distinctPacketsMinutes(upstreams, batchIds, AnalyzedTcpPacket.class);
        List<String> udpMinutes = this.distinctPacketsMinutes(upstreams, batchIds, AnalyzedUdpPacket.class);
        List<String> icmpMinutes = this.distinctPacketsMinutes(upstreams, batchIds, AnalyzedIcmpPacket.class);
        List<String> arpMinutes = this.distinctPacketsMinutes(upstreams, batchIds, AnalyzedArpPacket.class);
        Set<String> minutes = new HashSet<>();
        minutes.addAll(httpMinutes);
        minutes.addAll(httpsMinutes);
        minutes.addAll(tcpMinutes);
        minutes.addAll(udpMinutes);
        minutes.addAll(icmpMinutes);
        minutes.addAll(arpMinutes);
        List<String> minutesList = new ArrayList<>(minutes);
        Collections.sort(minutesList);
        return minutesList;
    }

    public Object[][] getUpstreamTimelinePacketsData() {
        List<String> upMinutes = this.distinctPacketsMinutesForAll(Arrays.asList(true), null);
        List<DistinctTimelinePacketCount> distinctTimelinePacketCounts = this.aggregatePacketsTimeline(Arrays.asList(false), null, "contentType", AnalyzedHttpPacket.class);
        for (DistinctTimelinePacketCount distinctTimelinePacketCount : distinctTimelinePacketCounts) {
            Helper.println(distinctTimelinePacketCount);
        }
        return null;
    }

    private void saveRealmPackets(Map<Long, IAnalysisRealm> analysisRealms) {
        Iterator<Map.Entry<Long, IAnalysisRealm>> it = analysisRealms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, IAnalysisRealm> entry = it.next();
            IAnalysisRealm realm = entry.getValue();
            AbsAnalyzedPacket toSavePacket = realm.makePacket4Save();
            if (toSavePacket != null) {
                try {
                    genericDao.save(toSavePacket);
                } catch (org.bson.BsonSerializationException e) {
                    toSavePacket.minimizeContent();
                    genericDao.save(toSavePacket);
                }
            }
        }
    }

}
