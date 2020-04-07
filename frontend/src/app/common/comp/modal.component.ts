/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Component, Input, OnInit} from '@angular/core';

declare const $: any;

@Component({
    selector: 'app-modal',
    templateUrl: './modal.component.html'
})
export class ModalComponent implements OnInit {

    @Input()
    public modalId: string;

    @Input()
    public title: string;

    @Input()
    public description: string;

    cpenFunc: any = null;
    closeFunc: any = null;

    constructor() {

    }

    openModal() {
        $('#' + this.modalId).modal('show');
    }

    closeModal() {
        $('#' + this.modalId).modal('hide');
    }

    onOpen(cpenFunc: any) {
        this.cpenFunc = cpenFunc;
    }

    onClose(closeFunc: any) {
        this.closeFunc = closeFunc;
    }

    ngOnInit(): void {
        $('#' + this.modalId).on('show.bs.modal', () => {
            if (this.cpenFunc)
                this.cpenFunc();
        });

        $('#' + this.modalId).on('hide.bs.modal', () => {
            if (this.closeFunc)
                this.closeFunc();
            this.title = null;
            this.description = null;
        });
    }

}
