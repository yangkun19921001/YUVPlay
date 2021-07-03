package com.devyk.opengl;

public class VideoUtils {
    public VideoUtils() {
    }

    public static void NV21ToI420pWithRotate90DegreeLeftwise(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int size2 = size * 5 / 4;
        int k = 0;

        int i;
        int j;
        int bv;
        for (i = width; i > 0; --i) {
            bv = 0;

            for (j = 0; j < height; ++j) {
                dst[k] = src[bv + i - 1];
                bv += width;
                ++k;
            }
        }

        k = 0;

        for (i = width; i > 0; i -= 2) {
            bv = 0;

            for (j = 0; j < height / 2; ++j) {
                dst[size + k] = src[size + bv + i - 1];
                dst[size2 + k] = src[size + bv + i - 2];
                bv += width;
                ++k;
            }
        }

    }

    public static void NV21ToI420pWithRotate90DegreeLeftwiseMi(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int size2 = size * 5 / 4;
        int k = 0;

        int i;
        int j;
        int bv;
        for (i = width; i > 0; --i) {
            bv = 0;

            for (j = 0; j < height; ++j) {
                dst[k] = src[bv + i - 1];
                bv += width;
                ++k;
            }
        }

        k = 0;

        for (i = width; i > 0; i -= 2) {
            bv = 0;

            for (j = 0; j < height / 2; ++j) {
                dst[size + k] = src[size + bv + i - 2];
                dst[size2 + k] = src[size + bv + i - 1];
                bv += width;
                ++k;
            }
        }

    }

    public static void NV21ToI420pWithRotate90DegreeRightwise(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;
        int maxv = (height - 1) * width;

        int i;
        int j;
        int bv;
        for (i = 0; i < width; ++i) {
            bv = maxv;

            for (j = height; j > 0; --j) {
                dst[k] = src[bv + i];
                bv -= width;
                ++k;
            }
        }

        k = 0;
        maxv = (height / 2 - 1) * width;

        for (i = 0; i < width; i += 2) {
            bv = maxv;

            for (j = height / 2; j > 0; --j) {
                dst[size + k] = src[size + bv + i + 1];
                dst[size + size / 4 + k] = src[size + bv + i];
                bv -= width;
                ++k;
            }
        }

    }

    public static void NV21ToI420pWithRotate90DegreeRightwiseMi(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;
        int maxv = (height - 1) * width;

        int i;
        int j;
        int bv;
        for (i = 0; i < width; ++i) {
            bv = maxv;

            for (j = height; j > 0; --j) {
                dst[k] = src[bv + i];
                bv -= width;
                ++k;
            }
        }

        k = 0;
        maxv = (height / 2 - 1) * width;

        for (i = 0; i < width; i += 2) {
            bv = maxv;

            for (j = height / 2; j > 0; --j) {
                dst[size + k] = src[size + bv + i];
                dst[size + size / 4 + k] = src[size + bv + i + 1];
                bv -= width;
                ++k;
            }
        }

    }

    public static void NV21Rotate90DegreeLeftwise(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;

        int i;
        int j;
        int bv;
        for (i = width; i > 0; --i) {
            bv = 0;

            for (j = 0; j < height; ++j) {
                dst[k] = src[bv + i - 1];
                bv += width;
                ++k;
            }
        }

        for (i = width; i > 0; i -= 2) {
            bv = 0;

            for (j = 0; j < height / 2; ++j) {
                dst[k++] = src[size + bv + i - 2];
                dst[k++] = src[size + bv + i - 1];
                bv += width;
            }
        }

    }

    public static void NV21Rotate90DegreeRightwise(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;
        int maxv = (height - 1) * width;

        int i;
        int j;
        int bv;
        for (i = 0; i < width; ++i) {
            bv = maxv;

            for (j = height; j > 0; --j) {
                dst[k] = src[bv + i];
                bv -= width;
                ++k;
            }
        }

        maxv = (height / 2 - 1) * width;

        for (i = 0; i < width; i += 2) {
            bv = maxv;

            for (j = height / 2; j > 0; --j) {
                dst[k++] = src[size + bv + i];
                dst[k++] = src[size + bv + i + 1];
                bv -= width;
            }
        }

    }

    public static void NV21Rotate90DegreeLeftwiseMi(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;

        int i;
        int j;
        int bv;
        for (i = width; i > 0; --i) {
            bv = 0;

            for (j = 0; j < height; ++j) {
                dst[k] = src[bv + i - 1];
                bv += width;
                ++k;
            }
        }

        for (i = width; i > 0; i -= 2) {
            bv = 0;

            for (j = 0; j < height / 2; ++j) {
                dst[k++] = src[size + bv + i - 1];
                dst[k++] = src[size + bv + i - 2];
                bv += width;
            }
        }

    }

