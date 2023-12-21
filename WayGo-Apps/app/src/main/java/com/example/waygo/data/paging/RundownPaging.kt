package com.example.waygo.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.waygo.data.response.AllTouristSpotsItem
import com.example.waygo.data.response.RundownItem
import com.example.waygo.data.retrofit.ApiService
import com.example.waygo.data.retrofit.MlService

class RundownPaging  (private val mlService: MlService) : PagingSource<Int, RundownItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RundownItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = mlService.getRundown(position, params.loadSize).rundown

            LoadResult.Page(
                data = response,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (response.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RundownItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}