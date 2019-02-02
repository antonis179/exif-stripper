package org.amoustakos.exifstripper.io.file.functions

import org.amoustakos.exifstripper.io.Function
import timber.log.Timber
import java.io.*

open class InputStreamFileCreate(
		private val saveLocation: () -> String,
		private val bufferSize: () -> Int = {4096},
		private val outputStream: (file: File) -> OutputStream = { FileOutputStream(it) }
) : Function<InputStream, Boolean> {

	operator fun invoke(item: InputStream) = exec(item)

	override fun exec(input: InputStream): Boolean {
		try {
			val outputStream: OutputStream = outputStream(getFile())

			try {
				val fileReader = ByteArray(bufferSize())

				while (true) {
					val read = input.read(fileReader)

					if (read == -1) break

					outputStream.write(fileReader, 0, read)
				}
				outputStream.flush()

				return true
			} catch (e: IOException) {
				Timber.e(e)
				return false
			} finally {
				input.close()
				outputStream.close()
			}
		} catch (e: IOException) {
			Timber.e(e)
			return false
		}
	}

	private fun getFile() = File(saveLocation())
}