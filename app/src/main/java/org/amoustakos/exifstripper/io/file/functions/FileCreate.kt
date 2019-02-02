package org.amoustakos.exifstripper.io.file.functions

import org.amoustakos.exifstripper.io.Function
import timber.log.Timber
import java.io.*

open class FileCreate(
		private val saveLocation: () -> String,
		private val bufferSize: () -> Int = {4096},
		private val outputStream: (file: File) -> OutputStream = { FileOutputStream(it) }
) : Function<File, Boolean> {

	operator fun invoke(item: File) = exec(item)

	override fun exec(input: File): Boolean {
		try {
			val outputStream: OutputStream = outputStream(getFile())
			val inputStream = FileInputStream(input)

			try {
				val fileReader = ByteArray(bufferSize())

				while (true) {
					val read = inputStream.read(fileReader)

					if (read == -1) break

					outputStream.write(fileReader, 0, read)
				}
				outputStream.flush()

				return true
			} catch (e: IOException) {
				Timber.e(e)
				return false
			} finally {
				inputStream.close()
				outputStream.close()
			}
		} catch (e: IOException) {
			Timber.e(e)
			return false
		}
	}

	private fun getFile() = File(saveLocation())
}