package com.goldenage.podcast.nakedcast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.goldenage.podcast.nakedcast.model.Podcast
import com.thoughtworks.xstream.XStream
import rx.Observable
import rx.functions.Func0
import rx.schedulers.Schedulers
import java.net.URL
import java.util.Collections

val EXTRA_FEED_URL = "feed_url"
fun getIntent(activity: Activity, feedUrl: String): Intent {
    val intent = Intent(activity, javaClass<PodcastFeedActivity>())
    intent.putExtra(EXTRA_FEED_URL, feedUrl)
    return intent
}

class PodcastFeedActivity : AppCompatActivity() {
    var feedUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedUrl = getIntent().getStringExtra(EXTRA_FEED_URL)
        if (feedUrl == null) {
            finish()
            return
        }
        
        val url = feedUrl;
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map({url: String -> parse(url)})
                .doOnNext({println("Done")})
                .subscribe()
    }

    private fun parse(url: String?): Podcast {
        val xStream = XStream()
        xStream.alias("item", javaClass<Podcast.Item>())
        val podcast = xStream.fromXML(URL(url)) as Podcast
        return podcast
    }

    private class SearchResultHolder {
        val titleView: TextView
        val descriptionView: TextView
        constructor(view: View) {
            titleView = view.findViewById(R.id.search_result_title) as TextView
            descriptionView = view.findViewById(R.id.search_result_description) as TextView
        }
    }

    private class PodcastItemAdapter : BaseAdapter {

        private var activity: Activity
        private var items: List<Podcast.Item> = Collections.emptyList()

        constructor(activity: Activity) {
            this.activity = activity;
        }

        fun setItems(items: List<Podcast.Item>) {
            this.items = items;
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return items.size()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var result: View
            var holder: PodcastFeedActivity.SearchResultHolder
            if (convertView == null) {
                result = activity.getLayoutInflater().inflate(R.layout.search_result_row, parent, false)
                holder = SearchResultHolder(result)
                result.setTag(holder)
            } else {
                result = convertView
                holder = convertView.getTag() as SearchResultHolder
            }
            val podcast: Podcast.Item = getItem(position)
            holder.titleView.setText(podcast.title)
            holder.descriptionView.setText(podcast.description)
            return result
        }

        override fun getItem(position: Int): Podcast.Item {
            return items.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

    }
}