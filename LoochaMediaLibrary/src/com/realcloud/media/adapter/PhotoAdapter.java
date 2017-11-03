package com.realcloud.media.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.realcloud.loochadroid.ui.adapter.BaseRecyclerAdapter;
import com.realcloud.media.R;
import com.realcloud.media.model.Photo;
import com.realcloud.media.view.LocalCoverView;
import com.realcloud.media.view.LocalImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/8/30.
 */

public class PhotoAdapter extends BaseRecyclerAdapter implements View.OnClickListener{

    private Context mContext;
    private List<Photo> photos;
    private PhotoListener mPhotoListener;
    private boolean isChooseSingle; //判断是否单选图片，如果是，直接跳转裁剪界面

    public PhotoAdapter(Context context,PhotoListener photoListener,boolean isChooseSingle){
        mContext = context;
        mPhotoListener = photoListener;
        photos = new ArrayList<>();
        this.isChooseSingle = isChooseSingle;
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

    @Override
    public RecyclerView.ViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_grid_view_cell, parent, false);
        PhotoViewHolder photoViewHolder = new PhotoViewHolder(view);
        photoViewHolder.itemView.setOnClickListener(this);
        photoViewHolder.checkBox.setOnClickListener(this);
        return photoViewHolder;
    }

    @Override
    public void onBaseBindViewHolder(RecyclerView.ViewHolder viewHolder, int RealPosition) {
        PhotoViewHolder holder = (PhotoViewHolder) viewHolder;
        Photo photo = photos.get(RealPosition);
        holder.itemView.setTag(photo);
        holder.checkBox.setTag(photo);
        if (photo.isImage()){
            holder.image.loadImage(photo.getFilePath());
        }else{
            holder.image.loadVideoImage(photo.getFilePath(),photo.getVideoThumb());
        }
        holder.time.setText(photo.getDuration());
        holder.checkBox.setVisibility(photo.isImage() && !isChooseSingle ? View.VISIBLE : View.INVISIBLE);
        if (mPhotoListener != null) {
            holder.checkBox.setChecked(mPhotoListener.isPhotoSelect(photo));
            holder.cover.setVisibility(photo.isImage() || mPhotoListener.canVideoSelect() ? View.INVISIBLE : View.VISIBLE);
        }
    }

    /**
     * 修改视频是否可以选择
     * @param viewHolder
     * @param position
     * @param payloads
     */
    @Override
    public void onBindViewHolderChange(RecyclerView.ViewHolder viewHolder, int position, List payloads) {
        Photo photo = photos.get(position);
        if (viewHolder instanceof PhotoViewHolder && mPhotoListener != null) {
            ((PhotoViewHolder)viewHolder).checkBox.setChecked(mPhotoListener.isPhotoSelect(photo));
            ((PhotoViewHolder)viewHolder).cover.setVisibility(photo.isImage() || mPhotoListener.canVideoSelect() ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public int getBaseItemCount() {
        return photos.size();
    }

    @Override
    public void onClick(View view) {
        Photo photo = (Photo) view.getTag();
        if (view.getId() == R.id.id_grid_view_cell_checkbox){
            if (photo != null && mPhotoListener != null){
                mPhotoListener.selectPhoto(photo);
                notifyItemRangeChanged(0,getItemCount(),photos);
            }
        }else{
            if (photo != null && mPhotoListener != null){
                if (photo.isImage() || mPhotoListener.canVideoSelect()){
                    mPhotoListener.jumpPhoto(photo);
                }
            }
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder{
        public LocalImageView image;
        public CheckBox checkBox;
        public TextView time;
        public LocalCoverView cover;

        public PhotoViewHolder(View itemView){
            super(itemView);
            image =  itemView.findViewById(R.id.id_grid_view_cell);
            time =  itemView.findViewById(R.id.id_time);
            checkBox = itemView.findViewById(R.id.id_grid_view_cell_checkbox);
            cover = itemView.findViewById(R.id.id_cover);
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

    public interface PhotoListener{
        void jumpPhoto(Photo photo);
        void selectPhoto(Photo photo);
        boolean isPhotoSelect(Photo photo);
        boolean canVideoSelect();
    }
}
