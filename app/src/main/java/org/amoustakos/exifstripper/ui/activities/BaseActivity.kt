package org.amoustakos.exifstripper.ui.activities

import android.os.Bundle
import android.util.LongSparseArray
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import org.amoustakos.exifstripper.ExifApplication
import org.amoustakos.exifstripper.di.component.ActivityComponent
import org.amoustakos.exifstripper.di.component.ConfigPersistentComponent
import org.amoustakos.exifstripper.di.component.DaggerConfigPersistentComponent
import org.amoustakos.exifstripper.di.module.injectors.ActivityModule
import org.amoustakos.exifstripper.view.base.IActivityViewComponent
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract activity that every other Activity in this application must implement. It handles
 * creation of Dagger components and makes sure that instances of ConfigPersistentComponent survive
 * across configuration changes.
 */
abstract class BaseActivity : AppCompatActivity() {

    private var mActivityComponent: ActivityComponent? = null
    private var mActivityId: Long = 0


    protected fun setupViewComponents(components: List<IActivityViewComponent>) {
	    components.forEach { setupViewComponent(it) }
    }

	protected fun setupViewComponent(component: IActivityViewComponent) {
		component.setup(this)
	}


    // =========================================================================================
    // Overridden
    // =========================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        makeID(savedInstanceState)
        makeComponent(mActivityId)
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

    private fun makeComponent(activityID: Long) {
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
    // Getters
    // =========================================================================================

    @Throws(IllegalStateException::class)
    fun activityComponent(): ActivityComponent {
        return mActivityComponent ?:
                throw IllegalStateException("Activity component not instantiated")
    }

    fun application(): ExifApplication {
        return ExifApplication[this]
    }

    companion object {
        private const val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID = AtomicLong(0)
        private val sComponentsMap = LongSparseArray<ConfigPersistentComponent>()
    }

}
