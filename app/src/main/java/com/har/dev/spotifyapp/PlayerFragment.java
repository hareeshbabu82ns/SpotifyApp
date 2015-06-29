package com.har.dev.spotifyapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.har.dev.spotifyapp.data.SpotifyDBContract;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by hareesh on 6/27/15.
 */
public class PlayerFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>, PlayerService.PlayerCallbacks {

  public static final String[] PROJECTION_COLUMNS = {
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track._ID,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_ALBUM_ID,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_ARTIST_ID,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_NAME,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_SONG_URI,
      SpotifyDBContract.Track.TABLE_NAME + "." + SpotifyDBContract.Track.COLUMN_IMAGE_URI,
      SpotifyDBContract.Album.TABLE_NAME + "." + SpotifyDBContract.Album.COLUMN_NAME,
      SpotifyDBContract.Artist.TABLE_NAME + "." + SpotifyDBContract.Artist.COLUMN_NAME
  };

  public static final int COLUMN_INDEX_TRACK_ID = 0;
  public static final int COLUMN_INDEX_ALBUM_ID = 1;
  public static final int COLUMN_INDEX_ARTIST_ID = 2;
  public static final int COLUMN_INDEX_TRACK_NAME = 3;
  public static final int COLUMN_INDEX_TRACK_SONG_URI = 4;
  public static final int COLUMN_INDEX_TRACK_IMAGE_URI = 5;
  public static final int COLUMN_INDEX_ALBUM_NAME = 6;
  public static final int COLUMN_INDEX_ARTIST_NAME = 7;
  private static final long PROGRESS_UPDATE_INTERNAL = 1000;
  private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
  private final Handler mHandler = new Handler();
  private final ScheduledExecutorService mExecutorService =
      Executors.newSingleThreadScheduledExecutor();
  Uri mTrackUri;
  TextView mTxtArtistName;
  TextView mTxtAlbumName;
  TextView mTxtTrackName;
  TextView mTxtTimePlayed;
  TextView mTxtTimeRemaining;
  SeekBar mSeekBarPlayer;
  ImageButton mButPrevious;
  ImageButton mButPlay;
  ImageButton mButNext;
  ImageView mImgTrack;
  View mControls;
  ProgressBar mStatusProgress;
  int mSongLength;
  boolean mViewLoaded = false;
  private SpotifyDBContract.Track mTrack;
  private PlayerService mPlayerService;
  private final Runnable mUpdateProgressTask = new Runnable() {
    @Override
    public void run() {
      updateProgress();
    }
  };
  private ScheduledFuture<?> mScheduleFuture;
  private PlaybackState mLastPlaybackState;

