package com.har.dev.spotifyapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SpotifyProvider extends ContentProvider {
  // URI Matcher Codes
  public static final int ARTISTS = 100;
  public static final int ARTIST_ID = 101;
  public static final int ALBUMS = 200;
  public static final int ALBUM_ID = 201;
  public static final int ARTIST_ALBUMS = 202;
  public static final int TRACKS = 300;
  public static final int TRACK_ID = 301;
  public static final int ARTIST_TRACKS = 302;
  public static final int ALBUM_TRACKS = 303;
  public static final UriMatcher URI_MATCHER = buildUriMatcher();
  private static final SQLiteQueryBuilder sAlbumTracksQueryBuilder;

  static {
    //Inner Join to get fields from multiple tables
    sAlbumTracksQueryBuilder = new SQLiteQueryBuilder();
    sAlbumTracksQueryBuilder.setTables(
        SpotifyDBContract.Album.TABLE_NAME + " INNER JOIN " +
            SpotifyDBContract.Track.TABLE_NAME +
            " ON " + SpotifyDBContract.Album.TABLE_NAME +
            "." + SpotifyDBContract.Album._ID +
            " = " + SpotifyDBContract.Track.TABLE_NAME +
            "." + SpotifyDBContract.Track.COLUMN_ALBUM_ID);
//    sAlbumTracksQueryBuilder.setDistinct(true);
  }

  private SpotifyDBHelper mDBHelper;


  public SpotifyProvider() {
  }

  private static UriMatcher buildUriMatcher() {
    // # - for number path segments
    // * - for string path segments
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = SpotifyDBContract.CONTENT_AUTHORITY;

    matcher.addURI(authority, SpotifyDBContract.PATH_ARTISTS, ARTISTS);
    matcher.addURI(authority, SpotifyDBContract.PATH_ARTISTS + "/#", ARTIST_ID);

    matcher.addURI(authority, SpotifyDBContract.PATH_ALBUMS, ALBUMS);
    matcher.addURI(authority, SpotifyDBContract.PATH_ALBUMS + "/#", ALBUM_ID);
    matcher.addURI(authority, "#/" + SpotifyDBContract.PATH_ALBUMS, ARTIST_ALBUMS);

    matcher.addURI(authority, SpotifyDBContract.PATH_TRACKS, TRACKS);
    matcher.addURI(authority, SpotifyDBContract.PATH_TRACKS + "/#", TRACK_ID);
    matcher.addURI(authority, SpotifyDBContract.PATH_ARTISTS +
        "/#/" + SpotifyDBContract.PATH_TRACKS, ARTIST_TRACKS);
    matcher.addURI(authority, SpotifyDBContract.PATH_ALBUMS +
        "/#/" + SpotifyDBContract.PATH_TRACKS, ALBUM_TRACKS);

    return matcher;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    //TODO add support for ITEM URIs
    final SQLiteDatabase db = mDBHelper.getWritableDatabase();
    final int match = URI_MATCHER.match(uri);
    int deletedRows;
    switch (match) {
      case ARTISTS:
        deletedRows = db.delete(SpotifyDBContract.Artist.TABLE_NAME, selection, selectionArgs);
        break;
      case ARTIST_ID:
        deletedRows = db.delete(SpotifyDBContract.Artist.TABLE_NAME,
            SpotifyDBContract.Artist._ID + " = ?",
            new String[]{uri.getLastPathSegment()});
        break;
      case ALBUMS:
        deletedRows = db.delete(SpotifyDBContract.Album.TABLE_NAME, selection, selectionArgs);
        break;
      case ALBUM_ID:
        deletedRows = db.delete(SpotifyDBContract.Album.TABLE_NAME,
            SpotifyDBContract.Album._ID + " = ?",
            new String[]{uri.getLastPathSegment()});
        break;
      case TRACKS:
        deletedRows = db.delete(SpotifyDBContract.Track.TABLE_NAME, selection, selectionArgs);
        break;
      case TRACK_ID:
        deletedRows = db.delete(SpotifyDBContract.Track.TABLE_NAME,
            SpotifyDBContract.Track._ID + " = ?",
            new String[]{uri.getLastPathSegment()});
        break;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }
    if (selection == null || deletedRows > 0)
      getContext().getContentResolver().notifyChange(uri, null);
    return deletedRows;
  }

  @Override
  public String getType(Uri uri) {
    // all the URIs match to either content type DIR or ITEM
    final int match = URI_MATCHER.match(uri);
    switch (match) {
      case ARTISTS:
        return SpotifyDBContract.Artist.CONTENT_TYPE;
      case ARTIST_ID:
        return SpotifyDBContract.Artist.CONTENT_ITEM_TYPE;

      case ALBUMS:
        return SpotifyDBContract.Album.CONTENT_TYPE;
      case ALBUM_ID:
        return SpotifyDBContract.Album.CONTENT_ITEM_TYPE;
      case ARTIST_ALBUMS:
        return SpotifyDBContract.Album.CONTENT_TYPE;

      case TRACKS:
        return SpotifyDBContract.Track.CONTENT_TYPE;
      case TRACK_ID:
        return SpotifyDBContract.Track.CONTENT_ITEM_TYPE;
      case ARTIST_TRACKS:
        return SpotifyDBContract.Track.CONTENT_TYPE;
      case ALBUM_TRACKS:
        return SpotifyDBContract.Track.CONTENT_TYPE;

      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = mDBHelper.getWritableDatabase();
    //parse uri to find where to insert data
    final int match = URI_MATCHER.match(uri);
    long id;
    Uri returnUri;
    // insert data
    switch (match) {
      case ARTISTS:
        id = db.insert(SpotifyDBContract.Artist.TABLE_NAME, null, values);
        if (id > 0)
          returnUri = SpotifyDBContract.Artist.buildArtistUri(id);
        else
          throw new SQLException("insert failed into Artists " + uri);
        break;
      case ALBUMS:
        id = db.insert(SpotifyDBContract.Album.TABLE_NAME, null, values);
        if (id > 0)
          returnUri = SpotifyDBContract.Album.buildAlbumUri(id);
        else
          throw new SQLException("insert failed into Albums " + uri);
        break;
      case TRACKS:
        id = db.insert(SpotifyDBContract.Track.TABLE_NAME, null, values);
        if (id > 0)
          returnUri = SpotifyDBContract.Track.buildTrackUri(id);
        else
          throw new SQLException("insert failed into Tracks " + uri);
        break;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }
    //notify that the data has changed
    getContext().getContentResolver().notifyChange(returnUri, null);
    return returnUri;
  }

  @Override
  public boolean onCreate() {
    mDBHelper = new SpotifyDBHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    final SQLiteDatabase db = new SpotifyDBHelper(getContext()).getReadableDatabase();
    final int match = URI_MATCHER.match(uri);
    Cursor cursor = null;

    switch (match) {
      case ARTISTS:
        cursor = db.query(SpotifyDBContract.Artist.TABLE_NAME,
            projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case ARTIST_ID:
        cursor = db.query(SpotifyDBContract.Artist.TABLE_NAME,
            projection, SpotifyDBContract.Artist._ID + " = ?",
            new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
        break;
      case ALBUMS:
        cursor = db.query(SpotifyDBContract.Album.TABLE_NAME,
            projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case ALBUM_ID:
        cursor = db.query(SpotifyDBContract.Album.TABLE_NAME,
            projection, SpotifyDBContract.Album._ID + " = ?",
            new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
        break;
      case ARTIST_ALBUMS:
        cursor = db.query(SpotifyDBContract.Album.TABLE_NAME,
            projection, SpotifyDBContract.Album.COLUMN_ARTIST_ID + " = ?",
            new String[]{uri.getPathSegments().get(0)}, null, null, sortOrder);
        break;
      case TRACKS:
        cursor = db.query(SpotifyDBContract.Track.TABLE_NAME,
            projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case TRACK_ID:
        cursor = db.query(SpotifyDBContract.Track.TABLE_NAME,
            projection, SpotifyDBContract.Track._ID + " = ?",
            new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
        break;
      case ALBUM_TRACKS:
        cursor = db.query(SpotifyDBContract.Track.TABLE_NAME,
            projection, SpotifyDBContract.Track.COLUMN_ALBUM_ID + " = ?",
            new String[]{uri.getPathSegments().get(1)}, null, null, sortOrder);
        break;
      case ARTIST_TRACKS:
        if (projection == null) {
          cursor = db.query(SpotifyDBContract.Track.TABLE_NAME,
              projection, SpotifyDBContract.Track.COLUMN_ARTIST_ID + " = ?",
              new String[]{uri.getPathSegments().get(1)}, null, null, sortOrder);
        } else { //use INNER JOIN Query
          final String sel = SpotifyDBContract.Track.TABLE_NAME + "." +
              SpotifyDBContract.Track.COLUMN_ARTIST_ID + " = ?";
          cursor = sAlbumTracksQueryBuilder.query(db, projection, sel,
              new String[]{uri.getPathSegments().get(1)}, null, null, sortOrder);
        }
        break;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
    final SQLiteDatabase db = mDBHelper.getWritableDatabase();
    final int match = URI_MATCHER.match(uri);
    int updatedRows = 0;
    switch (match) {
      case ARTISTS:
        updatedRows = db.update(SpotifyDBContract.Artist.TABLE_NAME,
            values, selection, selectionArgs);
        break;
      case ARTIST_ID:
        updatedRows = db.update(SpotifyDBContract.Artist.TABLE_NAME,
            values, SpotifyDBContract.Artist._ID + " = ?",
            new String[]{uri.getLastPathSegment()});
        break;
      case ALBUMS:
        updatedRows = db.update(SpotifyDBContract.Album.TABLE_NAME,
            values, selection, selectionArgs);
        break;
      case ALBUM_ID:
        updatedRows = db.update(SpotifyDBContract.Album.TABLE_NAME,
            values, SpotifyDBContract.Album._ID + " = ?",
            new String[]{uri.getLastPathSegment()});
        break;
      case TRACKS:
        updatedRows = db.update(SpotifyDBContract.Track.TABLE_NAME,
            values, selection, selectionArgs);
        break;
      case TRACK_ID:
        updatedRows = db.update(SpotifyDBContract.Track.TABLE_NAME,
            values, SpotifyDBContract.Track._ID + " = ?",
            new String[]{uri.getLastPathSegment()});
        break;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }
    if (updatedRows > 0)
      getContext().getContentResolver().notifyChange(uri, null);
    return updatedRows;
  }

 /* @Override
  public int bulkInsert(Uri uri, ContentValues[] values) {
    final SQLiteDatabase db = mDBHelper.getWritableDatabase();
    final int match = URI_MATCHER.match(uri);
    int insertedRows = 0;
    long resID;

    switch (match) {
      case ARTISTS:
        db.beginTransaction();
        try {
          for (ContentValues v : values) {
            resID = db.insert(SpotifyDBContract.Artist.TABLE_NAME, null, v);
            if (resID != -1) insertedRows++;
          }
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }
        if (insertedRows > 0)
          getContext().getContentResolver().notifyChange(uri, null);
        return insertedRows;
      default:
        return super.bulkInsert(uri, values);
    }
  }*/
}
