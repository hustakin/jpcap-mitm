/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Routes} from "@angular/router";
import {AnalysisComponent} from "./analysis.component";

export const AnalysisRoutes: Routes = [
    {
        path: '',
        children: [{
            path: 'analysis',
            component: AnalysisComponent
        }]
    }
];
