package com.realcloud.media.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.realcloud.loochadroid.ui.adapter.BaseRecyclerAdapter;
import com.realcloud.media.R;
import com.realcloud.media.model.Album;
import com.realcloud.media.view.LocalImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/8/30.
 */

public class AlbumAdapter extends BaseRecyclerAdapter implements View.OnClickListener{

    private Context mContext;
    private List<Album> albums;
    private AlbumListener mAlbumListener;

    public AlbumAdapter(Context context,AlbumListener albumListener){
        mContext = context;
        this.mAlbumListener = albumListener;
        albums = new ArrayList<>();
    }

    public void setData(List<Album> datas){
        List<Album> oldDatas = new ArrayList<>();
        oldDatas.addAll(albums);
        albums.clear();
        albums.addAll(datas);
        DiffUtil.DiffResult diffResult =
            DiffUtil.calculateDiff(new DiffCallBack(oldDatas, albums), true);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public RecyclerView.ViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_album_cell, parent, false);
        view.setOnClickListener(this);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBaseBindViewHolder(RecyclerView.ViewHolder viewHolder, int RealPosition) {
        AlbumViewHolder holder = (AlbumViewHolder) viewHolder;
        Album album = albums.get(RealPosition);
        holder.setAlbum(album);
    }

    @Override
    public int getBaseItemCount() {
        return albums.size();
    }

    @Override
    public void onClick(View view) {
        Album album = (Album) view.getTag();
        if(album != null && mAlbumListener != null){
            mAlbumListener.jumpFragment(album);
        }
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder{
        public LocalImageView image;
        public TextView name;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.id_album_view_cell_image);
            name = itemView.findViewById(R.id.id_album_view_cell_text);
        }

        public void setAlbum(Album album){
            itemView.setTag(album);
            name.setText(album.getDisplayName());
            image.loadImage(album.getCover());
        }
    }

    static class DiffCallBack extends DiffUtil.Callback {
        private List<Album> mOldDatas, mNewDatas;

        DiffCallBack(List<Album> mOldDatas, List<Album> mNewDatas) {
            this.mOldDatas = mOldDatas;
            this.mNewDatas = mNewDatas;
        }

        @Override
        public int getOldListSize() {
            return mOldDatas != null ? mOldDatas.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return mNewDatas != null ? mNewDatas.size() : 0;
        }
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mNewDatas.get(newItemPosition).equals(mOldDatas.get(oldItemPosition));
        }
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }

    public interface AlbumListener{
        void jumpFragment(Album album);
    }

}
