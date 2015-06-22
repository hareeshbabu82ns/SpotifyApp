package com.har.dev.spotifyapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by hareesh on 6/16/15.
 */
public class SpotifyDBHelper extends SQLiteOpenHelper {
  public static final String DB_NAME = "spotify.db";
  public static final int DB_VERSION = 1;
  public static final String[] thumbUris = {
      "https://cloud.githubusercontent.com/assets/1978258/8221494/07b995e4-157d-11e5-9dce-d5b78e596d95.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221497/0ff7d52c-157d-11e5-9a08-cad80d0b2873.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221501/16307282-157d-11e5-8ca1-09d3a60d675c.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221504/26f7def2-157d-11e5-81bd-f2884a0e7660.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221509/2f09d154-157d-11e5-8bd6-8d987b3d0020.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221511/37f86c26-157d-11e5-993e-51eb5b4e6087.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221512/3dd5dda4-157d-11e5-8cc8-2a936cbdccc4.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221516/458ed0be-157d-11e5-8e9b-ef00d83a2b78.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221521/5211bef0-157d-11e5-825f-b265c587be64.png",
      "https://cloud.githubusercontent.com/assets/1978258/8221523/5733be60-157d-11e5-879f-bf185e645a2f.png"
  };
  public static final String[] imgUris = {
      "https://cloud.githubusercontent.com/assets/1978258/8202504/40fa009e-14f8-11e5-833f-88b1e497b0b9.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202512/5096dd2e-14f8-11e5-90eb-83ed50c667bc.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202514/54d58070-14f8-11e5-9825-2c3a2545c4f2.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202517/59da4ca4-14f8-11e5-8078-a7d50068b7ff.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202518/5f510a6a-14f8-11e5-90f0-95d6ef75d9a8.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202522/65cbbfca-14f8-11e5-86ca-bb6e393b73c9.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202527/7131e15a-14f8-11e5-9e05-9e0967ea6a93.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202530/76085984-14f8-11e5-9bd6-7de904b21319.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202540/85a91f86-14f8-11e5-820a-79ed5a80cec3.png",
      "https://cloud.githubusercontent.com/assets/1978258/8202552/96dd8d78-14f8-11e5-873c-16e4f40e3a75.png"
  };
  public static final String[] songUris = {
      "http://www.stephaniequinn.com/Music/Commercial%20DEMO%20-%2011.mp3",
      "http://www.stephaniequinn.com/Music/Commercial%20DEMO%20-%2013.mp3",
      "http://www.stephaniequinn.com/Music/Commercial%20DEMO%20-%2016.mp3",
      "http://www.stephaniequinn.com/Music/Commercial%20DEMO%20-%2012.mp3",
      "http://www.stephaniequinn.com/Music/Commercial%20DEMO%20-%2015.mp3"
  };

  public SpotifyDBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onConfigure(SQLiteDatabase db) {
    super.onConfigure(db);
    //will be disabled by default
    db.setForeignKeyConstraintsEnabled(true);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    if (DB_VERSION == 1) {
      db.execSQL(SpotifyDBContract.Artist.TABLE_CREATE_V1);
      db.execSQL(SpotifyDBContract.Album.TABLE_CREATE_V1);
      db.execSQL(SpotifyDBContract.Track.TABLE_CREATE_V1);
    }
    initDatabase(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(SpotifyDBContract.Artist.TABLE_DELETE);
    db.execSQL(SpotifyDBContract.Album.TABLE_DELETE);
    db.execSQL(SpotifyDBContract.Track.TABLE_DELETE);
    onCreate(db);
  }

  void initDatabase(SQLiteDatabase db) {
    switch (DB_VERSION) {
      case 1:
        //TODO create entries specific to version
    }
    createDummyData(db);
  }

  void createDummyData(SQLiteDatabase db) {
    final ContentValues values = new ContentValues();
    Random random = new Random(Calendar.getInstance().getTimeInMillis());
    int artistRandom = random.nextInt(50), albumRandom, trackRandom;
    long artistID = 0, albumID = 0;
    //create Artist entries
    for (int i = 0; i < artistRandom; i++) {
      values.clear();
      values.put(SpotifyDBContract.Artist.COLUMN_NAME, "Artist " + i);
      values.put(SpotifyDBContract.Artist.COLUMN_POPULARITY, random.nextInt(10));
      values.put(SpotifyDBContract.Artist.COLUMN_THUMBNAIL_URI,
          thumbUris[random.nextInt(thumbUris.length)]);
      values.put(SpotifyDBContract.Artist.COLUMN_IMAGE_URI,
          imgUris[random.nextInt(imgUris.length)]);

      artistID = db.insert(SpotifyDBContract.Artist.TABLE_NAME, null, values);
      if (artistID == -1)
        continue;

      //create Albums for Artist
      albumRandom = random.nextInt(50);
      for (int j = 0; j < albumRandom; j++) {
        values.clear();
        values.put(SpotifyDBContract.Album.COLUMN_ARTIST_ID, artistID);
        values.put(SpotifyDBContract.Album.COLUMN_NAME, "Album " + j);
        values.put(SpotifyDBContract.Album.COLUMN_DESCRIPTION, "Album Description " + j);
        values.put(SpotifyDBContract.Album.COLUMN_THUMBNAIL_URI,
            thumbUris[random.nextInt(thumbUris.length)]);
        values.put(SpotifyDBContract.Album.COLUMN_IMAGE_URI,
            imgUris[random.nextInt(imgUris.length)]);

        albumID = db.insert(SpotifyDBContract.Album.TABLE_NAME, null, values);
        if (albumID == -1)
          continue;

        //create Tracks for Album
        trackRandom = random.nextInt(10);
        for (int k = 0; k < trackRandom; k++) {
          values.clear();
          values.put(SpotifyDBContract.Track.COLUMN_ARTIST_ID, artistID);
          values.put(SpotifyDBContract.Track.COLUMN_ALBUM_ID, albumID);
          values.put(SpotifyDBContract.Track.COLUMN_NAME, "Track " + j);
          values.put(SpotifyDBContract.Track.COLUMN_DESCRIPTION, "Track Description " + j);
          values.put(SpotifyDBContract.Track.COLUMN_THUMBNAIL_URI,
              thumbUris[random.nextInt(thumbUris.length)]);
          values.put(SpotifyDBContract.Track.COLUMN_IMAGE_URI,
              imgUris[random.nextInt(imgUris.length)]);
          values.put(SpotifyDBContract.Track.COLUMN_SONG_URI,
              songUris[random.nextInt(songUris.length)]);
          db.insert(SpotifyDBContract.Track.TABLE_NAME, null, values);
        }
      }
    }

    switch (DB_VERSION) {
      case 1:
        //TODO create entries specific to version
    }

  }
}
