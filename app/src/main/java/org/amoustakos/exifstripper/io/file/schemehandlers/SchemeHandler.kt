package org.amoustakos.exifstripper.io.file.schemehandlers

import android.content.Context

import java.io.FileNotFoundException
import java.io.InputStream

interface SchemeHandler {

    fun init(path: String, context: Context)

    fun getLength(): Long

    @Throws(FileNotFoundException::class)
    fun getInputStream(): InputStream?

    fun getContentType(): String

    fun getFileExtension(): String?

    fun getName(): String

    fun hasReadPermission(): Boolean

	fun getPath(): String?

}
