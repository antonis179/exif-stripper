package org.amoustakos.exifstripper.view.navigation

import android.app.Activity
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import io.reactivex.subjects.PublishSubject
import org.amoustakos.exifstripper.view.base.IActivityViewComponent
import org.amoustakos.models.messages.Event

class BasicNavDrawer(
		@IdRes val nViewId: Int,
		@IdRes val drawerLaoutId: Int,
		val toolbar: Toolbar
): IActivityViewComponent, INavDrawerView, NavigationView.OnNavigationItemSelectedListener {

	private var navView: NavigationView? = null
	private var drawerLayout: DrawerLayout? = null
	private var toggle: ActionBarDrawerToggle? = null

	private val subject: PublishSubject<Event<MenuItem>> = PublishSubject.create()



	override fun setup(activity: Activity) {
		navView = activity.findViewById(nViewId)
		drawerLayout = activity.findViewById(drawerLaoutId)

		toggle = ActionBarDrawerToggle(
				activity,
				drawerLayout,
				toolbar,
				getOpenDescription(),
				getClosedDescription())
		toggle?.let { drawerLayout?.addDrawerListener(it) }
		toggle?.syncState()

		navView?.setNavigationItemSelectedListener(this)
	}


	@Throws(IllegalArgumentException::class)
	override fun selectItem(position: Int) {
		val item = navView?.menu?.getItem(position)
		item?.let {
			it.isChecked = true
			onNavigationItemSelected(it)
		} ?: throw IllegalArgumentException("Unknown menu item")
	}


	override fun isOpen(gravity: Int) = drawerLayout?.isDrawerOpen(gravity) ?: false
	override fun isVisible(gravity: Int) = drawerLayout?.isDrawerVisible(gravity) ?: false


	override fun close(gravity: Int) {
		drawerLayout?.closeDrawer(gravity)
	}

	override fun closeAll() {
		drawerLayout?.closeDrawers()
	}

	override fun open(gravity: Int) {
		drawerLayout?.openDrawer(gravity)
	}


	override fun getClicks() = subject

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		subject.onNext(Event(hasError = false, item = item))
		return true
	}

}