  public static final PlayerFragment newInstance(Uri trackUri) {
    final PlayerFragment fragment = new PlayerFragment();
    final Bundle args = new Bundle();
    args.putParcelable(Utils.EXTRA_TRACK_URI, trackUri);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mTrackUri = getArguments().getParcelable(Utils.EXTRA_TRACK_URI);
    }
    getActivity().getLoaderManager().initLoader(0, null, this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_player_full, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mTxtAlbumName = (TextView) view.findViewById(R.id.txtAlbumName);
    mTxtArtistName = (TextView) view.findViewById(R.id.txtArtistName);
    mTxtTrackName = (TextView) view.findViewById(R.id.txtTrackName);
    mTxtTimePlayed = (TextView) view.findViewById(R.id.txtTimePlayed);
    mTxtTimeRemaining = (TextView) view.findViewById(R.id.txtTimeRemaining);
    mSeekBarPlayer = (SeekBar) view.findViewById(R.id.seekBarPlayer);
    mButNext = (ImageButton) view.findViewById(R.id.imgTrackNext);
    mButPlay = (ImageButton) view.findViewById(R.id.imgTrackPlay);
    mButPrevious = (ImageButton) view.findViewById(R.id.imgTrackPrev);
    mImgTrack = (ImageView) view.findViewById(R.id.imgTrackImage);
    mControls = view.findViewById(R.id.controllers);
    mStatusProgress = (ProgressBar) view.findViewById(R.id.progressStatus);
    mViewLoaded = true;
    initPlayerView();
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), mTrackUri, PROJECTION_COLUMNS, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (data.moveToFirst()) {
      mTrack = SpotifyDBContract.Track.buildFrom(data);
      mTrack.name = data.getString(COLUMN_INDEX_TRACK_NAME);
      mTrack.album.name = data.getString(COLUMN_INDEX_ALBUM_NAME);
      mTrack.album.artist.name = data.getString(COLUMN_INDEX_ARTIST_NAME);
      data.close();
    }
    initPlayerView();
  }

  private void initPlayerView() {
    if (!mViewLoaded || mTrack == null) return;
    mTxtArtistName.setText(mTrack.album.artist.name);
    mTxtAlbumName.setText(mTrack.album.name);
    mTxtTrackName.setText(mTrack.name);
    mStatusProgress.setVisibility(View.VISIBLE);
    Picasso.with(getActivity()).load(mTrack.imageUri)
//        .resize(100, 100).centerCrop()
        .fit()
        .into(mImgTrack);
    mPlayerService = SpotifyApp.getPlayerService();
    mPlayerService.setPlayerCallbacks(this);
    mPlayerService.handleActionPlay(mTrack.songUri.toString());

    mButPlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPlayerService.handleActionPlay(mTrack.songUri.toString());
      }
    });
    mSeekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          mPlayerService.mPlayer.seekTo(progress);
        }
        mTxtTimePlayed.setText(Utils.formatDuration(progress));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        stopSeekbarUpdate();
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        scheduleSeekbarUpdate();
      }
    });
    updateProgress();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mPlayerService.setPlayerCallbacks(null);
    stopSeekbarUpdate();
  }

  private void updateProgress() {
    mSeekBarPlayer.setProgress(mPlayerService.mPlayer.getCurrentPosition());
  }

  private void scheduleSeekbarUpdate() {
    stopSeekbarUpdate();
    if (!mExecutorService.isShutdown()) {
      mScheduleFuture = mExecutorService.scheduleAtFixedRate(
          new Runnable() {
            @Override
            public void run() {
              mHandler.post(mUpdateProgressTask);
            }
          }, PROGRESS_UPDATE_INITIAL_INTERVAL,
          PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
    }
  }

  private void stopSeekbarUpdate() {
    if (mScheduleFuture != null) {
      mScheduleFuture.cancel(false);
    }
  }


  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mTrack = null;
  }

  public void updatePlaybackState(MediaPlayer mp) {
    mTxtTimeRemaining.setText(Utils.formatDuration(mp.getDuration()));
    mSeekBarPlayer.setMax(mp.getDuration());
    mTxtTimePlayed.setText(Utils.formatDuration(mp.getCurrentPosition()));
  }

  @Override
  public boolean onPrepared(MediaPlayer mp) {
    updatePlaybackState(mp);
    scheduleSeekbarUpdate();
    mStatusProgress.setVisibility(View.GONE);
    mControls.setVisibility(View.VISIBLE);
    return true;//start playing now
  }

  @Override
  public void onPlay(MediaPlayer mp) {
    mStatusProgress.setVisibility(View.GONE);
    mControls.setVisibility(View.VISIBLE);
    updatePlaybackState(mp);
    mButPlay.setImageResource(android.R.drawable.ic_media_pause);
    scheduleSeekbarUpdate();
  }

  @Override
  public void onPause(MediaPlayer mp) {
    mStatusProgress.setVisibility(View.GONE);
    mControls.setVisibility(View.VISIBLE);
    updatePlaybackState(mp);
    mButPlay.setImageResource(android.R.drawable.ic_media_play);
    stopSeekbarUpdate();
  }

  @Override
  public void onCompleation(MediaPlayer mp) {
    updatePlaybackState(mp);
    mButPlay.setImageResource(android.R.drawable.ic_media_play);
    stopSeekbarUpdate();
  }
}
