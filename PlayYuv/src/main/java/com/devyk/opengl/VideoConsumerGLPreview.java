package com.devyk.opengl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


/**
 * <pre>
 *     author  : devyk on 2019-11-15 22:12
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is PlayYUVUtils --> OpenGL Surface view
 * </pre>
 */
public class VideoConsumerGLPreview extends GLSurfaceView implements GLSurfaceView.Renderer {
    int mBufferWidthY, mBufferHeightY, mBufferWidthUV, mBufferHeightUV;
    ByteBuffer mBuffer;
    int mBufferPositionY, mBufferPositionU, mBufferPositionV;
    private boolean IS_RequestRender = false;

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int SHORT_SIZE_BYTES = 2;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private static final float[] TRIANFLE_VERTICES_DATA = {
            1, -1, 0, 1, 1,
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0,
            -1, -1, 0, 0, 1
    };
    private static final short[] INDICES_DATA = {
            0, 1, 2,
            2, 3, 0};

    private FloatBuffer mTriangleVertices;
    private ShortBuffer mIndices;

    private static final String VERTEX_SHADER_SOURCE =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = aPosition;\n" +
                    "  vTextureCoord = aTextureCoord;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_SOURCE = "precision mediump float;" +
            "varying vec2 vTextureCoord;" +
            "" +
            "uniform sampler2D SamplerY; " +
            "uniform sampler2D SamplerU;" +
            "uniform sampler2D SamplerV;" +
            "" +
            "const mat3 yuv2rgb = mat3(1, 0, 1.2802,1, -0.214821, -0.380589,1, 2.127982, 0);" +
            "" +
            "void main() {    " +
            "    vec3 yuv = vec3(1.1643 * (texture2D(SamplerY, vTextureCoord).r - 0.0625)," +
            "                    texture2D(SamplerU, vTextureCoord).r - 0.5," +
            "                    texture2D(SamplerV, vTextureCoord).r - 0.5);" +
            "    vec3 rgb = yuv * yuv2rgb;    " +
            "    gl_FragColor = vec4(rgb, 1.0);" +
            "} ";

    private int mProgram;
    private int maPositionHandle;
    private int maTextureHandle;
    private int muSamplerYHandle;
    private int muSamplerUHandle;
    private int muSamplerVHandle;
    private int[] mTextureY = new int[1];
    private int[] mTextureU = new int[1];
    private int[] mTextureV = new int[1];

    private boolean mSurfaceCreated;
    private boolean mSurfaceDestroyed;
    @SuppressWarnings("unused")
    private Context mContext;

    private int mViewWidth, mViewHeight, mViewX, mViewY;
    private boolean mFullScreenRequired;
    private String TAG = this.getClass().getSimpleName();


    /**
     * Y 分量
     */
    private ByteBuffer yByteBuffer = ByteBuffer.allocateDirect(1280 * 720);

    /**
     * uv 分量
     */
    private ByteBuffer uvByteBuffer = ByteBuffer.allocateDirect(1280 * 720 >> 1);

    /**
     * @param context
     * @param fullScreenRequired 是否全屏
     * @param buffer             传入的 buffer
     * @param bufferWidth        宽
     * @param bufferHeight       高
     */


    public VideoConsumerGLPreview(Context context, boolean fullScreenRequired, ByteBuffer buffer, int bufferWidth, int bufferHeight) {
        super(context);
        init(context, fullScreenRequired, buffer, bufferWidth, bufferHeight);
    }


    public VideoConsumerGLPreview(Context context) {
        super(context);
    }

