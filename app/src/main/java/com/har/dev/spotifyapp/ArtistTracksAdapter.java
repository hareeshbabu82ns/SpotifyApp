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

import com.squareup.picasso.Picasso;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ArtistTracksAdapter extends CursorRecyclerViewAdapter<ArtistTracksAdapter.ViewHolder> {
  final Context mContext;
  private final TypedValue mTypedValue = new TypedValue();
  private int mBackground;

  public ArtistTracksAdapter(Context context, Cursor cursor) {
    super(context, cursor);
    context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
    mBackground = mTypedValue.resourceId;
    mContext = context;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.track_item_layout, viewGroup, false);
    view.setBackgroundResource(mBackground);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
    viewHolder.trackName.setText(cursor.getString(ArtistTracksFragment.COLUMN_INDEX_TRACK_NAME));
    viewHolder.albumName.setText(cursor.getString(ArtistTracksFragment.COLUMN_INDEX_ALBUM_NAME));
    Picasso.with(mContext).load(Uri.parse(
        cursor.getString(ArtistTracksFragment.COLUMN_INDEX_TRACK_THUMBNAIL_URI)))
        .into(viewHolder.thumbnail);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView trackName;
    TextView albumName;
    ImageView thumbnail;

    public ViewHolder(View itemView) {
      super(itemView);
      trackName = (TextView) itemView.findViewById(R.id.trackName);
      albumName = (TextView) itemView.findViewById(R.id.albumName);
      thumbnail = (ImageView) itemView.findViewById(R.id.draweeView);
    }
  }
}
