package org.amoustakos.exifstripper.view.navigation

import android.view.MenuItem
import io.reactivex.Observable
import org.amoustakos.exifstripper.R
import org.amoustakos.models.messages.Event

interface INavDrawerView {


	fun isOpen(gravity: Int): Boolean
	fun isVisible(gravity: Int): Boolean

	fun close(gravity: Int)
	fun closeAll()
	fun open(gravity: Int)

	@Throws(IllegalArgumentException::class)
	fun selectItem(position: Int)


	fun getClicks(): Observable<Event<MenuItem>>


	fun getOpenDescription() = R.string.navigation_drawer_open
	fun getClosedDescription() = R.string.navigation_drawer_close


}