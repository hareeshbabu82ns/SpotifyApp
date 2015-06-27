package com.har.dev.spotifyapp;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * An {@link Service} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlayerService extends Service
    implements MediaPlayer.OnPreparedListener {
  // TODO: Rename actions, choose action names that describe tasks that this
  // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
  private static final String ACTION_PLAY = "com.har.dev.spotifyapp.action.PLAY";
  private static final String ACTION_PAUSE = "com.har.dev.spotifyapp.action.PAUSE";
  private static final String ACTION_STOP = "com.har.dev.spotifyapp.action.STOP";

  // TODO: Rename parameters
  private static final String EXTRA_SONG = "com.har.dev.spotifyapp.extra.song_uri";
  // Binder given to clients
  private final IBinder mBinder = new PlayerBinder();
  String mCurrentSong = null;
  MediaPlayer mPlayer = null;

  public PlayerService() {
  }

  /**
   * Starts this service to perform action PLAY with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void startActionPlay(Context context, String song) {
    Intent intent = new Intent(context, PlayerService.class);
    intent.setAction(ACTION_PLAY);
    intent.putExtra(EXTRA_SONG, song);
    context.startService(intent);
  }

  /**
   * Starts this service to perform action PAUSE with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void startActionPause(Context context, String song) {
    Intent intent = new Intent(context, PlayerService.class);
    intent.setAction(ACTION_PAUSE);
    intent.putExtra(EXTRA_SONG, song);
    context.startService(intent);
  }

  /**
   * Starts this service to perform action STOP with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void startActionStop(Context context) {
    Intent intent = new Intent(context, PlayerService.class);
    intent.setAction(ACTION_STOP);
    context.startService(intent);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    onHandleIntent(intent);
    // If we get killed, after returning from here, restart
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    mPlayer = new MediaPlayer();
    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    mPlayer.setOnPreparedListener(this);
    Log.d("Player", "Initializing");
  }

  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_PLAY.equals(action)) {
        final String song = intent.getStringExtra(EXTRA_SONG);
        handleActionPlay(song);
      } else if (ACTION_PAUSE.equals(action)) {
        final String song = intent.getStringExtra(EXTRA_SONG);
        handleActionPause(song);
      } else if (ACTION_STOP.equals(action)) {
        handleActionStop();
      }
    }
  }

  /**
   * Handle action Foo in the provided background thread with the provided
   * parameters.
   */
  public void handleActionPlay(String song) {
    if (mCurrentSong != null && mCurrentSong.equals(song)) {
      if (!mPlayer.isPlaying())
        mPlayer.start();
      else
        mPlayer.pause();
    } else {//switch song
      mPlayer.reset();//reset first
      try {
        mCurrentSong = song;
        mPlayer.setDataSource(song);
        mPlayer.prepareAsync();
//        mPlayer.prepare();
//        mPlayer.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Handle action Baz in the provided background thread with the provided
   * parameters.
   */
  public void handleActionPause(String song) {
    if (mCurrentSong.equals(song)) {
      if (mPlayer.isPlaying())
        mPlayer.pause();
    }
  }

  /**
   * Handle action Baz in the provided background thread with the provided
   * parameters.
   */
  public void handleActionStop() {
    if (mPlayer.isPlaying())
      mPlayer.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mPlayer.isPlaying())
      mPlayer.stop();
    mPlayer.release();
    mPlayer = null;
    Log.d("Player", "Destroying");
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onPrepared(MediaPlayer mp) {
    mp.start();
    Log.d("Player", "Playing: " + mCurrentSong);
  }

  /**
   * Class used for the client Binder.  Because we know this service always
   * runs in the same process as its clients, we don't need to deal with IPC.
   */
  public class PlayerBinder extends Binder {
    PlayerService getService() {
      // Return this instance of LocalService so clients can call public methods
      return PlayerService.this;
    }
  }
}
