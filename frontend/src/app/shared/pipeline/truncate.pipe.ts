/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'truncate'
})
export class TruncatePipe implements PipeTransform {
    transform(value: string, limit = 25, completeWords = false, ellipsis = '...') {
        if (!value || value.length == 0)
            return '';
        if (completeWords) {
            limit = value.substr(0, limit).lastIndexOf(' ');
        }
        return `${value.substr(0, limit)}${ellipsis}`;
    }
}
