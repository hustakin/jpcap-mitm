/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.dto;

import com.jpcap.mitm.model.DataTableHeader;
import com.jpcap.mitm.model.PageBean;

import java.io.Serializable;

/**
 *
 * @author Frankie
 */
public class PagedRecordDTO<T> implements Serializable {
    private PageBean page;
    private DataTableHeader[] headers;
    private T[] datas;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public DataTableHeader[] getHeaders() {
        return headers;
    }

    public void setHeaders(DataTableHeader[] headers) {
        this.headers = headers;
    }

    public T[] getDatas() {
        return datas;
    }

    public void setDatas(T[] datas) {
        this.datas = datas;
    }
}
