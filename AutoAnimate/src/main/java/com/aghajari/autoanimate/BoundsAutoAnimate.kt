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

import android.animation.RectEvaluator
import android.animation.TypeEvaluator
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionValues
import java.lang.reflect.Method

class BoundsAutoAnimate : AutoAnimateProperty<View, Rect>(
    RectEvaluator::class.java
) {

    private var method: Method? = null

    override fun save(transitionValues: TransitionValues, isTarget: Boolean) {
        transitionValues.view.apply {
            transitionValues.values[getKey()] = Rect(
                left, top, right, bottom
            )
        }
    }

    override fun animate(
        view: View,
        fraction: Float,
        evaluator: TypeEvaluator<Rect>,
        start: Rect,
        end: Rect
    ) {
        if (method == null) {
            method = getLeftTopRightBottomSetterMethod()
        }
        val rect = evaluator.evaluate(fraction, start, end)
        method?.invoke(null, view, rect.left, rect.top, rect.right, rect.bottom)
    }

    override fun onEnd(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues,
        view: View
    ) {
        view.requestLayout()
    }

    private fun getLeftTopRightBottomSetterMethod(): Method {
        val cls = Class.forName("androidx.transition.ViewUtils")
        return cls.getDeclaredMethod(
            "setLeftTopRightBottom",
            View::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        ).apply {
            isAccessible = true
        }
    }
}