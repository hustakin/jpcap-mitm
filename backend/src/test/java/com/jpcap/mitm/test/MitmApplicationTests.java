/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import com.jpcap.mitm.analysis.arp.ArpPacketModelFilter;
import com.jpcap.mitm.analysis.http.HttpPacketModelFilter;
import com.jpcap.mitm.analysis.http.HttpsPacketModelFilter;
import com.jpcap.mitm.analysis.icmp.IcmpPacketModelFilter;
import com.jpcap.mitm.analysis.tcp.TcpPacketModelFilter;
import com.jpcap.mitm.analysis.udp.UdpPacketModelFilter;
import com.jpcap.mitm.dao.GenericDao;
import com.jpcap.mitm.entity.AnalyzedHttpPacket;
import com.jpcap.mitm.entity.CapturedPacket;
import com.jpcap.mitm.entity.Device;
import com.jpcap.mitm.entity.TestCapturedPacket;
import com.jpcap.mitm.model.PacketModel;
import com.jpcap.mitm.model.TCPPacketModel;
import com.jpcap.mitm.service.AnalysisService;
import com.jpcap.mitm.service.NsLookupService;
import com.jpcap.mitm.utils.Helper;
import com.jpcap.mitm.utils.NetworkUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.*;

@SuppressWarnings("Duplicates")
@RunWith(SpringRunner.class)
@SpringBootTest
public class MitmApplicationTests {

    private static final String HEX_NEW_LINE = "0D0A0D0A";
    private static String CONTENT_TYPE_HTML = "text/html";
    private static String HEADER_CONTENT_TYPE = "content-type";
    private static String HEADER_ENCODING = "content-encoding";
    private static String HEADER_ENCODING_GZIP = "gzip";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private NsLookupService nsLookupService;

    @Test
    public void testAtlas() {
        //MongoClientURI uri = new MongoClientURI(
        //        "mongodb://terminal:hello1234@cluster0-shard-00-00-8pqgy.mongodb.net:27017,cluster0-shard-00-01-8pqgy.mongodb.net:27017,cluster0-shard-00-02-8pqgy.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://terminal:hello1234@cluster0-8pqgy.mongodb.net/test?retryWrites=true");
        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("myTestCollection");
        Document document = new Document("name", "Frankie Fan")
                .append("contact", new Document("phone", "110")
                        .append("email", "haha@example.com")
                        .append("location", Arrays.asList(-73.92502, 40.8279556)))
                .append("stars", 3)
                .append("categories", Arrays.asList("Bakery", "Coffee", "Pastries"));

        collection.insertOne(document);
    }

    @Test
    public void testFind() {
        List<Device> deviceList = genericDao.findAll(Device.class);
        Helper.println(deviceList);
    }

    @Test
    public void testAnalysis() {
        analysisService.cleanAnalysisHttpHis();
        analysisService.cleanAnalysisHttpsHis();
        analysisService.cleanAnalysisTcpHis();
        analysisService.cleanAnalysisUdpHis();
        analysisService.cleanAnalysisIcmpHis();
        analysisService.cleanAnalysisArpHis();
        List<CapturedPacket> capturedPackets = genericDao.findAll(CapturedPacket.class);
        Helper.println(analysisService.analysisHttp(capturedPackets));
        Helper.println(analysisService.analysisHttps(capturedPackets));
        Helper.println(analysisService.analysisTcp(capturedPackets));
        Helper.println(analysisService.analysisUdp(capturedPackets));
        Helper.println(analysisService.analysisIcmp(capturedPackets));
        Helper.println(analysisService.analysisArp(capturedPackets));
    }

    @Test
    public void testAnalysisBatch() {
        Query query = new Query(Criteria.where("batchId").is(38));
        List<CapturedPacket> capturedPackets = genericDao.find(query, CapturedPacket.class);
        Helper.println(capturedPackets.size());

        analysisService.cleanByBatchId(38L);
        Helper.println(analysisService.analysisHttp(capturedPackets));
        Helper.println(analysisService.analysisHttps(capturedPackets));
        Helper.println(analysisService.analysisTcp(capturedPackets));
        Helper.println(analysisService.analysisUdp(capturedPackets));
        Helper.println(analysisService.analysisIcmp(capturedPackets));
        Helper.println(analysisService.analysisArp(capturedPackets));
        Helper.println(analysisService.analysisOthers(capturedPackets));
    }

    @Test
    public void testDistinctBatchIds() {
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
        Helper.println(batchIds);
    }

