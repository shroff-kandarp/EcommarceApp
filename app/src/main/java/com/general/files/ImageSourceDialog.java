package com.general.files;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.ecommarceapp.R;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Shroff on 25-Jan-18.
 */

public class ImageSourceDialog implements Runnable {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";

    Context mContext;

    GeneralFunctions generalFunc;
    private Uri fileUri;
    Fragment fragment;
    FileURICreateListener fileUriList;

    public ImageSourceDialog(Context mContext, Uri fileUri, Fragment fragment) {
        this.mContext = mContext;
        this.fileUri = fileUri;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        final Dialog dialog_img_update = new Dialog(mContext, R.style.ImageSourceDialogStyle);

        dialog_img_update.setContentView(R.layout.design_image_source_select);

        MTextView chooseImgHTxt = (MTextView) dialog_img_update.findViewById(R.id.chooseImgHTxt);
        chooseImgHTxt.setText("Choose Category");

        SelectableRoundedImageView cameraIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.cameraIconImgView);
        SelectableRoundedImageView galleryIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.galleryIconImgView);

        ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

        closeDialogImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
            }
        });

        new CreateRoundedView(mContext.getResources().getColor(R.color.appThemeColor_Dark_1), Utils.dipToPixels(mContext, 25), 0,
                Color.parseColor("#00000000"), cameraIconImgView);

        cameraIconImgView.setColorFilter(mContext.getResources().getColor(R.color.appThemeColor_TXT_1));

        new CreateRoundedView(mContext.getResources().getColor(R.color.appThemeColor_Dark_1), Utils.dipToPixels(mContext, 25), 0,
                Color.parseColor("#00000000"), galleryIconImgView);

        galleryIconImgView.setColorFilter(mContext.getResources().getColor(R.color.appThemeColor_TXT_1));


        cameraIconImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }

                if (!isDeviceSupportCamera()) {
                    generalFunc.showMessage(generalFunc.getCurrentView((Activity) mContext), "This device does not support camera.");
                } else {
                    chooseFromCamera();
                }

            }
        });

        galleryIconImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                chooseFromGallery();
            }
        });

        dialog_img_update.setCanceledOnTouchOutside(true);

        Window window = dialog_img_update.getWindow();
        window.setGravity(Gravity.BOTTOM);

        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog_img_update.show();

    }

    public void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (fragment != null) {
            fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        } else {
            ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }
    }

    public void chooseFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        if (fileUriList != null) {
            fileUriList.onFileUriCreate(fileUri);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (fragment != null) {
            fragment.startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } else {
            ((Activity) mContext).startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private boolean isDeviceSupportCamera() {
        if (mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public interface FileURICreateListener {
        void onFileUriCreate(Uri fileUri);
    }

    public void setFileUriListener(FileURICreateListener fileUriList) {
        this.fileUriList = fileUriList;
    }
}
