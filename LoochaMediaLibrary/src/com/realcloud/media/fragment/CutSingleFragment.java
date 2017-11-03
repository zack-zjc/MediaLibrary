package com.realcloud.media.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.realcloud.loochadroid.LoochaCookie;
import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.R;
import com.realcloud.media.model.Photo;

import net.bither.util.NativeUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zack on 2017/11/1.
 */

public class CutSingleFragment extends BaseSupportFragment implements View.OnClickListener {

    private CropImageView cropImageView;

    private int ratioX = 1,ratioY = 1;

    private String filePth;

    public static CutSingleFragment newInstance(int ratioX,int ratioY,String filePth) {
        Bundle args = new Bundle();
        CutSingleFragment fragment = new CutSingleFragment();
        fragment.setScale(ratioX,ratioY,filePth);
        fragment.setArguments(args);
        return fragment;
    }

    public void setScale(int x,int y,String filePth){
        this.ratioX = x;
        this.ratioY = y;
        this.filePth = filePth;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_crop_single_view,container,false);
        cropImageView = view.findViewById(R.id.id_crop_image);
        ImageView cut_finish = view.findViewById(R.id.id_cut_finish);
        cut_finish.setOnClickListener(this);
        ImageView cut_back = view.findViewById(R.id.id_cut_cancel);
        cut_back.setOnClickListener(this);
        cropImageView.setCropEnabled(true);
        cropImageView.setCropMode(CropImageView.CropMode.CUSTOM);
        cropImageView.setCustomRatio(ratioX,ratioY);
        cropImageView.load(Uri.fromFile(new File(filePth))).useThumbnail(true).execute(null);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_cut_finish){
            finishCut();
        }else if (v.getId() == R.id.id_cut_cancel){
            pop();
        }
    }

    /**
     * 保存视图
     * @param bitmap
     * @return
     */
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

    private void finishCut(){
        String path = saveImageFile(cropImageView.getCroppedBitmap());
        ArrayList<Photo> mediaList = new ArrayList<>();
        Photo photo = new Photo(1,"","",path);
        mediaList.add(photo);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MediaConstant.MEDIA_SELECT_RESULT,mediaList);
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK,intent);
        ActivityCompat.finishAfterTransition(getActivity());
    }
}
