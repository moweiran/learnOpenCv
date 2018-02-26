package com.ycy.learnopencv.Utils;

import android.graphics.Bitmap;

import com.ycy.learnopencv.Bean.OpenCVConstants;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by kimi9 on 2018/2/23.
 */

public class ImageProcessUtils {

    private static Mat sSrc = new Mat();
    private static Mat sDst = new Mat();
    private static int sWidth; //width
    private static int sHeight;  //height
    private static int sRow; //Row--height
    private static int sCol; //col--width
    private static int sIndex;

    //ARGB values
    private static int sA = 0;
    private static int sR = 0;
    private static int sG = 0;
    private static int sB = 0;
    private static int[] sPixels;
    private static int sPixel = 0;
    private static Mat sKernel;

    public static Bitmap covert2Gray(Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);//convert Bitmap to mat
        Imgproc.cvtColor(sSrc, sDst, Imgproc.COLOR_BGRA2GRAY);
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sSrc.release();
        sDst.release();
        return bitmap;
    }

    public static Bitmap invertMat(Bitmap bitmap) {

        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);

        //pixel operation
        //width of mat
        sWidth = sSrc.cols();
        //height of mat
        sHeight = sSrc.rows();
        int cnum = sSrc.channels();//Get channel

        byte[] bgra = new byte[cnum];//ARGB(Bitmap)-->BGRA(mat)

        for (sRow = 0; sRow < sHeight; sRow++) {
            for (sCol = 0; sCol < sWidth; sCol++) {
                sSrc.get(sRow, sCol, bgra);
                for (sIndex = 0; sIndex < cnum; sIndex++) {
                    bgra[sIndex] = (byte) (255 - bgra[sIndex] & 0xff);
                }
                sSrc.put(sRow, sCol, bgra);
            }
        }
        org.opencv.android.Utils.matToBitmap(sSrc, bitmap);
        sSrc.release();

        return bitmap;
    }

    public static Bitmap invertBitmap(Bitmap bitmap) {
        sWidth = bitmap.getWidth();
        sHeight = bitmap.getHeight();
        sPixels = new int[sWidth * sHeight];
        bitmap.getPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);

        sIndex = 0;
        for (sRow = 0; sRow < sHeight; sRow++) {
            sIndex = sRow * sWidth;
            for (sCol = 0; sCol < sWidth; sCol++) {
                sPixel = sPixels[sIndex];
                sA = (sPixel >> 24) & 0xff;
                sR = (sPixel >> 16) & 0xff;
                sG = (sPixel >> 8) & 0xff;
                sB = sPixel & 0xff;

                sR = 255 - sR;
                sG = 255 - sG;
                sB = 255 - sB;

                sPixel = ((sA & 0xff) << 24 | (sR & 0xff) << 16 | (sG & 0xff) << 8 | sB & 0xff);

                sPixels[sIndex] = sPixel;

                sIndex++;
            }
        }
        bitmap.setPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);
        return bitmap;
    }

    public static void contrast_ratio_adjust(Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        //This operation enables you to adjust CM in a float number.
        sSrc.convertTo(sSrc, CvType.CV_32F);
        Mat whiteImage = new Mat(sSrc.size(), sSrc.type(), Scalar.all(1.25));//Contrast Ratio
        Mat bwImage = new Mat(sSrc.size(), sSrc.type(), Scalar.all(30));//Brightness+30
        Core.multiply(whiteImage, sSrc, sSrc);
        Core.add(bwImage, sSrc, sSrc);
        sSrc.convertTo(sSrc, CvType.CV_8U);
        org.opencv.android.Utils.matToBitmap(sSrc, bitmap);
        //Don't forget to release.
        bwImage.release();
        whiteImage.release();
        sSrc.release();
    }

    public static void mat_operation(Bitmap bitmap) {
        sDst = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4, Scalar.all(127));
        //learn CV_8UC4,CV_8UC3,8UC1 and other figures
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sDst.release();
    }

    public static Bitmap getRoi(Bitmap bitmap) {
        Rect roi = new Rect(200, 150, 200, 300);
        Bitmap roiMap = Bitmap.createBitmap(roi.width, roi.height, Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        Mat roiMat = sSrc.submat(roi);
        Mat roiDstMat = new Mat();
        Imgproc.cvtColor(roiMat, roiDstMat, Imgproc.COLOR_BGRA2GRAY);
        org.opencv.android.Utils.matToBitmap(roiDstMat, roiMap);

        roiDstMat.release();
        roiMat.release();
        sSrc.release();
        return roiMap;

    }

    /**
     * These things should be in Imgproc, but I couldn't find them.
     * So these figures are based on official website of OpenCV.
     * <p>
     * public static final int	BORDER_DEFAULT	4
     * public static final int	BORDER_ISOLATED	16
     * public static final int	BORDER_REFLECT	2
     * public static final int	BORDER_REFLECT_101	4
     * public static final int	BORDER_REFLECT101	4
     * public static final int	BORDER_REPLICATE	1
     * public static final int	BORDER_TRANSPARENT	5
     * public static final int	BORDER_WRAP	3
     **/

    public static void boxBlur(Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);//convert Bitmap to mat
        Imgproc.blur(sSrc, sDst, new Size(15, 15), new Point(-1, -1), 4);
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sSrc.release();
        sDst.release();
    }

    public static void gaussianBlur(Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);//convert Bitmap to mat
        Imgproc.GaussianBlur(sSrc, sDst, new Size(5, 5), 0, 0, 4);
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sSrc.release();
        sDst.release();
    }

    public static void bilBlur(Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);//convert Bitmap to mat
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2BGR);
        Imgproc.bilateralFilter(sSrc, sDst, 15, 150, 15, 4);
        sKernel = new Mat(3, 3, CvType.CV_16S);
        sKernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        Imgproc.filter2D(sDst, sDst, -1, sKernel, new Point(-1, -1), 0.0, 4);
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sKernel.release();
        sSrc.release();
        sDst.release();
    }

    public static void customFilter(String command, Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        sKernel = getCustomOperator(command);
        Imgproc.filter2D(sSrc, sDst, -1, sKernel, new Point(-1, -1), 0.0, 4);
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sKernel.release();
        sSrc.release();
        sDst.release();
    }

    private static Mat getCustomOperator(String command) {
        sKernel = new Mat(3, 3, CvType.CV_32FC1);
        if (command.equals(OpenCVConstants.CUSTOM_BLUR_NAME)) {
            sKernel.put(0, 0, 1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0,
                    1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0,
                    1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0);
        } else if (command.equals(OpenCVConstants.CUSTOM_EDGE_NAME)) {
            sKernel.put(0, 0, -1, -1, -1, -1, 8, -1, -1, -1, -1);
        } else if (command.equals(OpenCVConstants.CUSTOM_SHARPEN_NAME)) {
            sKernel.put(0, 0, -1, -1, -1, -1, 9, -1, -1, -1, -1);
        }
        return sKernel;
    }

}
