/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.controller;

import com.jpcap.mitm.analysis.HttpContentTypeEnum;
import com.jpcap.mitm.analysis.ProtocolEnum;
import com.jpcap.mitm.dao.GenericDao;
import com.jpcap.mitm.dto.*;
import com.jpcap.mitm.entity.*;
import com.jpcap.mitm.model.AbsAnalyzedPacket;
import com.jpcap.mitm.model.DistinctPacketCount;
import com.jpcap.mitm.model.DumpRecord;
import com.jpcap.mitm.model.NetworkInterfaceModel;
import com.jpcap.mitm.req.DeleteAttackHisReq;
import com.jpcap.mitm.req.PacketFilterParamsReq;
import com.jpcap.mitm.req.PacketStatisticFilterParamsReq;
import com.jpcap.mitm.service.AnalysisService;
import com.jpcap.mitm.service.AttackService;
import com.jpcap.mitm.dto.*;
import com.jpcap.mitm.entity.*;
import jpcap.NetworkInterface;
import org.bson.Document;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Frankie
 * @date 19/5/19
 */
@SuppressWarnings("Duplicates")
@Controller
@RequestMapping("/api")
public class ApiController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private AttackService attackService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;

    private boolean deviceOpened = false;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "Hello world!";
    }

    @RequestMapping(value = "/getDevicelist", method = RequestMethod.GET)
    @ResponseBody
    public List<NetworkInterfaceModel> getDevicelist() {
        List<NetworkInterfaceModel> devices = new ArrayList<>();
        NetworkInterface[] interfaces = attackService.getDevices();
        if (interfaces != null)
            for (NetworkInterface networkInterface : interfaces) {
                NetworkInterfaceModel model = new NetworkInterfaceModel();
                model.setName(networkInterface.name);
                model.setDescription(networkInterface.description);
                model.setDatalinkName(networkInterface.datalink_name);
                model.setDatalinkDescription(networkInterface.datalink_description);
                devices.add(model);
            }
        return devices;
    }

    @RequestMapping(value = "/getAttackConfig", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO getAttackConfig() {
        List<AttackConfig> configs = genericDao.findAll(AttackConfig.class);
        if (configs != null && configs.size() > 0) {
            AttackConfig config = configs.get(0);
            return new ResultDTO(config);
        } else {
            AttackConfig config = new AttackConfig();
            return new ResultDTO(config);
        }
    }

    @RequestMapping(value = "/isDeviceOpened", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO isDeviceOpened() {
        return new ResultDTO(deviceOpened);
    }

    @RequestMapping(value = "/updateConfigAndOpenDevice", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO updateConfigAndOpenDevice(@RequestBody AttackConfig req) throws IOException {
        if (req.getSrcIp() != null && req.getSrcMac() != null
                && req.getDestIp() != null && req.getDestMac() != null
                && req.getGateIp() != null && req.getGateMac() != null
                && req.getFilterDomain() != null) {

            if (req.getSrcMac() != null)
                req.setSrcMac(req.getSrcMac().replace('-', ':').toLowerCase());
            if (req.getDestMac() != null)
                req.setDestMac(req.getDestMac().replace('-', ':').toLowerCase());
            if (req.getGateMac() != null)
                req.setGateMac(req.getGateMac().replace('-', ':').toLowerCase());

            attackService.updateConfigAndOpenDevice(req.getDeviceName(), req.getDestIp(), req.getDestMac(),
                    req.getSrcIp(), req.getSrcMac(),
                    req.getGateIp(), req.getGateMac(),
                    req.getFilterDomain());
            deviceOpened = true;
            List<AttackConfig> configs = genericDao.findAll(AttackConfig.class);
            if (configs != null && configs.size() > 0) {
                req.setId(configs.get(0).getId());
            }
            genericDao.save(req);
            return new ResultDTO(true);
        } else {
            return new ResultDTO(false);
        }
    }

    @RequestMapping(value = "/startAttack", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO startAttack() {
        logger.info("Start attacking...");
        attackService.attack();
        return new ResultDTO(true);
    }

    @RequestMapping(value = "/stopAttack", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO stopAttack() {
        logger.info("Stop attacking...");
        Long batchId = attackService.stopAttack();
        return new ResultDTO(batchId);
    }

    @RequestMapping(value = "/isAttacking", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO isAttacking() {
        ResultDTO dto = new ResultDTO();
        dto.setResult(attackService.isAttacking());
        return dto;
    }

    @RequestMapping(value = "/getAttackStatistic", method = RequestMethod.GET)
    @ResponseBody
    public AttackStatisticDTO getAttackStatistic() {
        AttackStatisticDTO dto = new AttackStatisticDTO();
        dto.setUpStreamNum(attackService.getUpStreamNum());
        dto.setDownStreamNum(attackService.getDownStreamNum());
        dto.setUpTcpNum(attackService.getUpTcpNum());
        dto.setUpUdpNum(attackService.getUpUdpNum());
        dto.setUpIcmpNum(attackService.getUpIcmpNum());
        dto.setUpArpNum(attackService.getUpArpNum());
        dto.setDownTcpNum(attackService.getDownTcpNum());
        dto.setDownUdpNum(attackService.getDownUdpNum());
        dto.setDownIcmpNum(attackService.getDownIcmpNum());
        dto.setDownArpNum(attackService.getDownArpNum());
        return dto;
    }

    @RequestMapping(value = "/getDumpRecords", method = RequestMethod.GET)
    @ResponseBody
    public List<DumpRecord> getDumpRecords() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group(Fields.fields("batchId"))
                        .first("batchId").as("batchId")
                        .first("upstream").as("upstream")
                        .first("startAttackTime").as("startAttackTime"),
                Aggregation.project("batchId", "upstream", "startAttackTime"),
                Aggregation.sort(Sort.Direction.DESC, "batchId")
        );
        AggregationResults<DumpRecord> groupResults
                = mongoTemplate.aggregate(agg, CapturedPacket.class, DumpRecord.class);
        List<DumpRecord> dumpRecords = groupResults.getMappedResults();
        for (DumpRecord dumpRecord : dumpRecords) {
            Query upQuery = new Query(Criteria.where("batchId").is(dumpRecord.getBatchId())
                    .and("upstream").is(true));
            long upCount = genericDao.count(upQuery, CapturedPacket.class);
            Query downQuery = new Query(Criteria.where("batchId").is(dumpRecord.getBatchId())
                    .and("upstream").is(false));
            long downCount = genericDao.count(downQuery, CapturedPacket.class);
            dumpRecord.setUpstreamPackets(upCount);
            dumpRecord.setDownstreamPackets(downCount);
        }
        return dumpRecords;
    }

    @RequestMapping(value = "/deleteAttackHis", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO deleteAttackHis(@RequestBody DeleteAttackHisReq req) {
        Query query = new Query(Criteria.where("batchId").is(req.getBatchId()));
        long n = genericDao.delete(query, CapturedPacket.class);
        this.analysisService.cleanByBatchId(req.getBatchId());
        ResultDTO dto = new ResultDTO(n);
        return dto;
    }

    @RequestMapping(value = "/analysisByBatchId", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO analysisByBatchId(@RequestParam("batchId") Long batchId) {
        logger.info("Begin to analysis packets for batchId: " + batchId);
        this.analysisService.cleanByBatchId(batchId);
        long size = this.analysisService.analysisByBatchId(batchId);
        logger.info("Finish the analysis packets for batchId: " + batchId + ", n: " + size);
        ResultDTO dto = new ResultDTO(size);
        return dto;
    }

    @RequestMapping(value = "/isAnalyzing", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO isAnalyzing() {
        ResultDTO dto = new ResultDTO();
        dto.setResult(analysisService.isAnalyzing());
        return dto;
    }

    @RequestMapping(value = "/distinctBatchIds", method = RequestMethod.GET)
    @ResponseBody
    public List<Long> distinctBatchIds() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group(Fields.fields("batchId"))
                        .first("batchId").as("batchId"),
                Aggregation.project("batchId"),
                Aggregation.sort(Sort.Direction.DESC, "batchId")
        );
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, CapturedPacket.class, Document.class);
        List<Document> results = groupResults.getMappedResults();
        List<Long> batchIds = new ArrayList<>();
        for (Document doc : results) {
            Long batchId = (Long) doc.get("batchId");
            batchIds.add(batchId);
        }
        return batchIds;
    }

    @RequestMapping(value = "/filterPackets", method = RequestMethod.POST)
    @ResponseBody
    public List<AbsAnalyzedPacket> filterPackets(@RequestBody PacketFilterParamsReq req) {
        List<AbsAnalyzedPacket> packets = new ArrayList<>();
        Criteria criteria = new Criteria();
        if (!CollectionUtils.isEmpty(req.getBatchIds()))
            criteria = criteria.and("batchId").in(req.getBatchIds());
        if (!CollectionUtils.isEmpty(req.getDirections()))
            criteria = criteria.and("upstream").in(req.getDirections());
        if (!CollectionUtils.isEmpty(req.getContentTypes()))
            criteria = criteria.and("contentType").in(req.getContentTypes());

        Query query = new Query(criteria);
        if (!CollectionUtils.isEmpty(req.getProtocols())) {
            for (String protocol : req.getProtocols()) {
                if (ProtocolEnum.HTTP.name().equals(protocol)) {
                    List<AnalyzedHttpPacket> httpPackets = genericDao.find(query, AnalyzedHttpPacket.class);
                    packets.addAll(httpPackets);
                } else if (ProtocolEnum.HTTPS.name().equals(protocol)) {
                    List<AnalyzedHttpsPacket> httpsPackets = genericDao.find(query, AnalyzedHttpsPacket.class);
                    packets.addAll(httpsPackets);
                } else if (ProtocolEnum.TCP.name().equals(protocol)) {
                    List<AnalyzedTcpPacket> tcpPackets = genericDao.find(query, AnalyzedTcpPacket.class);
                    packets.addAll(tcpPackets);
                } else if (ProtocolEnum.UDP.name().equals(protocol)) {
                    List<AnalyzedUdpPacket> udpPackets = genericDao.find(query, AnalyzedUdpPacket.class);
                    packets.addAll(udpPackets);
                } else if (ProtocolEnum.ICMP.name().equals(protocol)) {
                    List<AnalyzedIcmpPacket> icmpPackets = genericDao.find(query, AnalyzedIcmpPacket.class);
                    packets.addAll(icmpPackets);
                } else if (ProtocolEnum.ARP.name().equals(protocol)) {
                    List<AnalyzedArpPacket> arpPackets = genericDao.find(query, AnalyzedArpPacket.class);
                    packets.addAll(arpPackets);
                }
            }
        } else {
            List<AnalyzedHttpPacket> httpPackets = genericDao.find(query, AnalyzedHttpPacket.class);
            packets.addAll(httpPackets);
            List<AnalyzedHttpsPacket> httpsPackets = genericDao.find(query, AnalyzedHttpsPacket.class);
            packets.addAll(httpsPackets);
            List<AnalyzedTcpPacket> tcpPackets = genericDao.find(query, AnalyzedTcpPacket.class);
            packets.addAll(tcpPackets);
            List<AnalyzedUdpPacket> udpPackets = genericDao.find(query, AnalyzedUdpPacket.class);
            packets.addAll(udpPackets);
            List<AnalyzedIcmpPacket> icmpPackets = genericDao.find(query, AnalyzedIcmpPacket.class);
            packets.addAll(icmpPackets);
            List<AnalyzedArpPacket> arpPackets = genericDao.find(query, AnalyzedArpPacket.class);
            packets.addAll(arpPackets);
        }
        return packets;
    }

    @RequestMapping(value = "/statisticPackets", method = RequestMethod.POST)
    @ResponseBody
    public AnalysisStatisticDTO statisticPackets(@RequestBody PacketStatisticFilterParamsReq req) {
        Criteria upCriteria = new Criteria();
        Criteria downCriteria = new Criteria();
        if (!CollectionUtils.isEmpty(req.getBatchIds())) {
            upCriteria = upCriteria.and("batchId").in(req.getBatchIds());
            downCriteria = downCriteria.and("batchId").in(req.getBatchIds());
        }
        upCriteria = upCriteria.and("upstream").is(true);
        downCriteria = downCriteria.and("upstream").is(false);
        Query upQuery = new Query(upCriteria);
        Query downQuery = new Query(downCriteria);

        long upHttpNum = genericDao.count(upQuery, AnalyzedHttpPacket.class);
        long upHttpsNum = genericDao.count(upQuery, AnalyzedHttpsPacket.class);
        long upTcpNum = genericDao.count(upQuery, AnalyzedTcpPacket.class);
        long upUdpNum = genericDao.count(upQuery, AnalyzedUdpPacket.class);
        long upIcmpNum = genericDao.count(upQuery, AnalyzedIcmpPacket.class);
        long upArpNum = genericDao.count(upQuery, AnalyzedArpPacket.class);

        long downHttpNum = genericDao.count(downQuery, AnalyzedHttpPacket.class);
        long downHttpsNum = genericDao.count(downQuery, AnalyzedHttpsPacket.class);
        long downTcpNum = genericDao.count(downQuery, AnalyzedTcpPacket.class);
        long downUdpNum = genericDao.count(downQuery, AnalyzedUdpPacket.class);
        long downIcmpNum = genericDao.count(downQuery, AnalyzedIcmpPacket.class);
        long downArpNum = genericDao.count(downQuery, AnalyzedArpPacket.class);

        long upStreamNum = upHttpNum + upHttpsNum + upTcpNum + upUdpNum + upIcmpNum + upArpNum;
        long downStreamNum = downHttpNum + downHttpsNum + downTcpNum + downUdpNum + downIcmpNum + downArpNum;

        AnalysisStatisticDTO dto = new AnalysisStatisticDTO();
        dto.setUpStreamNum(upStreamNum);
        dto.setDownStreamNum(downStreamNum);
        dto.setUpHttpNum(upHttpNum);
        dto.setUpHttpsNum(upHttpsNum);
        dto.setUpTcpNum(upTcpNum);
        dto.setUpUdpNum(upUdpNum);
        dto.setUpIcmpNum(upIcmpNum);
        dto.setUpArpNum(upArpNum);
        dto.setDownHttpNum(downHttpNum);
        dto.setDownHttpsNum(downHttpsNum);
        dto.setDownTcpNum(downTcpNum);
        dto.setDownUdpNum(downUdpNum);
        dto.setDownIcmpNum(downIcmpNum);
        dto.setDownArpNum(downArpNum);
        return dto;
    }

    @RequestMapping(value = "/statisticPacketsContentType", method = RequestMethod.POST)
    @ResponseBody
    public AnalysisContentTypeStatisticDTO statisticPacketsContentType(@RequestBody PacketStatisticFilterParamsReq req) {
        Criteria upCriteria = new Criteria();
        Criteria downCriteria = new Criteria();
        if (!CollectionUtils.isEmpty(req.getBatchIds())) {
            upCriteria = upCriteria.and("batchId").in(req.getBatchIds());
            downCriteria = downCriteria.and("batchId").in(req.getBatchIds());
        }
        upCriteria = upCriteria.and("upstream").is(true);
        downCriteria = downCriteria.and("upstream").is(false);
        Query upQuery = new Query(upCriteria);
        Query downQuery = new Query(downCriteria);

        long upHttpsNum = genericDao.count(upQuery, AnalyzedHttpsPacket.class);
        long upTcpNum = genericDao.count(upQuery, AnalyzedTcpPacket.class);
        long upUdpNum = genericDao.count(upQuery, AnalyzedUdpPacket.class);
        long upIcmpNum = genericDao.count(upQuery, AnalyzedIcmpPacket.class);
        long upArpNum = genericDao.count(upQuery, AnalyzedArpPacket.class);

        long downHttpsNum = genericDao.count(downQuery, AnalyzedHttpsPacket.class);
        long downTcpNum = genericDao.count(downQuery, AnalyzedTcpPacket.class);
        long downUdpNum = genericDao.count(downQuery, AnalyzedUdpPacket.class);
        long downIcmpNum = genericDao.count(downQuery, AnalyzedIcmpPacket.class);
        long downArpNum = genericDao.count(downQuery, AnalyzedArpPacket.class);

        long upStreamOtherNum = upHttpsNum + upTcpNum + upUdpNum + upIcmpNum + upArpNum;
        long downStreamOtherNum = downHttpsNum + downTcpNum + downUdpNum + downIcmpNum + downArpNum;

        AnalysisContentTypeStatisticDTO dto = new AnalysisContentTypeStatisticDTO();
        dto.setUpOtherNum(upStreamOtherNum);
        dto.setDownOtherNum(downStreamOtherNum);
        List<DistinctPacketCount> distinctPacketCounts = analysisService.aggregatePacketsByProperty(req.getBatchIds(), "contentType", AnalyzedHttpPacket.class);
        for (DistinctPacketCount distinctPacketCount : distinctPacketCounts) {
            if (HttpContentTypeEnum.HTML.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpHtmlNum(distinctPacketCount.getCount());
                else
                    dto.setDownHtmlNum(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.PLAIN.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpPlainNum(distinctPacketCount.getCount());
                else
                    dto.setDownPlainNum(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.XML.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpXmlNum(distinctPacketCount.getCount());
                else
                    dto.setDownXmlNum(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.JSON.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpJsonNum(distinctPacketCount.getCount());
                else
                    dto.setDownJsonNum(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.ATTACHMENT.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpAttachmentNum(distinctPacketCount.getCount());
                else
                    dto.setDownAttachmentNum(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.WEBP.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpWebpNum(distinctPacketCount.getCount());
                else
                    dto.setDownWebpNum(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.JPEG.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpJpegNum(distinctPacketCount.getCount());
                else
                    dto.setDownJpegNum(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.MP4.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpMp4Num(distinctPacketCount.getCount());
                else
                    dto.setDownMp4Num(distinctPacketCount.getCount());
            } else if (HttpContentTypeEnum.FLV.name().equals(distinctPacketCount.getPropertyValue())) {
                if (distinctPacketCount.getUpstream())
                    dto.setUpFlvNum(distinctPacketCount.getCount());
                else
                    dto.setDownFlvNum(distinctPacketCount.getCount());
            }
        }
        return dto;
    }

    @RequestMapping(value = "/statisticTimelinePackets", method = RequestMethod.POST)
    @ResponseBody
    public AnalysisTimelineStatisticDTO statisticTimelinePackets(@RequestBody PacketStatisticFilterParamsReq req) {
        AnalysisTimelineStatisticDTO dto = analysisService.calculateTimelineData(req.getBatchIds());
        return dto;
    }

    @RequestMapping(value = "/getOriginalPackets", method = RequestMethod.GET)
    @ResponseBody
    public List<OriginalPacketDTO> getOriginalPackets(@RequestParam("originalIds") List<String> originalIds) {
        Query query = new Query(Criteria.where("id").in(originalIds));
        List<CapturedPacket> capturedPackets = genericDao.find(query, CapturedPacket.class);
        List<OriginalPacketDTO> dtos = new ArrayList<>();
        for (CapturedPacket packet : capturedPackets) {
            OriginalPacketDTO dto = OriginalPacketDTO.readFrom(packet.getPacket());
            dtos.add(dto);
        }
        return dtos;
    }

    @RequestMapping(value = "/capture", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO capture(HttpServletRequest request) {
        String ip = getIpAddr(request);
        System.out.println("================" + ip);
        ResultDTO dto = new ResultDTO();
        dto.setResult(ip);
        return dto;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}
