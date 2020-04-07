/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {MdModule} from "../md/md.module";
import {MaterialModule} from "../app.module";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";
import {NgSelectModule} from "@ng-select/ng-select";
import {AppCommonModule} from "../common/common.module";
import {AnalysisService} from "../service/analysis.service";
import {StatisticService} from "../service/statistic.service";
import {StatisticComponent} from "./statistic.component";
import {StatisticRoutes} from "./statistic.routing";
import {NgxEchartsModule} from "ngx-echarts";

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(StatisticRoutes),
        FormsModule,
        MdModule,
        MaterialModule,
        NgSelectModule,
        NgxDatatableModule,
        AppCommonModule,
        NgxEchartsModule,
    ],
    providers: [
        AnalysisService,
        StatisticService,
    ],
    declarations: [
        StatisticComponent,
    ]
})

export class StatisticModule {
}
