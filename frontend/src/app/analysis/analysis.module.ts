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
import {AnalysisComponent} from "./analysis.component";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";
import {AttackService} from "../service/attack.service";
import {AnalysisRoutes} from "./analysis.routing";
import {NgSelectModule} from "@ng-select/ng-select";
import {AppCommonModule} from "../common/common.module";
import {AnalysisService} from "../service/analysis.service";

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(AnalysisRoutes),
        FormsModule,
        MdModule,
        MaterialModule,
        NgSelectModule,
        NgxDatatableModule,
        AppCommonModule,
    ],
    providers: [
        AttackService,
        AnalysisService,
    ],
    declarations: [
        AnalysisComponent,
    ]
})

export class AnalysisModule {
}
