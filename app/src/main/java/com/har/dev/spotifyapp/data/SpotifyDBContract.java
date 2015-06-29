package com.har.dev.spotifyapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hareesh on 6/16/15.
 */
public class SpotifyDBContract {

  public static final String CONTENT_AUTHORITY = "com.har.dev.spotify";
  public static final String PATH_ARTISTS = "artists";
  public static final String PATH_ALBUMS = "albums";
  public static final String PATH_TRACKS = "tracks";

  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
  // Format used for storing dates in the database.  ALso used for converting those strings
  // back into date objects for comparison/processing.
  public static final String DATE_FORMAT = "yyyyMMdd";
  public static final SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);

  /**
   * Converts Date class to a string representation, used for easy comparison and database lookup.
   *
   * @param date The input date
   * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
   */
  public static String getDbDateString(Date date) {
    // Because the API returns a unix timestamp (measured in seconds),
    // it must be converted to milliseconds in order to be converted to valid date.
    return dbDateFormat.format(date);
  }

  /**
   * Converts a dateText to a long Unix time representation
   *
   * @param dateText the input date string
   * @return the Date object
   */
  public static Date getDateFromDb(String dateText) {
    try {
      return dbDateFormat.parse(dateText);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static final class Artist implements BaseColumns {
    public static final String TABLE_NAME = "artists";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_IMAGE_URI = "uri";
    public static final String COLUMN_THUMBNAIL_URI = "thumbnail";

    public static final String TABLE_CREATE_V1 = "CREATE TABLE " + TABLE_NAME
        + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_NAME + " TEXT UNIQUE NOT NULL, "
        + COLUMN_POPULARITY + " INTEGER NOT NULL, "
        + COLUMN_IMAGE_URI + " TEXT NOT NULL, "
        + COLUMN_THUMBNAIL_URI + " TEXT NOT NULL )";

    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
        CONTENT_AUTHORITY + "/" + PATH_ARTISTS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
        CONTENT_AUTHORITY + "/" + PATH_ARTISTS;

    //DIR - URI to fetch all Artists
    // content://com.har.dev.spotify/artists
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
        .appendPath(PATH_ARTISTS).build();
    public long id;
    public String name;
    public int popularity;
    public Uri imageUri;
    public Uri thumbnailUri;

    //ITEM - URI to fetch details of given Artist ID
    // content://com.har.dev.spotify/artists/{id}
    public static final Uri buildArtistUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static final long getArtistFromUri(Uri uri) {
      return Long.valueOf(uri.getLastPathSegment());
    }

    public static final Artist buildFrom(Cursor cursor) {
      int idx = -1;
      final Artist artist = new Artist();
      if ((idx = cursor.getColumnIndex(Artist.TABLE_NAME + "." + _ID)) >= 0) {
        artist.id = cursor.getLong(idx);
      } else if ((idx = cursor.getColumnIndex(_ID)) >= 0) {
        artist.id = cursor.getLong(idx);
      }
      if ((idx = cursor.getColumnIndex(Artist.TABLE_NAME + "." + COLUMN_NAME)) >= 0) {
        artist.name = cursor.getString(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_NAME)) >= 0) {
        artist.name = cursor.getString(idx);
      }
      if ((idx = cursor.getColumnIndex(Artist.TABLE_NAME + "." + COLUMN_POPULARITY)) >= 0) {
        artist.popularity = cursor.getInt(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_POPULARITY)) >= 0) {
        artist.popularity = cursor.getInt(idx);
      }
      if ((idx = cursor.getColumnIndex(Artist.TABLE_NAME + "." + COLUMN_THUMBNAIL_URI)) >= 0) {
        artist.thumbnailUri = Uri.parse(cursor.getString(idx));
      } else if ((idx = cursor.getColumnIndex(COLUMN_THUMBNAIL_URI)) >= 0) {
        artist.thumbnailUri = Uri.parse(cursor.getString(idx));
      }
      if ((idx = cursor.getColumnIndex(Artist.TABLE_NAME + "." + COLUMN_IMAGE_URI)) >= 0) {
        artist.imageUri = Uri.parse(cursor.getString(idx));
      } else if ((idx = cursor.getColumnIndex(COLUMN_IMAGE_URI)) >= 0) {
        artist.imageUri = Uri.parse(cursor.getString(idx));
      }
      return artist;
    }

  }

  public static final class Album implements BaseColumns {
    public static final String TABLE_NAME = "albums";

    public static final String COLUMN_ARTIST_ID = "artist";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE_URI = "uri";
    public static final String COLUMN_THUMBNAIL_URI = "thumbnail";

    public static final String TABLE_CREATE_V1 = "CREATE TABLE " + TABLE_NAME
        + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_ARTIST_ID + " INTEGER NOT NULL, "
        + COLUMN_NAME + " TEXT NOT NULL, "
        + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
        + COLUMN_IMAGE_URI + " TEXT NOT NULL, "
        + COLUMN_THUMBNAIL_URI + " TEXT NOT NULL, "
        + "FOREIGN KEY ( " + COLUMN_ARTIST_ID + " ) REFERENCES "
        + Artist.TABLE_NAME + " ( " + Artist._ID + " ) ON DELETE CASCADE)";

    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
        CONTENT_AUTHORITY + "/" + PATH_ALBUMS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
        CONTENT_AUTHORITY + "/" + PATH_ALBUMS;

    //DIR - URI to fetch all Albums
    // content://com.har.dev.spotify/Albums
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
        .appendPath(PATH_ALBUMS).build();
    public long id;
    public Artist artist;
    public String name;
    public String description;
    public Uri imageUri;
    public Uri thumbnailUri;

    //DIR - URI to fetch Albums of an Artist
    // content://com.har.dev.spotify/{artist_id}/albums
    public static final Uri buildArtistAlbumUri(long artistID) {
      return BASE_CONTENT_URI.buildUpon()
          .appendPath(String.valueOf(artistID)).appendPath(PATH_ALBUMS).build();
    }

    public static final long getArtistFromUri(Uri uri) {
      return Long.valueOf(uri.getPathSegments().get(0));
    }

    //ITEM - URI to fetch details of given Albums ID
    // content://com.har.dev.spotify/albums/{id}
    public static final Uri buildAlbumUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static final long getAlbumFromUri(Uri uri) {
      return Long.valueOf(uri.getLastPathSegment());
    }

    public static final Album buildFrom(Cursor cursor) {
      int idx = -1;
      final Album album = new Album();
      if ((idx = cursor.getColumnIndex(Album.TABLE_NAME + "." + _ID)) >= 0) {
        album.id = cursor.getLong(idx);
      } else if ((idx = cursor.getColumnIndex(_ID)) >= 0) {
        album.id = cursor.getLong(idx);
      }
      if ((idx = cursor.getColumnIndex(Album.TABLE_NAME + "." + COLUMN_NAME)) >= 0) {
        album.name = cursor.getString(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_NAME)) >= 0) {
        album.name = cursor.getString(idx);
      }
      if ((idx = cursor.getColumnIndex(Album.TABLE_NAME + "." + COLUMN_DESCRIPTION)) >= 0) {
        album.description = cursor.getString(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_DESCRIPTION)) >= 0) {
        album.description = cursor.getString(idx);
      }
      if ((idx = cursor.getColumnIndex(Album.TABLE_NAME + "." + COLUMN_THUMBNAIL_URI)) >= 0) {
        album.thumbnailUri = Uri.parse(cursor.getString(idx));
      } else if ((idx = cursor.getColumnIndex(COLUMN_THUMBNAIL_URI)) >= 0) {
        album.thumbnailUri = Uri.parse(cursor.getString(idx));
      }
      if ((idx = cursor.getColumnIndex(Album.TABLE_NAME + "." + COLUMN_IMAGE_URI)) >= 0) {
        album.imageUri = Uri.parse(cursor.getString(idx));
      } else if ((idx = cursor.getColumnIndex(COLUMN_IMAGE_URI)) >= 0) {
        album.imageUri = Uri.parse(cursor.getString(idx));
      }
      album.artist = Artist.buildFrom(cursor);
      if ((idx = cursor.getColumnIndex(Album.TABLE_NAME + "." + COLUMN_ARTIST_ID)) >= 0) {
        album.artist.id = cursor.getLong(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_ARTIST_ID)) >= 0) {
        album.artist.id = cursor.getLong(idx);
      }
      return album;
    }
  }

  public static final class Track implements BaseColumns {
    public static final String TABLE_NAME = "tracks";

    public static final String COLUMN_ALBUM_ID = "album";
    public static final String COLUMN_ARTIST_ID = "artist";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE_URI = "uri";
    public static final String COLUMN_THUMBNAIL_URI = "thumbnail";
    public static final String COLUMN_SONG_URI = "song";

    public static final String TABLE_CREATE_V1 = "CREATE TABLE " + TABLE_NAME
        + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_ALBUM_ID + " INTEGER NOT NULL, "
        + COLUMN_ARTIST_ID + " INTEGER NOT NULL, "
        + COLUMN_NAME + " TEXT NOT NULL, "
        + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
        + COLUMN_IMAGE_URI + " TEXT NOT NULL, "
        + COLUMN_THUMBNAIL_URI + " TEXT NOT NULL, "
        + COLUMN_SONG_URI + " TEXT NOT NULL, "
        + " FOREIGN KEY ( " + COLUMN_ALBUM_ID + " ) REFERENCES "
        + Album.TABLE_NAME + " ( " + Album._ID + " ) ON DELETE CASCADE"
        + " FOREIGN KEY ( " + COLUMN_ARTIST_ID + " ) REFERENCES "
        + Artist.TABLE_NAME + " ( " + Artist._ID + " ) ON DELETE CASCADE"
        + " )";

    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
        CONTENT_AUTHORITY + "/" + PATH_TRACKS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
        CONTENT_AUTHORITY + "/" + PATH_TRACKS;

    //DIR - URI to fetch all Tracks
    // content://com.har.dev.spotify/tracks
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
        .appendPath(PATH_TRACKS).build();
    public long id;
    public Album album;
    public String name;
    public String description;
    public Uri imageUri;
    public Uri thumbnailUri;
    public Uri songUri;

    //DIR - URI to fetch Tracks of given Artist
    // content://com.har.dev.spotify/artists/{artist_id}/tracks
    public static final Uri buildArtistTracksUri(long artistID) {
      return Artist.buildArtistUri(artistID).buildUpon()
          .appendPath(PATH_TRACKS).build();
    }

    public static final long getArtistFromUri(Uri uri) {
      return Long.valueOf(uri.getPathSegments().get(1));
    }

    //DIR - URI to fetch Tracks of given Album
    // content://com.har.dev.spotify/albums/{album_id}/tracks
    public static final Uri buildAlbumTracksUri(long albumID) {
      return Album.buildAlbumUri(albumID).buildUpon()
          .appendPath(PATH_TRACKS).build();
    }

    public static final long getAlbumFromUri(Uri uri) {
      return Long.valueOf(uri.getPathSegments().get(1));
    }

    //ITEM - URI to fetch details of given Track ID
    // content://com.har.dev.spotify/tracks/{id}
    public static final Uri buildTrackUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static final long getTrackFromUri(Uri uri) {
      return Long.valueOf(uri.getLastPathSegment());
    }

    public static final Track buildFrom(Cursor cursor) {
      int idx = -1;
      final Track track = new Track();
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + _ID)) >= 0) {
        track.id = cursor.getLong(idx);
      } else if ((idx = cursor.getColumnIndex(_ID)) >= 0) {
        track.id = cursor.getLong(idx);
      }
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + COLUMN_NAME)) >= 0) {
        track.name = cursor.getString(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_NAME)) >= 0) {
        track.name = cursor.getString(idx);
      }
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + COLUMN_DESCRIPTION)) >= 0) {
        track.description = cursor.getString(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_DESCRIPTION)) >= 0) {
        track.description = cursor.getString(idx);
      }
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + COLUMN_THUMBNAIL_URI)) >= 0) {
        track.thumbnailUri = Uri.parse(cursor.getString(idx));
      } else if ((idx = cursor.getColumnIndex(COLUMN_THUMBNAIL_URI)) >= 0) {
        track.thumbnailUri = Uri.parse(cursor.getString(idx));
      }
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + COLUMN_IMAGE_URI)) >= 0) {
        track.imageUri = Uri.parse(cursor.getString(idx));
      } else if ((idx = cursor.getColumnIndex(COLUMN_IMAGE_URI)) >= 0) {
        track.imageUri = Uri.parse(cursor.getString(idx));
      }
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + COLUMN_SONG_URI)) >= 0) {
        track.songUri = Uri.parse(cursor.getString(idx));
      } else if ((idx = cursor.getColumnIndex(COLUMN_SONG_URI)) >= 0) {
        track.songUri = Uri.parse(cursor.getString(idx));
      }
      track.album = Album.buildFrom(cursor);
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + COLUMN_ALBUM_ID)) >= 0) {
        track.album.id = cursor.getLong(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_ALBUM_ID)) >= 0) {
        track.album.id = cursor.getLong(idx);
      }
      if ((idx = cursor.getColumnIndex(Track.TABLE_NAME + "." + COLUMN_ARTIST_ID)) >= 0) {
        track.album.artist.id = cursor.getLong(idx);
      } else if ((idx = cursor.getColumnIndex(COLUMN_ARTIST_ID)) >= 0) {
        track.album.artist.id = cursor.getLong(idx);
      }
      return track;
    }
  }
}
