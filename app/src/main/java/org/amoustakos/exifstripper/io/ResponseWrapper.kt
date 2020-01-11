package org.amoustakos.exifstripper.io

data class ResponseWrapper<T>(
		var value: T? = null
)

data class ResponseWrapperList<T>(
		var values: MutableList<ResponseWrapper<T>>? = null
) {
	infix fun plus(item: ResponseWrapper<T>) {
		values?.add(item)
	}
}