package com.aditya.news.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.news.model.Article
import com.aditya.news.model.NewsResponse
import com.aditya.news.repository.NewsRepository
import com.aditya.news.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class NewsViewModel(private val newsRepository: NewsRepository): ViewModel() {

    companion object {
        private const val TAG = "NewsViewModel"
    }

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        try {
            breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
        } catch (e: Exception) {
            Log.e(TAG,"Error Occurred : ${e.localizedMessage}")
            breakingNews.postValue(Resource.Error(e.localizedMessage!!))
        }
    }

    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        try {
            searchNews.postValue(Resource.Loading())
            val response = newsRepository.searchNews(searchQuery,searchNewsPage)
            searchNews.postValue(handleSearchNewsResponse(response))

        } catch (e: Exception) {
            Log.e(TAG,"Error Occurred : ${e.localizedMessage}")
            breakingNews.postValue(Resource.Error(e.localizedMessage!!))
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if(breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if(searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()
}