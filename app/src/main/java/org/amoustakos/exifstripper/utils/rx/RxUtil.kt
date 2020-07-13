package org.amoustakos.utils.android.rx

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers

object RxUtil {

    val REALM_SCHEDULER = Schedulers.io()


    // =========================================================================================
    // Schedulers
    // =========================================================================================

    fun <T> applyNewThread(): ObservableTransformer<T, T> = ObservableTransformer {
        val scheduler = Schedulers.newThread()
        it.subscribeOn(scheduler)
                .observeOn(scheduler)
    }

    fun <T> applyRealmObservableScheduler(): ObservableTransformer<T, T> = ObservableTransformer {
        it.subscribeOn(REALM_SCHEDULER)
            .observeOn(REALM_SCHEDULER)
    }

    fun <T> applyRealmFlowableScheduler(): FlowableTransformer<T, T> = FlowableTransformer {
        it.subscribeOn(REALM_SCHEDULER)
            .observeOn(REALM_SCHEDULER)
    }

    fun <T> applyDefaultSchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
        it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> applyForegroundSchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
        it.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> applyIOSchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
        it.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    fun <T> applyComputationSchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
        it.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> applyTrampolineSchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
        it.subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
    }


    // =========================================================================================
    // Policies
    // =========================================================================================

    fun <T> applyRetryPolicy(retry: Int): ObservableTransformer<T, T> = ObservableTransformer {
        it.retry(retry.toLong())
    }

    // =========================================================================================
    // Testing
    // =========================================================================================

    fun <T> check(consumer: Consumer<T>): Predicate<T> = Predicate {
        consumer.accept(it)
        true
    }
}