    public static void NV21Rotate90DegreeRightwiseMi(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;
        int maxv = (height - 1) * width;

        int i;
        int j;
        int bv;
        for (i = 0; i < width; ++i) {
            bv = maxv;

            for (j = height; j > 0; --j) {
                dst[k] = src[bv + i];
                bv -= width;
                ++k;
            }
        }

        maxv = (height / 2 - 1) * width;

        for (i = 0; i < width; i += 2) {
            bv = maxv;

            for (j = height / 2; j > 0; --j) {
                dst[k++] = src[size + bv + i + 1];
                dst[k++] = src[size + bv + i];
                bv -= width;
            }
        }

    }

    public static void NV21ToI420pWithRotate180Degree(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;

        int i;
        for (i = size; i > 0; --i) {
            dst[k++] = src[i - 1];
        }

        k = 0;

        for (i = size / 2; i > 0; i -= 2) {
            dst[size + k] = src[size + i - 1];
            dst[size + size / 4 + k] = src[size + i - 2];
            ++k;
        }

    }

    public static void NV21ToI420pWithRotate180DegreeMi(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;

        int i;
        for (i = size; i > 0; --i) {
            dst[k++] = src[i - 1];
        }

        k = 0;

        for (i = size / 2; i > 0; i -= 2) {
            dst[size + k] = src[size + i - 2];
            dst[size + size / 4 + k] = src[size + i - 1];
            ++k;
        }

    }

    public static byte[] NV21ToI420p(int width, int height, byte[] src) {
        byte[] dst = new byte[src.length];
        int size = width * height;
        System.arraycopy(src, 0, dst, 0, size);
        int k = 0;

        for (int i = 0; i < size / 2; i += 2) {
            dst[size + k] = src[size + i + 1];
            dst[size + size / 4 + k] = src[size + i];
            ++k;
        }

        return dst;

    }



    public static void NV21ToI420pMi(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        System.arraycopy(src, 0, dst, 0, size);
        int k = 0;

        for (int i = 0; i < size / 2; i += 2) {
            dst[size + k] = src[size + i];
            dst[size + size / 4 + k] = src[size + i + 1];
            ++k;
        }

    }

    public static void NV21Rotate180Degree(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;

        int i;
        for (i = size; i > 0; --i) {
            dst[k++] = src[i - 1];
        }

        k = 0;

        for (i = size / 2; i > 0; i -= 2) {
            dst[size + k] = src[size + i - 2];
            dst[size + k + 1] = src[size + i - 1];
            k += 2;
        }

    }

    public static void NV21Rotate180DegreeMi(int width, int height, byte[] src, byte[] dst) {
        int size = width * height;
        int k = 0;

        int i;
        for (i = size; i > 0; --i) {
            dst[k++] = src[i - 1];
        }

        k = 0;

        for (i = size / 2; i > 0; i -= 2) {
            dst[size + k] = src[size + i - 1];
            dst[size + k + 1] = src[size + i - 2];
            k += 2;
        }

    }

    private void changeUV2(int w, int h, byte[] src) {
        int size = w * h;
        byte[] temp = new byte[w * h / 4];
        System.arraycopy(src, size, temp, 0, temp.length);
        System.arraycopy(src, size + size / 4, src, size, temp.length);
        System.arraycopy(temp, 0, src, size + size / 4, temp.length);
    }

    byte[] YUV420spRotateNegative90(byte[] src, int srcWidth, int height) {
        byte[] dst = new byte[src.length];
        int nWidth = 0;
        int nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (srcWidth != nWidth || height != nHeight) {
            wh = srcWidth * height;
            uvHeight = height >> 1;
        }

        int k = 0;

        int i;
        int nPos;
        int j;
        for (i = 0; i < srcWidth; ++i) {
            nPos = srcWidth - 1;

            for (j = 0; j < height; ++j) {
                dst[k] = src[nPos - i];
                ++k;
                nPos += srcWidth;
            }
        }

        for (i = 0; i < srcWidth; i += 2) {
            nPos = wh + srcWidth - 1;

            for (j = 0; j < uvHeight; ++j) {
                dst[k] = src[nPos - i - 1];
                dst[k + 1] = src[nPos - i];
                k += 2;
                nPos += srcWidth;
            }
        }

        return dst;
    }

