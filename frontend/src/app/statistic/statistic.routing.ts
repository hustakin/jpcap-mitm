/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Routes} from "@angular/router";
import {StatisticComponent} from "./statistic.component";

export const StatisticRoutes: Routes = [
    {
        path: '',
        children: [{
            path: 'statistic',
            component: StatisticComponent
        }]
    }
];