    @Test
    public void testFindPacket() {
        List<CapturedPacket> capturedPackets = genericDao.findAll(CapturedPacket.class);
        Helper.println(capturedPackets.size());
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (packetModel instanceof TCPPacketModel) {
                Helper.println(ToStringBuilder.reflectionToString(packetModel));
            }
        }
    }

    @Test
    public void testFindHttpPacket() {
        List<AnalyzedHttpPacket> httpPackets = genericDao.findAll(AnalyzedHttpPacket.class);
        for (AnalyzedHttpPacket httpPacket : httpPackets) {
            Helper.println(httpPacket.getHttpHeaders());
            Helper.println(httpPacket.getContent());
        }
    }

    @Test
    public void testFindDownstreamPacket() throws UnsupportedEncodingException {
        Query query = new Query(Criteria.where("upstream").is(false));
        List<CapturedPacket> capturedPackets = genericDao.find(query, CapturedPacket.class);
        HttpPacketModelFilter httpPacketModelFilter = new HttpPacketModelFilter();
        HttpsPacketModelFilter httpsPacketModelFilter = new HttpsPacketModelFilter();
        IcmpPacketModelFilter icmpPacketModelFilter = new IcmpPacketModelFilter();
        TcpPacketModelFilter tcpPacketModelFilter = new TcpPacketModelFilter();
        UdpPacketModelFilter udpPacketModelFilter = new UdpPacketModelFilter();
        ArpPacketModelFilter arpPacketModelFilter = new ArpPacketModelFilter();
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (!httpPacketModelFilter.filter(packetModel)) {
                System.out.println("-----HTTP");
            } else if (!httpsPacketModelFilter.filter(packetModel)) {
                System.out.println("-----HTTPS");
            } else if (!icmpPacketModelFilter.filter(packetModel)) {
                System.out.println("-----ICMP");
            } else if (!tcpPacketModelFilter.filter(packetModel)) {
                System.out.println("-----TCP");
            } else if (!udpPacketModelFilter.filter(packetModel)) {
                System.out.println("-----UDP");
            } else if (!arpPacketModelFilter.filter(packetModel)) {
                System.out.println("-----ARP");
            }
        }
    }

    @Test
    public void testFindTestPackets() throws UnsupportedEncodingException {
        List<TestCapturedPacket> testCapturedPackets = genericDao.findAll(TestCapturedPacket.class);
        Helper.println(testCapturedPackets.size());
        Map<Long, byte[]> chunkeds = new HashMap<>();
        for (TestCapturedPacket packet : testCapturedPackets) {
            PacketModel packetModel = packet.getPacket();
            if (packetModel instanceof TCPPacketModel) {
                TCPPacketModel tcpPacketModel = (TCPPacketModel) packetModel;
                //Helper.println(tcpPacketModel.getSequence() + " -- " + tcpPacketModel.getAck_num() + " -- " + tcpPacketModel.isPsh());
                //if (tcpPacketModel.isPsh()) {
                //    testData(tcpPacketModel.getData());
                //}
                Helper.println(NetworkUtils.bytesToHexString(tcpPacketModel.getData()));

                String hex = NetworkUtils.bytesToHexString(tcpPacketModel.getData());
                if (!chunkeds.containsKey(tcpPacketModel.getAck_num())) {
                    int indexOfNewLine = hex.indexOf("0D0A0D0A");
                    if (indexOfNewLine > 0) {
                        String contentHex = hex.substring(indexOfNewLine + 8);

                        String gzipContentHex = NetworkUtils.parseGzipContent(contentHex);
                        byte[] contentBytes = NetworkUtils.toBytes(gzipContentHex);
                        chunkeds.put(tcpPacketModel.getAck_num(), contentBytes);
                    }
                } else {
                    String gzipContentHex = NetworkUtils.parseGzipContent(hex);
                    byte[] contentBytes = NetworkUtils.toBytes(gzipContentHex);

                    byte[] existed = chunkeds.get(tcpPacketModel.getAck_num());
                    byte[] newBytes = NetworkUtils.concatBytes(existed, contentBytes);
                    chunkeds.put(tcpPacketModel.getAck_num(), newBytes);
                }
            }
        }

        Iterator<Map.Entry<Long, byte[]>> it = chunkeds.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, byte[]> entry = it.next();
            Long ack = entry.getKey();
            byte[] data = entry.getValue();

            String hex = NetworkUtils.bytesToHexString(data);
            Helper.println(hex);
            byte[] contentBytes = NetworkUtils.toBytes(hex);
            byte[] unGzipContentBytes = NetworkUtils.unGzip(contentBytes);
            Helper.println(new String(unGzipContentBytes, "GBK"));
        }
    }

    @Test
    public void analysisHttp() {
        List<CapturedPacket> capturedPackets = genericDao.findAll(CapturedPacket.class);
        Map<Long, Map<String, String>> headers = new HashMap<>();
        Map<Long, byte[]> gzipAcks = new HashMap<>();
        Map<Long, byte[]> normalAcks = new HashMap<>();
        this.groupHttpPackets(capturedPackets, headers, gzipAcks, normalAcks);

        this.processGzipHttpPackets(gzipAcks, headers);
    }

    private void groupHttpPackets(List<CapturedPacket> capturedPackets, Map<Long, Map<String, String>> headers, Map<Long, byte[]> gzipAcks, Map<Long, byte[]> normalAcks) {
        HttpPacketModelFilter httpPacketModelFilter = new HttpPacketModelFilter();
        for (CapturedPacket packet : capturedPackets) {
            PacketModel packetModel = packet.getPacket();
            //filter http packet and process it
            if (!httpPacketModelFilter.filter(packetModel)) {
                TCPPacketModel tcpPacketModel = (TCPPacketModel) packetModel;
                String hex = NetworkUtils.bytesToHexString(tcpPacketModel.getData());

                //parse http header and body
                HttpHeaderBodyProcessor httpHeaderBodyProcessor = new HttpHeaderBodyProcessor();
                httpHeaderBodyProcessor.process(packetModel);
                String contentHex = httpHeaderBodyProcessor.getContentHex();
                Map<String, String> httpHeaders = httpHeaderBodyProcessor.getHttpHeaders();
                headers.put(tcpPacketModel.getAck_num(), httpHeaders);

                if (!gzipAcks.containsKey(tcpPacketModel.getAck_num()) && !normalAcks.containsKey(tcpPacketModel.getAck_num())) {
                    int indexOfNewLine = hex.indexOf(HEX_NEW_LINE);
                    if (indexOfNewLine > 0) {

                        //unGzip content for gzip data
                        if (HEADER_ENCODING_GZIP.equals(httpHeaders.get(HEADER_ENCODING))) {
                            String gzipContentHex = NetworkUtils.parseGzipContent(contentHex);
                            byte[] contentBytes = NetworkUtils.toBytes(gzipContentHex);
                            gzipAcks.put(tcpPacketModel.getAck_num(), contentBytes);
                            System.err.println("============= find gzip http packet ===============: " + tcpPacketModel.getAck_num() + " - " + packet.getId());
                        } else {
                            //System.err.println("--------------unknown encoding http ack: " + tcpPacketModel.getAck_num() + ", encoding: " + httpHeaders.get(HEADER_ENCODING));
                            normalAcks.put(tcpPacketModel.getAck_num(), NetworkUtils.toBytes(contentHex));
                        }
                    }
                } else {
                    //unGzip content for gzip data
                    if (HEADER_ENCODING_GZIP.equals(headers.get(tcpPacketModel.getAck_num()).get(HEADER_ENCODING))) {
                        String gzipContentHex = NetworkUtils.parseGzipContent(contentHex);
                        byte[] contentBytes = NetworkUtils.toBytes(gzipContentHex);

                        byte[] existed = gzipAcks.get(tcpPacketModel.getAck_num());
                        byte[] newBytes = NetworkUtils.concatBytes(existed, contentBytes);
                        gzipAcks.put(tcpPacketModel.getAck_num(), newBytes);
                        System.err.println("============= continue gzip http packet ===============: " + tcpPacketModel.getAck_num() + " - " + packet.getId());
                    } else {
                        byte[] existed = normalAcks.get(tcpPacketModel.getAck_num());
                        byte[] newBytes = NetworkUtils.concatBytes(existed, NetworkUtils.toBytes(contentHex));
                        normalAcks.put(tcpPacketModel.getAck_num(), newBytes);
                    }
                }
            }
        }
    }

    private void processGzipHttpPackets(Map<Long, byte[]> gzipAcks, Map<Long, Map<String, String>> headers) {
        GzipHttpDataProcessor gzipHttpDataProcessor = new GzipHttpDataProcessor(genericDao);
        Iterator<Map.Entry<Long, byte[]>> it = gzipAcks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, byte[]> entry = it.next();
            Long ackNum = entry.getKey();
            Map<String, String> httpHeaders = headers.get(ackNum);
            byte[] data = entry.getValue();

            gzipHttpDataProcessor.process(ackNum, httpHeaders, data);
        }
    }

    //unGzip is correct
    @Test
    public void testUnGzip() throws UnsupportedEncodingException {
        //String srcDataHex = "1F8B08000000000000038C565D6FD446147D0689FF304C55F2E2F56C3E4808AC4D49B27C17509A0AF18466EDC19EC4F66CC6E35D12F5CF04A92AAA404D9A120AA450509B9400252488A0A812424295AA0AA1F6A1A2511F3A33F66E9C6493F6C5BE339E7BE6DC7BCFDC7169EFC0D9FEA10BE7CAE0F8D0C7A7C1B94FFB4E9FE807B080D0F9CE7E84068606D20F5D66B11D0C711CC5545016E100A1F219080080BE10D58308D5EB75B3DE6932EEA1A141E48B30E842016331315DE1427BCF6E00F6ECDEB3BBA4BED8A945B09B5A211118289C02194D68CD820E8B048944418C550904D9C882825C161AFA10707CCC6322AC637DA720401A3E858970482C788C448463C178E6ED48EF93B8863F7138AD0AA3EA570D1C570D8505B7389F2A5F38CFB81BE77CCB950A8D3C1F27B1E18C3901012109191F3340ECF8C44D0262009C081662411D0378E9EE324FA0CA99C771684C5E9BFAEBF6DCA35BC6ECFCD29B17BFCCAD1A4B3F3EF9677EEDE92B3DA38D67D7EFDC5403E3DB578B5F6863616AF6CDD45B63E5F9CC57C6C3B5E987C6F4E3C53F8DE96BCD95B3F3373EFF66C178F4C3FDBF676796AF2CBF33F4F8DEECB34565A8D82626F2B10D94639D02C9AD6578201F5E33BAF5E0768AADC12A65AD482BCE8AF2561E4712E16FA8CEF1A4426442EFCC2DFF61DC7F7F7BF9EEF2C23D6883CD7E83ACC244AE3200E220D0EBA48E041501B1D783D99E5061616569BA845207ED8BD6E55861EE18A8780E0B18B7E0073D478F1E1DE882E93797D6000EA817599C7ABE50DA295D925C40B6BA122452B15A52525452D59C5CB2208D5C72D994AA8376BA31568C7795E22A8EECCF4A48BF41E6A4E0EC866B5BC03C1A29D736FBC6FC8359E5BAD16DEBCAC362D8E2C4A3B120BCCD5EB9F2F5EB165E486FB36B13D1DC611E1F8F47C64C27425E426251616C041D4E62C2AD61C259488A1DBDD09E7CF7624D616F1B4B0337C43410EC60474F4F777B6FF781DE8F46474D8785D0BEF572793545001925550B99659D6E69A77205018EBC047B247F8C55494A7B0B05B50EA14241BBA6EB336F397189F110604709D682C4D74590F296EA732D5865718AE2C8FE42B8AD3867CCFD4E7B6B61ED8999FFA3AE899B592C52559D0D2DF81CD4A92B7C0B76173F848DD90A472D4C1A55130154F7931B2742A8C35AC3928105171F4CFEFAD3EFF756216091135067C4820173F481B4DA647069886D10D9CD507640BBFB7007B40E94E2A92EA90155997700FB7969E9CB273373538F5F7F7775E9B79690352608CAB064C1B3B4ABBAA93AA547AC39D9CCCA7A55D251EB8CB65C23E5014E523CE4D318F469BAA0AF7CECC419A0E5A297640A4B43D257CCB094583A0B41CC9DE6B1A87598C3140B89A5C48B1CE612944D5CE4E6707C386435621561F37682EA7AB273A2DC8E53F9CC408E51AA841D73BD2EC3C63DF2FCFAF76F5BE63C235FC1748498F2E9269A7D8D923AEA2A163BDB8BAAC28DA26C2F4A9DD716F60E2C9F3E78FE72E5EAFCFB96BC5C568F0286DD757D6DAAF37FD859DDF205DAD0B764AFAA5187C8322591489FA6BCF43737B1ED8AB591CC56B3C5F671C77EB9F1F8B8CE6F2CB0D0DD98BA565771FF81CEEE9E7D7552B9981BC63EAB5B55EAB4C36DFADB66E5B466926F531E272482763FAB8EE90B0AEC73A479087414DBDB4141BDBAF2D792E0CC4D747334D37FA1B371C822577564131C090230A8406230485436899BEBD1CD63AA06EACAD4B3FAF7EE5F000000FFFF0300F4BC7A12550A0000";
        //String rstDataHex = "3c21444f43545950452048544d4c205055424c494320222d2f2f5733432f2f4454442048544d4c20342e3031205472616e736974696f6e616c2f2f454e2220202022687474703a2f2f7777772e77332e6f72672f54522f68746d6c342f6c6f6f73652e647464223e0d0a20200d0a0d0a3c68746d6c3e0d0a0d0a3c686561643e0d0a0d0a3c6d65746120687474702d65717569763d22636f6e74656e742d747970652220636f6e74656e743d22746578742f68746d6c3b20636861727365743d47424b22202f3e0d0a20203c6d657461206e616d653d2247656e657261746f722220636f6e746563743d224a6176615363726970742c7068702c6173702c68746d6c223e0d0a20203c6d657461206e616d653d224b4559576f7264732220636f6e746563743d22456262696e67686175732c6379636c65206d656d6f72792c207363686564756c652c206175746f6d617469632c2067656e65726174696f6e2070726f6772616d2cb0acb1f6bac6cbb92cbcc7d2e4d6dcc6da2cd2c5cdfcc7facfdf2cbcc7d2e4c7facfdf2cd1adbbb7bcc7d2e42cb8dfd0a7bcc7d2e42ccab1bce4b1ed2cd7d4b6af2cc9fab3c92cb3ccd0f22cb3acd1adbbb7bcc7d2e42cbcc7b5a5b4ca2ccbc4c1f9bcb6d3a2d3ef2cb5a5b4ca2cbfbcd1d0b5a5b4ca223e0d0aa1a13c6d657461206e616d653d224445736372697074696f6e2220636f6e746563743d22456262696e6768617573206379636c65206d656d6f7279207363686564756c65206175746f6d617469632067656e65726174696f6e2070726f6772616d2cb0acb1f6bac6cbb9d1adbbb7bcc7d2e4cab1bce4b1edd7d4b6afc9fab3c9b3ccd0f2223e0d0aa1a13c6d657461206e616d653d22417574686f722220636f6e746563743d224875626572792cbbc6d3f12cc1f8bad3bed3cabf223e200d0aa1a13c6d657461206e616d653d22526f626f74732220636f6e746563743d2022616c6c223e200d0a0d0a3c7469746c653e456262696e6768617573d1adbbb7bcc7d2e4cab1bce4b1edd7d4b6afc9fab3c9b3ccd0f22dcad7d2b33c2f7469746c653e0d0a0d0a3c2f686561643e0d0a0d0a3c626f6479206267636f6c6f723d2223374646464434223e0d0a0d0a3c64697620616c69676e3d72696768743e0d0a203c666f6e7420636f6c6f723d22626c756522203e0d0a202020203c6120687265663d22696e6465782e706870223ecad7d2b33c2f613e200d0a093c7370616e3e7c3c2f7370616e3e200d0a202020203c666f6e743e3c6120687265663d276c6f67696e2e706870273eb5c7c2bc3c2f613e3c7370616e3e7c3c2f7370616e3e3c6120687265663d276c6f67696e2e7068703f746a3d7265676973746572273ed7a2b2e13c2f613e3c7370616e3e7c3c2f7370616e3e3c2f666f6e743e090d0a202020203c6120687265663d22687474703a2f2f7777772e7a7a736b792e636e2f6775657374626f6f6b2f3f757365723d6a65726f6d65303239223eb0efd6fa3c2f613e0d0a093c7370616e3e7c3c2f7370616e3e200d0a202020203c6120687265663d226d61696c746f3a3237373631393638394071712e636f6d223eb9d8d3da3c2f613e0d0a203c2f666f6e743e0d0a3c2f6469763e0d0a0d0a0d0a3c736372697074206c616e67756167653d224a617661536372697074223e0d0a3c212d2d0d0a0d0a2f2f2d2d3e0d0a3c2f7363726970743e0d0a0d0a0d0a0d0a3c666f726d20616374696f6e3d2265682e70687022206d6574686f643d22706f7374223e0d0a3c63656e7465723e202020200d0a202020203c68333e3c666f6e7420636f6c6f723d22626c7565223ea1b6456262696e6768617573d1adbbb7bcc7d2e4cab1bce4b1edd7d4b6afc9fab3c9b3ccd0f2a1b73c2f666f6e743e3c2f68333e0d0a202020203c68722077696474683d22363025223e0d0a202020203c62722f3e0d0a202020203c62722f3e0d0a202020203c696e70757420747970653d22627574746f6e222076616c75653d22d0c2b0e6c8ebbfda22206f6e636c69636b3d226c6f636174696f6e3d2770687065682e70687027222f3e20200d0a202020203c696e70757420747970653d22627574746f6e222076616c75653d22bec9b0e6c8ebbfda22206f6e636c69636b3d226c6f636174696f6e3d27322f70687065682e68746d6c27222f3e200d0a093c696e70757420747970653d22627574746f6e222076616c75653d22ced2d2aacdb6c6b1cce1bda8d2e922206f6e636c69636b3d226c6f636174696f6e3d27766f74652f27222f3e200d0a203c2f63656e7465723e0d0a3c2f666f726d3e0d0a0d0a3c63656e7465723e0d0a202020203c62722f3e2020200d0a202020203c62722f3e20200d0a202020203c68722077696474683d22363025223e200d0a202020203c62722f3e20200d0a202020203c212d2d204a69615468697320427574746f6e20424547494e202d2d3e0d0a202020203c73637269707420747970653d22746578742f6a61766173637269707422207372633d22687474703a2f2f76322e6a6961746869732e636f6d2f636f64652f6a6961746869735f722e6a733f6d6f76653d302220636861727365743d2247424b223e3c2f7363726970743e0d0a202020203c212d2d204a69615468697320427574746f6e20454e44202d2d3e0d0a202020203c62722f3e0d0a093c696e70757420747970653d22627574746f6e222076616c75653d22456262696e6768617573d2c5cdfcc7facfdfd4adc0ed22206f6e636c69636b3d226c6f636174696f6e3d27687474703a2f2f6261696b652e62616964752e636f6d2f766965772f3430303331302e68746d27222f3e200d0a202020203c62722f3e0d0a202020203c62722f3e0d0a202020203c62722f3e200d0a202020203c62722f3e200d0a202020203c696e70757420747970653d22627574746f6e222076616c75653d22cfc2d4d8d7a8c7f822206f6e636c69636b3d226c6f636174696f6e3d27646f776e6c6f61642e68746d6c27222f3e202020200d0a202020203c62722f3e200d0a202020203c62722f3e200d0a202020203c62722f3e200d0a202020203c736372697074207372633d22687474703a2f2f7777772e7a7a736b792e636e2f736572766963652f636f756e742f636f756e742e6173703f757365723d6a65726f6d653032392220636861727365743d2247424b223e3c2f7363726970743e2020200d0a202020203c62722f3e0d0a202020203c62722f3e0d0a202020203c736372697074207372633d22687474703a2f2f7332352e636e7a7a2e636f6d2f737461742e7068703f69643d34303538333637267765625f69643d343035383336372673686f773d7069633122206c616e67756167653d224a617661536372697074223e3c2f7363726970743e0d0a202020203c62722f3e0d0a202020203c62722f3e0d0a202020203c666f6e7420636f6c6f723d22677265656e223e436f707972696768742026636f70793b2032303131202d2032303134203c6120687265663d22696e74726f64756374696f6e2e68746d6c223e4f736d6f6e643c2f613e2e20416c6c205269676874732052657365727665643c2f666f6e743e0d0a3c2f63656e7465723e0d0a0d0a3c2f626f64793e0d0a3c2f68746d6c3e";
        //byte[] srcData = toBytes(srcDataHex);
        //byte[] unGzipData = unGzip(srcData);
        //String unGzipDataHex = bytesToHexString(unGzipData);
        //System.out.println(unGzipDataHex);
        //System.out.println(rstDataHex);
        //System.out.println(unGzipDataHex.equalsIgnoreCase(rstDataHex));
        //System.out.println(new String(unGzipData, "GBK"));

        //String srcDataHex2 = "3465340D0A1F8B08000000000000038C565D6FD446147D0689FF304C55F2E2F56C3E4808AC4D49B27C17509A0AF18466EDC19EC4F66CC6E35D12F5CF04A92AAA404D9A120AA450509B9400252488A0A812424295AA0AA1F6A1A2511F3A33F66E9C6493F6C5BE339E7BE6DC7BCFDC7169EFC0D9FEA10BE7CAE0F8D0C7A7C1B94FFB4E9FE807B080D0F9CE7E84068606D20F5D66B11D0C711CC5545016E100A1F219080080BE10D58308D5EB75B3DE6932EEA1A141E48B30E842016331315DE1427BCF6E00F6ECDEB3BBA4BED8A945B09B5A211118289C02194D68CD820E8B048944418C550904D9C882825C161AFA10707CCC6322AC637DA720401A3E858970482C788C448463C178E6ED48EF93B8863F7138AD0AA3EA570D1C570D8505B7389F2A5F38CFB81BE77CCB950A8D3C1F27B1E18C3901012109191F3340ECF8C44D0262009C081662411D0378E9EE324FA0CA99C771684C5E9BFAEBF6DCA35BC6ECFCD29B17BFCCAD1A4B3F3EF9677EEDE92B3DA38D67D7EFDC5403E3DB578B5F6863616AF6CDD45B63E5F9CC57C6C3B5E987C6F4E3C53F8DE96BCD95B3F3373EFF66C178F4C3FDBF676796AF2CBF33F4F8DEECB34565A8D82626F2B10D94639D02C9AD6578201F5E33BAF5E0768AADC12A65AD482BCE8AF2561E4712E16FA8CEF1A4426442EFCC2DFF61DC7F7F7BF9EEF2C23D6883CD7E83ACC244AE3200E220D0EBA48E041501B1D783D99E5061616569BA845207ED8BD6E55861EE18A8780E0B18B7E0073D478F1E1DE882E93797D6000EA817599C7ABE50DA295D925C40B6BA122452B15A52525452D59C5CB2208D5C72D994AA8376BA31568C7795E22A8EECCF4A48BF41E6A4E0EC866B5BC03C1A29D736FBC6FC8359E5BAD16DEBCAC362D8E2C4A3B120BCCD5EB9F2F5EB165E486FB36B13D1DC611E1F8F47C64C27425E426251616C041D4E62C2AD61C259488A1DBDD09E7CF7624D616F1B4B0337C43410EC60474F4F777B6FF781DE8F46474D8785D0BEF572793545001925550B99659D6E69A77205018EBC047B247F8C55494A7B0B05B50EA14241BBA6EB336F397189F110604709D682C4D74590F296EA732D5865718AE2C8FE42B8AD3867CCFD4E7B6B61ED8999FFA3AE899B592C52559D0D2DF81CD4A92B7C0B76173F848DD90A472D4C1A55130154F7931B2742A8C35AC3928105171F4CFEFAD3EFF756216091135067C4820173F481B4DA647069886D10D9CD507640BBFB7007B40E94E2A92EA90155997700FB7969E9CB273373538F5F7F7775E9B79690352608CAB064C1B3B4ABBAA93AA547AC39D9CCCA7A55D251EB8CB65C23E5014E523CE4D318F469BAA0AF7CECC419A0E5A297640A4B43D257CCB094583A0B41CC9DE6B1A87598C3140B89A5C48B1CE612944D5CE4E6707C386435621561F37682EA7AB273A2DC8E53F9CC408E51AA841D73BD2EC3C63DF2FCFAF76F5BE63C235FC1748498F2E9269A7D8D923A";
        String srcDataHex2 = "1F8B08000000000000038C565D6FD446147D0689FF304C55F2E2F56C3E4808AC4D49B27C17509A0AF18466EDC19EC4F66CC6E35D12F5CF04A92AAA404D9A120AA450509B9400252488A0A812424295AA0AA1F6A1A2511F3A33F66E9C6493F6C5BE339E7BE6DC7BCFDC7169EFC0D9FEA10BE7CAE0F8D0C7A7C1B94FFB4E9FE807B080D0F9CE7E84068606D20F5D66B11D0C711CC5545016E100A1F219080080BE10D58308D5EB75B3DE6932EEA1A141E48B30E842016331315DE1427BCF6E00F6ECDEB3BBA4BED8A945B09B5A211118289C02194D68CD820E8B048944418C550904D9C882825C161AFA10707CCC6322AC637DA720401A3E858970482C788C448463C178E6ED48EF93B8863F7138AD0AA3EA570D1C570D8505B7389F2A5F38CFB81BE77CCB950A8D3C1F27B1E18C3901012109191F3340ECF8C44D0262009C081662411D0378E9EE324FA0CA99C771684C5E9BFAEBF6DCA35BC6ECFCD29B17BFCCAD1A4B3F3EF9677EEDE92B3DA38D67D7EFDC5403E3DB578B5F6863616AF6CDD45B63E5F9CC57C6C3B5E987C6F4E3C53F8DE96BCD95B3F3373EFF66C178F4C3FDBF676796AF2CBF33F4F8DEECB34565A8D82626F2B10D94639D02C9AD6578201F5E33BAF5E0768AADC12A65AD482BCE8AF2561E4712E16FA8CEF1A4426442EFCC2DFF61DC7F7F7BF9EEF2C23D6883CD7E83ACC244AE3200E220D0EBA48E041501B1D783D99E5061616569BA845207ED8BD6E55861EE18A8780E0B18B7E0073D478F1E1DE882E93797D6000EA817599C7ABE50DA295D925C40B6BA122452B15A52525452D59C5CB2208D5C72D994AA8376BA31568C7795E22A8EECCF4A48BF41E6A4E0EC866B5BC03C1A29D736FBC6FC8359E5BAD16DEBCAC362D8E2C4A3B120BCCD5EB9F2F5EB165E486FB36B13D1DC611E1F8F47C64C27425E426251616C041D4E62C2AD61C259488A1DBDD09E7CF7624D616F1B4B0337C43410EC60474F4F777B6FF781DE8F46474D8785D0BEF572793545001925550B99659D6E69A77205018EBC047B247F8C55494A7B0B05B50EA14241BBA6EB336F397189F110604709D682C4D74590F296EA732D5865718AE2C8FE42B8AD3867CCFD4E7B6B61ED8999FFA3AE899B592C52559D0D2DF81CD4A92B7C0B76173F848DD90A472D4C1A55130154F7931B2742A8C35AC3928105171F4CFEFAD3EFF756216091135067C4820173F481B4DA647069886D10D9CD507640BBFB7007B40E94E2A92EA90155997700FB7969E9CB273373538F5F7F7775E9B79690352608CAB064C1B3B4ABBAA93AA547AC39D9CCCA7A55D251EB8CB65C23E5014E523CE4D318F469BAA0AF7CECC419A0E5A297640A4B43D257CCB094583A0B41CC9DE6B1A87598C3140B89A5C48B1CE612944D5CE4E6707C386435621561F37682EA7AB273A2DC8E53F9CC408E51AA841D73BD2EC3C63DF2FCFAF76F5BE63C235FC1748498F2E9269A7D8D923AEA2A163BDB8BAAC28DA26C2F4A9DD716F60E2C9F3E78FE72E5EAFCFB96BC5C568F0286DD757D6DAAF37FD859DDF205DAD0B764AFAA5187C8322591489FA6BCF43737B1ED8AB591CC56B3C5F671C77EB9F1F8B8CE6F2CB0D0DD98BA565771FF81CEEE9E7D7552B9981BC63EAB5B55EAB4C36DFADB66E5B466926F531E272482763FAB8EE90B0AEC73A479087414DBDB4141BDBAF2D792E0CC4D747334D37FA1B371C822577564131C090230A8406230485436899BEBD1CD63AA06EACAD4B3FAF7EE5F000000FFFF0D0A610D0A0300F4BC7A12550A00000D0A300D0A0D0A";
        byte[] srcData2 = NetworkUtils.toBytes(srcDataHex2);
        byte[] unGzipData2 = NetworkUtils.unGzip(srcData2);
        String unGzipDataHex2 = NetworkUtils.bytesToHexString(unGzipData2);
        System.out.println(srcDataHex2);
        System.out.println(unGzipDataHex2);
        System.out.println(new String(unGzipData2, "GBK"));
    }

    public void testData(byte[] data) {
        String str;
        try {
            String parsedStr = new String(data);
            System.out.println("-------- Default --------: " + parsedStr);
            if (parsedStr.contains("HTTP/1.1")) {
                str = parsedStr;
            }

            parsedStr = new String(data, "ASCII");
            System.out.println("-------- ASCII --------: " + parsedStr);
            if (parsedStr.contains("HTTP/1.1")) {
                str = parsedStr;
            }

            parsedStr = new String(data, "UTF-8");
            System.out.println("-------- UTF-8 --------: " + parsedStr);
            if (parsedStr.contains("HTTP/1.1")) {
                str = parsedStr;
            }

            parsedStr = new String(data, "GB2312");
            System.out.println("-------- GB2312 --------: " + parsedStr);
            if (parsedStr.contains("HTTP/1.1")) {
                str = parsedStr;
            }

            parsedStr = new String(data, "GBK");
            System.out.println("-------- GBK --------: " + parsedStr);
            if (parsedStr.contains("HTTP/1.1")) {
                str = parsedStr;
            }

            str = new String(data, "Unicode");
            System.out.println("-------- Unicode --------: " + parsedStr);
            if (parsedStr.contains("HTTP/1.1")) {
                str = parsedStr;
            }

            System.out.println("data--------: " + str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
