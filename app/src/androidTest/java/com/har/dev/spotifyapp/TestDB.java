package com.har.dev.spotifyapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.har.dev.spotifyapp.data.SpotifyDBContract;
import com.har.dev.spotifyapp.data.SpotifyDBHelper;

/**
 * Created by hareesh on 6/17/15.
 */
public class TestDB extends AndroidTestCase {
  public static final String LOG = TestDB.class.getSimpleName();
  static final ContentValues values = new ContentValues();

  public static long createArtist(SQLiteDatabase db,
                                  String name, int popularity, String imgUri, String thumbUri) {
    values.clear();
    values.put(SpotifyDBContract.Artist.COLUMN_NAME, name);
    values.put(SpotifyDBContract.Artist.COLUMN_POPULARITY, popularity);
    values.put(SpotifyDBContract.Artist.COLUMN_IMAGE_URI, imgUri);
    values.put(SpotifyDBContract.Artist.COLUMN_THUMBNAIL_URI, thumbUri);

    return db.insert(SpotifyDBContract.Artist.TABLE_NAME, null, values);
  }

  public void testUri() {
    final Uri uri = Uri.parse("content://com.test/p1/p2/p3");
    final String segment = uri.getPathSegments().get(2);
    assertTrue(segment.equals("p3"));
  }

  public void testCreate() {
    //delete existing database
    mContext.deleteDatabase(SpotifyDBHelper.DB_NAME);
    //get Database instance
    SQLiteDatabase db = new SpotifyDBHelper(mContext).getWritableDatabase();
    //check if Database is open
    assertTrue(db.isOpen());
    //close Database
    db.close();
  }

  public void testCRUD() {

    SQLiteDatabase db = new SpotifyDBHelper(mContext).getWritableDatabase();

    long artistID = createArtist(db, "Artist 1", 10,
        "http://some_img.jpg", "http://some_thumb.jpg");
    artistID = createArtist(db, "Artist 2", 20,
        "http://some_img.jpg", "http://some_thumb.jpg");
    artistID = createArtist(db, "Artist 3", 30,
        "http://some_img.jpg", "http://some_thumb.jpg");
    assertTrue(artistID > 0); //id must be greater than 0

    //read back all the entries from Artist Table
    Cursor cursor = db.query(SpotifyDBContract.Artist.TABLE_NAME,
        null, null, null, null, null, null);
    assertEquals(cursor.getCount(), 3); // must be 3 records


    db.close();
  }
}
