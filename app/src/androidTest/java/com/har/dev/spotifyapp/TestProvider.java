package com.har.dev.spotifyapp;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.har.dev.spotifyapp.data.SpotifyDBContract;
import com.har.dev.spotifyapp.data.SpotifyProvider;

/**
 * Created by hareesh on 6/17/15.
 */
public class TestProvider extends AndroidTestCase {
  public static final String TAG = TestProvider.class.getSimpleName();
  final ContentValues values = new ContentValues();

  public void testUriMatcher() {
    final UriMatcher matcher = SpotifyProvider.URI_MATCHER;
    int match = matcher.match(SpotifyDBContract.Album.buildArtistAlbumUri(1l));
    assertEquals(SpotifyProvider.ARTIST_ALBUMS, match);

    match = matcher.match(SpotifyDBContract.Track.buildAlbumTracksUri(1l));
    assertEquals(SpotifyProvider.ALBUM_TRACKS, match);

    match = matcher.match(SpotifyDBContract.Track.buildArtistTracksUri(1l));
    assertEquals(SpotifyProvider.ARTIST_TRACKS, match);

  }

  public void testArtistCRUD() {
    Uri returnUri;
    long id;

    //create sample Artist entries
    fillArtistValues("Artist 1", 10,
        "http://some_img.jpg", "http://some_thumb.jpg");
    getContext().getContentResolver().insert(SpotifyDBContract.Artist.CONTENT_URI, values);
    fillArtistValues("Artist 2", 20,
        "http://some_img.jpg", "http://some_thumb.jpg");
    getContext().getContentResolver().insert(SpotifyDBContract.Artist.CONTENT_URI, values);
    fillArtistValues("Artist 3", 30,
        "http://some_img.jpg", "http://some_thumb.jpg");
    returnUri = getContext().getContentResolver().insert(SpotifyDBContract.Artist.CONTENT_URI, values);
    id = Long.valueOf(returnUri.getLastPathSegment());
    assertTrue(id > 0);

    //fetch all Artist entries, must be 3
    Cursor cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Artist.CONTENT_URI, null, null, null, null);
    assertEquals(3, cursor.getCount());

