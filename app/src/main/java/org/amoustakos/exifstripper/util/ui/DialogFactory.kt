package org.amoustakos.exifstripper.util.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import org.amoustakos.exifstripper.injection.annotations.context.ActivityContext


/**
 * TODO: WiP
 */
object DialogFactory {


    fun createSimpleOkErrorDialog(
            @ActivityContext context: Context,
            title: String,
            message: String,
            neutralButtonStr: String
    ): Dialog {
        val alertDialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(neutralButtonStr, null)
        return alertDialog.create()
    }


    fun createSimpleOkErrorDialog(
            @ActivityContext context: Context,
            @StringRes titleResource: Int,
            @StringRes messageResource: Int,
            @StringRes neutralButtonStr: Int
    ): Dialog {
        return createSimpleOkErrorDialog(
                context,
                context.getString(titleResource),
                context.getString(messageResource),
                context.getString(neutralButtonStr)
        )
    }


    fun createProgressDialog(context: Context, message: String): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(message)
        return progressDialog
    }


    fun createProgressDialog(context: Context, @StringRes messageResource: Int): ProgressDialog {
        return createProgressDialog(context, context.getString(messageResource))
    }


}
