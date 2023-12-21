package com.example.waygo.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.waygo.data.response.AllTouristSpotsItem
import com.example.waygo.data.retrofit.ApiService

class TouristPaging (private val apiService: ApiService) : PagingSource<Int, AllTouristSpotsItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AllTouristSpotsItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getAllTourist(position, params.loadSize).allTouristSpots

            LoadResult.Page(
                data = response,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (response.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, AllTouristSpotsItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}