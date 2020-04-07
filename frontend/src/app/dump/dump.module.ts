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
import {DumpRoutes} from "./dump.routing";
import {DumpComponent} from "./dump.component";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";
import {AttackService} from "../service/attack.service";
import {AnalysisService} from "../service/analysis.service";

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(DumpRoutes),
        FormsModule,
        MdModule,
        MaterialModule,
        NgxDatatableModule,
    ],
    providers: [
        AttackService,
        AnalysisService,
    ],
    declarations: [DumpComponent]
})

export class DumpModule {
}
