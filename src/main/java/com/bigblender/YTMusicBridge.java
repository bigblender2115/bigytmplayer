package com.bigblender;

import com.google.gson.Gson; //for converting java objects into JSON and back
import com.google.gson.reflect.TypeToken;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//bridge to ytmusic api using jpython coz there unfortunately is no alternative for that for java :pensive:
public class YTMusicBridge {
    private PythonInterpreter interpreter;
    private Gson gson;
    private boolean authenticated;

    public YTMusicBridge() {
        this.gson = new Gson();
        this.authenticated = false;
        initializePython();
    }

    private void initializePython() {
        //setting python properties
        Properties props = new Properties();
        props.put("python.console.encoding", "UTF-8");
        PythonInterpreter.initialize(System.getProperties(), props, new String[0]);

        interpreter = new PythonInterpreter();

        interpreter.exec("import sys");
        interpreter.exec("import json");

        try{
            interpreter.exec("ytmusicapi");

            //oauth.json check
            Path oauthPath = getConfigPath("oauth.json");
            if(Files.exists(oauthPath)) {
                interpreter.exec("ytmusic = ytmusicapi.YTMusic('" + oauthPath.toString().replace("\\", "\\\\") + "')");
                authenticated = true;
            }
            else {
                interpreter.exec("ytmusic = ytmusicapi.YTMusic()");
                authenticated = false;
            }
            System.out.println("✓ytm API initialized");
            if (authenticated) {
                System.out.println("Authenticated");
            }
        }
        catch (Exception e) {
            System.err.println("Error initializing ytmusicapi: " + e.getMessage());
        }
    }

    private Path getConfigPath(String filename) {
        String appdata = System.getenv("APPDATA");
        if (appdata != null) {
            Path configDir = Paths.get(appdata, "YTMusicPlayer");

            try {
                Files.createDirectories(configDir);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return configDir.resolve(filename);
        }
        return Paths.get(filename);
    }

    //search func. i dont understand ts much :v:
    public List<Song> search(String query, int limit) {
        try {
            String pythonCode = String.format(
                    "results = ytmusic.search('%s', filter='songs', limit=%d)\n" +
                            "json.dumps(results)",
                    query.replace("'", "\\'"), limit
            );

            interpreter.exec(pythonCode);
            PyObject result = interpreter.get("results");

            String jsonResults = result.toString();
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> rawResults = gson.fromJson(jsonResults, listType);

            List<Song> songs = new ArrayList<>();
            for (Map<String, Object> item : rawResults) {
                songs.add(Song.fromMap(item));
            }

            return songs;
        }
        catch (Exception e) {
            System.err.println("Search error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    //i dont get this much either :v:
    public List<Playlist> getPlaylists(int limit) {
        //needs auth to fetch the user's playlists
        if (!authenticated) {
            System.out.println("Authentication required for playlists");
            return new ArrayList<>();
        }

        try {
            String pythonCode = String.format(
                    "playlists = ytmusic.get_library_playlists(limit=%d)\n" +
                            "json.dumps(playlists)",
                    limit
            );

            interpreter.exec(pythonCode);
            PyObject result = interpreter.get("playlists");

            String jsonResults = result.toString();
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> rawResults = gson.fromJson(jsonResults, listType);

            List<Playlist> playlists = new ArrayList<>();
            for (Map<String, Object> item : rawResults) {
                playlists.add(Playlist.fromMap(item));
            }

            return playlists;
        } catch (Exception e) {
            System.err.println("Playlist error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    //songs from a playlist
    public List<Song> getPlaylistSongs(String playlistId) {
        if (!authenticated) {
            return new ArrayList<>();
        }

        try {
            String pythonCode = String.format(
                    "playlist = ytmusic.get_playlist('%s')\n" +
                            "tracks = playlist.get('tracks', [])\n" +
                            "json.dumps(tracks)",
                    playlistId
            );

            interpreter.exec(pythonCode);
            PyObject result = interpreter.get("tracks");

            String jsonResults = result.toString();
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> rawResults = gson.fromJson(jsonResults, listType);

            List<Song> songs = new ArrayList<>();
            for (Map<String, Object> item : rawResults) {
                songs.add(Song.fromMap(item));
            }

            return songs;
        } catch (Exception e) {
            System.err.println("Get playlist songs error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    //oauth setup
    public void runSetup() {
        try {
            Path oauthPath = getConfigPath("oauth.json");
            String pythonCode = String.format(
                    "ytmusicapi.setup(filepath='%s')",
                    oauthPath.toString().replace("\\", "\\\\")
            );
            interpreter.exec(pythonCode);
            System.out.println("OAuth setup complete");
            System.out.println("Config file saved to: " + oauthPath);
        } catch (Exception e) {
            System.err.println("setup error: " + e.getMessage());
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
}
