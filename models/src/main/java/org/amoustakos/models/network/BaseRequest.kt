package org.amoustakos.models.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Modifier
import java.util.*


abstract class BaseRequest {


    /**
     * Note: remember to mark the members you want to be included with [Expose]
     */
    open fun stringsAsMap(): Map<String, String> {
        val mapping = HashMap<String, String>()
        val fields = javaClass.declaredFields
        val fieldsList = ArrayList(Arrays.asList(*fields))
        var current: Class<*> = javaClass

        while (current.superclass != null && current.superclass != BaseRequest::class.java) {
            current = javaClass.superclass
            fieldsList.addAll(Arrays.asList(*current.declaredFields))
        }

        for (f in fieldsList) {
            if (Modifier.isTransient(f.modifiers))
                continue
            if (f.getAnnotation(Expose::class.java) == null)
                continue

            val name: String
            val serializedName = f.getAnnotation(SerializedName::class.java)

            name = serializedName?.value ?: f.name

            f.isAccessible = true
            try {
                val o = f.get(this)
                getValue(o)?. let {
                    mapping[name] = it
                } ?: mapping.remove(name)
            } catch (ignored: IllegalArgumentException) {}
            catch (ignored: IllegalAccessException) {}
        }

        return mapping
    }


    @Throws(IllegalArgumentException::class)
    private fun getValue(o: Any?): String? {
        if (o == null)
            return null

        return o as? String ?: if (o is Int
                || o is Long
                || o is Double
                || o is Float) {
            o.toString()
        }
        else (o as? Enum<*>)?.toString()
                ?: ((o as? Boolean)?.toString()
                ?: throw IllegalArgumentException("Illegal object type"))
    }

}
