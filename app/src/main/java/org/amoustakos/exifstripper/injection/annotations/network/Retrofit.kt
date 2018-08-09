package org.amoustakos.exifstripper.injection.annotations.network

import javax.inject.Qualifier


@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class DefaultRetrofitEngine

@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class DefaultRetrofitOptions