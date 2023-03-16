package com.ewida.rickmorti.ui.home.fragments.search.adapters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ewida.rickmorti.api.ApiCalls
import com.ewida.rickmorti.model.common_movie_response.CommonMovie
import com.ewida.rickmorti.model.common_movie_response.CommonMovieResponse
import javax.inject.Inject

class SearchPagingSource @Inject constructor(
    private val apiCalls: ApiCalls,
    private val callId: Int,
    private val query: String? = null,
) : PagingSource<Int, CommonMovie>() {
    
    private lateinit var response: CommonMovieResponse

    override fun getRefreshKey(state: PagingState<Int, CommonMovie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommonMovie> {
        val pageIndex = params.key ?: 1
        return try {
            response = when (callId) {
                1 -> apiCalls.getUpComingMovies(pageIndex)
                2 -> apiCalls.getTrendingMovies("movie", "day", pageIndex)
                3 -> apiCalls.getPopular(pageIndex)
                4 -> apiCalls.getTopRated(pageIndex)
                5 -> apiCalls.getCountryMovies("EG","ar",pageIndex)
                6 -> apiCalls.getCountryMovies("IN","en",pageIndex)
                else -> apiCalls.searchMovie(query!!, pageIndex)
            }
            //response=apiCalls.searchMovie(query,pageIndex)
            val nextPage = if (response.results.isEmpty()) null else pageIndex + 1
            val prevPage = if (pageIndex == 1) null else pageIndex - 1
            LoadResult.Page(
                data = response.results,
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}