package com.core.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.core.data.paging.GenericPagingSource
import com.core.domain.model.PageData
import com.core.domain.result.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GenericPagingSourceTest {

    private data class TestPageData(
        override val items: List<String>,
        override val nextCursor: Int?
    ) : PageData<String>

    @Test
    fun `load returns page with data on first page`() = runTest {
        // Given
        val items = listOf("Item1", "Item2", "Item3")
        val pageData = TestPageData(items, nextCursor = 25)
        val loadPage: suspend (Int) -> AppResult<TestPageData> = { cursor ->
            assertEquals(0, cursor)
            AppResult.Success(pageData)
        }
        val pagingSource = GenericPagingSource(loadPage)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(items, pageResult.data)
        assertNull(pageResult.prevKey)
        assertEquals(25, pageResult.nextKey)
    }

    @Test
    fun `load returns page with data on subsequent page`() = runTest {
        // Given
        val items = listOf("Item4", "Item5", "Item6")
        val pageData = TestPageData(items, nextCursor = 75)
        val loadPage: suspend (Int) -> AppResult<TestPageData> = { cursor ->
            assertEquals(50, cursor)
            AppResult.Success(pageData)
        }
        val pagingSource = GenericPagingSource(loadPage)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 50,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(items, pageResult.data)
        assertNull(pageResult.prevKey)
        assertEquals(75, pageResult.nextKey)
    }

    @Test
    fun `load returns page with no next key on last page`() = runTest {
        // Given
        val items = listOf("LastItem1", "LastItem2")
        val pageData = TestPageData(items, nextCursor = null)
        val loadPage: suspend (Int) -> AppResult<TestPageData> = { cursor ->
            assertEquals(100, cursor)
            AppResult.Success(pageData)
        }
        val pagingSource = GenericPagingSource(loadPage)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 100,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(items, pageResult.data)
        assertNull(pageResult.prevKey)
        assertNull(pageResult.nextKey)
    }

    @Test
    fun `load returns page with empty list`() = runTest {
        // Given
        val items = emptyList<String>()
        val pageData = TestPageData(items, nextCursor = null)
        val loadPage: suspend (Int) -> AppResult<TestPageData> = { cursor ->
            assertEquals(0, cursor)
            AppResult.Success(pageData)
        }
        val pagingSource = GenericPagingSource(loadPage)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertTrue(pageResult.data.isEmpty())
        assertNull(pageResult.prevKey)
        assertNull(pageResult.nextKey)
    }

    @Test
    fun `load returns error on AppResult Error`() = runTest {
        // Given
        val errorMessage = "Network error occurred"
        val loadPage: suspend (Int) -> AppResult<TestPageData> = {
            AppResult.Error(message = errorMessage, code = 500)
        }
        val pagingSource = GenericPagingSource(loadPage)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val errorResult = result as PagingSource.LoadResult.Error
        assertEquals(errorMessage, errorResult.throwable.message)
    }

    @Test
    fun `load returns error on exception during load`() = runTest {
        // Given
        val exceptionMessage = "Unexpected error"
        val loadPage: suspend (Int) -> AppResult<TestPageData> = {
            throw RuntimeException(exceptionMessage)
        }
        val pagingSource = GenericPagingSource(loadPage)

        // When & Then
        try {
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )
            fail("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertEquals(exceptionMessage, e.message)
        }
    }

    @Test
    fun `getRefreshKey returns null when anchorPosition is null`() = runTest {
        // Given
        val loadPage: suspend (Int) -> AppResult<TestPageData> = {
            AppResult.Success(TestPageData(emptyList(), null))
        }
        val pagingSource = GenericPagingSource(loadPage)

        val state = PagingState<Int, String>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        // When
        val refreshKey = pagingSource.getRefreshKey(state)

        // Then
        assertNull(refreshKey)
    }
}
