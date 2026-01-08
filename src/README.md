# stuff

### Youtube music API response format
```json
{
  "videoId": "CdntS8x9K40",
  "title": "Blinding Lights",
  "artists": [
    {
      "name": "The Weeknd",
      "id": "UC0WP5P-ufpRfjbNrmOWwLBQ"
    }
  ],
  "album": {
    "name": "After Hours",
    "id": "MPREb_..."
  },
  "duration_seconds": 200,
  "thumbnails": [
    {
      "url": "https://lh3.googleusercontent.com/...",
      "width": 60,
      "height": 60
    }
  ],
  "isExplicit": false
}
```

### Song
```
    videoId
    title
    artist
    album
    duration(in seconds)
    
    display string:
    title - artist [album]
```

### Playlist
``` 
playlistId
title
count

display string:
<title> (<count> songs)
```