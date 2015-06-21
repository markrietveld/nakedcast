package com.goldenage.podcast.nakedcast

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.goldenage.podcast.nakedcast.model.ItunesResponse
import com.goldenage.podcast.nakedcast.network.Itunes
import com.goldenage.podcast.nakedcast.util.SubscriberAdapter
import rx.android.schedulers.AndroidSchedulers
import java.util.Collections

open class SearchActivity : AppCompatActivity() {
    val toolbar: Toolbar by bindView(R.id.search_toolbar)
    val listView: ListView by bindView(R.id.search_listview)
    val searchResultAdapter: SearchResultAdapter = SearchResultAdapter(this@SearchActivity)

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
        
        listView.setAdapter(searchResultAdapter)
    }

    private fun search(query: String) {
        Itunes.client.searchPodcast(query)
                .observeOn(AndroidSchedulers.mainThread())
                .map({ itunesResponse: ItunesResponse -> itunesResponse.results })
                .doOnNext({searchResultAdapter.setItems(it)})
                .subscribe(object: SubscriberAdapter<List<ItunesResponse.Podcast>>() {
                    override fun onNext(itunesReponse: List<ItunesResponse.Podcast>) {
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
    
    private class SearchResultHolder {
        val titleView: TextView
        val descriptionView: TextView
        constructor(view: View) {
            titleView = view.findViewById(R.id.search_result_title) as TextView
            descriptionView = view.findViewById(R.id.search_result_description) as TextView
        }
    }
    
    private class SearchResultAdapter : BaseAdapter {

        private var activity: Activity
        private var items: List<ItunesResponse.Podcast> = Collections.emptyList()

        constructor(activity: Activity) {
            this.activity = activity;
        }

        fun setItems(items: List<ItunesResponse.Podcast>) {
            this.items = items;
            notifyDataSetChanged()
        }
        
        override fun getCount(): Int {
            return items.size()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var result: View
            var holder: SearchResultHolder
            if (convertView == null) {
                result = activity.getLayoutInflater().inflate(R.layout.search_result_row, parent, false)
                holder = SearchResultHolder(result)
                result.setTag(holder)
            } else {
                result = convertView
                holder = convertView.getTag() as SearchResultHolder
            }
            val podcast: ItunesResponse.Podcast = getItem(position)
            holder.titleView.setText(podcast.collectionName)
            holder.descriptionView.setText(podcast.artistName)
            return result
        }

        override fun getItem(position: Int): ItunesResponse.Podcast {
            return items.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

    }
}