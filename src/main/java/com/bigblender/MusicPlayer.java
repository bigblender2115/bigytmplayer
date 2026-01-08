package com.bigblender;

import com.bigblender.Song;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
    private final MediaPlayerFactory factory;
    private final MediaPlayer mediaPlayer;
    private List<Song> queue;
    private int currentIndex;
    private Song currentSong;
    private boolean isPlaying;
    private PlayerStateListener stateListener;

    public interface PlayerStateListener {
        void onSongChanged(Song song);
        void onPlaybackStateChanged(boolean isPlaying);
        void onError(String error);
    }

    public MusicPlayer() {
        this.factory = new MediaPlayerFactory();
        this.mediaPlayer = factory.mediaPlayers().newMediaPlayer();
        this.queue = new ArrayList<>();
        this.currentIndex = -1;
        this.isPlaying = false;

        //event listener for end of song
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                nextTrack();
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                if(stateListener != null) {
                    stateListener.onError("Playback error has occurred");
                }
            }
        });

        System.out.println("VLCJ media player works gg");
    }

    public void setStateListener(PlayerStateListener listener) {
        this.stateListener = listener;
    }

    public void play(Song song) {
        try {
            currentSong = song;
            String url = song.getUrl();

            mediaPlayer.media().play(url);
            isPlaying = true;

            if(stateListener != null) {
                stateListener.onSongChanged(song);
                stateListener.onPlaybackStateChanged(true);
            }

            System.out.println("Playing: " + song.getDisplayString());
        }
        catch (Exception e) {
            System.err.println("Error playing song: " + e.getMessage());
            if(stateListener != null) {
                stateListener.onError("Failed to play: " + e.getMessage());
            }
        }
    }

    //setting playback queue
    public void setQueue(List<Song> songs, int startIndex) {
        this.queue = new ArrayList<>(songs);
        this.currentIndex = startIndex;

        if(!queue.isEmpty() && startIndex >= 0 && startIndex < queue.size()) {
            play(queue.get(startIndex));
        }
    }

    //pause/play toggle
    public void togglePause() {
        if(mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().pause();
            isPlaying = false;
            System.out.println("Paused");
        }
        else {
            mediaPlayer.controls().play();
            isPlaying = true;
            System.out.println("Resumed");
        }

        if (stateListener != null) {
            stateListener.onPlaybackStateChanged(isPlaying);
        }
    }

    //stopping playback
    public void stop() {
        mediaPlayer.controls().stop();
        isPlaying = false;
        currentSong = null;

        if(stateListener != null) {
            stateListener.onPlaybackStateChanged(false);
        }
        System.out.println("Playback stopped");
    }

    //playing next track in the queue
    public void nextTrack() {
        if(queue.isEmpty()) {
            return;
        }

        currentIndex++;
        if (currentIndex >= queue.size()) {
            currentIndex = 0; //loop back to start
        }

        play(queue.get(currentIndex));
    }

    public void prevTrack() {
        if(queue.isEmpty()) {
            return;
        }

        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = queue.size() - 1; //loop to end
        }

        play(queue.get(currentIndex));
    }

    //current song
    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public List<Song> getQueue() {
        return new ArrayList<>(queue);
    }

    //releasing resources
    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (factory != null) {
            factory.release();
        }
    }
}