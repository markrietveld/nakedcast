package com.goldenage.podcast.nakedcast.model;

import java.util.List;

public class Podcast {
    List<Item> item;

    public static class Item {
        public String title;
        public String description;
    }
}
