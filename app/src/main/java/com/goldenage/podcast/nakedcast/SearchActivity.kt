package com.goldenage.podcast.nakedcast

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.widget.Toast
import butterknife.bindView
import com.goldenage.podcast.nakedcast.model.ItunesResponse
import com.goldenage.podcast.nakedcast.network.Itunes
import com.goldenage.podcast.nakedcast.util.SubscriberAdapter
import rx.android.schedulers.AndroidSchedulers

open class SearchActivity : AppCompatActivity() {
    val toolbar: Toolbar by bindView(R.id.search_toolbar)

    protected override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_screen)

        val inflater = getMenuInflater()
        inflater.inflate(R.menu.search, toolbar.getMenu());
        val searchView = toolbar.getMenu().findItem(R.id.search).getActionView() as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()))

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationContentDescription(R.string.abc_action_bar_home_description);
        toolbar.setNavigationOnClickListener({onBackPressed()})
        
        searchView.setSubmitButtonEnabled(true)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val toast = Toast.makeText(this@SearchActivity, "Searching: " + query, Toast.LENGTH_SHORT)
                toast.show()
                search(query)
                return false
            }
        })
    }

    private fun search(query: String) {
        Itunes.client.searchPodcast(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SubscriberAdapter<ItunesResponse>() {
                    override fun onNext(itunesReponse: ItunesResponse) {
                        val toast = Toast.makeText(this@SearchActivity, "Got result", Toast.LENGTH_SHORT)
                        toast.show()
                    }
    
                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        val toast = Toast.makeText(this@SearchActivity, "Error: " + e, Toast.LENGTH_SHORT)
                        toast.show()
                    }
                })
    }
}