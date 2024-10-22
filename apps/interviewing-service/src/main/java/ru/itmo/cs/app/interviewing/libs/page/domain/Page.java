package ru.itmo.cs.app.interviewing.libs.page.domain;

import java.util.List;

public interface Page<T>{
    List<T> content();
    int pageNumber();
    int pageSize();
    long totalElements();
}