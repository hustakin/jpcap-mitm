/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {tap} from "rxjs/operators";
import swal from "sweetalert2";

@Injectable()
export class HeaderInterceptor implements HttpInterceptor {

    intercept(req: HttpRequest<any>, next: HttpHandler) {
        const authReq = req.clone({
            headers: req.headers.set('X-Requested-By', 'Angular')
        });
        return next.handle(authReq)
            .pipe(
                tap(event => {
                        if (event instanceof HttpResponse && event.status == 200) {
                            if (event.body && event.body['handlerError']) {
                                swal({
                                    title: "Warning",
                                    text: "Unknown error happened: " + event.body,
                                    timer: 2000,
                                    showConfirmButton: false
                                }).catch(swal.noop);
                                throw new Error(JSON.stringify(event.body));
                            }
                        }
                        return event;
                    },
                    error => {
                        if (error instanceof HttpErrorResponse && error.status == 401) {
                            swal({
                                title: "Warning",
                                text: "Unknown error happened: " + error['message'],
                                timer: 2000,
                                showConfirmButton: false
                            }).catch(swal.noop);
                        }
                    })
            );
    }
}
