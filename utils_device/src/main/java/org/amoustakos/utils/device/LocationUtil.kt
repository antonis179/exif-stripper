package org.amoustakos.utils.device

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.location.Location
import android.os.Looper
import android.support.annotation.NonNull
import android.support.annotation.RequiresPermission
import com.google.android.gms.location.*
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.lang.ref.WeakReference


class LocationUtil : LocationCallback, DefaultLifecycleObserver {

	// Defaults
	var mUpdateInterval: Long = 120000
	var mFastestUpdateInterval = mUpdateInterval / 2
	var mRequestPriority = LocationRequest.PRIORITY_LOW_POWER

	//Publishers
	val locationSubject = PublishSubject.create<Location>()!!
	val availabilitySubject = PublishSubject.create<LocationAvailability?>()!!

	//Client
	private var mLocationRequest: LocationRequest? = null
	private var mFusedLocationClient: FusedLocationProviderClient? = null

	//State
	var mRequestingLocationUpdates: Boolean = false
	@get:Synchronized private var mCurrentLocation: Location? = null


	//Internal variables
	private val mContext: WeakReference<Context>


	// =========================================================================================
	// Constructors
	// =========================================================================================

	@Throws(NullPointerException::class)
	constructor(@NonNull context: Context) {
		mContext = WeakReference(context)
		init()
	}

	@Throws(NullPointerException::class)
	constructor(
			context: Context,
			updateInterval: Long,
			fastestUpdateInterval: Long,
			requestPriority: Int) {
		mUpdateInterval = updateInterval
		mFastestUpdateInterval = fastestUpdateInterval
		mRequestPriority = requestPriority
		mContext = WeakReference(context)
		init()
	}

	@Throws(NullPointerException::class)
	private fun init() {
		mRequestingLocationUpdates = false
		mLocationRequest = null
		mCurrentLocation = null
		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext.get()!!)

		if (mFusedLocationClient == null)
			throw NullPointerException("Could not instantiate fused location client")

		createLocationRequest()
	}


	// =========================================================================================
	// Client
	// =========================================================================================


	private fun createLocationRequest() {
		mLocationRequest = LocationRequest.create()
		mLocationRequest!!.interval = mUpdateInterval
		mLocationRequest!!.fastestInterval = mFastestUpdateInterval
		mLocationRequest!!.priority = mRequestPriority
	}


	// =========================================================================================
	// Functionality
	// =========================================================================================

	@SuppressLint("MissingPermission")
	@RequiresPermission(value = ACCESS_COARSE_LOCATION)
	fun startLocationUpdates(): Boolean {
		if (mRequestingLocationUpdates) {
			Timber.i("Already requesting location updates. Ignoring request.")
			return true
		}

		try {
			mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, this, Looper.myLooper())
			mRequestingLocationUpdates = true
			Timber.d("Started location updates.")
		} catch (e: Exception) {
			Timber.w(e)
			mRequestingLocationUpdates = false
		}

		return mRequestingLocationUpdates
	}

	fun stopLocationUpdates() {
		mFusedLocationClient!!.removeLocationUpdates(this)
		mRequestingLocationUpdates = false
		Timber.d("Stopped location updates.")
	}


	// =========================================================================================
	// Location updates
	// =========================================================================================

	override fun onLocationResult(p0: LocationResult?) {
		super.onLocationResult(p0)

		mCurrentLocation = p0?.lastLocation
		if (mCurrentLocation != null)
			locationSubject.onNext(mCurrentLocation!!)
	}

	override fun onLocationAvailability(p0: LocationAvailability?) {
		super.onLocationAvailability(p0)
		p0?.let { availabilitySubject.onNext(it) }
	}

	// =========================================================================================
	// Lifecycle
	// =========================================================================================

	override fun onCreate(owner: LifecycleOwner) {}
	override fun onStart(owner: LifecycleOwner) {}
	override fun onStop(owner: LifecycleOwner) {}
	override fun onDestroy(owner: LifecycleOwner) {}

	@SuppressLint("MissingPermission")
	override fun onResume(owner: LifecycleOwner) {
		startLocationUpdates()
	}

	override fun onPause(owner: LifecycleOwner) = stopLocationUpdates()
}
