/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import { Pipe, PipeTransform } from '@angular/core';
import {IPProtocol} from "../../model/common";

@Pipe({
  name: 'parseProtocol'
})
export class ProtocolPipe implements PipeTransform {

  transform(value: number, args?: any): any {
      switch(value) {
          case IPProtocol.IPPROTO_TCP: {
              return "TCP";
          }
          case IPProtocol.IPPROTO_IP: {
              return "IP";
          }
          default: {
              return value;
          }
      }
  }

}
