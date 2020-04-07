/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {NgModule} from '@angular/core';
import {ModalComponent} from "./comp/modal.component";
import {DetailViewComponent} from "./comp/detail-view.component";
import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TruncatePipe} from "../shared/pipeline/truncate.pipe";
import {SafePipe} from "../shared/pipeline/safe.pipe";
import {ProtocolPipe} from "../shared/pipeline/protocol.pipe";
import {DisableControlDirective} from "../shared/directive/disable-control.directive";
import {MdModule} from "../md/md.module";
import {MaterialModule} from "../app.module";


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MdModule,
        MaterialModule,
    ],
    declarations: [
        ModalComponent,
        DetailViewComponent,
        TruncatePipe,
        SafePipe,
        ProtocolPipe,
        DisableControlDirective
    ],
    providers: [],
    exports: [
        ModalComponent,
        DetailViewComponent,
        TruncatePipe,
        SafePipe,
        ProtocolPipe,
        DisableControlDirective
    ]
})
export class AppCommonModule {
}
