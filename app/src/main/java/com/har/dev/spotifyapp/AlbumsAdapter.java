package com.har.dev.spotifyapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.har.dev.spotifyapp.data.SpotifyDBContract;
import com.squareup.picasso.Picasso;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class AlbumsAdapter extends CursorRecyclerViewAdapter<AlbumsAdapter.ViewHolder> {
  final Context mContext;
  private final TypedValue mTypedValue = new TypedValue();
  private int mBackground;

  public AlbumsAdapter(Context context, Cursor cursor) {
    super(context, cursor);
    context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
    mBackground = mTypedValue.resourceId;
    mContext = context;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.album_item_layout, viewGroup, false);
    view.setBackgroundResource(mBackground);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
    viewHolder.albumName.setText(cursor.getString(
        cursor.getColumnIndex(SpotifyDBContract.Album.COLUMN_NAME)));
    Picasso.with(mContext).load(Uri.parse(cursor.getString(
        cursor.getColumnIndex(SpotifyDBContract.Album.COLUMN_THUMBNAIL_URI))))
        .into(viewHolder.thumbnail);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView albumName;
    ImageView thumbnail;

    public ViewHolder(View itemView) {
      super(itemView);
      albumName = (TextView) itemView.findViewById(R.id.albumName);
      thumbnail = (ImageView) itemView.findViewById(R.id.draweeView);
    }
  }
}
