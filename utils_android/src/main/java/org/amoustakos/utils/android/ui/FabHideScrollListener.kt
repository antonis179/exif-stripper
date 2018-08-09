package org.amoustakos.utils.android.ui

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.amoustakos.utils.android.enums.Orientation
import org.amoustakos.utils.android.enums.Orientation.VERTICAL
import timber.log.Timber


class FabHideScrollListener(
        private val fab: FloatingActionButton,
        private val threshold: Int = 20,
        private val orientation: Orientation = VERTICAL,
        private val animFactor: Float = 2F,
        private val animTime: Long = 300,
        startHidden: Boolean = true
) : RecyclerView.OnScrollListener() {

    private var scrolledDistance: Int = 0
    private var isAnimatorActive = false

    init {
        if (startHidden)
            animate(false)
    }


    override fun onScrolled(rv: RecyclerView?, dx: Int, dy: Int) {
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
        rv?.isAtTop()?:false || (scrolledDistance > threshold && isVisible())

    private fun shouldShow()    = scrolledDistance < -threshold && !isVisible()
    private fun isVertical()    = orientation == VERTICAL
    private fun isVisible()     = fab.visibility == VISIBLE

    private fun RecyclerView.isAtTop(): Boolean {
        return try {
            val linearLayoutManager = this.layoutManager as LinearLayoutManager
            val pos = linearLayoutManager.findFirstVisibleItemPosition()

            linearLayoutManager.findViewByPosition(pos).top == 0 && pos == 0
        }
        catch (e: ClassCastException){
            Timber.e(e)
            false
        }
    }



    protected fun chooseAnimation(rv: RecyclerView?) {
        if (shouldHide(rv)) {
            animate(false)
            resetDistance()
        }
        else if (shouldShow()) {
            animate(true)
            resetDistance()
        }
    }

    protected fun animate(show: Boolean) {
        if (show)
            animateShow()
        else
            animateHide()
    }


    protected fun animateShow() {
        if (isAnimatorActive)
            return
        isAnimatorActive = true
        var animator = fab.animate()
                .setInterpolator(DecelerateInterpolator(animFactor))
                .setDuration(animTime)
                .withStartAction { fab.visibility = View.VISIBLE }
                .withEndAction { isAnimatorActive = false }

        animator =  if (isVertical())
                        animator.translationY(0F)
                    else
                        animator.translationX(0F)

        animator.start()
    }

    protected fun animateHide() {
        if (isAnimatorActive)
            return
        isAnimatorActive = true

        val fabMargin = when {
            fab.layoutParams is RelativeLayout.LayoutParams ->
                (fab.layoutParams as RelativeLayout.LayoutParams).bottomMargin

            fab.layoutParams is LinearLayout.LayoutParams ->
                (fab.layoutParams as LinearLayout.LayoutParams).bottomMargin

            else -> 0
        }

        val d = (fab.height + fabMargin).toFloat()
        var animator = fab.animate()
                .setInterpolator(AccelerateInterpolator(animFactor))
                .setDuration(animTime)
                .withEndAction {
                    isAnimatorActive = false
                    fab.visibility = View.GONE
                }

        animator =  if (isVertical())
                        animator.translationY(d)
                    else
                        animator.translationX(d)

        animator.start()
    }

}