    //fetch Artist with ID, check against name
    cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Artist.buildArtistUri(id), null, null, null, null);
    cursor.moveToFirst();
    String name = cursor.getString(cursor.getColumnIndex(SpotifyDBContract.Artist.COLUMN_NAME));
    assertTrue(name.equals("Artist 3"));

    //change Artist Name and check again
    values.put(SpotifyDBContract.Artist.COLUMN_NAME, "Artist 3 Chg");
    getContext().getContentResolver()
        .update(SpotifyDBContract.Artist.buildArtistUri(id), values, null, null);

    cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Artist.buildArtistUri(id), null, null, null, null);
    cursor.moveToFirst();
    name = cursor.getString(cursor.getColumnIndex(SpotifyDBContract.Artist.COLUMN_NAME));
    assertTrue(name.equals("Artist 3 Chg"));

    //test for deletion
    id = getContext().getContentResolver()
        .delete(SpotifyDBContract.Artist.buildArtistUri(id), null, null);
    assertTrue(id == 1);
    deleteData();
  }

  public void testAlbumCRUD() {
    long artistID = 0;

    //insert Artist
    fillArtistValues("Artist 4", 10,
        "http://some_img.jpg", "http://some_thumb.jpg");
    artistID = Long.valueOf(getContext().getContentResolver()
        .insert(SpotifyDBContract.Artist.CONTENT_URI, values).getLastPathSegment());
    assertTrue(artistID > 0);

    //insert Albums
    fillAlbumValues(artistID, "Album 1", "desc 1",
        "http://some_img.jpg", "http://some_thumb.jpg");
    getContext().getContentResolver()
        .insert(SpotifyDBContract.Album.CONTENT_URI, values);
    fillAlbumValues(artistID, "Album 2", "desc 2",
        "http://some_img.jpg", "http://some_thumb.jpg");
    getContext().getContentResolver()
        .insert(SpotifyDBContract.Album.CONTENT_URI, values);
    fillAlbumValues(artistID, "Album 3", "desc 3",
        "http://some_img.jpg", "http://some_thumb.jpg");
    getContext().getContentResolver()
        .insert(SpotifyDBContract.Album.CONTENT_URI, values);

    //check insertions
    Cursor cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Album.CONTENT_URI, null, null, null, null);
    assertEquals(3, cursor.getCount());

    //check Artist Albums
    cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Album.buildArtistAlbumUri(artistID),
            null, null, null, null);
    assertEquals(3, cursor.getCount());
    deleteData();
  }

  public void testTracksCRUD() {
    long artistID = 0;
    long albumID1 = 0, albumID2 = 0;
    int ret;

    //insert Artist
    fillArtistValues("Artist 1", 10,
        "http://some_img.jpg", "http://some_thumb.jpg");
    artistID = Long.valueOf(getContext().getContentResolver()
        .insert(SpotifyDBContract.Artist.CONTENT_URI, values).getLastPathSegment());
    assertTrue(artistID > 0);

    //insert Albums
    fillAlbumValues(artistID, "Album 1", "desc 1",
        "http://some_img.jpg", "http://some_thumb.jpg");
    albumID1 = Long.valueOf(getContext().getContentResolver()
        .insert(SpotifyDBContract.Album.CONTENT_URI, values).getLastPathSegment());
    fillAlbumValues(artistID, "Album 2", "desc 2",
        "http://some_img.jpg", "http://some_thumb.jpg");
    albumID2 = Long.valueOf(getContext().getContentResolver()
        .insert(SpotifyDBContract.Album.CONTENT_URI, values).getLastPathSegment());
    assertTrue(albumID1 > 0);

    //insert Tracks
    fillTrackValues(artistID, albumID1, "track 1", "desc 1",
        "http://some_img.jpg", "http://some_thumb.jpg", "http://some_song.mp3");
    getContext().getContentResolver()
        .insert(SpotifyDBContract.Track.CONTENT_URI, values);
    fillTrackValues(artistID, albumID1, "track 2", "desc 2",
        "http://some_img.jpg", "http://some_thumb.jpg", "http://some_song.mp3");
    getContext().getContentResolver()
        .insert(SpotifyDBContract.Track.CONTENT_URI, values);
    fillTrackValues(artistID, albumID2, "track 3", "desc 3",
        "http://some_img.jpg", "http://some_thumb.jpg", "http://some_song.mp3");
    getContext().getContentResolver()
        .insert(SpotifyDBContract.Track.CONTENT_URI, values);

    Cursor cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Track.buildArtistTracksUri(artistID), null, null, null, null);
    assertEquals(3, cursor.getCount());

    cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Track.buildAlbumTracksUri(albumID1), null, null, null, null);
    assertEquals(2, cursor.getCount());

    //delete album2 to check casecade deletion
    ret = getContext().getContentResolver()
        .delete(SpotifyDBContract.Album.buildAlbumUri(albumID2), null, null);
    assertEquals(1, ret);

    cursor = getContext().getContentResolver()
        .query(SpotifyDBContract.Track.buildAlbumTracksUri(albumID2), null, null, null, null);
    assertEquals(0, cursor.getCount());

    deleteData();
  }

  public void testGetType() {
    // content://com.har.dev.spotify/artists
    String type = mContext.getContentResolver()
        .getType(SpotifyDBContract.Artist.CONTENT_URI);
    // vnd.android.cursor.dir/com.har.dev.spotify/artists
    assertEquals(SpotifyDBContract.Artist.CONTENT_TYPE, type);

    // content://com.har.dev.spotify/artists/1
    type = mContext.getContentResolver()
        .getType(SpotifyDBContract.Artist.buildArtistUri(1L));
    // vnd.android.cursor.dir/com.har.dev.spotify/artists/1
    assertEquals(SpotifyDBContract.Artist.CONTENT_ITEM_TYPE, type);

    // content://com.har.dev.spotify/artists/1/tracks
    type = mContext.getContentResolver()
        .getType(SpotifyDBContract.Track.buildArtistTracksUri(1L));
    // vnd.android.cursor.dir/com.har.dev.spotify/artists/1/tracks
    assertEquals(SpotifyDBContract.Track.CONTENT_TYPE, type);
  }

  void fillArtistValues(String name, int popularity, String imgUri, String thumbUri) {
    values.clear();
    values.put(SpotifyDBContract.Artist.COLUMN_NAME, name);
    values.put(SpotifyDBContract.Artist.COLUMN_POPULARITY, popularity);
    values.put(SpotifyDBContract.Artist.COLUMN_IMAGE_URI, imgUri);
    values.put(SpotifyDBContract.Artist.COLUMN_THUMBNAIL_URI, thumbUri);
  }

  void fillAlbumValues(long artist_id, String name, String description,
                       String imgUri, String thumbUri) {
    values.clear();
    values.put(SpotifyDBContract.Album.COLUMN_ARTIST_ID, artist_id);
    values.put(SpotifyDBContract.Album.COLUMN_NAME, name);
    values.put(SpotifyDBContract.Album.COLUMN_DESCRIPTION, description);
    values.put(SpotifyDBContract.Album.COLUMN_IMAGE_URI, imgUri);
    values.put(SpotifyDBContract.Album.COLUMN_THUMBNAIL_URI, thumbUri);
  }

  void fillTrackValues(long artist_id, long album_id, String name,
                       String description, String imgUri, String thumbUri, String songUri) {
    values.clear();
    values.put(SpotifyDBContract.Track.COLUMN_ARTIST_ID, artist_id);
    values.put(SpotifyDBContract.Track.COLUMN_ALBUM_ID, album_id);
    values.put(SpotifyDBContract.Track.COLUMN_NAME, name);
    values.put(SpotifyDBContract.Track.COLUMN_DESCRIPTION, description);
    values.put(SpotifyDBContract.Track.COLUMN_IMAGE_URI, imgUri);
    values.put(SpotifyDBContract.Track.COLUMN_THUMBNAIL_URI, thumbUri);
    values.put(SpotifyDBContract.Track.COLUMN_SONG_URI, songUri);
  }

  public void deleteData() {
    getContext().getContentResolver().delete(SpotifyDBContract.Artist.CONTENT_URI, null, null);
    getContext().getContentResolver().delete(SpotifyDBContract.Album.CONTENT_URI, null, null);
    getContext().getContentResolver().delete(SpotifyDBContract.Track.CONTENT_URI, null, null);
  }

//  public void testDelete() {
//    getContext().deleteDatabase(SpotifyDBHelper.DB_NAME);
//  }
}
