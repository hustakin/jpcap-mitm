/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {AfterViewInit, Component, OnInit} from "@angular/core";
import {
    AnalysisContentTypeStatistic,
    AnalysisStatistic,
    AnalysisTimelineStatistic,
    HttpContentType,
    PacketStatisticFilterParams,
    Protocol,
    Result
} from "../model/common";
import {AnalysisService} from "../service/analysis.service";
import {StatisticService} from "../service/statistic.service";

@Component({
    selector: 'app-statistic',
    templateUrl: './statistic.component.html'
})
export class StatisticComponent implements OnInit, AfterViewInit {
    private isAnalyzing: boolean = false;

    private filterParams: PacketStatisticFilterParams;
    private packetsStatistic: AnalysisStatistic;
    private packetsContentTypeStatistic: AnalysisContentTypeStatistic;
    private analysisTimelineStatistic: AnalysisTimelineStatistic;

    private toSelectBatchIds: any[];
    private selectedBatchIds: number[];

    private upstreamProtocolPieHasData: boolean = false;
    private upstreamProtocolPieChartOption: any;
    private downstreamProtocolPieHasData: boolean = false;
    private downstreamProtocolPieChartOption: any;

    private upstreamContentTypePieHasData: boolean = false;
    private upstreamContentTypePieChartOption: any;
    private downstreamContentTypePieHasData: boolean = false;
    private downstreamContentTypePieChartOption: any;

    private upstreamTimelineHasData: boolean = false;
    private upstreamTimelineChartOption: any;
    private downstreamTimelineHasData: boolean = false;
    private downstreamTimelineChartOption: any;

    private PROTOCOLS: string[] = [Protocol.HTTP, Protocol.HTTPS, Protocol.TCP, Protocol.UDP, Protocol.ICMP, Protocol.ARP];
    private CONTENT_TYPES: string[] = [HttpContentType.HTML, HttpContentType.PLAIN, HttpContentType.XML, HttpContentType.JSON, HttpContentType.ATTACHMENT,
        HttpContentType.WEBP, HttpContentType.JPEG, HttpContentType.MP4, HttpContentType.FLV, HttpContentType.OTHER];

