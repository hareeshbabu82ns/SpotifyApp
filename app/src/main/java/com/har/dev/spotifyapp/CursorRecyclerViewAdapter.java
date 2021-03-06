package com.har.dev.spotifyapp;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hareesh on 6/19/15.
 */

public abstract class CursorRecyclerViewAdapter<ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

  OnItemClickListener mItemClickListener = null;
  private Context mContext;
  private Cursor mCursor;
  private boolean mDataValid;
  private int mRowIdColumn;
  private DataSetObserver mDataSetObserver;

  public CursorRecyclerViewAdapter(Context context, Cursor cursor) {
    mContext = context;
    mCursor = cursor;
    mDataValid = cursor != null;
    mRowIdColumn = mDataValid ? mCursor.getColumnIndex(BaseColumns._ID) : -1;
    mDataSetObserver = new NotifyingDataSetObserver();
    if (mCursor != null) {
      mCursor.registerDataSetObserver(mDataSetObserver);
    }
  }

  public Cursor getCursor() {
    return mCursor;
  }

  @Override
  public int getItemCount() {
    if (mDataValid && mCursor != null) {
      return mCursor.getCount();
    }
    return 0;
  }

  @Override
  public long getItemId(int position) {
    if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
      return mCursor.getLong(mRowIdColumn);
    }
    return 0;
  }

  @Override
  public void setHasStableIds(boolean hasStableIds) {
    super.setHasStableIds(true);
  }

  public abstract void onBindViewHolder(ViewHolder viewHolder, Cursor cursor);

  @Override
  public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
    if (!mDataValid) {
      throw new IllegalStateException("this should only be called when the cursor is valid");
    }
    if (!mCursor.moveToPosition(position)) {
      throw new IllegalStateException("couldn't move cursor to position " + position);
    }
    onBindViewHolder(viewHolder, mCursor);
    if (mItemClickListener != null) {
      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mCursor.moveToPosition(viewHolder.getAdapterPosition());
          mItemClickListener.onItemClick(viewHolder, mCursor);
        }
      });
    }
  }

  /**
   * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
   * closed.
   */
  public void changeCursor(Cursor cursor) {
    Cursor old = swapCursor(cursor);
    if (old != null) {
      old.close();
    }
  }

  /**
   * Swap in a new Cursor, returning the old Cursor.  Unlike
   * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
   * closed.
   */
  public Cursor swapCursor(Cursor newCursor) {
    if (newCursor == mCursor) {
      return null;
    }
    final Cursor oldCursor = mCursor;
    if (oldCursor != null && mDataSetObserver != null) {
      oldCursor.unregisterDataSetObserver(mDataSetObserver);
    }
    mCursor = newCursor;
    if (mCursor != null) {
      if (mDataSetObserver != null) {
        mCursor.registerDataSetObserver(mDataSetObserver);
      }
      mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
      mDataValid = true;
      notifyDataSetChanged();
    } else {
      mRowIdColumn = -1;
      mDataValid = false;
      notifyDataSetChanged();
      //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
    }
    return oldCursor;
  }

  public OnItemClickListener getOnItemClickListener() {
    return mItemClickListener;
  }

  public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
    this.mItemClickListener = mItemClickListener;
  }

  public interface OnItemClickListener<ViewHolder extends RecyclerView.ViewHolder> {
    void onItemClick(ViewHolder viewHolder, Cursor cursor);
  }

  private class NotifyingDataSetObserver extends DataSetObserver {
    @Override
    public void onChanged() {
      super.onChanged();
      mDataValid = true;
      notifyDataSetChanged();
    }

    @Override
    public void onInvalidated() {
      super.onInvalidated();
      mDataValid = false;
      notifyDataSetChanged();
      //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
    }
  }
}