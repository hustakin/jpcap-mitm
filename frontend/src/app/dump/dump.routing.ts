/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Routes} from "@angular/router";
import {DumpComponent} from "./dump.component";

export const DumpRoutes: Routes = [
    {
        path: '',
        children: [{
            path: 'dump',
            component: DumpComponent
        }]
    }
];
