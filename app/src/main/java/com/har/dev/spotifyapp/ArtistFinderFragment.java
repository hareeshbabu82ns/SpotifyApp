package com.har.dev.spotifyapp;

import android.app.Activity;
import android.app.Fragment;
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
 * {@link OnArtistSelectedListener} interface
 * to handle interaction events.
 * Use the {@link ArtistFinderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistFinderFragment extends Fragment {
  private static final String ARG_ARTIST_SEARCH_TERM = "artist_search_term";

  private String mSearchTerm;
  private ArtistsAdapter mAdapter;

  private OnArtistSelectedListener mListener;

  public ArtistFinderFragment() {
    // Required empty public constructor
  }

  public static ArtistFinderFragment newInstance(String search) {
    ArtistFinderFragment fragment = new ArtistFinderFragment();
    Bundle args = new Bundle();
    args.putString(ARG_ARTIST_SEARCH_TERM, search);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mSearchTerm = getArguments().getString(ARG_ARTIST_SEARCH_TERM);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_artist_finder, container, false);
  }

  public void onArtistSelected(Uri uri) {
    if (mListener != null) {
      mListener.onArtistSelected(uri);
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupTracksList((RecyclerView) view.findViewById(R.id.tracks_recycler_view));
  }

  @Override
  public void onResume() {
    super.onResume();
    Cursor cursor = getActivity().getContentResolver().query(SpotifyDBContract.Artist.CONTENT_URI
        , null, null, null, null);
    mAdapter.swapCursor(cursor);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnArtistSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  private void setupTracksList(RecyclerView rv) {
    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    mAdapter = new ArtistsAdapter(getActivity(), null);
    rv.setAdapter(mAdapter);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnArtistSelectedListener {
    void onArtistSelected(Uri uri);
  }

}
