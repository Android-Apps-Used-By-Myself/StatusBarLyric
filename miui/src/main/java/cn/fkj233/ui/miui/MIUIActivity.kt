@file:Suppress("DEPRECATION")

package cn.fkj233.ui.miui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.miui.data.Item

open class MIUIActivity : Activity() {

    @Suppress("LeakingThis")
    private val activity = this

    private var callbacks: (() -> Unit)? = null

    private val backButton by lazy {
        ImageView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.gravity = Gravity.CENTER_VERTICAL }
            background = getDrawable(R.drawable.abc_ic_ab_back_material)
            setPadding(0, 0, dp2px(activity, 25f),0)
            visibility = View.GONE
        }
    }

    private val menuButton by lazy {
        ImageView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.gravity = Gravity.CENTER_VERTICAL }
            background = getDrawable(R.drawable.abc_ic_menu_overflow_material)
            setPadding(0, 0, dp2px(activity, 25f),0)
        }
    }

    private val titleView by lazy {
        TextView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                it.gravity = Gravity.CENTER_VERTICAL
                it.weight = 1f
            }
            textSize = sp2px(activity, 10f)
        }
    }

    private var frameLayoutId: Int = -1
    private val frameLayout by lazy {
        val mFrameLayout = FrameLayout(activity)
        frameLayoutId = View.generateViewId()
        mFrameLayout.id = frameLayoutId
        mFrameLayout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContentView(LinearLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            background = getDrawable(R.color.foreground)
            orientation = LinearLayout.VERTICAL
            addView(LinearLayout(activity).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setPadding(dp2px(activity, 25f), dp2px(activity, 30f), dp2px(activity, 25f), dp2px(activity, 15f))
                orientation = LinearLayout.HORIZONTAL
                addView(backButton)
                addView(titleView)
                addView(menuButton)
            })
            addView(frameLayout)
        })
    }

    override fun setTitle(title: CharSequence?) {
        titleView.text = title
    }

    fun setSP(sharedPreferences: SharedPreferences) {
        OwnSP.ownSP = sharedPreferences
    }

    fun showFragment(dataItem:  List<Item>, title: CharSequence?) {
        this.title = title
        val fragment = MIUIFragment(callbacks).setDataItem(dataItem)
        fragmentManager.beginTransaction().setCustomAnimations(
            R.animator.slide_right_in,
            R.animator.slide_left_out,
            R.animator.slide_left_in,
            R.animator.slide_right_out
        ).replace(frameLayoutId, fragment).addToBackStack(title.toString()).commit()
    }

    fun setAllCallBacks(callbacks: () -> Unit) {
        this.callbacks = callbacks
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            val string =
                fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 2).name
            titleView.text = string
            fragmentManager.popBackStack()
        }
    }

}