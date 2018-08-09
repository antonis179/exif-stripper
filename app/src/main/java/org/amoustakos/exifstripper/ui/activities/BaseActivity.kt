package org.amoustakos.exifstripper.ui.activities

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.LongSparseArray
import android.view.View
import android.view.ViewGroup
import org.amoustakos.exifstripper.ExifStripperApplication
import org.amoustakos.exifstripper.R
import org.amoustakos.exifstripper.injection.component.ActivityComponent
import org.amoustakos.exifstripper.injection.component.ConfigPersistentComponent
import org.amoustakos.exifstripper.injection.component.DaggerConfigPersistentComponent
import org.amoustakos.exifstripper.injection.module.injectors.ActivityModule
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract activity that every other Activity in this application must implement. It handles
 * creation of Dagger components and makes sure that instances of ConfigPersistentComponent survive
 * across configuration changes.
 */
abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mActivityComponent: ActivityComponent? = null
    private var mActivityId: Long = 0

    protected val rootView: View
        get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)

    protected val toolbar: Toolbar?
        get() = findViewById<View>(R.id.toolbar) as Toolbar

    // =========================================================================================
    // Overridden
    // =========================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        makeID(savedInstanceState)
        makeComponents(mActivityId)

        setupToolbar()
//        setupNavBar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_ACTIVITY_ID, mActivityId)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Timber.d("Clearing ConfigPersistentComponent id=%d", mActivityId)
            sComponentsMap.remove(mActivityId)
        }
        super.onDestroy()
    }

    // =========================================================================================
    // Injection
    // =========================================================================================

    private fun makeID(savedInstanceState: Bundle?) {
        mActivityId = savedInstanceState?.getLong(KEY_ACTIVITY_ID) ?: NEXT_ID.getAndIncrement()
    }

    private fun makeComponents(activityID: Long) {
        val configPersistentComponent: ConfigPersistentComponent
        val index = sComponentsMap.indexOfKey(activityID)

        if (index < 0) { //component does not exist
            Timber.d("Creating new ConfigPersistentComponent id=%d", activityID)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(application().component)
                    .build()
            sComponentsMap.put(activityID, configPersistentComponent)
        } else {
            Timber.d("Reusing ConfigPersistentComponent id=%d", activityID)
            configPersistentComponent = sComponentsMap.get(index.toLong())
        }

        mActivityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
    }


    // =========================================================================================
    // View helpers
    // =========================================================================================

    @LayoutRes
    protected abstract fun layoutId(): Int


    // =========================================================================================
    // Toolbar / Navigation bar
    // =========================================================================================

    private fun setupToolbar() {
        val toolbar = toolbar
        if (toolbar != null)
            setSupportActionBar(toolbar)
    }


//    private fun setupNavBar() {
//        if (drawer_layout == null)
//            return
//
//        val toggle = ActionBarDrawerToggle(
//                this,
//                drawer_layout,
//                toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close)
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        nav_view.setNavigationItemSelectedListener(this)
//    }

    // =========================================================================================
    // Getters
    // =========================================================================================

    fun activityComponent(): ActivityComponent {
        return mActivityComponent ?:
                throw IllegalStateException("Activity component not instantiated")
    }

    fun application(): ExifStripperApplication {
        return ExifStripperApplication[this]
    }

    companion object {
        private const val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID = AtomicLong(0)
        private val sComponentsMap = LongSparseArray<ConfigPersistentComponent>()
    }

}
