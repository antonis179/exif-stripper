package org.amoustakos.exifstripper.io.file.schemehandlers

import org.amoustakos.exifstripper.utils.FileUtils

//TODO: make the rest sealed classes
object ContentType {

	const val WILDCARD = "*/*"

	// =========================================================================================
	// Application
	// =========================================================================================

	const val APPLICATION = "application/"

	const val APPLICATION_ENVOY = APPLICATION + "envoy"
	const val APPLICATION_FRACTALS = APPLICATION + "fractals"
	const val APPLICATION_FUTURESPLASH = APPLICATION + "futuresplash"
	const val APPLICATION_HTA = APPLICATION + "hta"
	const val APPLICATION_INTERNET_PROPERTY_STREAM = APPLICATION + "internet-property-stream"
	const val APPLICATION_MAC_BINHEX40 = APPLICATION + "mac-binhex40"
	const val APPLICATION_MS_WORD = APPLICATION + "msword"
	const val APPLICATION_OCTET_STREAM = APPLICATION + "octet-stream"
	const val APPLICATION_ODA = APPLICATION + "oda"
	const val APPLICATION_OLESCRIPT = APPLICATION + "olescript"
	const val APPLICATION_PDF = APPLICATION + "pdf"
	const val APPLICATION_PICS_RULES = APPLICATION + "pics-rules"
	const val APPLICATION_PKCS10 = APPLICATION + "pkcs10"
	const val APPLICATION_PKIX_CRL = APPLICATION + "pkix-crl"
	const val APPLICATION_POSTSCRIPT = APPLICATION + "postscript"
	const val APPLICATION_RTF = APPLICATION + "rtf"
	const val APPLICATION_SETPAY = APPLICATION + "set-payment-initiation"
	const val APPLICATION_SETREG = APPLICATION + "set-registration-initiation"
	const val APPLICATION_MS_EXCEL = APPLICATION + "vnd.ms-excel"
	const val APPLICATION_MS_OUTLOOK = APPLICATION + "vnd.ms-outlook"
	const val APPLICATION_MS_PKICERTSTORE = APPLICATION + "vnd.ms-pkicertstore"
	const val APPLICATION_MS_PKISECCAT = APPLICATION + "vnd.ms-pkiseccat"
	const val APPLICATION_MS_PKISTL = APPLICATION + "vnd.ms-pkistl"
	const val APPLICATION_MS_POWERPOINT = APPLICATION + "vnd.ms-powerpoint"
	const val APPLICATION_MS_PROJECT = APPLICATION + "vnd.ms-project"
	const val APPLICATION_MS_WORKS = APPLICATION + "vnd.ms-works"
	const val APPLICATION_WINHLP = APPLICATION + "winhlp"
	const val APPLICATION_BCPIO = APPLICATION + "x-bcpio"
	const val APPLICATION_CDF = APPLICATION + "x-cdf"
	const val APPLICATION_Z = APPLICATION + "x-compress"
	const val APPLICATION_TGZ = APPLICATION + "x-compressed"
	const val APPLICATION_CPIO = APPLICATION + "x-cpio"
	const val APPLICATION_CSH = APPLICATION + "x-csh"
	const val APPLICATION_DIRECTOR = APPLICATION + "x-director"
	const val APPLICATION_DVI = APPLICATION + "x-dvi"
	const val APPLICATION_GTAR = APPLICATION + "x-gtar"
	const val APPLICATION_GZIP = APPLICATION + "x-gzip"
	const val APPLICATION_HDF = APPLICATION + "x-hdf"
	const val APPLICATION_INTERNET_SIGNUP = APPLICATION + "x-internet-signup"
	const val APPLICATION_IPHONE = APPLICATION + "x-iphone"
	const val APPLICATION_JAVASCRIPT = APPLICATION + "x-javascript"
	const val APPLICATION_LATEX = APPLICATION + "x-latex"
	const val APPLICATION_MS_ACCESS = APPLICATION + "x-msaccess"
	const val APPLICATION_MS_CARD_FILE = APPLICATION + "x-mscardfile"
	const val APPLICATION_MS_CLIP = APPLICATION + "x-msclip"
	const val APPLICATION_MS_DOWNLOAD = APPLICATION + "x-msdownload"
	const val APPLICATION_MS_MEDIAVIEW = APPLICATION + "x-msmediaview"
	const val APPLICATION_MS_METAFILE = APPLICATION + "x-msmetafile"
	const val APPLICATION_MS_MONEY = APPLICATION + "x-msmoney"
	const val APPLICATION_MS_PUBLISHER = APPLICATION + "x-mspublisher"
	const val APPLICATION_MS_SCHEDULE = APPLICATION + "x-msschedule"
	const val APPLICATION_MS_TERMINAL = APPLICATION + "x-msterminal"
	const val APPLICATION_MS_WRITE = APPLICATION + "x-mswrite"
	const val APPLICATION_NET_CDF = APPLICATION + "x-netcdf"
	const val APPLICATION_PERFMON = APPLICATION + "x-perfmon"
	const val APPLICATION_PKCS_12 = APPLICATION + "x-pkcs12"
	const val APPLICATION_PKCS_7_CERTIFICATES = APPLICATION + "x-pkcs7-certificates"
	const val APPLICATION_PKCS_7_CERTREQRESP = APPLICATION + "x-pkcs7-certreqresp"
	const val APPLICATION_PKCS_7_MIME = APPLICATION + "x-pkcs7-mime"
	const val APPLICATION_PKCS_7_SIGNATURE = APPLICATION + "x-pkcs7-signature"
	const val APPLICATION_SH = APPLICATION + "x-sh"
	const val APPLICATION_SHAR = APPLICATION + "x-shar"
	const val APPLICATION_SHOCKWAVE_FLASH = APPLICATION + "x-shockwave-flash"
	const val APPLICATION_STUFFIT = APPLICATION + "x-stuffit"
	const val APPLICATION_SV4CPIO = APPLICATION + "x-sv4cpio"
	const val APPLICATION_SV4CRC = APPLICATION + "x-sv4crc"
	const val APPLICATION_TAR = APPLICATION + "x-tar"
	const val APPLICATION_TCL = APPLICATION + "x-tcl"
	const val APPLICATION_TEX = APPLICATION + "x-tex"
	const val APPLICATION_TEXINFO = APPLICATION + "x-texinfo"
	const val APPLICATION_TROFF = APPLICATION + "x-troff"
	const val APPLICATION_TROFF_MAN = APPLICATION + "x-troff-man"
	const val APPLICATION_TROFF_ME = APPLICATION + "x-troff-me"
	const val APPLICATION_TROFF_MS = APPLICATION + "x-troff-ms"
	const val APPLICATION_USTAR = APPLICATION + "x-ustar"
	const val APPLICATION_WAIS_SOURCE = APPLICATION + "x-wais-source"
	const val APPLICATION_X509_CA_CERT = APPLICATION + "x-x509-ca-cert"
	const val APPLICATION_PKO = APPLICATION + "ynd.ms-pkipko"
	const val APPLICATION_ZIP = APPLICATION + "zip"
	const val APPLICATION_XML = APPLICATION + "xml"

