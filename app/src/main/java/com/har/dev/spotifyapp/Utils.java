package com.har.dev.spotifyapp;

/**
 * Created by hareesh on 6/25/15.
 */
public class Utils {
  public static final String EXTRA_ALBUM_URI = "album_uri";
  public static final String EXTRA_ARTIST_URI = "artist_uri";
  public static final String EXTRA_TRACK_URI = "track_uri";

  public static final String formatDuration(int duration) {
    final int sec = duration / 1000;
    final int min = sec / 60;
    if (min > 60)
      return String.format("%2d:%02d:%02d", min / 60, min % 60, sec % 60);
    else
      return String.format("%2d:%02d", min % 60, sec % 60);
  }
}
