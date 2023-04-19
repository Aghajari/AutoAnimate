package com.aghajari.autoanimate

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment

private val autoAnimateTransaction = AutoAnimateTransaction.build(
    mapOf(
        "image" to listOf(
            BoundsAutoAnimate()
        ),
        "title" to listOf(
            BoundsAutoAnimate().apply {
                interpolator = AccelerateDecelerateInterpolator()
            },
            TextColorAutoAnimate(),
            ScaleXAutoAnimate(),
            ScaleYAutoAnimate()
        ),
        "footer" to listOf(
            TranslationYAutoAnimate().apply {
                interpolator = DecelerateInterpolator()
            },
            ElevationAutoAnimate()
        ),
        "footer_title" to listOf(
            BoundsAutoAnimate().apply {
                interpolator = LinearInterpolator()
            }
        )
    ),
    duration = 600,
    interpolator = OvershootInterpolator()
)

fun Fragment.startFragment(
    fragment: Fragment,
    vararg views: View
) {
    fragment.sharedElementEnterTransition = autoAnimateTransaction
    sharedElementReturnTransition = autoAnimateTransaction

    requireActivity()
        .supportFragmentManager
        .beginTransaction()
        .addToBackStack(null)
        .also {
            views.forEach { view ->
                it.addSharedElement(view, view.transitionName)
            }
        }
        .replace(R.id.container, fragment)
        .commit()
}

fun Fragment.popBackStack() {
    requireActivity()
        .supportFragmentManager
        .popBackStack()
}