package org.amoustakos.exifstripper.usecases.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.ui.activities.BaseActivity
import org.amoustakos.exifstripper.usecases.home.MainActivity


class SplashActivity : BaseActivity() {


	override fun layoutId() = R.layout.activity_splash


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		Handler().postDelayed(
				{
					startActivity(
							Intent(this, MainActivity::class.java)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
									.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					)
				},
				2*1000
		)
	}

}
