/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Routes} from "@angular/router";
import {AdminLayoutComponent} from "./layouts/admin/admin-layout.component";

export const AppRoutes: Routes = [
    {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
    }, {
        path: '',
        component: AdminLayoutComponent,
        children: [
            {
                path: '',
                loadChildren: './dashboard/dashboard.module#DashboardModule'
            },
            {
                path: '',
                loadChildren: './analysis/analysis.module#AnalysisModule'
            },
            {
                path: '',
                loadChildren: './statistic/statistic.module#StatisticModule'
            },
            {
                path: '',
                loadChildren: './dump/dump.module#DumpModule'
            }
        ]
    }
];
