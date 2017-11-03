package com.realcloud.media.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.realcloud.loochadroid.LoochaCookie;
import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.loochadroid.utils.ConvertUtil;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.R;
import com.realcloud.media.adapter.FilterAdapter;
import com.realcloud.media.listener.MediaSelectListener;
import com.realcloud.media.model.Filter;
import com.realcloud.media.model.Photo;
import com.realcloud.media.util.MediaCacheUtil;

import net.bither.util.NativeUtil;

import java.io.File;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLookupFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by zack on 2017/10/13.
 */

public class EditFragment extends BaseSupportFragment implements View.OnClickListener{

    //当前页面状态
    private static final int STATE_NORMAL = 0;
    private static final int STATE_CUT    = 1;
    private static final int STATE_ROTATE  = 2;
    private static final int STATE_FILTER = 3;

    //选择的裁剪尺寸
    private static final int SIZE_FREE = 0;
    private static final int SIZE_2_AND_3 = 1;
    private static final int SIZE_3_AND_2 = 2;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    //媒体文件处理listener
    private MediaSelectListener mediaSelectListener;

    //头部
    private RelativeLayout mHeader;

    //底部
    private LinearLayout mFooter;

    //裁剪底部
    private RelativeLayout cutFooter;

    //旋转底部
    private RelativeLayout rotateFooter;

    //滤镜底部
    private RelativeLayout filterFooter;

    //裁剪尺寸
    private ImageView cut_free;
    private ImageView cut_23;
    private ImageView cut_32;

    //当前旋转的角度，取消旋转时需要对应切换
    private CropImageView.RotateDegrees mTotalDegree;

    //cropImageview
    private CropImageView cropImageView;

    //gpuImageview
    private GPUImageView gpuImageView;

    //滤镜适配器
    private FilterAdapter filterAdapter;

