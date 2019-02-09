package org.amoustakos.exifstripper.io.file.schemehandlers

import android.content.Context
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException
import java.util.*

class SchemeHandlerFactory(context: Context) {

    private val handlers = LinkedHashMap<String, Class<out SchemeHandler>>()
    private val contextWeak: WeakReference<Context> = WeakReference(context)


    init {
        FileSchemeHandler.SCHEMES.forEach { handlers[it] = FileSchemeHandler::class.java }
        handlers[ContentSchemeHandler.SCHEME] = ContentSchemeHandler::class.java
    }

    /**
     * Provides a [SchemeHandler] that can handle the provided path/uri
     */
    @Throws(
            NoSuchMethodException::class,
            IllegalAccessException::class,
            InvocationTargetException::class,
            InstantiationException::class,
            NullPointerException::class
    )
    operator fun get(path: String): SchemeHandler {
        for ((key, value) in handlers) {
            if (path.startsWith(key)) {
                val schemeHandler = value.newInstance()
                schemeHandler.init(path, contextWeak.get()!!) //This should fail if context is null
                return schemeHandler
            }
        }

        throw UnsupportedOperationException("No handlers for $path")
    }

    /**
     * Checks whether this path/uri can be handled by one of the registered schemes
     */
    fun isSupported(path: String): Boolean {
        for (scheme in handlers.keys)
            if (path.startsWith(scheme))
                return true
        return false
    }

}