package org.amoustakos.exifstripper.io.file


open class File<O, R, C, S, U, D> (
		private val openable: Openable<O>,
		private val readable: Readable<R>,
		private val cacheable: Cacheable<C>,
		private val storeable: Storeable<S>,
		private val uploadable: Uploadable<U>,
		private val downloadable: Downloadable<D>
)
	: FileIO<O, R, C, S, U, D>
{
	override fun open()     = openable.open()
	override fun read()     = readable.read()
	override fun cache()    = cacheable.cache()
	override fun store()    = storeable.store()
	override fun upload()   = uploadable.upload()
	override fun download() = downloadable.download()
}

interface FileIO<O, R, C, S, U, D> :
		Openable<O>,
		Readable<R>,
		Cacheable<C>,
		Storeable<S>,
		Uploadable<U>,
		Downloadable<D>





interface Openable<Response> {
	fun open(): Response
}

interface Readable<Response> {
	fun read(): Response
}

interface Storeable<Response> {
	fun store(): Response
}

interface Uploadable<Response> {
	fun upload(): Response
}

interface Cacheable<Response> {
	fun cache(): Response
}

interface Downloadable<Response> {
	fun download(): Response
}