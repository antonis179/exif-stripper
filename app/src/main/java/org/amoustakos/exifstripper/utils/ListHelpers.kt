package org.amoustakos.exifstripper.utils


/**
 * @receiver will be cleared
 * @return Copy of the current list before cleared
 */
internal inline fun <reified T> MutableList<T>.pollAndClear(): List<T> {
    val list = listOf(*toTypedArray())
    this.clear()
    return list
}