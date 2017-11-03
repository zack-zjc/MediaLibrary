package com.realcloud.media.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realcloud.loochadroid.ui.adapter.BaseRecyclerAdapter;
import com.realcloud.loochadroid.utils.ConvertUtil;
import com.realcloud.media.R;
import com.realcloud.media.model.Photo;
import com.realcloud.media.view.LocalImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/9/29.
 */

public class PhotoSelectAdapter extends BaseRecyclerAdapter implements View.OnClickListener{

    private Context mContext;
    private List<Photo> photos;
    private Photo mCurrentPhoto;
    private View.OnClickListener onClickListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public PhotoSelectAdapter(Context context){
        mContext = context;
        photos = new ArrayList<>();
    }

    public void setDatas(List<Photo> list){
        if (list != null && !list.isEmpty()){
            photos.clear();
            photos.addAll(list);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void addData(Photo photo){
        if (!photos.contains(photo)){
            photos.add(photo);
            notifyItemInserted(photos.size()-1);
        }
    }

    public void removeData(Photo photo){
        if (photos.contains(photo)){
            int index = photos.indexOf(photo);
            photos.remove(photo);
            notifyItemRemoved(index);
        }
    }

    public void setCurrentPhoto(Photo photo){
        mCurrentPhoto = photo;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemRangeChanged(0,getItemCount(),mCurrentPhoto);
            }
        });
    }

    public int getPhotoPosition(Photo photo){
        return photo != null && photos.contains(photo) ? photos.indexOf(photo) : 0;
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener){
        this.onClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_select_cell, parent, false);
        PhotoSelectViewHolder photoSelectViewHolder = new PhotoSelectViewHolder(view);
        photoSelectViewHolder.itemView.setOnClickListener(this);
        return photoSelectViewHolder;
    }

    @Override
    public void onBaseBindViewHolder(RecyclerView.ViewHolder viewHolder, int RealPosition) {
        PhotoSelectViewHolder holder = (PhotoSelectViewHolder) viewHolder;
        Photo photo = photos.get(RealPosition);
        holder.itemView.setTag(photo);
        holder.image.loadRoundImage(photo.getEditImagePath(), ConvertUtil.convertDpToPixel(3));
        if (mCurrentPhoto != null){
            holder.image.setBackgroundResource(mCurrentPhoto.equals(photo) ? R.drawable.bg_bitmap_selected : 0);
        }
    }

    @Override
    public void onBindViewHolderChange(RecyclerView.ViewHolder viewHolder, int position, List payloads) {
        super.onBindViewHolderChange(viewHolder, position, payloads);
        Object changePhoto = payloads.get(0);
        if (changePhoto != null && changePhoto instanceof Photo && viewHolder instanceof PhotoSelectViewHolder){
            Photo photo = photos.get(position);
            ((PhotoSelectViewHolder)viewHolder).image.setBackgroundResource(changePhoto.equals(photo) ? R.drawable.bg_bitmap_selected : 0);
        }
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


    static class PhotoSelectViewHolder extends RecyclerView.ViewHolder{
        public LocalImageView image;

        public PhotoSelectViewHolder(View itemView){
            super(itemView);
            image =  itemView.findViewById(R.id.id_image_select_cell);
        }
    }

}
