package com.bigblender;

import java.util.Map;

public class Playlist {
    private String playlistId;
    private String title;
    private int count;

    public Playlist(String playlistId, String title, int count) {
        this.playlistId = playlistId;
        this.title = title;
        this.count = count;
    }

    //creating playlist
    public static Playlist fromMap(Map<String, Object> map) {
        String playlistId = (String) map.get("playlistId");
        String title = (String) map.getOrDefault("title", "Unknown playlist");

        int count = 0;
        Object countObj = map.get("count");

        if (countObj instanceof Number) {
            count = ((Number) countObj).intValue();
        }

        return new Playlist(playlistId, title, count);
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }

    public String getDisplayString() {
        return title + " (" + count + " songs)";
    }

    @Override
    public String toString() {
        return getDisplayString();
    }
}
