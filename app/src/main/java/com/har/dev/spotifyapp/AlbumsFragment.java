package com.har.dev.spotifyapp;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.har.dev.spotifyapp.data.SpotifyDBContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAlbumSelectedListener} interface
 * to handle interaction events.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment {

  private AlbumsAdapter mAdapter;

  private OnAlbumSelectedListener mListener;
  private Uri mArtistUri;

  public AlbumsFragment() {
    // Required empty public constructor
  }

  public static AlbumsFragment newInstance(Uri artistUri) {
    final AlbumsFragment fragment = new AlbumsFragment();
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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_albums, container, false);
  }

  public void onArtistSelected(Uri uri) {
    if (mListener != null) {
      mListener.onAlbumSelected(uri);
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupTracksList((RecyclerView) view.findViewById(R.id.recycler_view));
  }

  @Override
  public void onResume() {
    super.onResume();
    Cursor cursor = getActivity().getContentResolver().query(SpotifyDBContract.Album
        .buildArtistAlbumUri(Long.valueOf(mArtistUri.getLastPathSegment()))
        , null, null, null, null);
    mAdapter.swapCursor(cursor);
    Log.d("AlbumsActivity:", "cursor count: " + cursor.getCount());
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnAlbumSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnAlbumSelectedListener");
    }
  }

  private void setupTracksList(RecyclerView rv) {
    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    mAdapter = new AlbumsAdapter(getActivity(), null);
    rv.setAdapter(mAdapter);
    rv.setHasFixedSize(true);
    mAdapter.setOnItemClickListener(new CursorRecyclerViewAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
//        viewHolder.itemView.setClickable(true);
        AlbumsAdapter.ViewHolder holder = (AlbumsAdapter.ViewHolder) viewHolder;
        holder.albumName.setText(holder.albumName.getText() + " clicked");
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnAlbumSelectedListener {
    void onAlbumSelected(Uri uri);
  }

}
