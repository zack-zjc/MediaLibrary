package com.realcloud.media.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realcloud.loochadroid.ui.adapter.BaseRecyclerAdapter;
import com.realcloud.media.R;
import com.realcloud.media.model.Filter;
import com.realcloud.media.util.MediaCacheUtil;
import com.realcloud.media.view.GpuLoadableImageView;

import java.io.File;
import java.util.List;

/**
 * Created by zack on 2017/10/13.
 */

public class FilterAdapter extends BaseRecyclerAdapter implements View.OnClickListener{

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private View.OnClickListener onClickListener;
    private List<Filter> filterList;
    private Context mContext;
    private Filter mCurrentFilter;
    private int currentPosition = 0;

    public FilterAdapter(Context context){
        this.mContext =context;
        filterList = MediaCacheUtil.initFilter();
        mCurrentFilter = filterList.get(0);
    }

    /**
     * 设置当前滤镜
     * @param filter
     */
    public void setCurrentFilter(Filter filter){
        if (this.mCurrentFilter == null || (filter != null && !filter.equals(this.mCurrentFilter))){
            this.mCurrentFilter = filter;
            final int index = filterList.indexOf(filter);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(currentPosition,false);
                    notifyItemChanged(index,true);
                }
            });
        }
    }

    /**
     * 设置滤镜点击事件
     * @param onClickListener
     */
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_filter_cell, parent, false);
        FilterViewHolder filterViewHolder = new FilterViewHolder(view);
        filterViewHolder.itemView.setOnClickListener(this);
        return filterViewHolder;
    }

    @Override
    public void onBaseBindViewHolder(RecyclerView.ViewHolder viewHolder, int RealPosition) {
        FilterViewHolder filterViewHolder = (FilterViewHolder) viewHolder;
        Filter filter = filterList.get(RealPosition);
        filterViewHolder.mName.setText(TextUtils.isEmpty(filter.name) ? "" : filter.name);
        filterViewHolder.mSelectedState.setVisibility(filter.equals(mCurrentFilter)? View.VISIBLE:View.INVISIBLE);
        filterViewHolder.imageView.setBackgroundResource(filter.equals(mCurrentFilter) ? R.drawable.bg_bitmap_selected : 0);
        filterViewHolder.mName.setTextColor(filter.equals(mCurrentFilter) ? Color.parseColor("#ffde08") : Color.WHITE);
        if (filter.equals(mCurrentFilter)){
            currentPosition = RealPosition;
        }
        filterViewHolder.imageView.loadFilterImage(filter);
        filterViewHolder.itemView.setTag(filter);
    }

    @Override
    public void onBindViewHolderChange(RecyclerView.ViewHolder viewHolder, int position, List payloads) {
        boolean selected = (Boolean) payloads.get(0);
        FilterViewHolder filterViewHolder = (FilterViewHolder)viewHolder;
        filterViewHolder.mSelectedState.setVisibility(selected? View.VISIBLE:View.INVISIBLE);
        filterViewHolder.imageView.setBackgroundResource(selected? R.drawable.bg_bitmap_selected : 0);
        filterViewHolder.mName.setTextColor(selected ? Color.parseColor("#ffde08") : Color.WHITE);
        if (selected){
            currentPosition = getRealPosition(viewHolder);
        }
    }

    @Override
    public int getBaseItemCount() {
        return filterList.size();
    }

    @Override
    public void onClick(View view) {
        Filter filter = (Filter) view.getTag();
        if (filter != null && filter.gpuImageFilter != null){
            setCurrentFilter(filter);
            if (onClickListener != null){
                onClickListener.onClick(view);
            }
        }
    }

    static class FilterViewHolder  extends RecyclerView.ViewHolder{

        public GpuLoadableImageView imageView;
        public TextView mName;
        public ImageView mSelectedState;

        public FilterViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_image_view);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mName = itemView.findViewById(R.id.id_title);
            mSelectedState = itemView.findViewById(R.id.id_select_view);
        }
    }
}
