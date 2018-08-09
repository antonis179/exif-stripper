package org.amoustakos.exifstripper.util.ui

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import org.amoustakos.exifstripper.injection.annotations.context.ActivityContext


object ViewUtil {

    /**
     * Translates pixels to dp
     */
    fun pxToDp(px: Float): Float {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
        return px / (densityDpi / 160f)
    }


    /**
     * Translates dp to pixels
     */
    fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return Math.round(dp * density)
    }


    /**
     * Hide the keyboard
     */
    fun hideKeyboard(@ActivityContext activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }


    fun showToast(context: Context, text: String, isLong: Boolean) {
        Toast.makeText(context, text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

}
