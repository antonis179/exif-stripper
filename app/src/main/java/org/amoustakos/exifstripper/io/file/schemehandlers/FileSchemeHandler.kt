package org.amoustakos.exifstripper.io.file.schemehandlers

import android.content.Context
import org.amoustakos.exifstripper.utils.FileUtils.getExtension
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

internal class FileSchemeHandler : SchemeHandler {

    private var file: File? = null

    override fun init(path: String, context: Context) {
        file = File(path)
    }

	@Throws(NullPointerException::class)
    override fun getLength() = file?.length() ?: throw nullPointer()

    @Throws(FileNotFoundException::class)
    override fun getInputStream() = FileInputStream(file)

	@Throws(NullPointerException::class)
    override fun getContentType() = file?.let {
        ContentType.autoDetect(it.absolutePath)
    } ?: throw nullPointer()

    override fun getFileExtension() = file?.let { getExtension(it.absolutePath) }

	@Throws(NullPointerException::class)
    override fun getName() = file?.name ?: throw nullPointer()

    override fun hasReadPermission() = try {
        getName()
        true
    } catch (npe: NullPointerException) {
        false
    }

    override fun getPath() = file?.path



    private fun nullPointer(): Throwable = NullPointerException("File does not exist")

    companion object {
        val SCHEMES = arrayOf("/", "file://")
    }
}
