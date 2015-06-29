package com.har.dev.spotifyapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.har.dev.spotifyapp.data.SpotifyDBContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTrackSelectedListener} interface
 * to handle interaction events.
 * Use the {@link ArtistTracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistTracksFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String[] PROJECTION_COLUMNS = {
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track._ID,
      SpotifyDBContract.Album.TABLE_NAME + "." + SpotifyDBContract.Album._ID,
      SpotifyDBContract.Album.TABLE_NAME + "." + SpotifyDBContract.Album.COLUMN_NAME,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_ARTIST_ID,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_NAME,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_DESCRIPTION,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_IMAGE_URI,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_THUMBNAIL_URI,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_SONG_URI,
  };
  public static final int COLUMN_INDEX_TRACK_ID = 0;
  public static final int COLUMN_INDEX_ALBUM_ID = 1;
  public static final int COLUMN_INDEX_ALBUM_NAME = 2;
  public static final int COLUMN_INDEX_ARTIST_ID = 3;
  public static final int COLUMN_INDEX_TRACK_NAME = 4;
  public static final int COLUMN_INDEX_TRACK_DESCRIPTION = 5;
  public static final int COLUMN_INDEX_TRACK_IMAGE_URI = 6;
  public static final int COLUMN_INDEX_TRACK_THUMBNAIL_URI = 7;
  public static final int COLUMN_INDEX_TRACK_SONG_URI = 8;
  private ArtistTracksAdapter mAdapter;
  private OnTrackSelectedListener mListener;
  private Uri mArtistUri;

  public ArtistTracksFragment() {
    // Required empty public constructor
  }

  public static ArtistTracksFragment newInstance(Uri artistUri) {
    final ArtistTracksFragment fragment = new ArtistTracksFragment();
    final Bundle args = new Bundle();
    args.putParcelable(Utils.EXTRA_ARTIST_URI, artistUri);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mArtistUri = getArguments().getParcelable(Utils.EXTRA_ARTIST_URI);
    }
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_albums, container, false);
  }

  public void onTrackSelected(Uri trackUri, Uri songUri) {
    if (mListener != null) {
      mListener.onTrackSelected(trackUri, songUri);
    }
//    PlayerService.startActionPlay(getActivity(), songUri.toString());
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupTracksList((RecyclerView) view.findViewById(R.id.recycler_view));
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnTrackSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnTrackSelectedListener");
    }
  }

  private void setupTracksList(RecyclerView rv) {
    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    mAdapter = new ArtistTracksAdapter(getActivity(), null);
    rv.setAdapter(mAdapter);
    rv.setHasFixedSize(true);
    mAdapter.setOnItemClickListener(new CursorRecyclerViewAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
//        viewHolder.itemView.setClickable(true);
        ArtistTracksAdapter.ViewHolder holder = (ArtistTracksAdapter.ViewHolder) viewHolder;
        holder.albumName.setText(holder.albumName.getText() + " clicked");
        onTrackSelected(SpotifyDBContract.Track.buildTrackUri(cursor.getLong(COLUMN_INDEX_TRACK_ID))
            , Uri.parse(cursor.getString(COLUMN_INDEX_TRACK_SONG_URI)));
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
//    getActivity().stopService(new Intent(getActivity(), PlayerService.class));
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), SpotifyDBContract.Track
        .buildArtistTracksUri(Long.valueOf(mArtistUri.getLastPathSegment()))
        , PROJECTION_COLUMNS, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.changeCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.changeCursor(null);
  }

  public interface OnTrackSelectedListener {
    void onTrackSelected(Uri uri, Uri song);
  }

}
