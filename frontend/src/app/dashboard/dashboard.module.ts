/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdModule} from '../md/md.module';
import {MaterialModule} from '../app.module';

import {DashboardComponent} from './dashboard.component';
import {DashboardRoutes} from './dashboard.routing';
import {AttackService} from "../service/attack.service";
import {BusyModule} from "angular2-busy";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";
import {NgSelectModule} from "@ng-select/ng-select";
import {AnalysisService} from "../service/analysis.service";
import {AppCommonModule} from "../common/common.module";

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(DashboardRoutes),
        FormsModule,
        ReactiveFormsModule,
        MdModule,
        MaterialModule,
        BusyModule,
        NgSelectModule,
        NgxDatatableModule,
        AppCommonModule,
    ],
    providers: [
        AttackService,
        AnalysisService,
    ],
    declarations: [
        DashboardComponent,
    ]
})

export class DashboardModule {
}
