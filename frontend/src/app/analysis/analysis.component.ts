/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {OriginalPacket, Packet, PacketFilterParams, Result} from "../model/common";
import {AnalysisService} from "../service/analysis.service";
import {DetailViewComponent, InformationItem} from "../common/comp/detail-view.component";
import {ModalComponent} from "../common/comp/modal.component";

@Component({
    selector: 'app-analysis',
    templateUrl: './analysis.component.html'
})
export class AnalysisComponent implements OnInit, AfterViewInit {
    private isAnalyzing: boolean = false;

    private filterParams: PacketFilterParams;
    private packetDataLoading: boolean;
    private packetRows: Packet[];

    private originalPacketDataLoading: boolean;
    private originalPacketRows: OriginalPacket[];

    private toSelectBatchIds: any[];
    private selectedBatchIds: number[];

    private toSelectDirections: any[] = [{
        viewValue: 'Upstream',
        value: true
    }, {
        viewValue: 'Downstream',
        value: false
    }];
    private selectedDirections: boolean[];

    private toSelectProtocols: any[] = [{
        viewValue: 'HTTP',
        value: 'HTTP'
    }, {
        viewValue: 'HTTPS',
        value: 'HTTPS'
    }, {
        viewValue: 'TCP',
        value: 'TCP'
    }, {
        viewValue: 'UDP',
        value: 'UDP'
    }, {
        viewValue: 'ICMP',
        value: 'ICMP'
    }, {
        viewValue: 'ARP',
        value: 'ARP'
    }];
    private selectedProtocols: string[];

    private toSelectContentTypes: any[] = [{
        viewValue: 'HTML',
        value: 'HTML'
    }, {
        viewValue: 'PLAIN',
        value: 'PLAIN'
    }, {
        viewValue: 'XML',
        value: 'XML'
    }, {
        viewValue: 'JSON',
        value: 'JSON'
    }, {
        viewValue: 'ATTACHMENT',
        value: 'ATTACHMENT'
    }, {
        viewValue: 'WEBP',
        value: 'WEBP'
    }, {
        viewValue: 'JPEG',
        value: 'JPEG'
    }, {
        viewValue: 'MP4',
        value: 'MP4'
    }, {
        viewValue: 'FLV',
        value: 'FLV'
    }];
    private selectedContentTypes: string[];

    @ViewChild('httpHeadersDetailModal')
    httpHeadersDetailModal: DetailViewComponent;
    httpHeadersItems: InformationItem[];

    @ViewChild('dataDetailModal')
    dataDetailModal: ModalComponent;
    dataDetail: string;

    @ViewChild('dataStrDetailModal')
    dataStrDetailModal: ModalComponent;
    dataStrDetail: string;

    selectedAnalyzedPacket = [];

    constructor(private analysisService: AnalysisService) {
    }

    public ngOnInit() {
        this.getAnalyzingStatus();
        this.automaticRefreshAnalysisStatus();
        this.initSelectItems();
    }

    automaticRefreshAnalysisStatus() {
        setTimeout(() => {
            this.refreshAnalysisStatus();
        }, 1500);
    }

    refreshAnalysisStatus() {
        this.getAnalyzingStatus();
        this.automaticRefreshAnalysisStatus();
    }

    getAnalyzingStatus() {
        this.analysisService.isAnalyzing()
            .subscribe((res: Result) => {
                this.isAnalyzing = res.result;
            });
    }

    ngAfterViewInit() {

    }

    initSelectItems() {
        let toSelectBatchIds = [];
        this.analysisService.distinctBatchIds()
            .subscribe((batchIds: number[]) => {
                batchIds && batchIds.forEach((batchId: number) => {
                    toSelectBatchIds.push({
                        value: batchId,
                        viewValue: batchId
                    });
                    this.toSelectBatchIds = toSelectBatchIds;
                });
            });
    }

    searchPackets() {
        this.packetDataLoading = true;
        this.filterParams = {
            batchIds: this.selectedBatchIds,
            directions: this.selectedDirections,
            protocols: this.selectedProtocols,
            contentTypes: this.selectedContentTypes,
        };
        this.analysisService.filterPackets(this.filterParams)
            .subscribe((packets: Packet[]) => {
                packets && packets.forEach((packet: Packet) => {
                    if (packet.capturedPacketIds)
                        packet.originalPacketNum = packet.capturedPacketIds.length;
                });
                this.packetRows = packets;
                this.packetDataLoading = false;
                this.originalPacketRows = []
            });
    }

    onFilterClick() {
        this.searchPackets();
    }

    showHttpHeadersDetail(value) {
        let httpHeadersItems = [];
        let keys = Array.from(Object.keys(value)).sort();
        keys.forEach(key => {
            httpHeadersItems.push({
                label: key,
                value: value[key]
            });
        });
        this.httpHeadersItems = httpHeadersItems;
        this.httpHeadersDetailModal.openModal();
    }

    showDataDetail(value) {
        this.dataDetail = this.bytes2Str(value);
        this.dataDetailModal.openModal();
    }

    showDataStrDetail(value) {
        this.dataStrDetail = value;
        this.dataStrDetailModal.openModal();
    }

    isBooleanValue(value: any) {
        if (typeof value == 'boolean') {
            return true;
        }
        return false;
    }

    bytes2Str(arr: number[]) {
        let str = "";
        if (!arr)
            return str;
        for (let i = 0; i < arr.length; i++) {
            let tmp = arr[i].toString(16);
            if (tmp.length == 1) {
                tmp = "0" + tmp;
            }
            str += tmp;
        }
        return str;
    }

    onSelectAnalyzedPacket({selected}) {
        this.originalPacketDataLoading = true;
        this.analysisService.getOriginalPackets(selected[0]['capturedPacketIds'])
            .subscribe((originalPackets: OriginalPacket[]) => {
                this.originalPacketRows = originalPackets;
                this.originalPacketDataLoading = false;
            });
    }
}
