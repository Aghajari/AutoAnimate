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

import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.aghajari.autoanimate.evaluator.DrawableEvaluator

typealias AutoAnimateProperties = List<AutoAnimateProperty<out View, out Any>>

val allAutoAnimateProperties: AutoAnimateProperties =
    listOf(
        LayoutAutoAnimate(),
        RotationAutoAnimate(),
        RotationXAutoAnimate(),
        RotationYAutoAnimate(),
        BackgroundAutoAnimate(),
        ScaleXAutoAnimate(),
        ScaleYAutoAnimate(),
        AlphaAutoAnimate(),
        ElevationAutoAnimate(),
        TranslationXAutoAnimate(),
        TranslationYAutoAnimate(),
        TextColorAutoAnimate(),
        HintTextColorAutoAnimate(),
        TextSizeAutoAnimate(),
    )


class RotationAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getRotation,
    View::setRotation
)

class RotationXAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getRotationX,
    View::setRotationX
)

class RotationYAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getRotationY,
    View::setRotationY
)

class BackgroundAutoAnimate : AutoAnimateProperty<View, Drawable>(
    DrawableEvaluator::class.java,
    View::getBackground,
    View::setBackground,
    maxFraction = 1f,
    minFraction = 0f
)

class ScaleXAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getScaleX,
    View::setScaleX
)

class ScaleYAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getScaleY,
    View::setScaleY
)

class AlphaAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getAlpha,
    View::setAlpha,
    maxFraction = 1f,
    minFraction = 0f
)

class ElevationAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    ViewCompat::getElevation,
    ViewCompat::setElevation
)

class TranslationXAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getTranslationX,
    View::setTranslationX
)

class TranslationYAutoAnimate : AutoAnimateProperty<View, Float>(
    FloatEvaluator::class.java,
    View::getTranslationY,
    View::setTranslationY
)

class TextColorAutoAnimate : AutoAnimateProperty<TextView, Int>(
    ArgbEvaluator::class.java,
    TextView::getCurrentTextColor,
    TextView::setTextColor,
    maxFraction = 1f,
    minFraction = 0f
)

class HintTextColorAutoAnimate : AutoAnimateProperty<TextView, Int>(
    ArgbEvaluator::class.java,
    TextView::getCurrentHintTextColor,
    TextView::setHighlightColor,
    maxFraction = 1f,
    minFraction = 0f
)

class TextSizeAutoAnimate : AutoAnimateProperty<TextView, Float>(
    FloatEvaluator::class.java,
    TextView::getTextSize,
    TextView::setTextSize
)