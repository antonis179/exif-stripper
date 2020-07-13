package org.amoustakos.exifstripper.utils.ui

import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import org.amoustakos.exifstripper.utils.Orientation
import org.amoustakos.exifstripper.utils.Orientation.VERTICAL
import timber.log.Timber

class ViewHideScrollListener(
        private val view: View,
        private val threshold: Int = 10,
        private val orientation: Orientation = VERTICAL,
        private val hideOnTop: Boolean = false,
        private val animFactor: Float = 2F,
        private val animTime: Long = 300,
        startHidden: Boolean = false
) : RecyclerView.OnScrollListener() {

    private var scrolledDistance: Int = 0
    private var isAnimatorActive = false

    init {
        if (startHidden)
            animate(false)
    }

    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(rv, dx, dy)

        chooseAnimation(rv)

        val d = if (isVertical()) dy else dx
        if (shouldUpdateDistance(d))
            scrolledDistance += d
    }

    private fun resetDistance() {
        scrolledDistance = 0
    }

    private fun shouldUpdateDistance(d: Int) =
            (isVisible() && d > 0) || (!isVisible() && d < 0)

    private fun shouldHide(rv: RecyclerView?) =
            (hideOnTop && rv?.isAtTop() ?: false) || (scrolledDistance > threshold && isVisible())

    private fun shouldShow()    = scrolledDistance < -threshold && !isVisible()
    private fun isVertical()    = orientation == VERTICAL
    private fun isVisible()     = view.visibility == VISIBLE

    private fun RecyclerView.isAtTop(): Boolean {
        return try {
            val linearLayoutManager = this.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
            val pos = linearLayoutManager.findFirstVisibleItemPosition()

            linearLayoutManager.findViewByPosition(pos)?.top == 0 && pos == 0
        }
        catch (e: ClassCastException){
            Timber.e(e)
            false
        }
    }

    private fun chooseAnimation(rv: RecyclerView?) {
        if (shouldHide(rv)) {
            animate(false)
            resetDistance()
        }
        else if (shouldShow()) {
            animate(true)
            resetDistance()
        }
    }

    private fun animate(show: Boolean) {
        if (show)
            animateShow()
        else
            animateHide()
    }

    private fun animateShow() {
        if (isAnimatorActive)
            return
        isAnimatorActive = true
        var animator = view.animate()
                .setInterpolator(DecelerateInterpolator(animFactor))
                .setDuration(animTime)
                .withStartAction { view.visibility = View.VISIBLE }
                .withEndAction { isAnimatorActive = false }

        animator =  if (isVertical())
                        animator.translationY(0F)
                    else
                        animator.translationX(0F)

        animator.start()
    }

    private fun animateHide() {
        if (isAnimatorActive)
            return
        isAnimatorActive = true

        val fabMargin = when {
            view.layoutParams is RelativeLayout.LayoutParams ->
                (view.layoutParams as RelativeLayout.LayoutParams).bottomMargin

            view.layoutParams is LinearLayout.LayoutParams ->
                (view.layoutParams as LinearLayout.LayoutParams).bottomMargin

            else -> 0
        }

        val d = (view.height + fabMargin).toFloat()
        var animator = view.animate()
                .setInterpolator(AccelerateInterpolator(animFactor))
                .setDuration(animTime)
                .withEndAction {
                    isAnimatorActive = false
                    view.visibility = View.GONE
                }

        animator =  if (isVertical())
                        animator.translationY(d)
                    else
                        animator.translationX(d)

        animator.start()
    }

}