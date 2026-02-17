package com.core.domain.model

interface PageData<T> {
    val items: List<T>
    val nextCursor: Int?
}