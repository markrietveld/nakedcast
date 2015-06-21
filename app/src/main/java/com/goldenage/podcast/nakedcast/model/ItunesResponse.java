package com.goldenage.podcast.nakedcast.model;

import java.util.List;

public class ItunesResponse {
    public int resultCount;

    public List<Podcast> results;

    public static class Podcast {
        public String artistName;
        public String feedUrl;
        public String artworkUrl30;
        public String artworkUrl60;
        public String artworkUrl100;
        public String collectionName;
    }
}
