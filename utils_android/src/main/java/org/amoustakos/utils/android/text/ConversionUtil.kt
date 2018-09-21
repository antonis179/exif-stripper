package org.amoustakos.utils.android.text

import java.text.DecimalFormat


object ConversionUtil {

    // =========================================================================================
    // Double conversions
    // =========================================================================================

    fun formatDouble(value: String): String {
        val d = getDouble(value, 0.0)

        return if (d != null)
            formatDouble(d)
        else
            value
    }

    fun getDouble(s: String, defaultNum: Double?): Double? {
        if (!StringUtils.isEmptyString(s)) {
            val s2 = s.trim { it <= ' ' }
            if (StringUtils.isEmptyString(s2))
                return java.lang.Double.parseDouble(s2)
        }

        return defaultNum
    }

    /**
     * Formats a double to 2 decimal places.
     */
    fun formatDouble(value: Double): String {
        return DecimalFormat("#.##").format(value)
    }


    // =========================================================================================
    // Float conversions
    // =========================================================================================

    fun formatFloat(value: String): String {
        val f = getFloat(value, 0f)

        return if (f != null)
            formatFloat(f)
        else
            value
    }

    fun getFloat(s: String, defaultNum: Float?): Float? {
        if (!StringUtils.isEmptyString(s)) {
            val s2 = s.trim { it <= ' ' }
            if (StringUtils.isEmptyString(s2))
                return java.lang.Float.parseFloat(s2)
        }

        return defaultNum
    }

    /**
     * Formats a float to 2 decimal places.
     */
    fun formatFloat(value: Float?): String {
        return DecimalFormat("#.##").format(value)
    }


    // =========================================================================================
    // Integer conversions
    // =========================================================================================


    fun getInt(s: String, defaultNum: Int?): Int? {
        if (!StringUtils.isEmptyString(s)) {
            val s2 = s.trim { it <= ' ' }
            if (StringUtils.isNumeric(s2))
                return Integer.parseInt(s2)
        }
        return defaultNum
    }


    // =========================================================================================
    // Long conversions
    // =========================================================================================


    fun getLong(s: String, defaultNum: Long?): Long? {
        if (!StringUtils.isEmptyString(s)) {
            val s2 = s.trim { it <= ' ' }
            if (StringUtils.isNumeric(s2))
                return java.lang.Long.parseLong(s2)
        }
        return defaultNum
    }


    // =========================================================================================
    // Boolean conversions
    // =========================================================================================


    fun getBoolean(s: String, defaultBool: Boolean?): Boolean? {
        if (!StringUtils.isEmptyString(s)) {
            val s2 = s.trim { it <= ' ' }
            return java.lang.Boolean.parseBoolean(s2)
        }
        return defaultBool
    }

    fun getBooleanFromInt(s: String, defaultBool: Boolean?): Boolean? {
        if (!StringUtils.isEmptyString(s)) {
            val number = getInt(s, null)
            if (number == null)
                return defaultBool
            else if (number == 0)
                return java.lang.Boolean.FALSE
            else if (number == 1)
                return java.lang.Boolean.TRUE
        }
        return defaultBool
    }


    // =========================================================================================
    // Percentage conversions
    // =========================================================================================

    fun getPercent(value: Int, max: Int): Int {
        return value * 100 / max
    }
}
