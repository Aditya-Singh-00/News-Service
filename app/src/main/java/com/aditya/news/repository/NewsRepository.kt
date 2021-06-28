package com.aditya.news.repository

import com.aditya.news.db.ArticleDatabase
import com.aditya.news.model.Article
import com.aditya.news.network.MarsApi

class NewsRepository( val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        MarsApi.retrofitService.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        MarsApi.retrofitService.getSearchedNews(searchQuery,pageNumber)

    suspend fun insertArticle(article: Article) = db.getArticleDao().insertArticle(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticle()
}