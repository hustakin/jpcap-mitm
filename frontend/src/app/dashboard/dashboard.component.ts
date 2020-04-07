/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {AfterViewInit, Component, OnInit} from "@angular/core";
import "rxjs-compat/add/operator/map";
import "rxjs-compat/add/operator/catch";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AttackService} from "../service/attack.service";
import {AttackConfig, AttackStatistic, NetworkInterface, Result} from "../model/common";
import {AnalysisService} from "../service/analysis.service";

declare const $: any;

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit, AfterViewInit {

    loading: boolean = true;
    attacking: boolean = false;
    automatic: boolean = false;
    deviceStatusLoading: boolean = true;
    deviceOpened: boolean = true;

    settingsForm: FormGroup;

    toSelectDevices = [];
    packetDataLoading: boolean;
    attackStatistic: AttackStatistic;

    constructor(private fb: FormBuilder,
                private attackService: AttackService,
                private analysisService: AnalysisService
    ) {
        this.settingsForm = this.fb.group({
            deviceName: ['', [Validators.required]],
            localIp: ['', [Validators.required]],
            localMac: ['', [Validators.required]],
            targetIp: ['', [Validators.required]],
            targetMac: ['', [Validators.required]],
            gatewayIp: ['', [Validators.required]],
            gatewayMac: ['', [Validators.required]],
            filterDomain: ['', [Validators.required]],
        });
    }

    getDeviceNameControl() {
        return this.settingsForm.get('deviceName');
    }

    getLocalIpControl() {
        return this.settingsForm.get('localIp');
    }

    getLocalMacControl() {
        return this.settingsForm.get('localMac');
    }

    getTargetIpControl() {
        return this.settingsForm.get('targetIp');
    }

    getTargetMacControl() {
        return this.settingsForm.get('targetMac');
    }

    getGatewayIpControl() {
        return this.settingsForm.get('gatewayIp');
    }

    getGatewayMacControl() {
        return this.settingsForm.get('gatewayMac');
    }

    getFilterDomainControl() {
        return this.settingsForm.get('filterDomain');
    }

    onStartAttack() {
        this.loading = true;
        this.attackService.startAttack()
            .subscribe((res: Result) => {
                this.updateStatistic();
                this.attacking = true;
                this.loading = false;
                console.log('Start attack..');
            });
        this.automatic = true;
        this.automaticUpdate();
    }

    onOpenDevice() {
        if (!this.settingsForm.valid)
            return;
        this.deviceStatusLoading = true;
        let config: AttackConfig = {
            deviceName: this.getDeviceNameControl().value,
            srcIp: this.getLocalIpControl().value,
            srcMac: this.getLocalMacControl().value,
            destIp: this.getTargetIpControl().value,
            destMac: this.getTargetMacControl().value,
            gateIp: this.getGatewayIpControl().value,
            gateMac: this.getGatewayMacControl().value,
            filterDomain: this.getFilterDomainControl().value,
        };
        this.attackService.updateConfigAndOpenDevice(config)
            .subscribe((status: any) => {
                if (status) {
                    this.deviceOpened = true;
                } else {
                    this.deviceOpened = false;
                    console.error('Fail in updating attack configs,', status);
                }
                this.deviceStatusLoading = false;
            });
    }

    automaticUpdate(): void {
        if (!this.automatic) return;
        setTimeout(() => this.automaticUpdateStatistic(), 1500);
    }

    onStopAttack() {
        this.loading = true;
        this.attackService.stopAttack()
            .subscribe((res: Result) => {
                this.attacking = false;
                this.loading = false;
                console.log('Stop attack: ' + res.result);

                //begin to analyze packets of this batch
                if (res.result) {
                    this.beginAnalyzing(res.result);
                }
            });
        this.automatic = false;
    }

    getAttackStatus() {
        this.loading = true;
        this.attackService.isAttacking()
            .subscribe((res: Result) => {
                this.attacking = res.result;
                this.loading = false;
            });
    }

    updateDeviceOpenedStatus() {
        this.deviceStatusLoading = true;
        this.attackService.isDeviceOpened()
            .subscribe((data: Result) => {
                this.deviceStatusLoading = false;
                this.deviceOpened = data.result;
            });
    }

    updateAttackConfig() {
        this.attackService.getAttackConfig()
            .subscribe((config: AttackConfig) => {
                this.settingsForm.patchValue({
                    deviceName: config.deviceName,
                    localIp: config.srcIp,
                    localMac: config.srcMac,
                    targetIp: config.destIp,
                    targetMac: config.destMac,
                    gatewayIp: config.gateIp,
                    gatewayMac: config.gateMac,
                    filterDomain: config.filterDomain,
                });
            });
    }

    updateStatistic() {
        this.packetDataLoading = true;
        this.attackService.getAttackStatistic()
            .subscribe((attackStatistic: AttackStatistic) => {
                this.attackStatistic = attackStatistic;
                this.packetDataLoading = false;
            });
    }

    updateDeviceNamesSelect() {
        this.toSelectDevices = [];
        this.attackService.getDevicelist()
            .subscribe((networkInterfaces: NetworkInterface[]) => {
                networkInterfaces && networkInterfaces.forEach((networkInterface: NetworkInterface) => {
                    this.toSelectDevices.push(networkInterface);
                });
                this.toSelectDevices = [...this.toSelectDevices];
            });
    }

    automaticUpdateStatistic() {
        this.packetDataLoading = true;
        this.attackService.getAttackStatistic()
            .subscribe((attackStatistic: AttackStatistic) => {
                this.attackStatistic = attackStatistic;
                this.packetDataLoading = false;
                this.automaticUpdate();
            });
    }

    beginAnalyzing(batchId: number) {
        this.showNotification('info', 'Begin analyzing, please wait..');
        this.analysisService.analysisByBatchId(batchId)
            .subscribe((data: Result) => {
                console.log('Finish the analyzing: ' + data.result);
                this.showNotification('success', 'Analyzing finished.');
            });
    }

    showNotification(type: string, text: string) {
        $.notify({
            icon: 'notifications',
            message: text
        }, {
            type: type,
            timer: 2000,
            placement: {
                from: 'top',
                align: 'right'
            },
            template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0} alert-with-icon" role="alert">' +
                '<button mat-raised-button type="button" aria-hidden="true" class="close" data-notify="dismiss">  <i class="material-icons">close</i></button>' +
                '<i class="material-icons" data-notify="icon">notifications</i> ' +
                '<span data-notify="title">{1}</span> ' +
                '<span data-notify="message">{2}</span>' +
                '<div class="progress" data-notify="progressbar">' +
                '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
                '</div>' +
                '<a href="{3}" target="{4}" data-notify="url"></a>' +
                '</div>'
        });
    }

    public ngOnInit() {
        this.updateDeviceOpenedStatus();
        this.getAttackStatus();
        this.updateAttackConfig();
        this.updateStatistic();
        this.updateDeviceNamesSelect();
    }

    ngAfterViewInit() {

    }

}
