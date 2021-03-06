package com.ycy.learnopencv.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blankj.utilcode.util.Utils;
import com.ycy.learnopencv.Bean.OpenCVConstants;
import com.ycy.learnopencv.R;
import com.ycy.learnopencv.Utils.ImageProcessUtils;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProcessActivity extends AppCompatActivity {

    @BindView(R.id.btn_process)
    Button mBtnProcess;
    @BindView(R.id.iv_process)
    ImageView mIvProcess;
    @BindView(R.id.btn_select)
    Button mBtnSelect;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Bitmap mBitmap;
    public static final int SELECT_PIC_RESULT_CODE = 202;
    private int maxSize = 1024;
    private String processName;
    private CascadeClassifier mFaceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ButterKnife.bind(this);
        Utils.init(this);
        processName = this.getIntent().getStringExtra("name");
        mBtnProcess.setText(processName);
        actionBarSetting();

        try {
            initFaceDetector();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_image_template_test);

        mIvProcess.setImageBitmap(mBitmap);
    }

    private void initFaceDetector() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
        File cascadeDir = this.getDir("cascade", Context.MODE_PRIVATE);
        File file = new File(cascadeDir.getAbsoluteFile(), "lbpcascade_frontalface.xml");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buff)) != -1) {
            fileOutputStream.write(buff, 0, len);
        }
        inputStream.close();
        fileOutputStream.close();

        mFaceDetector = new CascadeClassifier(file.getAbsolutePath());

        file.delete();
        cascadeDir.delete();
    }

    private void actionBarSetting() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(processName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @OnClick({R.id.btn_process, R.id.btn_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_process:
                select2Process();
                break;
            case R.id.btn_select:
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickIntent, "Browser Image……"),
                        SELECT_PIC_RESULT_CODE);
                break;
        }
    }

    private void select2Process() {
        Bitmap temp = mBitmap.copy(mBitmap.getConfig(), true);
        if (OpenCVConstants.GRAY_TEST_NAME.equals(processName)) {
            temp = ImageProcessUtils.covert2Gray(temp);
        } else if (OpenCVConstants.MAT_PIXEL_INVERT_NAME.equals(processName)) {
            temp = ImageProcessUtils.invertMat(temp);
        } else if (OpenCVConstants.BITMAP_PIXEL_INVERT_NAME.equals(processName)) {
            temp = ImageProcessUtils.invertBitmap(temp);
        } else if (OpenCVConstants.CONTRAST_RATIO_BRIGHTNESS_NAME.equals(processName)) {
            ImageProcessUtils.contrast_ratio_adjust(temp);
        } else if (OpenCVConstants.IMAGE_CONTAINER_MAT_NAME.equals(processName)) {
            ImageProcessUtils.mat_operation(temp);
        } else if (OpenCVConstants.GET_ROI_NAME.equals(processName)) {
            temp = ImageProcessUtils.getRoi(temp);
        } else if (OpenCVConstants.BOX_BLUR_IMAGE_NAME.equals(processName)) {
            ImageProcessUtils.boxBlur(temp);
        } else if (OpenCVConstants.GAUSSIAN_BLUR_IMAGE_NAME.equals(processName)) {
            ImageProcessUtils.gaussianBlur(temp);
        } else if (OpenCVConstants.BILATERAL_BLUR_IMAGE_NAME.equals(processName)) {
            ImageProcessUtils.bilBlur(temp);
        } else if (OpenCVConstants.CUSTOM_BLUR_NAME.equals(processName)
                || OpenCVConstants.CUSTOM_EDGE_NAME.equals(processName)
                || OpenCVConstants.CUSTOM_SHARPEN_NAME.equals(processName)) {
            ImageProcessUtils.customFilter(processName, temp);
        } else if (OpenCVConstants.ERODE_NAME.equals(processName)
                || OpenCVConstants.DILATE_NAME.equals(processName)) {
            ImageProcessUtils.erodeOrDilate(processName, temp);
        } else if (OpenCVConstants.OPEN_OPERATION_NAME.equals(processName)
                || OpenCVConstants.CLOSE_OPERATION_NAME.equals(processName)) {
            ImageProcessUtils.openOrClose(processName, temp);
        } else if (OpenCVConstants.MORPH_LINE_OPERATION_NAME.equals(processName)) {
            ImageProcessUtils.lineDetection(temp);
        } else if (OpenCVConstants.THRESH_BINARY_NAME.equals(processName)
                || OpenCVConstants.THRESH_BINARY_INV_NAME.equals(processName)
                || OpenCVConstants.THRESH_TRUNCAT_NAME.equals(processName)
                || OpenCVConstants.THRESH_ZERO_NAME.equals(processName)) {
            ImageProcessUtils.thresholdImg(processName, temp);
        } else if (OpenCVConstants.ADAPTIVE_THRESH_MEAN_NAME.equals(processName)
                || OpenCVConstants.ADAPTIVE_THRESH_GAUSSIAN_NAME.equals(processName)) {
            ImageProcessUtils.adaptiveThresholdImg(processName, temp);
        } else if (OpenCVConstants.HISTOGRAM_EQ_NAME.equals(processName)) {
            ImageProcessUtils.histogramEq(temp);
        } else if (OpenCVConstants.GRADIENT_SOBEL_X_NAME.equals(processName)
                || OpenCVConstants.GRADIENT_SOBEL_Y_NAME.equals(processName)) {
            ImageProcessUtils.gradientProcess(processName, temp);
        } else if (OpenCVConstants.GRADIENT_IMG_NAME.equals(processName)) {
            ImageProcessUtils.gradientXY(temp);
        } else if (OpenCVConstants.TEMPLATE_MATCH_NAME.equals(processName)) {
            Bitmap tpl = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_image_template_sample);
            ImageProcessUtils.templateMatch(tpl, temp);
        } else if (OpenCVConstants.FIND_FACE_NAME.equals(processName)) {
            ImageProcessUtils.faceDetector(temp, mFaceDetector);
        }

        mIvProcess.setImageBitmap(temp);
    }

    /**
     * ARGB_8888：
     * A->8bit->一个字节，
     * R->8bit->一个字节，
     * G->8bit->一个字节，
     * B->8bit->一个字节，
     * 即8888，一个像素总共占四个字节，8+8+8+8=32bit=8byte
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PIC_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(inputStream, null, options);

                int height = options.outHeight;
                int width = options.outWidth;
                int sampleSize = 1;
                int max = Math.max(height, width);

                if (max > maxSize) {
                    int nw = width / 2;
                    int nh = height / 2;
                    while ((nw / sampleSize) > maxSize || (nh / sampleSize) > maxSize) {
                        sampleSize *= 2;
                    }
                }

                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                mBitmap = BitmapFactory.decodeStream(getContentResolver().
                        openInputStream(uri), null, options);
                mIvProcess.setImageBitmap(mBitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}


