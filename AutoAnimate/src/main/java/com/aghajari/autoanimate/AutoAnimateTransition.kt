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

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.transition.Transition
import androidx.transition.TransitionValues

/**
 * @version 1.0.2
 * @author AmirHossein Aghajari
 */
open class AutoAnimateTransition : Transition() {

    open val propertyMapper = mutableMapOf<String, AutoAnimateProperties>()
    open var defaultInterpolator: TimeInterpolator? = null

    override fun setInterpolator(interpolator: TimeInterpolator?): Transition {
        defaultInterpolator = interpolator
        return this
    }

    override fun getInterpolator(): TimeInterpolator? {
        return null
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues, false)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues, true)
    }

    private fun captureValues(transitionValues: TransitionValues, isTarget: Boolean) {
        map(transitionValues.view).forEach {
            if (it.supports(transitionValues.view)) {
                it.save(transitionValues, isTarget)
            }
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }

        val diff = map(endValues.view).filter {
            it.supports(endValues.view) && it.equals(
                it.load(startValues),
                it.load(endValues)
            ).not()
        }

        val evaluatorMapper = mutableMapOf<Class<out TypeEvaluator<*>>, TypeEvaluator<*>>()
        diff.forEach { property ->
            property.onStart(sceneRoot, startValues, endValues, endValues.view)
            property.evaluator.apply {
                if (evaluatorMapper.containsKey(this).not()) {
                    evaluatorMapper[this] = newInstance()
                }
            }
        }

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            diff.forEach { property ->
                property.update(
                    endValues.view,
                    property.calculateFraction(it, defaultInterpolator),
                    evaluatorMapper[property.evaluator]!!,
                    property.load(startValues),
                    property.load(endValues)
                )
            }
        }
        animator.doOnEnd {
            diff.forEach { property ->
                property.onEnd(sceneRoot, startValues, endValues, endValues.view)
            }
        }
        return animator
    }

    private fun map(view: View): AutoAnimateProperties {
        if (propertyMapper.containsKey(view.transitionName).not()) {
            propertyMapper[view.transitionName] = allAutoAnimateProperties
        }
        return propertyMapper[view.transitionName]!!
    }

    @Suppress("UNCHECKED_CAST")
    fun <V, T> AutoAnimateProperty<V, T>.equals(
        start: Any?,
        end: Any?
    ): Boolean {
        return areEquals(start as T, end as T)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <V, T> AutoAnimateProperty<V, T>.update(
        view: View,
        fraction: Float,
        evaluator: TypeEvaluator<*>,
        start: Any,
        end: Any
    ) {
        animate(
            view as V,
            fraction,
            evaluator as TypeEvaluator<T>,
            start as T,
            end as T,
        )
    }

    companion object {

        fun build(
            propertyMapper: Map<String, AutoAnimateProperties> = mapOf(),
            duration: Long = 500,
            interpolator: TimeInterpolator = LinearInterpolator()
        ): AutoAnimateTransition {
            return AutoAnimateTransition().apply {
                this.propertyMapper.putAll(propertyMapper)
                this.duration = duration
                this.defaultInterpolator = interpolator
            }
        }
    }
}