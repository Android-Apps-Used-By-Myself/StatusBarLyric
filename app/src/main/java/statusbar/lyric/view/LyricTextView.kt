/*
 * Copyright (C) 2019 The Android Open Source Project
 *               2020 The exTHmUI Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package statusbar.lyric.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import kotlin.math.abs

@SuppressLint("ViewConstructor")
class LyricTextView constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0x01010084, defStyleRes: Int = 0) : TextView(context, attrs, defStyleAttr, defStyleRes) {
    private var isStop = true
    private var textLength = 0f
    private var viewWidth = 0f
    private var speed = 4f
    private var time: Long = 0
    private var xx = 0f
    private var text: String? = null
    private val mPaint: Paint?
    private var mStartScrollRunnable = Runnable { startScroll() }
    private fun init() {
        xx = 0f
        textLength = getTextLength()
        viewWidth = width.toFloat()
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(mStartScrollRunnable)
        super.onDetachedFromWindow()
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        stopScroll()
        this.text = text.toString()
        init()
        postInvalidate()
        postDelayed(mStartScrollRunnable, startScrollDelay.toLong())
    }

    override fun setTextColor(color: Int) {
        if (mPaint != null) mPaint.color = color
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        var mSpeed = speed
        if (text != null) {
            if (getText().length <= 20 && System.currentTimeMillis() - time <= 1500) {
                val y = height / 2 + abs(mPaint!!.ascent() + mPaint.descent()) / 2
                canvas.drawText(text!!, xx, y, mPaint)
                invalidateAfter()
                return
            } else if (getText().length >= 20) {
                mSpeed += mSpeed
            }
        }
        if (text != null) {
            val y = height / 2 + abs(mPaint!!.ascent() + mPaint.descent()) / 2
            canvas.drawText(text!!, xx, y, mPaint)
        }
        if (!isStop) {
            if (viewWidth - xx + mSpeed >= textLength) {
                xx = if (viewWidth > textLength) 0F else viewWidth - textLength
                stopScroll()
            } else {
                xx -= mSpeed
            }
            invalidateAfter()
        }
    }

    private fun invalidateAfter() {
        removeCallbacks(invalidateRunnable)
        postDelayed(invalidateRunnable, invalidateDelay.toLong())
    }

    private val invalidateRunnable = Runnable { this.invalidate() }

    init {
        mPaint = paint
    }

    private fun startScroll() {
        init()
        isStop = false
        postInvalidate()
    }

    private fun stopScroll() {
        isStop = true
        removeCallbacks(mStartScrollRunnable)
        postInvalidate()
    }

    private fun getTextLength(): Float {
        return mPaint?.measureText(text) ?: 0f
    }

    fun setSpeed(speed: Float) {
        this.speed = speed
    }

    fun setTextT(charSequence: CharSequence) {
        super.setText(charSequence)
        time = System.currentTimeMillis()
    }

    companion object {
        const val startScrollDelay = 500
        const val invalidateDelay = 10
    }
}