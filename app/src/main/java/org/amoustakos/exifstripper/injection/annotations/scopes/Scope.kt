package org.amoustakos.exifstripper.injection.annotations.scopes

import javax.inject.Scope


/**
 * A scoping annotation to permit dependencies conform to the life of the
 * [org.amoustakos.exifstripper.injection.component.ConfigPersistentComponent]
 */
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ConfigPersistent



/**
 * A scoping annotation to permit objects whose lifetime should
 * conform to the life of the Activity to be memorised in the
 * correct component.
 */
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity