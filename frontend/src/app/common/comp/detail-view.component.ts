/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ModalComponent} from "./modal.component";

export interface InformationItem {
    label: string,
    value: any,
}

@Component({
    selector: 'app-detail-view',
    templateUrl: './detail-view.component.html'
})
export class DetailViewComponent implements OnInit {

    @Input()
    public modalId: string;

    @Input()
    public title: string;

    @Input()
    public description: string;

    @Input()
    public informationItems: InformationItem[];

    @ViewChild('detailViewModal')
    detailViewModal: ModalComponent;

    constructor() {

    }

    isBooleanValue(value: any) {
        if (typeof value == 'boolean') {
            return true;
        }
        else {
            return false;
        }
    }

    openModal() {
        this.detailViewModal.openModal();
    }

    ngOnInit(): void {

    }

}
