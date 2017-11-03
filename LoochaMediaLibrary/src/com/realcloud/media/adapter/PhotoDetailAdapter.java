package com.realcloud.media.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realcloud.loochadroid.ui.adapter.BaseRecyclerAdapter;
import com.realcloud.media.R;
import com.realcloud.media.model.Photo;
import com.realcloud.media.view.SimpleLocalImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/9/29.
 */

public class PhotoDetailAdapter extends BaseRecyclerAdapter implements View.OnClickListener{

    private Context mContext;
    private List<Photo> photos;
    private View.OnClickListener onClickListener;

    public PhotoDetailAdapter(Context context){
        mContext = context;
        photos = new ArrayList<>();
    }

    public void setData(List<Photo> datas){
        List<Photo> oldDatas = new ArrayList<>();
        oldDatas.addAll(photos);
        photos.clear();
        photos.addAll(datas);
        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(new DiffCallBack(oldDatas, photos), true);
        diffResult.dispatchUpdatesTo(this);
    }

    public Photo getPhoto(int position){
        return photos.get(position);
    }

    public int getPhotoPosition(Photo photo){
        return photo != null &&photos.contains(photo) ? photos.indexOf(photo) : 0 ;
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener){
        this.onClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_pager_cell, parent, false);
        view.setOnClickListener(this);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBaseBindViewHolder(RecyclerView.ViewHolder viewHolder, int RealPosition) {
        PhotoViewHolder holder = (PhotoViewHolder) viewHolder;
        Photo photo = photos.get(RealPosition);
        holder.setPhoto(photo);
    }

    @Override
    public int getBaseItemCount() {
        return photos.size();
    }

    @Override
    public void onClick(View view) {
        if (onClickListener != null){
            onClickListener.onClick(view);
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder{
        public SimpleLocalImageView image;

        public PhotoViewHolder(View itemView){
            super(itemView);
            image =  itemView.findViewById(R.id.id_pager_view_cell);
        }

        public void setPhoto(Photo photo){
            itemView.setTag(photo);
            image.loadImage(photo.getEditImagePath());
        }
    }

    static class DiffCallBack extends DiffUtil.Callback {
        private List<Photo> mOldDatas, mNewDatas;

        DiffCallBack(List<Photo> mOldDatas, List<Photo> mNewDatas) {
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

}
