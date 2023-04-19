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

import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionValues
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

@Suppress("UNCHECKED_CAST")
abstract class AutoAnimateProperty<V, T>(
    val evaluator: Class<out TypeEvaluator<in T>>,
    private val getter: KFunction1<V, T>? = null,
    private val setter: KFunction2<V, T, Unit>? = null,
    var duration: Long = DURATION_MATCH_TRANSACTION,
    var maxFraction: Float = Float.MAX_VALUE,
    var minFraction: Float = Float.MIN_VALUE,
    var interpolator: TimeInterpolator? = null,
    var startDelay: Long = 0,
    var enabled: Boolean = true
) {

    open fun supports(view: View): Boolean {
        return try {
            getter?.invoke(view as V)
            true
        } catch (ignore: java.lang.ClassCastException) {
            false
        }
    }

    open fun getKey(): String {
        return this.javaClass.simpleName
    }

    open fun save(transitionValues: TransitionValues, isTarget: Boolean) {
        transitionValues.values[getKey()] = getter?.invoke(transitionValues.view as V)
    }

    open fun load(transitionValues: TransitionValues): T {
        return transitionValues.values[getKey()] as T
    }

    open fun areEquals(start: T, end: T): Boolean {
        return if (start == null || end == null || enabled.not()) {
            true
        } else {
            start == end
        }
    }

    open fun onStart(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues,
        view: View
    ) {
    }

    open fun onEnd(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues,
        view: View
    ) {
    }

    internal fun calculateFraction(
        valueAnimator: ValueAnimator,
        defaultInterpolator: TimeInterpolator?
    ): Float {
        val input = if (startDelay >= valueAnimator.currentPlayTime) {
            0f
        } else {
            val duration = max(
                1L,
                if (duration == DURATION_MATCH_TRANSACTION) {
                    valueAnimator.duration
                } else {
                    min(valueAnimator.duration, duration)
                }.minus(startDelay)
            )

            val current = max(
                0L,
                valueAnimator.currentPlayTime - startDelay
            )
            min(1f, current.toFloat() / duration.toFloat())
        }

        val fraction = interpolator?.getInterpolation(input)
            ?: defaultInterpolator?.getInterpolation(input)
            ?: input
        return min(maxFraction, max(minFraction, fraction))
    }

    open fun animate(
        view: V,
        fraction: Float,
        evaluator: TypeEvaluator<T>,
        start: T,
        end: T
    ) {
        try {
            setter?.invoke(
                view,
                evaluator.evaluate(fraction, start, end)
            )
        } catch (ignore: java.lang.Exception) {
        }
    }

    private companion object {
        const val DURATION_MATCH_TRANSACTION = -1L
    }
}