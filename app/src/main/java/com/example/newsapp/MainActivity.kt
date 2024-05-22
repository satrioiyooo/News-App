package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newsapp.network.NewsService
import com.example.newsapp.adapter.Alladapter
import com.example.newsapp.adapter.Headlinesadapter
import com.example.newsapp.news
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var headlineRV : RecyclerView
    private lateinit var allNewsRV : RecyclerView

    private lateinit var Headlinesadapter: Headlinesadapter
    private lateinit var Alladapter: Alladapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBarActivityMain : ProgressBar

    private lateinit var breakingNewsLayout : LinearLayout
    private lateinit var allNewsLayout: LinearLayout


    val allNewsLayoutManager = LinearLayoutManager(this)

    var pageNum = 1
    var totalAllNews = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        headlineRV = findViewById(R.id.terkinirv)
        allNewsRV = findViewById(R.id.semuaBeritarv)
        swipeRefreshLayout = findViewById(R.id.swipeContainer_ActivityMain)
        breakingNewsLayout = findViewById(R.id.terkiniLayout)
        allNewsLayout = findViewById(R.id.semuaBeritaLayout)
        progressBarActivityMain = findViewById(R.id.progressBar_ActivityMain)

        hideAll()
        getAllNews()
        getHeadLines()
        showAll()

        // untuk refresh dengan swipe
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            getHeadLines()
            getAllNews()
        }

        allNewsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(totalAllNews > allNewsLayoutManager.itemCount && allNewsLayoutManager.findFirstVisibleItemPosition() >= allNewsLayoutManager.itemCount -1) {
                    pageNum++
                    getAllNews()
                }
            }
        })

    }

    private fun showAll(){
        progressBarActivityMain.visibility = View.INVISIBLE
        breakingNewsLayout.visibility = View.VISIBLE
        allNewsLayout.visibility = View.VISIBLE
    }

    private fun hideAll(){
        breakingNewsLayout.visibility = View.INVISIBLE
        allNewsLayout.visibility = View.INVISIBLE
        progressBarActivityMain.visibility = View.VISIBLE
    }
    companion object {
        const val TAG = "client"
    }


    private fun getAllNews(){

        //memanggil object dari NewsInterface

        val news = NewsService.newsInstance.geteverything("keuangan",pageNum)
        news.enqueue(object : Callback<news> {
            override fun onResponse(call: Call<news>, response: Response<news>) {
                val allNews = response.body()
                if (allNews != null) {
                    totalAllNews = allNews.totalResults
                    Alladapter = Alladapter(this@MainActivity)
                    Alladapter.clear()
                    Alladapter.addAll(allNews.articles)
                    allNewsRV.adapter = Alladapter
                    allNewsRV.layoutManager = allNewsLayoutManager

                }
            }

            override fun onFailure(call: Call<news>, t: Throwable) {
                Log.d(TAG, "Failed Fetching News", t)
            }
        })
    }

    private fun getHeadLines(){
        val news = NewsService.newsInstance.getheadlines("id",1)
        news.enqueue(object : Callback<news> {
            override fun onResponse(call: Call<news>, response: Response<news>) {
                val headNews = response.body()
                if (headNews != null) {
                    Headlinesadapter = Headlinesadapter(this@MainActivity)
                    Headlinesadapter.clear()
                    Headlinesadapter.addAll(headNews.articles)
                    headlineRV.adapter = Headlinesadapter
                    headlineRV.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                }
            }

            override fun onFailure(call: Call<news>, t: Throwable) {
                Log.d(TAG, "Failed Fetching News", t)
            }
        })
    }

}