    constructor(private analysisService: AnalysisService,
                private statisticService: StatisticService) {
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

    statisticPackets() {
        this.filterParams = {
            batchIds: this.selectedBatchIds
        };
        this.statisticService.statisticPackets(this.filterParams)
            .subscribe((analysisStatistic: AnalysisStatistic) => {
                this.packetsStatistic = analysisStatistic;

                this.displayProtocolUpstreamPieChart(analysisStatistic);
                this.displayProtocolDownstreamPieChart(analysisStatistic);
            });
        this.statisticService.statisticPacketsContentType(this.filterParams)
            .subscribe((analysisContentTypeStatistic: AnalysisContentTypeStatistic) => {
                this.packetsContentTypeStatistic = analysisContentTypeStatistic;

                this.displayContentTypeUpstreamPieChart(analysisContentTypeStatistic);
                this.displayContentTypeDownstreamPieChart(analysisContentTypeStatistic);
            });
        this.statisticService.statisticTimelinePackets(this.filterParams)
            .subscribe((analysisTimelineStatistic: AnalysisTimelineStatistic) => {
                this.analysisTimelineStatistic = analysisTimelineStatistic;

                this.displayContentTypeUpstreamTimelineChart(analysisTimelineStatistic);
                this.displayContentTypeDownstreamTimelineChart(analysisTimelineStatistic);
            });

    }

    displayContentTypeUpstreamTimelineChart(analysisTimelineStatistic: AnalysisTimelineStatistic) {
        if (analysisTimelineStatistic) {
            this.upstreamTimelineHasData = true;
            this.upstreamTimelineChartOption = this.optionOfTimelineChart(analysisTimelineStatistic.timeTitle, analysisTimelineStatistic.upFirstMinute, analysisTimelineStatistic.upstreamData);
        }
    }

    displayContentTypeDownstreamTimelineChart(analysisTimelineStatistic: AnalysisTimelineStatistic) {
        if (analysisTimelineStatistic) {
            this.downstreamTimelineHasData = true;
            this.downstreamTimelineChartOption = this.optionOfTimelineChart(analysisTimelineStatistic.timeTitle, analysisTimelineStatistic.downFirstMinute, analysisTimelineStatistic.downstreamData);
        }
    }

    displayProtocolUpstreamPieChart(analysisStatistic: AnalysisStatistic) {
        this.upstreamProtocolPieHasData = true;
        let pieData = [
            {
                "value": analysisStatistic.upHttpNum,
                "name": Protocol.HTTP
            },
            {
                "value": analysisStatistic.upHttpsNum,
                "name": Protocol.HTTPS
            },
            {
                "value": analysisStatistic.upTcpNum,
                "name": Protocol.TCP
            },
            {
                "value": analysisStatistic.upUdpNum,
                "name": Protocol.UDP
            },
            {
                "value": analysisStatistic.upIcmpNum,
                "name": Protocol.ICMP
            },
            {
                "value": analysisStatistic.upArpNum,
                "name": Protocol.ARP
            }
        ];
        this.upstreamProtocolPieChartOption = this.optionOfPieChart('﻿Distribution of Upstream Protocol', 'Protocol', this.PROTOCOLS, pieData);
    }

    displayProtocolDownstreamPieChart(analysisStatistic: AnalysisStatistic) {
        this.downstreamProtocolPieHasData = true;
        let pieData = [
            {
                "value": analysisStatistic.downHttpNum,
                "name": Protocol.HTTP
            },
            {
                "value": analysisStatistic.downHttpsNum,
                "name": Protocol.HTTPS
            },
            {
                "value": analysisStatistic.downTcpNum,
                "name": Protocol.TCP
            },
            {
                "value": analysisStatistic.downUdpNum,
                "name": Protocol.UDP
            },
            {
                "value": analysisStatistic.downIcmpNum,
                "name": Protocol.ICMP
            },
            {
                "value": analysisStatistic.downArpNum,
                "name": Protocol.ARP
            }
        ];
        this.downstreamProtocolPieChartOption = this.optionOfPieChart('﻿Distribution of Downstream Protocol', 'Protocol', this.PROTOCOLS, pieData);
    }

    displayContentTypeUpstreamPieChart(analysisContentTypeStatistic: AnalysisContentTypeStatistic) {
        this.upstreamContentTypePieHasData = true;
        let pieData = [
            {
                "value": analysisContentTypeStatistic.upHtmlNum,
                "name": HttpContentType.HTML
            },
            {
                "value": analysisContentTypeStatistic.upPlainNum,
                "name": HttpContentType.PLAIN
            },
            {
                "value": analysisContentTypeStatistic.upXmlNum,
                "name": HttpContentType.XML
            },
            {
                "value": analysisContentTypeStatistic.upJsonNum,
                "name": HttpContentType.JSON
            },
            {
                "value": analysisContentTypeStatistic.upAttachmentNum,
                "name": HttpContentType.ATTACHMENT
            },
            {
                "value": analysisContentTypeStatistic.upWebpNum,
                "name": HttpContentType.WEBP
            },
            {
                "value": analysisContentTypeStatistic.upJpegNum,
                "name": HttpContentType.JPEG
            },
            {
                "value": analysisContentTypeStatistic.upMp4Num,
                "name": HttpContentType.MP4
            },
            {
                "value": analysisContentTypeStatistic.upFlvNum,
                "name": HttpContentType.FLV
            },
            {
                "value": analysisContentTypeStatistic.upOtherNum,
                "name": HttpContentType.OTHER
            },
        ];
        this.upstreamContentTypePieChartOption = this.optionOfPieChart('﻿Distribution of Upstream Content Type', 'Content Type', this.CONTENT_TYPES, pieData);
    }

    displayContentTypeDownstreamPieChart(analysisContentTypeStatistic: AnalysisContentTypeStatistic) {
        this.downstreamContentTypePieHasData = true;
        let pieData = [
            {
                "value": analysisContentTypeStatistic.downHtmlNum,
                "name": HttpContentType.HTML
            },
            {
                "value": analysisContentTypeStatistic.downPlainNum,
                "name": HttpContentType.PLAIN
            },
            {
                "value": analysisContentTypeStatistic.downXmlNum,
                "name": HttpContentType.XML
            },
            {
                "value": analysisContentTypeStatistic.downJsonNum,
                "name": HttpContentType.JSON
            },
            {
                "value": analysisContentTypeStatistic.downAttachmentNum,
                "name": HttpContentType.ATTACHMENT
            },
            {
                "value": analysisContentTypeStatistic.downWebpNum,
                "name": HttpContentType.WEBP
            },
            {
                "value": analysisContentTypeStatistic.downJpegNum,
                "name": HttpContentType.JPEG
            },
            {
                "value": analysisContentTypeStatistic.downMp4Num,
                "name": HttpContentType.MP4
            },
            {
                "value": analysisContentTypeStatistic.downFlvNum,
                "name": HttpContentType.FLV
            },
            {
                "value": analysisContentTypeStatistic.downOtherNum,
                "name": HttpContentType.OTHER
            },
        ];
        this.downstreamContentTypePieChartOption = this.optionOfPieChart('Distribution of Downstream Content Type', 'Content Type', this.CONTENT_TYPES, pieData);
    }

    onFilterClick() {
        this.statisticPackets();
    }

    onChartInit(echartsIntance) {
        // this.timelineEchartsIntance = echartsIntance;
        echartsIntance.on('updateAxisPointer', function (event) {
            let xAxisInfo = event.axesInfo[0];
            if (xAxisInfo) {
                let dimension = xAxisInfo.value + 1;
                echartsIntance.setOption({
                    series: {
                        id: 'pie',
                        label: {
                            formatter: '{b}: {@[' + dimension + ']} ({d}%)'
                        },
                        encode: {
                            value: dimension,
                            tooltip: dimension
                        }
                    }
                });
            }
        });
    }

    optionOfTimelineChart(title: string, firstMinute: string, data: any) {
        let option = {
            legend: {
                orient: 'vertical',
                left: 0,
                top: 30,
                bottom: 20,
                textStyle: {
                    color: '#2ca7c1'
                },
                inactiveColor: 'rgba(255, 255, 255, 0.3)'
            },
            tooltip: {
                trigger: 'axis',
            },
            dataset: {
                source: data
            },
            xAxis: {type: 'category'},
            yAxis: {gridIndex: 0},
            grid: {top: '55%'},
            series: [
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {
                    type: 'pie',
                    id: 'pie',
                    radius: '30%',
                    center: ['50%', '25%'],
                    itemStyle: this.getItemStyle(),
                    label: {
                        formatter: '{b}: {@' + firstMinute + '} ({d}%)'
                    },
                    encode: {
                        itemName: title,
                        value: firstMinute,
                        tooltip: firstMinute
                    }
                }
            ]
        };
        return option;
    };

    optionOfPieChart(title: string, tooltipTitle: string, categories: any, data: any) {
        let option = {
            title: {
                text: title,
                subtext: '',
                x: 'center',
                textStyle: {
                    color: '#d9e160'
                }
            },
            tooltip: {
                trigger: 'item',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                orient: 'vertical',
                left: 0,
                top: 30,
                bottom: 20,
                data: categories,  //categories
                textStyle: {
                    color: '#2ca7c1'
                },
                inactiveColor: 'rgba(255, 255, 255, 0.3)'
            },
            calculable: true,
            series: [
                {
                    name: tooltipTitle,
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', 225],
                    data: data,  //datas
                    itemStyle: this.getItemStyle(),
                    label: this.getLabelStyle(),
                    labelLine: this.getLabelLineStyle(),
                    textStyle: this.getTextStyle()
                }
            ]
        };
        return option;
    };

    getItemStyle() {
        let itemStyle = {
            normal: {
                color: function (params) {
                    // build a color map as your need.
                    let colorList = [
                        '#27727B', '#FE8463', '#9BCA63', '#F3A43B', '#60C0DD', '#D7504B', '#F0805A',
                        '#26C0C0', '#C1232B', '#E87C25', '#27c397', '#FCCE10', '#5caae5', '#7822f4', '#fa3c99',
                    ];
                    return colorList[params.dataIndex]
                },
                // 阴影的大小
                shadowBlur: 200,
                // 阴影水平方向上的偏移
                shadowOffsetX: 0,
                // 阴影垂直方向上的偏移
                shadowOffsetY: 0,
                // 阴影颜色
                shadowColor: 'rgba(255, 255, 255, 0.3)'
            },
            emphasis: {
                label: {
                    show: true
                },
                shadowBlur: 200,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        };
        return itemStyle;
    }

    getLabelStyle() {
        let labelStyle = {
            normal: {
                show: true,
                position: 'outer',
                formatter: '{b}: {c} ({d}%)'
            },
        };
        return labelStyle;
    }

    getLabelLineStyle() {
        let labelLine = {
            normal: {
                show: true
            }
        };
        return labelLine;
    }

    getTextStyle() {
        let textStyle = {
            normal: {
                color: 'rgba(255, 255, 255, 0.3)'
            }
        };
        return textStyle;
    }

}
