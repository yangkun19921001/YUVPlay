package com.devyk.opengl

import android.graphics.ImageFormat
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.lkl.opengl.R
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity(), Camera.PreviewCallback {
    companion object {
        private const val TAG = "MainActivity"
    }

    private var mCamera: Camera? = null
    // 设定默认的预览宽高
    private var mPreviewWidth = 1280
    private var mPreviewHeight = 720

    private lateinit var mBuffer:ByteArray

    private lateinit var mPlayManager: PlayManager

    private lateinit var mSurfaceHolder: SurfaceHolder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlay();
        addHolderCallback()
    }

    private fun initPlay() {
        mPlayManager = PlayManager.PlayManagerBuilder().
            /**
             * 预览的宽
             */
            withPreviewWidth(mPreviewWidth)
            /**
             * 预览的高
             */
            .withPreviewHeight(mPreviewHeight)
            /**
             * 必须传入一个 ViewGroup 框架会自动绑定播放控件
             */
            .bindPlayControl(play)
            /**
             * 是否开始播放，现在没有传入 byte 数据，最好设置为 false
             */
            .withRequestRender(false)
            /**
             * 通过 builder 构建播放管理类
             */
            .build(applicationContext)

        /**
         * init 播放器
         */
        mPlayManager.initPlayControl()

    }

    private fun addHolderCallback() {
        cameraPreSurface.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                releaseCamera()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                mSurfaceHolder = holder
                initCamera()
            }
        })
    }

    private fun initCamera() {
        try {
            mCamera = openCamera()

            mCamera?.apply {
                var params: Camera.Parameters = parameters
                val sizes = params.supportedPreviewSizes
                if (sizes != null) {
                    /* 选择一个最为合适的框口 */
                    calculateCameraFrameSize(sizes, mPreviewWidth, mPreviewHeight)
                }
                params.previewFormat = ImageFormat.NV21
                params.setPreviewSize(mPreviewWidth, mPreviewHeight)
                Log.d(TAG, "Set preview size to $mPreviewWidth x $mPreviewHeight")

                parameters = params
                var size = mPreviewWidth * mPreviewHeight
                size = size * ImageFormat.getBitsPerPixel(params.previewFormat) / 8
                mBuffer = ByteArray(size)
                addCallbackBuffer(mBuffer)
                setPreviewCallbackWithBuffer(this@MainActivity)
                setPreviewDisplay(mSurfaceHolder)
                setDisplayOrientation(90)
                startPreview()//开始预览
            }
        } catch (e: Exception) {
            Log.w(TAG, e.message)
        }
    }


private lateinit var i420 :ByteArray
    /**
     * 预览回调
     */
    override fun onPreviewFrame(data: ByteArray, camera: Camera?) {
//        i420 = ByteArray(data.size)

        //传入 YUV 数据开始预览
//        YuvUtil.convertNV21ToI420(data,i420,mPreviewWidth,mPreviewHeight);
        mPlayManager?.startPlay(data)
        camera?.addCallbackBuffer(mBuffer)
    }



    /**
     * 打开Camera
     */
    private fun openCamera(): Camera? {
        Log.d(TAG, "Trying to open camera with old open()")
        var camera: Camera? = null
        try {
            camera = Camera.open(1)
        } catch (e: Exception) {
            Log.w(TAG, "Camera is not available (in use or does not exist): ${e.message}")
        }

        if (camera == null) {
            var connected = false
            for (camIdx in 0 until Camera.getNumberOfCameras()) {
                Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + ")")
                try {
                    camera = Camera.open(camIdx)
                    connected = true
                } catch (e: RuntimeException) {
                    Log.w(TAG, "Camera #$camIdx failed to open: ${e.message}")
                }

                if (connected) break
            }
        }

        return camera
    }

    /**
     * 释放Camera资源
     */
    private fun releaseCamera() {
        mCamera?.apply {
            stopPreview()
            setPreviewCallback(null)
            release()
        }
        mCamera = null
    }

    /**
     * 找到一个最为合适的预览 size
     */
    private fun calculateCameraFrameSize(supportedSizes: List<*>, maxAllowedWidth: Int, maxAllowedHeight: Int) {
        var calcWidth = 0
        var calcHeight = 0

        for (size in supportedSizes) {
            val cameraSize = size as Camera.Size
            val width = cameraSize.width
            val height = cameraSize.height

            if (width <= maxAllowedWidth && height <= maxAllowedHeight) {
                if (width >= calcWidth && height >= calcHeight) {
                    // 找到临近的像素大小
                    calcWidth = width
                    calcHeight = height
                }
            }
        }

        mPreviewWidth = calcWidth
        mPreviewHeight = calcHeight
    }

    override fun onDestroy() {
        releaseCamera()
        mPlayManager?.onDestory()
        super.onDestroy()
    }






}