	// =========================================================================================
	// Audio
	// =========================================================================================

	const val AUDIO = "audio/"

	const val AUDIO_BASIC = AUDIO + "basic"
	const val AUDIO_MID = AUDIO + "mid"
	const val AUDIO_MPEG = AUDIO + "mpeg"
	const val AUDIO_AIFF = AUDIO + "x-aiff"
	const val AUDIO_M3U = AUDIO + "x-mpegurl"
	const val AUDIO_REAL_AUDIO = AUDIO + "x-pn-realaudio"
	const val AUDIO_WAV = AUDIO + "x-wav"

	// =========================================================================================
	// Image
	// =========================================================================================

	sealed class Image(val name: String, val type: String = "$TYPE$name") {
		operator fun invoke() = name
		fun equals(name: String) = name.equals(this.name, ignoreCase = true)

		companion object {
			const val TYPE = "image/"
			const val TYPE_GENERIC = "$TYPE*"
		}

		object BMP : Image("bmp")
		object COD : Image("cod")
		object GIF : Image("gif")
		object IEF : Image("ief")
		object JPEG : Image("jpeg")
		object PIPEG : Image("pipeg")
		object PNG : Image("png")
		object SVG : Image("svg+xml")
		object TIFF : Image("tiff")
		object CmuRaster : Image("x-cmu-raster")
		object CMX : Image("x-cmx")
		object ICO : Image("x-icon")
		object PortableAnymap : Image("x-portable-anymap")
		object PortableBitmap : Image("x-portable-bitmap")
		object PortableGraymap : Image("x-portable-graymap")
		object PortablePixmap : Image("x-portable-pixmap")
		object XRGB : Image("x-rgb")
		object XBITMAP : Image("x-xbitmap")
		object XPIXMAP : Image("x-xpixmap")
		object XWINDOWDUMP : Image("x-xwindowdump")
	}

	// =========================================================================================
	// Text
	// =========================================================================================

	const val TEXT = "text/"

	const val TEXT_CSS = TEXT + "css"
	const val TEXT_CSV = TEXT + "csv"
	const val TEXT_H323 = TEXT + "h323"
	const val TEXT_HTML = TEXT + "html"
	const val TEXT_IULS = TEXT + "iuls"
	const val TEXT_PLAIN = TEXT + "plain"
	const val TEXT_RICHTEXT = TEXT + "richtext"
	const val TEXT_SCRIPTLET = TEXT + "scriptlet"
	const val TEXT_TAB_SEPARATED_VALUES = TEXT + "tab-separated-values"
	const val TEXT_VIEWVIEW = TEXT + "webviewhtml"
	const val TEXT_COMPONENT = TEXT + "x-component"
	const val TEXT_SETEXT = TEXT + "x-setext"
	const val TEXT_VCARD = TEXT + "x-vcard"
	const val TEXT_XML = TEXT + "xml"

	// =========================================================================================
	// Video
	// =========================================================================================

	const val VIDEO = "video/"

	const val VIDEO_MPEG = VIDEO + "mpeg"
	const val VIDEO_MPEG4 = VIDEO + "mp4"
	const val VIDEO_QUICKTIME = VIDEO + "quicktime"
	const val VIDEO_LA_ASF = VIDEO + "x-la-asf"
	const val VIDEO_MS_ASF = VIDEO + "x-ms-asf"
	const val VIDEO_AVI = VIDEO + "avi"
	const val VIDEO_MOVIE = VIDEO + "x-sgi-movie"


	/**
	 * Tries to auto-detect the content type (MIME type) of a specific file.
	 * @param absolutePath absolute path to the file
	 * @return content type (MIME type) of the file, or application/octet-stream if no content
	 * type could be determined automatically
	 */
	fun autoDetect(absolutePath: String): String {
		val extension: String? = FileUtils.getExtension(absolutePath)

		return if (extension == null || extension.isEmpty()) {
			APPLICATION_OCTET_STREAM
		} else {
			FileUtils.getMimeTypeFromExtension(extension.toLowerCase()) ?:
				// mp4 does not always get recognized automatically
				return if ("mp4".equals(extension, ignoreCase = true))
					VIDEO_MPEG4
				else
					APPLICATION_OCTET_STREAM
		}
	}
}