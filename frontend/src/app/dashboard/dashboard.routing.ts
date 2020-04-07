/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import { Routes } from '@angular/router';

import { DashboardComponent } from './dashboard.component';

export const DashboardRoutes: Routes = [
    {

      path: '',
      children: [ {
        path: 'dashboard',
        component: DashboardComponent
    }]
}
];
