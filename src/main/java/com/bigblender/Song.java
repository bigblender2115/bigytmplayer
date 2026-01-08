package com.bigblender;

import java.util.Map;
import java.util.List;

public class Song {
    private String videoId;
    private String title;
    private String artist;
    private String album;
    private int duration;

    public Song(String videoId, String title, String artist, String album, int duration) {
        this.videoId = videoId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    @SuppressWarnings("unchecked")
    public static Song fromMap(Map<String, Object> map) {
        //extracts videoId and title from ytmusicapi response
        String videoId = (String) map.get("videoId");
        String title = (String) map.getOrDefault("title", "Unknown");

        //extracting artists
        String artist = "Unknown artist";
        Object artistsObj = map.get("artists");

        if(artistsObj instanceof List) {
            List<Map<String, Object>> artists = (List<Map<String, Object>>) artistsObj;
            if(!artists.isEmpty()) {
                artist = (String) artists.get(0).getOrDefault("name", "Unknown");
            }
        }

        //extracting album name
        String album = "";
        Object albumObj = map.get("album");

        if(albumObj instanceof Map) {
            Map<String, Object> albumMap = (Map<String, Object>) albumObj;
            album = (String) albumMap.getOrDefault("name", "");
        }

        //duration
        int duration = 0;
        Object durationObj = map.get("duration_seconds");
        if(durationObj instanceof Number) {
            duration = ((Number) durationObj).intValue();
        }

        return new Song(videoId, title, artist, album, duration);
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    public String getUrl() {
        return "https://music.youtube.com/watch?v=" + videoId;
    }

    public String getDisplayString() {
        StringBuilder thingy = new StringBuilder();
        thingy.append(title);

        if (!artist.isEmpty() && !artist.equals("Unknown artist")) {
            thingy.append(" - ").append(artist);
        }

        if (!album.isEmpty()) {
            thingy.append(" [").append(album).append("]");
        }

        return thingy.toString();
    }

    @Override
    public String toString() {
        return getDisplayString();
    }
}