    //滤镜点击事件
    private View.OnClickListener filterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Filter filter = (Filter) view.getTag();
            if (filter != null && gpuImageView.getFilter() != filter.gpuImageFilter){
                gpuImageView.setImage(cropImageView.getImageBitmap());
                gpuImageView.setFilter(filter.gpuImageFilter);
            }
        }
    };

    public static EditFragment newInstance(MediaSelectListener mediaSelectListener) {
        Bundle args = new Bundle();
        EditFragment fragment = new EditFragment();
        fragment.setArguments(args);
        fragment.setMediaSelectListener(mediaSelectListener);
        return fragment;
    }

    public void setMediaSelectListener(MediaSelectListener mediaSelectListener){
        this.mediaSelectListener = mediaSelectListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_media_edit_view,container,false);
        cropImageView = view.findViewById(R.id.id_crop_image);
        gpuImageView = view.findViewById(R.id.id_gpu_image);
        mFooter = view.findViewById(R.id.id_footer);
        mHeader = view.findViewById(R.id.id_layout_header);
        cutFooter = view.findViewById(R.id.id_footer_cut);
        rotateFooter = view.findViewById(R.id.id_footer_crop);
        filterFooter = view.findViewById(R.id.id_footer_filter);
        RecyclerView mFilterView = view.findViewById(R.id.id_filter_list);
        mFilterView.setItemViewCacheSize(0);
        filterAdapter = new FilterAdapter(getContext());
        mFilterView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        filterAdapter.setOnClickListener(filterClickListener);
        mFilterView.setAdapter(filterAdapter);
        TextView complete = view.findViewById(R.id.id_complete);
        complete.setOnClickListener(this);
        ImageView back = view.findViewById(R.id.id_back);
        back.setOnClickListener(this);
        ImageView cut = view.findViewById(R.id.id_function_cut);
        cut.setOnClickListener(this);
        ImageView rotate = view.findViewById(R.id.id_function_rotate);
        rotate.setOnClickListener(this);
        ImageView filter = view.findViewById(R.id.id_function_filter);
        filter.setOnClickListener(this);
        cut_free = view.findViewById(R.id.id_cut_free);
        cut_free.setOnClickListener(this);
        cut_23 = view.findViewById(R.id.id_cut_23);
        cut_23.setOnClickListener(this);
        cut_32 = view.findViewById(R.id.id_cut_32);
        cut_32.setOnClickListener(this);
        ImageView cut_finish = view.findViewById(R.id.id_cut_finish);
        cut_finish.setOnClickListener(this);
        ImageView cut_back = view.findViewById(R.id.id_cut_cancel);
        cut_back.setOnClickListener(this);
        ImageView rotate_left = view.findViewById(R.id.id_rotate_left);
        rotate_left.setOnClickListener(this);
        ImageView rotate_right = view.findViewById(R.id.id_rotate_right);
        rotate_right.setOnClickListener(this);
        ImageView rotate_finish = view.findViewById(R.id.id_rotate_finish);
        rotate_finish.setOnClickListener(this);
        ImageView rotate_back = view.findViewById(R.id.id_rotate_cancel);
        rotate_back.setOnClickListener(this);
        ImageView filter_finish = view.findViewById(R.id.id_filter_finish);
        filter_finish.setOnClickListener(this);
        ImageView filter_back = view.findViewById(R.id.id_filter_cancel);
        filter_back.setOnClickListener(this);
        updateViewState(STATE_NORMAL);
        MediaCacheUtil.clear();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        resetImage();
    }

    /**
     * 初始化图片状态
     */
    private void resetImage(){
        gpuImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
        Bundle arguments = getArguments();
        Photo photo = (Photo) arguments.getSerializable(MediaConstant.PHOTO_CONTENT);
        if (photo != null){
            updateImage(photo.getEditImagePath());
        }
    }

    /**
     * 更新界面
     * @param filePath 文件的路径
     */
    private void updateImage(String filePath){
        if (!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            if (file.exists()){
                cropImageView.load(Uri.fromFile(file)).useThumbnail(true).execute(null);
                gpuImageView.setImage(file);
            }
        }
    }

    /**
     * 更新界面
     * @param bitmap 图片对象
     */
    private void updateImage(Bitmap bitmap){
        if (bitmap != null ){
            MediaCacheUtil.clear();
            filterAdapter.notifyDataSetChanged();
            MediaCacheUtil.setBitmap(bitmap);
            cropImageView.setImageBitmap(bitmap);
            gpuImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
            gpuImageView.setImage(bitmap);
        }
    }

    /**
     * 切换模式时调用
     * @param state 要切换的模式
     */
    private void updateViewState(int state){
        mTotalDegree = null;
        gpuImageView.setFilter(new GPUImageFilter());
        gpuImageView.requestRender();
        gpuImageView.setVisibility(state == STATE_FILTER ? View.VISIBLE : View.INVISIBLE);
        cropImageView.setVisibility(state != STATE_FILTER ? View.VISIBLE : View.INVISIBLE);
        cropImageView.setCropEnabled(state == STATE_CUT);
        if (state == STATE_CUT){
            cropImageView.setPadding(ConvertUtil.convertDpToPixel(10),ConvertUtil.convertDpToPixel(10),
                    ConvertUtil.convertDpToPixel(10),ConvertUtil.convertDpToPixel(10));
        }else{
            cropImageView.setPadding(0,0,0,0);
        }
        mFooter.setVisibility(state == STATE_NORMAL ? View.VISIBLE : View.GONE);
        mHeader.setVisibility(state == STATE_NORMAL ? View.VISIBLE : View.GONE);
        cutFooter.setVisibility(state == STATE_CUT ? View.VISIBLE : View.GONE);
        rotateFooter.setVisibility(state == STATE_ROTATE ? View.VISIBLE : View.GONE);
        filterFooter.setVisibility(state == STATE_FILTER ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新选择的裁剪尺寸
     * @param selectedSize 选择的尺寸
     */
    private void updateCutSizeImage(int selectedSize){
        cut_free.setImageResource(selectedSize == SIZE_FREE ? R.drawable.icon_cut_free_selected : R.drawable.icon_cut_free);
        cut_23.setImageResource(selectedSize == SIZE_2_AND_3 ? R.drawable.icon_cut_23_selected : R.drawable.icon_cut_23);
        cut_32.setImageResource(selectedSize == SIZE_3_AND_2 ? R.drawable.icon_cut_32_selected : R.drawable.icon_cut_32);
    }

    /**
     * 更新当前总共旋转的角度
     * @param actionDegress 旋转角
     */
    private void updateRoateAngle(CropImageView.RotateDegrees actionDegress){
        if (actionDegress == CropImageView.RotateDegrees.ROTATE_90D){
            if (mTotalDegree == null){
                mTotalDegree = CropImageView.RotateDegrees.ROTATE_90D;
            }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_90D || mTotalDegree == CropImageView.RotateDegrees.ROTATE_M270D){
                mTotalDegree = CropImageView.RotateDegrees.ROTATE_180D;
            }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_180D || mTotalDegree == CropImageView.RotateDegrees.ROTATE_M180D){
                mTotalDegree = CropImageView.RotateDegrees.ROTATE_270D;
            }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_270D
                    || mTotalDegree == CropImageView.RotateDegrees.ROTATE_M90D){
                mTotalDegree = null;
            }
        }else if (actionDegress == CropImageView.RotateDegrees.ROTATE_M90D){
            if (mTotalDegree == null){
                mTotalDegree = CropImageView.RotateDegrees.ROTATE_270D;
            }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_M90D
                    || mTotalDegree == CropImageView.RotateDegrees.ROTATE_270D){
                mTotalDegree = CropImageView.RotateDegrees.ROTATE_180D;
            }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_M180D
                    || mTotalDegree == CropImageView.RotateDegrees.ROTATE_180D){
                mTotalDegree = CropImageView.RotateDegrees.ROTATE_90D;
            }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_M270D
                    || mTotalDegree == CropImageView.RotateDegrees.ROTATE_90D){
                mTotalDegree = null;
            }
        }
    }

    /**
     * 取消旋转reset图片旋转角度
     */
    private void resetRotateAngle(){
        if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_90D){
            cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
        }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_180D){
            cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_180D);
        }else if (mTotalDegree == CropImageView.RotateDegrees.ROTATE_270D){
            cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_back){
            getActivity().onBackPressed();
        }else if (view.getId() == R.id.id_complete){
            //完成跟新viewPagerFragment
            Bundle bundle = new Bundle();
            String savePath = saveImageFile(cropImageView.getCroppedBitmap());
            Photo photo = (Photo) getArguments().getSerializable(MediaConstant.PHOTO_CONTENT);
            if (!TextUtils.isEmpty(savePath) && photo != null){
                photo.setImageChangedPath(savePath);
                bundle.putSerializable(MediaConstant.PHOTO_CONTENT,photo);
                setFragmentResult(Activity.RESULT_OK,bundle);
            }else{
                Toast.makeText(getContext(),R.string.str_save_fail,Toast.LENGTH_SHORT).show();
                setFragmentResult(Activity.RESULT_CANCELED,bundle);
            }
            pop();
        }else if (view.getId() == R.id.id_cut_cancel || view.getId() == R.id.id_filter_cancel
                || view.getId() == R.id.id_rotate_cancel){
            //取消操作返回正常编辑展示页面
            resetRotateAngle();
            updateViewState(STATE_NORMAL);
        }else if (view.getId() == R.id.id_function_cut){
            updateViewState(STATE_CUT); //切换裁剪页面
        }else if (view.getId() == R.id.id_function_rotate){
            updateViewState(STATE_ROTATE);//切换旋转页面
        }else if (view.getId() == R.id.id_function_filter){
            MediaCacheUtil.setBitmap(cropImageView.getCroppedBitmap());
            updateViewState(STATE_FILTER);//切换滤镜页面
        }else if (view.getId() == R.id.id_cut_free){
            //自由裁剪
            updateCutSizeImage(SIZE_FREE);
            cropImageView.setCropMode(CropImageView.CropMode.FREE);
        }else if (view.getId() == R.id.id_cut_23){
            //2:3裁剪
            updateCutSizeImage(SIZE_2_AND_3);
            cropImageView.setCropMode(CropImageView.CropMode.CUSTOM);
            cropImageView.setCustomRatio(2,3);
        }else if (view.getId() == R.id.id_cut_32){
            //3:2裁剪
            updateCutSizeImage(SIZE_3_AND_2);
            cropImageView.setCropMode(CropImageView.CropMode.CUSTOM);
            cropImageView.setCustomRatio(3,2);
        }else if (view.getId() == R.id.id_cut_finish){
            //裁剪完成
            updateImage(cropImageView.getCroppedBitmap());
            updateViewState(STATE_NORMAL);
        }else if (view.getId() == R.id.id_rotate_left){
            //向左旋转
            cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            updateRoateAngle(CropImageView.RotateDegrees.ROTATE_M90D);
        }else if (view.getId() == R.id.id_rotate_right){
            //向右旋转
            cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            updateRoateAngle(CropImageView.RotateDegrees.ROTATE_90D);
        }else if (view.getId() == R.id.id_rotate_finish){
            //旋转完成
            updateImage(cropImageView.getCroppedBitmap());
            updateViewState(STATE_NORMAL);
        }else if (view.getId() == R.id.id_filter_finish){
            //旋转完成
            updateImage(gpuImageView.getGPUImage().getBitmapWithFilterApplied());
            updateViewState(STATE_NORMAL);
        }
    }

    private String saveImageFile(Bitmap bitmap){
        String filePath = LoochaCookie.LOOCHA_TMP_PATH + "edit_" + "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(filePath);
        try {
            if (file.exists()){
                file.delete();
            }
            boolean result = file.createNewFile();
            if (result){
                NativeUtil.compressBitmap(bitmap,filePath,false);
                return filePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
