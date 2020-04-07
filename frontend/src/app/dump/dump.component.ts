/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {AfterViewInit, Component, OnInit} from "@angular/core";
import {DumpRecord, Result} from "../model/common";
import {AttackService} from "../service/attack.service";
import swal from "sweetalert2";
import {AnalysisService} from "../service/analysis.service";

declare const $: any;

@Component({
    selector: 'app-dump',
    templateUrl: './dump.component.html'
})
export class DumpComponent implements OnInit, AfterViewInit {

    dumpRecordsLoading: boolean;
    dumpRows: DumpRecord[];

    constructor(private attackService: AttackService,
                private analysisService: AnalysisService) {
    }

    public ngOnInit() {
        this.refreshDumpRecords();
    }

    deleteAttackHis(batchId: any) {
        swal({
            title: 'Are you sure?',
            text: 'You will delete all captured packets of this attack history!',
            type: 'warning',
            showCancelButton: true,
            confirmButtonClass: 'btn btn-success',
            cancelButtonClass: 'btn btn-danger',
            confirmButtonText: 'OK!',
            buttonsStyling: false
        }).then((result) => {
            if (result.value) {
                this.attackService.deleteAttackHis(batchId)
                    .subscribe((data: Result) => {
                        this.refreshDumpRecords();
                        swal({
                            title: "Success",
                            text: "Succeed in deleting the attack history with " + data.result + " packets",
                            timer: 2000,
                            showConfirmButton: false
                        }).catch(swal.noop);
                    });
            }
        });
    }

    beginAnalyzing(batchId: number) {
        this.showNotification('info', 'Begin analyzing, please wait..');
        this.analysisService.analysisByBatchId(batchId)
            .subscribe((data: Result) => {
                console.log('Finish the re-analyzing: ' + data.result);
                this.showNotification('success', 'Analyzing finished.');
            });
    }

    refreshDumpRecords() {
        this.dumpRecordsLoading = true;
        this.attackService.getDumpRecords()
            .subscribe((dumpRows: DumpRecord[]) => {
                this.dumpRows = dumpRows;
                this.dumpRecordsLoading = false;
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

    ngAfterViewInit() {

    }
}
