/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Injectable} from '@angular/core';
import {Observable, of, throwError} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {catchError, switchMap} from "rxjs/operators";
import {AttackConfig, AttackStatistic, DumpRecord, NetworkInterface, Result} from "../model/common";
import swal from "sweetalert2";
import {environment} from '../../environments/environment';

@Injectable()
export class AttackService {
    constructor(private http: HttpClient) {
    }

    isDeviceOpened(): Observable<Result> {
        return this.http.get<Result>(`${environment.apiBaseUrl}/api/isDeviceOpened`).pipe(
            switchMap(data => {
                return of(data);
            }),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    getAttackConfig(): Observable<AttackConfig> {
        return this.http.get<Result>(`${environment.apiBaseUrl}/api/getAttackConfig`).pipe(
            switchMap(data => {
                let config: AttackConfig = data.result;
                return of(config);
            }),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    updateConfigAndOpenDevice(req: AttackConfig): Observable<any> {
        return this.http.post<Result>(`${environment.apiBaseUrl}/api/updateConfigAndOpenDevice`, req).pipe(
            switchMap(data => of(data.result)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    startAttack(): Observable<Result> {
        return this.http.get<Result>(`${environment.apiBaseUrl}/api/startAttack`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    stopAttack(): Observable<Result> {
        return this.http.get<Result>(`${environment.apiBaseUrl}/api/stopAttack`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    isAttacking(): Observable<Result> {
        return this.http.get<Result>(`${environment.apiBaseUrl}/api/isAttacking`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    getAttackStatistic(): Observable<AttackStatistic> {
        return this.http.get<AttackStatistic>(`${environment.apiBaseUrl}/api/getAttackStatistic`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    getDevicelist(): Observable<NetworkInterface[]> {
        return this.http.get<NetworkInterface[]>(`${environment.apiBaseUrl}/api/getDevicelist`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    getDumpRecords(): Observable<DumpRecord[]> {
        return this.http.get<DumpRecord[]>(`${environment.apiBaseUrl}/api/getDumpRecords`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    deleteAttackHis(batchId: any): Observable<Result> {
        let req = {
            "batchId": batchId
        };
        return this.http.post<Result>(`${environment.apiBaseUrl}/api/deleteAttackHis`, req).pipe(
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
        }
        else {
            swal({
                title: "Unknown Error",
                text: JSON.stringify(err),
                showConfirmButton: false
            }).catch(swal.noop);
        }
    }
}
