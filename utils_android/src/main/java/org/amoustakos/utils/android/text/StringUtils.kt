package org.amoustakos.utils.android.text

import android.text.TextUtils

object StringUtils {

    const val NULL_STRING = "null"
    const val EMPTY = " "


    fun compileUrl(protocol: String, ip: String, port: String): String {
        return "$protocol://$ip:$port"
    }


    /**
     * Splits the provided [String] into words and makes the first letter of each one uppercase
     */
    fun toCamelCase(text: String): String {
        val myText = text.toLowerCase()
        val arr = myText.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sb = StringBuilder()

        for (anArr in arr)
            sb.append(Character.toUpperCase(anArr[0]))
                    .append(anArr.substring(1)).append(" ")


        return sb.toString().trim { it <= ' ' }
    }


    /**
     * Checks if the text is: <br></br>
     * 1. null or "null" <br></br>
     * 2. "" <br></br>
     * 3. " " <br></br>
     */
    fun isEmptyString(text: String): Boolean {
        return TextUtils.isEmpty(text) ||
                text.trim { it <= ' ' } == NULL_STRING ||
                text == EMPTY
    }

    /**
     *
     * Checks if the CharSequence contains only Unicode digits.
     * A decimal point is not a Unicode digit and returns false.
     *
     *
     * `null` will return `false`.
     * An empty CharSequence (length()=0) will return `false`.
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = false
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
    </pre> *
     *
     * @param cs  the CharSequence to check, may be null
     * @return `true` if only contains digits, and is non-null
     * @since 3.0 Changed signature from isNumeric(String) to isNumeric(CharSequence)
     * @since 3.0 Changed "" to return false and not true
     */
    fun isNumeric(cs: CharSequence?): Boolean {
        if (cs == null || cs.isEmpty()) {
            return false
        }
        val sz = cs.length
        for (i in 0 until sz) {
            if (!Character.isDigit(cs[i])) {
                return false
            }
        }
        return true
    }
}
