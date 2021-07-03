package com.devyk.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup;

/**
 * <pre>
 *     author  : devyk on 2019-11-15 22:12
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is PlayYUVUtils 播放 YUV 数据管理类
 * </pre>
 */
public class PlayManager {
    /**
     * 预览宽
     */
    private int previewWidth;
    /**
     * 预览高
     */
    private int previewHeight;
    /**
     * 视频预览的控件
     */
    private ViewGroup bindPlayControl;
    /**
     * 播放控件
     */
    private GLSurfaceView videoConsumerGLPreview;
    /**
     * 是否开始渲染
     */
    private boolean isRequestRender;
    /**
     * 上下文
     */
    private Context context;

    private I420Renderer mRenderer;

    private PlayManager() {
    }

    public static final class PlayManagerBuilder {
        private int previewWidth = -1;
        private int previewHeight = -1;
        private boolean isRequestRender;
        private ViewGroup bindPlayControl;

        public PlayManagerBuilder() {
        }

        public static PlayManagerBuilder aPlayManager() {
            return new PlayManagerBuilder();
        }

        public PlayManagerBuilder withPreviewWidth(int previewWidth) {
            this.previewWidth = previewWidth;
            return this;
        }

        public PlayManagerBuilder withPreviewHeight(int previewHeight) {
            this.previewHeight = previewHeight;
            return this;
        }

        public PlayManagerBuilder withRequestRender(boolean requestRender) {
            this.isRequestRender = requestRender;
            return this;
        }


        public PlayManagerBuilder bindPlayControl(ViewGroup bindPlayControl) {
            this.bindPlayControl = bindPlayControl;
            return this;
        }


        public PlayManager build(Context context) {
            PlayManager playYUVUtils = new PlayManager();
            playYUVUtils.previewWidth = this.previewWidth;
            playYUVUtils.previewHeight = this.previewHeight;
            playYUVUtils.isRequestRender = this.isRequestRender;
            playYUVUtils.bindPlayControl = this.bindPlayControl;
            playYUVUtils.context = context.getApplicationContext();

            return playYUVUtils;
        }
    }

    public void initPlayControl() {
        checkControl();
        if (previewWidth == -1 || previewHeight == -1)
            throw new RuntimeException("previewWidth or previewHeight is init ?");
        if (videoConsumerGLPreview != null) return;
        videoConsumerGLPreview = new GLSurfaceView(context);
        videoConsumerGLPreview.setEGLContextClientVersion(3); // 设置OpenGL版本号
        mRenderer = new I420Renderer(context);
        videoConsumerGLPreview.setRenderer(mRenderer);
        videoConsumerGLPreview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // 设置渲染模式为仅当手动执行requestRender时才绘制
        bindPlayControl.removeAllViews();
        bindPlayControl.addView(videoConsumerGLPreview);

    }

    private void checkControl() {
        if (bindPlayControl == null || context == null)
            throw new NullPointerException("Context or bindPlayControl is null ？");
    }

    /**
     * 开始播放
     *
     * @param i420
     */
    public void setYUVI420(byte[] i420) {
        checkControl();
        mRenderer.setYuvData(i420, this.previewWidth, this.previewHeight);
        videoConsumerGLPreview.requestRender(); // 手动触发渲染
    }

    /**
     * 删除播放 YUV 的控件
     */
    public void removePlayControl() {
        checkControl();
        bindPlayControl.removeView(videoConsumerGLPreview);
    }

    /**
     * 销毁
     */
    public void onDestory() {
        try {
            removePlayControl();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

}