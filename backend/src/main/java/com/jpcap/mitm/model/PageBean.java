/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.model;

import java.io.Serializable;

/**
 * @author Frankie
 */
public class PageBean implements Serializable {
    private Integer pageNumber; //The current page number
    private Integer size; //The number of elements in the page
    private PageSort sort; //The sort column

    private Long totalElements; //The total number of elements
    private Long totalPages; //The total number of pages

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public PageSort getSort() {
        return sort;
    }

    public void setSort(PageSort sort) {
        this.sort = sort;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }
}
