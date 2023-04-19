/*
 * Copyright (C) 2023 - Amir Hossein Aghajari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.aghajari.autoanimate

import android.animation.TypeEvaluator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.transition.TransitionValues
import com.aghajari.autoanimate.evaluator.RectFEvaluator
import java.lang.reflect.Field

class LayoutAutoAnimate : AutoAnimateProperty<View, RectF>(
    RectFEvaluator::class.java
) {

    private val tmpPosition = IntArray(2)
    private var avoidClickView: View? = null

    private var mSavedLayoutField: Field? = null
    private var mLayoutField: Field? = null

    override fun save(transitionValues: TransitionValues, isTarget: Boolean) {
        transitionValues.view.apply {
            getLocationOnScreen(tmpPosition)
            val positionX = tmpPosition[0].toFloat()
            val positionY = tmpPosition[1].toFloat()

            transitionValues.values[getKey()] = RectF(
                positionX,
                positionY,
                positionX + measuredWidth,
                positionY + measuredHeight
            )
            transitionValues.values[TRANSLATION_X_KEY] = translationX
            transitionValues.values[TRANSLATION_Y_KEY] = translationY
            transitionValues.values[SCALE_X_KEY] = scaleX
            transitionValues.values[SCALE_Y_KEY] = scaleY

            transitionValues.values[PARENT_KEY] = parent
            transitionValues.values[LAYOUT_PARAMS_KEY] = layoutParams
            (parent as? ViewGroup)?.let {
                transitionValues.values[CHILD_INDEX_KEY] = it.indexOfChild(this)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStart(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues,
        view: View
    ) {
        sceneRoot.layoutParams = FrameLayout.LayoutParams(-1, -1)
        sceneRoot.getLocationOnScreen(tmpPosition)
        for (values in listOf(startValues, endValues)) {
            load(values).apply {
                offset(
                    -tmpPosition[0] - getTranslationX(values),
                    -tmpPosition[1] - getTranslationY(values)
                )
                offset(
                    width() * (getScaleX(values) - 1f) / 2f,
                    height() * (getScaleY(values) - 1f) / 2f,
                )
            }
        }

        (endValues.values[PARENT_KEY] as? ViewGroup)?.let {
            val copyView = View(sceneRoot.context)
            val layout = Rect(view.left, view.top, view.right, view.bottom)

            it.removeView(view)
            it.addView(
                copyView,
                endValues.values[CHILD_INDEX_KEY] as Int,
                endValues.values[LAYOUT_PARAMS_KEY] as LayoutParams
            )
            copyView.left = layout.left
            copyView.top = layout.top
            copyView.right = layout.right
            copyView.bottom = layout.bottom
        }

        load(startValues).let { rect ->
            view.layoutParams = FrameLayout.LayoutParams(
                rect.width().toInt(),
                rect.height().toInt()
            ).apply {
                leftMargin = rect.left.toInt()
                topMargin = rect.top.toInt()
            }
            view.left = rect.left.toInt()
            view.top = rect.top.toInt()
            view.right = view.left + rect.width().toInt()
            view.bottom = view.top + rect.height().toInt()
        }

        updateLayout(load(startValues), view)
        sceneRoot.addView(view, view.layoutParams)

        if (avoidClickView == null) {
            avoidClickView = View(sceneRoot.context)
            avoidClickView?.setOnTouchListener { _, _ -> true }
            ViewCompat.setElevation(avoidClickView!!, Float.MAX_VALUE)
            sceneRoot.addView(avoidClickView, FrameLayout.LayoutParams(-1, -1))
        }
    }

    override fun animate(
        view: View,
        fraction: Float,
        evaluator: TypeEvaluator<RectF>,
        start: RectF,
        end: RectF
    ) {
        val rect = evaluator.evaluate(fraction, start, end)
        updateLayout(rect, view)
    }

    private fun updateLayout(rect: RectF, view: View) {
        (view.layoutParams as FrameLayout.LayoutParams).apply {
            leftMargin = rect.left.toInt()
            topMargin = rect.top.toInt()
            width = rect.width().toInt()
            height = rect.height().toInt()
        }
        textViewNullLayouts(view)
        view.invalidate()
        view.requestLayout()
    }

    override fun onEnd(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues,
        view: View
    ) {
        sceneRoot.removeView(avoidClickView)
        avoidClickView = null

        sceneRoot.removeView(view)
        val parent = endValues.values[PARENT_KEY]
        (parent as? ViewGroup)?.let {
            val index = endValues.values[CHILD_INDEX_KEY] as Int
            it.removeViewAt(index)
            it.addView(
                view, index,
                endValues.values[LAYOUT_PARAMS_KEY] as LayoutParams
            )
        }
        view.requestLayout()
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun textViewNullLayouts(view: View) {
        if (view is TextView) {
            try {
                if (mSavedLayoutField == null) {
                    mSavedLayoutField = TextView::class.java.getDeclaredField("mSavedLayout")
                    mSavedLayoutField?.isAccessible = true
                }

                if (mLayoutField == null) {
                    mLayoutField = TextView::class.java.getDeclaredField("mLayout")
                    mLayoutField?.isAccessible = true
                }

                mSavedLayoutField?.set(view, null)
                mLayoutField?.set(view, null)
            } catch (ignore: Throwable) {
            }
        }
    }

    private fun getTranslationX(transitionValues: TransitionValues) =
        transitionValues.values[TRANSLATION_X_KEY] as Float

    private fun getTranslationY(transitionValues: TransitionValues) =
        transitionValues.values[TRANSLATION_Y_KEY] as Float

    private fun getScaleX(transitionValues: TransitionValues) =
        transitionValues.values[SCALE_X_KEY] as Float

    private fun getScaleY(transitionValues: TransitionValues) =
        transitionValues.values[SCALE_Y_KEY] as Float

    private companion object {
        const val TRANSLATION_X_KEY = "translationX"
        const val TRANSLATION_Y_KEY = "translationY"
        const val SCALE_X_KEY = "scaleX"
        const val SCALE_Y_KEY = "scaleY"
        const val PARENT_KEY = "parent"
        const val LAYOUT_PARAMS_KEY = "layoutParams"
        const val CHILD_INDEX_KEY = "childIndex"
    }
}