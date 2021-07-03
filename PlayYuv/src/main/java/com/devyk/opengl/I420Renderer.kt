package com.devyk.opengl

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * <pre>
 * author  : devyk on 2019-11-15 22:12
 * blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 * github  : https://github.com/yangkun19921001
 * mailbox : yang1001yk@gmail.com
 * desc    : This is I420Renderer
</pre> *
 */
class I420Renderer(private val mContext: Context) : GLSurfaceView.Renderer {
    private var mProgram = 0
    private lateinit var mTextureIds: IntArray
    private var yuvWidth = 0
    private var yuvHeight = 0
    private var yBuffer: ByteBuffer? = null
    private var uBuffer: ByteBuffer? = null
    private var vBuffer: ByteBuffer? = null
    protected var mVertexBuffer: FloatBuffer? = null
    override fun onSurfaceCreated(
        gl: GL10,
        config: EGLConfig
    ) {
        init()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // 视距区域设置使用 GLSurfaceView 的宽高
        GLES30.glViewport(0, 0, width, height)
    }

    private fun init() {
        val vertexSource =
            ShaderUtil.loadFromAssets("vertex.vsh", mContext.resources)
        val fragmentSource =
            ShaderUtil.loadFromAssets("fragment.fsh", mContext.resources)
        mProgram = ShaderUtil.createProgram(vertexSource, fragmentSource)
        //创建纹理
        mTextureIds = IntArray(3)
        GLES30.glGenTextures(mTextureIds.size, mTextureIds, 0)
        for (i in mTextureIds.indices) {
            //绑定纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIds[i])
            //设置环绕和过滤方式
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR
            )
        }

        // OpenGL的世界坐标系是 [-1, -1, 1, 1]，纹理的坐标系为 [0, 0, 1, 1]
        val vertices = floatArrayOf( // 前三个数字为顶点坐标(x, y, z)，后两个数字为纹理坐标(s, t)
            // 第一个三角形
            1f, 1f, 0f, 1f, 0f,
            1f, -1f, 0f, 1f, 1f,
            -1f, -1f, 0f, 0f, 1f,  // 第二个三角形
            1f, 1f, 0f, 1f, 0f,
            -1f, -1f, 0f, 0f, 1f,
            -1f, 1f, 0f, 0f, 0f
        )
        val vbb =
            ByteBuffer.allocateDirect(vertices.size * 4) // 一个 float 是四个字节
        vbb.order(ByteOrder.nativeOrder()) // 必须要是 native order
        mVertexBuffer = vbb.asFloatBuffer()
        mVertexBuffer?.put(vertices)
    }

    fun setYuvData(i420: ByteArray, width: Int, height: Int) {
        if (yBuffer != null) yBuffer!!.clear()
        if (uBuffer != null) uBuffer!!.clear()
        if (vBuffer != null) vBuffer!!.clear()

        // 该函数多次被调用的时，不要每次都new，可以设置为全局变量缓存起来
        val y = ByteArray(width * height)
        val u = ByteArray(width * height / 4)
        val v = ByteArray(width * height / 4)
        System.arraycopy(i420, 0, y, 0, y.size)
        System.arraycopy(i420, y.size, u, 0, u.size)
        System.arraycopy(i420, y.size + u.size, v, 0, v.size)
        yBuffer = ByteBuffer.wrap(y)
        uBuffer = ByteBuffer.wrap(u)
        vBuffer = ByteBuffer.wrap(v)
        yuvWidth = width
        yuvHeight = height
    }

    override fun onDrawFrame(gl: GL10) {
        if (yBuffer == null || uBuffer == null || vBuffer == null) {
            return
        }
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT) // clear color buffer
        // 1. 选择使用的程序
        GLES30.glUseProgram(mProgram)
        // 2.1 加载纹理y
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0) //激活纹理0
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIds[0]) //绑定纹理
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, yuvWidth,
            yuvHeight, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, yBuffer
        ) // 赋值
        GLES30.glUniform1i(0, 0) // sampler_y的location=0, 把纹理0赋值给sampler_y
        // 2.2 加载纹理u
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIds[1])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, yuvWidth / 2,
            yuvHeight / 2, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, uBuffer
        )
        GLES30.glUniform1i(1, 1) // sampler_u的location=1, 把纹理1赋值给sampler_u
        // 2.3 加载纹理v
        GLES30.glActiveTexture(GLES30.GL_TEXTURE2)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIds[2])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, yuvWidth / 2,
            yuvHeight / 2, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, vBuffer
        )
        GLES30.glUniform1i(2, 2) // sampler_v的location=2, 把纹理1赋值给sampler_v
        // 3. 加载顶点数据
        mVertexBuffer!!.position(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 5 * 4, mVertexBuffer)
        GLES30.glEnableVertexAttribArray(0)
        mVertexBuffer!!.position(3)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 5 * 4, mVertexBuffer)
        GLES30.glEnableVertexAttribArray(1)
        // 4. 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
    }

    companion object {
        private const val TAG = "I420Renderer"
    }
}