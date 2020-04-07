/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var Chartist = require('chartist');
(function (ChartType) {
    ChartType[ChartType["Pie"] = 0] = "Pie";
    ChartType[ChartType["Line"] = 1] = "Line";
    ChartType[ChartType["Bar"] = 2] = "Bar";
})(exports.ChartType || (exports.ChartType = {}));
var ChartType = exports.ChartType;
var MdChartComponent = (function () {
    function MdChartComponent() {
    }
    MdChartComponent.prototype.ngOnInit = function () {
        this.chartId = "md-chart-" + MdChartComponent.currentId++;
    };
    MdChartComponent.prototype.ngAfterViewInit = function () {
        switch (this.chartType) {
            case ChartType.Pie:
                new Chartist.Pie("#" + this.chartId, this.chartData, this.chartOptions, this.chartResponsive);
                break;
            case ChartType.Line:
                new Chartist.Line("#" + this.chartId, this.chartData, this.chartOptions, this.chartResponsive);
                break;
            case ChartType.Bar:
                new Chartist.Bar("#" + this.chartId, this.chartData, this.chartOptions, this.chartResponsive);
                break;
        }
    };
    MdChartComponent.currentId = 1;
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "title");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "subtitle");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "chartClass");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "chartType");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "chartData");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "chartOptions");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "chartResponsive");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "footerIconClass");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "footerText");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "legendItems");
    __decorate([
        core_1.Input()
    ], MdChartComponent.prototype, "withHr");
    MdChartComponent = __decorate([
        core_1.Component({
            selector: 'app-md-chart',
            templateUrl: './md-chart.component.html',
            styleUrls: ['./md-chart.component.css']
        })
    ], MdChartComponent);
    return MdChartComponent;
}());
exports.MdChartComponent = MdChartComponent;
//# sourceMappingURL=md-chart.component.js.map
