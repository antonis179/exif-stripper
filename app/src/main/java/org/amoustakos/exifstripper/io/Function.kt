package org.amoustakos.exifstripper.io


interface Function<Request, Response> {
	fun exec(input: Request): Response
}