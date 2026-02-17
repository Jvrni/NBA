package com.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.core.domain.model.PageData
import com.core.domain.result.AppResult

class GenericPagingSource<T : Any, P : PageData<T>>(
    private val loadPage: suspend (page: Int) -> AppResult<P>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val cursor = params.key ?: 0

        return when (val result = loadPage(cursor)) {
            is AppResult.Success -> {
                val pageData = result.data
                val nextKey = pageData.nextCursor

                LoadResult.Page(
                    data = pageData.items,
                    prevKey = null,
                    nextKey = nextKey
                )
            }

            is AppResult.Error -> {
                LoadResult.Error(Exception(result.message))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

