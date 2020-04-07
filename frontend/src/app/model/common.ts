/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

export interface Result {
    result: any;
}

export interface AttackConfig {
    deviceName: string;
    srcIp: string;
    srcMac: string;
    destIp: string;
    destMac: string;
    gateIp: string;
    gateMac: string;
    filterDomain: string;
}

export interface NetworkInterface {
    name: string;
    description: string;
    datalinkName: string;
    datalinkDescription: string;
}

export interface IPPacket {
    version: number;
    priority: number;
    d_flag: boolean;
    t_flag: boolean;
    r_flag: boolean;
    rsv_tos: number;
    length: number;
    rsv_frag: boolean;
    dont_frag: boolean;
    more_frag: boolean;
    offset: number;
    hop_limit: number;
    protocol: number;
    ident: number;
    flow_label: number;
    src_ip: any;
    dst_ip: any;
    option: number[];
    options: any[];
    updated: string;
}

export interface OriginalPacket {
    //Packet
    capLen: number;
    len: number;
    hexHeader: string;
    headerLen: number;
    hexData: string;
    dataLen: number;

    //datalink
    srcMac: string;
    dstMac: string;
    frameType: number;

    //ARP
    hardwareType: number;
    prototype: number;
    hardwareAddressLen: number;
    protocolAddressLen: number;
    operation: number;
    senderHardwareAddress: string;
    senderProtocolAddress: string;
    targetHardwareAddress: string;
    targetProtocolAddress: string;

    //IP
    version: number;
    rsvTos: number;
    srcIp: string;
    dstIp: string;
    option: string;
    priority: number;
    length: number;
    ident: number;
    dontFrag: boolean;
    moreFrag: boolean;
    offset: number;
    hopLimit: number;
    protocol: number; //(TCP = 6; UDP = 17)

    //ICMP
    type: number;
    aliveTime: number;

    //TCP
    sequence: number;
    ackNum: number;
    urg: boolean;
    ack: boolean;
    psh: boolean;
    rst: boolean;
    syn: boolean;
    fin: boolean;
    window: number;
    urgentPointer: number;
    //TCP or UDP
    srcPort: number;
    dstPort: number;
}

export interface AttackStatistic {
    upStreamNum: number;
    downStreamNum: number;
    upTcpNum: number;
    upUdpNum: number;
    upIcmpNum: number;
    upArpNum: number;
    downTcpNum: number;
    downUdpNum: number;
    downIcmpNum: number;
    downArpNum: number;
}

export interface AnalysisStatistic {
    upStreamNum: number;
    downStreamNum: number;
    upHttpNum: number;
    upHttpsNum: number;
    upTcpNum: number;
    upUdpNum: number;
    upIcmpNum: number;
    upArpNum: number;
    downHttpNum: number;
    downHttpsNum: number;
    downTcpNum: number;
    downUdpNum: number;
    downIcmpNum: number;
    downArpNum: number;
}

export interface AnalysisContentTypeStatistic {
    upHtmlNum: number;
    upPlainNum: number;
    upXmlNum: number;
    upJsonNum: number;
    upAttachmentNum: number;
    upWebpNum: number;
    upJpegNum: number;
    upMp4Num: number;
    upFlvNum: number;
    upOtherNum: number;
    downHtmlNum: number;
    downPlainNum: number;
    downXmlNum: number;
    downJsonNum: number;
    downAttachmentNum: number;
    downWebpNum: number;
    downJpegNum: number;
    downMp4Num: number;
    downFlvNum: number;
    downOtherNum: number;
}

export interface AnalysisTimelineStatistic {
    timeTitle: string;
    upFirstMinute: string;
    downFirstMinute: string;
    upstreamData: any;
    downstreamData: any;
}

export interface DumpRecord {
    batchId: number;
    startAttackTime: any;
    upstreamPackets: string;
    downstreamPackets: string;
}

export enum IPProtocol {
    IPPROTO_ICMP = 1,
    IPPROTO_IGMP = 2,
    IPPROTO_IP = 4,
    IPPROTO_TCP = 6,
    IPPROTO_UDP = 17,
    IPPROTO_IPv6 = 41,
    IPPROTO_HOPOPT = 0,
    IPPROTO_IPv6_Route = 43,
    IPPROTO_IPv6_Frag = 44,
    IPPROTO_IPv6_ICMP = 58,
    IPPROTO_IPv6_NoNxt = 59,
    IPPROTO_IPv6_Opts = 60
}

export enum IPVersion {
    IPv4 = 1,
    IPv6 = 2
}

export enum Protocol {
    HTTP = 'HTTP',
    HTTPS = 'HTTPS',
    TCP = 'TCP',
    UDP = 'UDP',
    ICMP = 'ICMP',
    ARP = 'ARP',
}

export enum HttpContentType {
    HTML = 'HTML',
    PLAIN = 'PLAIN',
    XML = 'XML',
    JSON = 'JSON',
    ATTACHMENT = 'ATTACHMENT',
    WEBP = 'WEBP',
    JPEG = 'JPEG',
    MP4 = 'MP4',
    FLV = 'FLV',

    OTHER = 'OTHER',
}

export interface PacketFilterParams {
    batchIds: number[];
    directions: boolean[];
    protocols: string[];
    contentTypes: string[]; //only for http packets
}

export interface PacketStatisticFilterParams {
    batchIds: number[];
}

export interface Packet {
    id: string;
    ackNum: number;
    batchId: number;
    capturedPacketIds: string[];
    upstream: boolean;
    minimized: boolean; //too large so minimize it
    data: string;
    content: string;
    protocol: string;
    created: any;
    originalPacketNum: number;

    srcIp: string | null;
    destIp: string | null;
    srcMac: string | null;
    destMac: string | null;
    srcPort: number | null;
    destPort: number | null;

    compressed: boolean | null; //http is gzip or not
    contentType: string | null; //http
    httpHeaders: any | null; //http or https
    decrypted: boolean; //https
    httpReq: boolean;
    method: string;
}
