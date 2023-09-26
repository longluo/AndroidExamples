package com.zhengsr.opengldemo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.zhengsr.opengldemo.render.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var curRender: BaseRender? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = arrayListOf<RenderItem>(
            RenderItem(L1_PointRender::class.java, "L1 - 基础类型，点"),
            RenderItem(L2_ShapeRender::class.java, "L2 - 点,线，三角形"),
            RenderItem(L3_ShapeRender::class.java, "L3 - 正交投影，修复横竖屏，图形变形的问题"),
            RenderItem(L4_ShapeRender::class.java, "L4 - 渐变色"),
            RenderItem(L5_ShapeRender::class.java, "L5 - 优化数据VBO,VAO"),
            RenderItem(L6_ShapeRender::class.java, "L6 - 纹理"),
            RenderItem(L6_ShapeRender_1::class.java, "L6-1 - 多纹理-图片混合"),
            RenderItem(L7_ShapeRender::class.java, "L7 - Matrix变换"),
            RenderItem(L7_ShapeRender_1::class.java, "L7-1 - 透视投影"),
            RenderItem(L7_ShapeRender_2::class.java, "L7-2 - 3D效果"),
            RenderItem(L8_ShapeRender::class.java, "L8 - 模拟EGL环境,不使用GlSurfaceView"),
            RenderItem(L9_Render::class.java, "L9 - 渲染YUV视频"),
            RenderItem(L9_Render_1::class.java, "L9-1 - 渲染视频,抖音特效"),
            RenderItem(L10_Render::class.java, "L10 - MediaCodec+OpenGL实现视频渲染"),
            RenderItem(L11_Render::class.java, "L11 - FBO 纹理"),
            RenderItem(L11_Render_1::class.java, "L11-1 - FBO RBO 纹理"),
            RenderItem(
                L11_Render_2::class.java,
                "L11-2 - MediaCodec+OpenGL实现视频渲染，使用 FBO 截图"
            ),
        )
        with(recycleView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val testAdapter = TestAdapter()
            testAdapter.submitList(data)
            testAdapter.setOnItemClickListener { adapter, view, position ->
                curRender = getRenderer(data[position].className)?.apply {
                    showUI(this@MainActivity)
                    recycleView.visibility = View.GONE
                    rootContent.addView(this.view)
                }
            }

            adapter = testAdapter
        }
    }

    override fun onBackPressed() {
        if (recycleView.visibility == View.GONE) {
            recycleView.visibility = View.VISIBLE
            curRender?.dismiss()
            return
        }

        super.onBackPressed()
    }

    class TestAdapter : BaseQuickAdapter<RenderItem, QuickViewHolder>() {

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): QuickViewHolder {
            // 返回一个 ViewHolder
            return QuickViewHolder(R.layout.layout_item, parent)
        }

        override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: RenderItem?) {
            // 设置item数据
            item?.let {
                holder.setText(R.id.item_text, it.content)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    data class RenderItem(val className: Class<*>, val content: String)

    fun getRenderer(className: Class<*>): BaseRender? {
        try {
            val constructor = className.getConstructor()
            return constructor.newInstance() as BaseRender
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}