    byte[] YUV420spRotatePositive90(byte[] src, int srcWidth, int height) {
        byte[] dst = new byte[src.length];
        int nWidth = 0;
        int nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (srcWidth != nWidth || height != nHeight) {
            nHeight = height;
            wh = srcWidth * height;
            uvHeight = height >> 1;
        }

        int k = 0;

        int i;
        int nPos;
        int j;
        for (i = 0; i < srcWidth; ++i) {
            nPos = (nHeight - 1) * srcWidth;

            for (j = 0; j < height; ++j) {
                dst[k] = src[nPos - i];
                ++k;
                nPos -= srcWidth;
            }
        }

        for (i = 0; i < srcWidth; i += 2) {
            nPos = wh + srcWidth - 1;

            for (j = 0; j < uvHeight; ++j) {
                dst[k] = src[nPos - i - 1];
                dst[k + 1] = src[nPos - i];
                k += 2;
                nPos += srcWidth;
            }
        }

        return dst;
    }

    void changeNV21ToY420(byte[] src, byte[] dst, int width, int heigth) {
        int size = width * heigth;

        for (int i = 0; i < size; ++i) {
            dst[i] = src[i];
        }

        for (int j = 0; j < size / 2; j += 2) {
            dst[j + size] = src[size + j + 1];
            dst[j + size / 4] = src[size + j];
        }

    }

    void YUV420spRotateClockwise90ToUV(byte[] des, byte[] src, int width, int height) {
        int size = width * height;
        int k = 0;
        int bindex = height - 1;
        int maxv = (height - 1) * width;

        int bv;
        int i;
        int j;
        for (i = 0; i < width; ++i) {
            bv = maxv;

            for (j = bindex; j >= 0; --j) {
                des[k] = src[bv + i];
                bv -= width;
                ++k;
            }
        }

        bindex = height / 2 - 1;
        maxv = (height / 2 - 1) * width;

        for (i = 0; i < width; i += 2) {
            bv = maxv;

            for (j = bindex; j >= 0; --j) {
                des[k] = src[size + bv + i];
                des[k + 1] = src[size + bv + i + 1];
                bv -= width;
                k += 2;
            }
        }

    }

    void YUV420spRotateClockwise90To420P(byte[] des, byte[] src, int width, int height) {
        int size = width * height;
        int k = 0;
        int bindex = height - 1;
        int maxv = (height - 1) * width;

        int bv;
        int i;
        int j;
        for (i = 0; i < width; ++i) {
            bv = maxv;

            for (j = bindex; j >= 0; --j) {
                des[k] = src[bv + i];
                bv -= width;
                ++k;
            }
        }

        bindex = height / 2 - 1;
        maxv = (height / 2 - 1) * width;
        k = 0;

        for (i = 0; i < width; i += 2) {
            bv = maxv;

            for (j = bindex; j >= 0; --j) {
                des[k + size] = src[size + bv + i + 1];
                des[size + size / 4 + k] = src[size + bv + i];
                bv -= width;
                ++k;
            }
        }

    }

    private void yuv420spToyuv420p(int w, int h, byte[] src) {
        int size = w * h;
        byte[] tmpUV = new byte[w * h / 2];
        System.arraycopy(src, size, tmpUV, 0, w * h / 2);
        for (int i = 0; i < size / 4; ++i) {
            src[size + i] = tmpUV[2 * i + 1];
            src[size + i + size / 4] = tmpUV[2 * i];
        }
    }

    public static void changeUV(int w, int h, byte[] src) {
        int size = w * h;

        for (int i = 0; i < size / 2; i += 2) {
            byte tmp = src[size + i];
            src[size + i] = src[size + i + 1];
            src[size + i + 1] = tmp;
        }
    }

    public static void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }

    public static void Nv21ToI420(byte[] data, byte[] dstData, int w, int h) {

        int size = w * h;
        // Y
        System.arraycopy(data, 0, dstData, 0, size);
        for (int i = 0; i < size / 4; i++) {
            dstData[size + i] = data[size + i * 2 + 1]; //U
            dstData[size + size / 4 + i] = data[size + i * 2]; //V
        }
    }

    public static void Nv21ToYuv420SP(byte[] data, byte[] dstData, int w, int h) {
        int size = w * h;
        // Y
        System.arraycopy(data, 0, dstData, 0, size);

        for (int i = 0; i < size / 4; i++) {
            dstData[size + i * 2] = data[size + i * 2 + 1]; //U
            dstData[size + i * 2 + 1] = data[size + i * 2]; //V
        }
    }

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                        + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

}