    public VideoConsumerGLPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context, boolean fullScreenRequired, ByteBuffer buffer, int bufferWidth, int bufferHeight) {
        setEGLContextClientVersion(2);
        //设置默认颜色
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setRenderer(this);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setBuffer(buffer, bufferWidth, bufferHeight);

        mContext = context;

        mTriangleVertices = ByteBuffer.allocateDirect(TRIANFLE_VERTICES_DATA.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(TRIANFLE_VERTICES_DATA).position(0);

        mIndices = ByteBuffer.allocateDirect(INDICES_DATA.length
                * SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(INDICES_DATA).position(0);

        mFullScreenRequired = fullScreenRequired;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setPreserveEGLContextOnPause(true);//如果没有这一句，那onPause之后再onResume屏幕将会是黑屏滴
        }
    }


    public void setBuffer(ByteBuffer buffer, int bufferWidth, int bufferHeight) {

        mBuffer = buffer;
        mBufferWidthY = bufferWidth;
        mBufferHeightY = bufferHeight;

        mBufferWidthUV = (mBufferWidthY >> 1);
        mBufferHeightUV = (mBufferHeightY >> 1);

        mBufferPositionY = 0;
        mBufferPositionU = (mBufferWidthY * mBufferHeightY);
        mBufferPositionV = (mBufferPositionU + (mBufferWidthUV * mBufferHeightUV));



    }




    public void setNV21Buffer(byte [] buffer, int bufferWidth, int bufferHeight) {

        int len =(buffer.length<<1)/3;
        yByteBuffer.rewind();
        uvByteBuffer.rewind();
        yByteBuffer.put(buffer,0,len).position(0);
        uvByteBuffer.put(buffer,len,len>>1).position(0);

    }

    public boolean isReady() {
        return (mSurfaceCreated && !mSurfaceDestroyed);
    }

    public boolean isDestroyed() {
        return mSurfaceDestroyed;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//            mSurfaceCreated = false;
//            mSurfaceDestroyed = true;
        super.surfaceDestroyed(holder);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glViewport(mViewX, mViewY, mViewWidth, mViewHeight);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        if (mBuffer != null) {
            synchronized (this) {
                 //y
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mBufferWidthY, mBufferHeightY, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mBuffer.position(mBufferPositionY));
                GLES20.glUniform1i(muSamplerYHandle, 0);

                 //u
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mBufferWidthUV, mBufferHeightUV, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mBuffer.position(mBufferPositionU));
                GLES20.glUniform1i(muSamplerUHandle, 1);

                //v
                GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mBufferWidthUV, mBufferHeightUV, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mBuffer.position(mBufferPositionV));
                GLES20.glUniform1i(muSamplerVHandle, 2);
            }
        }


        if (IS_RequestRender)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES_DATA.length, GLES20.GL_UNSIGNED_SHORT, mIndices);

    }


    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        setViewport(width, height);
        // GLU.gluPerspective(glUnused, 45.0f, (float)width/(float)height, 0.1f, 100.0f);
    }


    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_DITHER);
        GLES20.glDisable(GLES20.GL_STENCIL_TEST);
        GLES20.glDisable(GL10.GL_DITHER);

        String extensions = GLES20.glGetString(GL10.GL_EXTENSIONS);
        Log.d("OPGL", "OpenGL extensions=" + extensions);

        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        mProgram = createProgram(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muSamplerYHandle = GLES20.glGetUniformLocation(mProgram, "SamplerY");
        if (muSamplerYHandle == -1) {
            throw new RuntimeException("Could not get uniform location for SamplerY");
        }
        muSamplerUHandle = GLES20.glGetUniformLocation(mProgram, "SamplerU");
        if (muSamplerUHandle == -1) {
            throw new RuntimeException("Could not get uniform location for SamplerU");
        }
        muSamplerVHandle = GLES20.glGetUniformLocation(mProgram, "SamplerV");
        if (muSamplerVHandle == -1) {
            throw new RuntimeException("Could not get uniform location for SamplerV");
        }

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maPosition");

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        checkGlError("glEnableVertexAttribArray maTextureHandle");

        GLES20.glGenTextures(1, mTextureY, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glGenTextures(1, mTextureU, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glGenTextures(1, mTextureV, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        mSurfaceCreated = true;

        setViewport(getWidth(), getHeight());

    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void setViewport(int width, int height) {
        if (mFullScreenRequired) {
            mViewWidth = width;
            mViewHeight = height;
            mViewX = mViewY = 0;
        } else {
            float fRatio = ((float) mBufferWidthY / (float) mBufferHeightY);
            mViewWidth = (int) ((float) width / fRatio) > height ? (int) ((float) height * fRatio) : width;
            mViewHeight = (int) (mViewWidth / fRatio) > height ? height : (int) (mViewWidth / fRatio);
            mViewX = ((width - mViewWidth) >> 1);
            mViewY = ((height - mViewHeight) >> 1);
        }
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public void setIsRequestRender(boolean isRequestRender
    ) {
        IS_RequestRender = isRequestRender;
    }
}
