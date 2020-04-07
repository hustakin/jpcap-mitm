/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Injectable} from '@angular/core';
import {Observable, of, throwError} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {catchError, switchMap} from "rxjs/operators";
import {
    AnalysisContentTypeStatistic,
    AnalysisStatistic,
    AnalysisTimelineStatistic,
    PacketStatisticFilterParams
} from "../model/common";
import swal from "sweetalert2";

@Injectable()
export class StatisticService {
    constructor(private http: HttpClient) {
    }

    statisticPackets(params: PacketStatisticFilterParams): Observable<AnalysisStatistic> {
        return this.http.post<AnalysisStatistic>(`${'/api/statisticPackets'}`, params).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    statisticPacketsContentType(params: PacketStatisticFilterParams): Observable<AnalysisContentTypeStatistic> {
        return this.http.post<AnalysisContentTypeStatistic>(`${'/api/statisticPacketsContentType'}`, params).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    statisticTimelinePackets(params: PacketStatisticFilterParams): Observable<AnalysisTimelineStatistic> {
        return this.http.post<AnalysisTimelineStatistic>(`${'/api/statisticTimelinePackets'}`, params).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    swalError(err) {
        if (err && err.message) {
            swal({
                title: "Unknown Error",
                text: err.message,
                showConfirmButton: false
            }).catch(swal.noop);
        } else {
            swal({
                title: "Unknown Error",
                text: JSON.stringify(err),
                showConfirmButton: false
            }).catch(swal.noop);
        }
    }
}
