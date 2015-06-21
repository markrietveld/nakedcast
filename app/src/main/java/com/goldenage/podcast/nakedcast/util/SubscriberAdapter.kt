package com.goldenage.podcast.nakedcast.util

import rx.Subscriber

open class SubscriberAdapter<T>() : Subscriber<T>() {
    override fun onCompleted() {
        // do nothing
    }

    override fun onError(e: Throwable?) {
        // do nothing
    }

    override fun onNext(t: T) {
        // do nothing
    }

}