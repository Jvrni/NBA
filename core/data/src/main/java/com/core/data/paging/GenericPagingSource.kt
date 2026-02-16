package com.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.core.domain.model.PageData
import com.core.domain.result.AppResult

class GenericPagingSource<T : Any, P : PageData<T>>(
    private val loadPage: suspend (page: Int) -> AppResult<P>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentPage = params.key ?: 1

        return when (val result = loadPage(currentPage)) {
            is AppResult.Success -> {
                val pageData = result.data
                LoadResult.Page(
                    data = pageData.items,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (pageData.hasNextPage) currentPage + 1 else null
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

