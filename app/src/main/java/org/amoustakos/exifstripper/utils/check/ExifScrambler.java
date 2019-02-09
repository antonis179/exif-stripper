package org.amoustakos.exifstripper.utils.check;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import androidx.exifinterface.media.ExifInterface;
import timber.log.Timber;

public class ExifScrambler {
    private final Context context;
    private static Set<String> exifAttributes;
    private Utils utils;

    public ExifScrambler(Context context) {
        this.context = context;
    }

//    public Uri scrambleImage(Uri imageUri) {
//        File scrambledImageFile = getUtils().copyToCacheDir(imageUri);
//        removeExifData(scrambledImageFile);
//        return FileProvider.getUriForFile(context, "com.jarsilio.android.scrambledeggsif.fileprovider", scrambledImageFile);
//    }

//    private void removeExifData(File image) {
//        if (getSettings().isRewriteImages()) {
//            Timber.d("Force image re-write is on: rewriting whole image instead of just removing EXIF data");
//            removeExifDataByResavingImage(image);
//        } else {
//            Timber.d("Force image re-write is off: trying to just remove EXIF data");
//            removeExifDataWithExifInterface(image);
//        }
//    }
//
//    private void removeExifDataByResavingImage(File image) {
//        String originalOrientation = null; // We might not need this
//        if (getSettings().isKeepJpegOrientation()) {
//             originalOrientation = getOrientation(image);
//        }
//
//        Bitmap originalImage = BitmapFactory.decodeFile(image.getPath());
//        Utils.ImageType originalImageType = getUtils().getImageType(image);
//        try {
//            FileOutputStream outputStream = new FileOutputStream(image);
//            if (originalImageType.equals(Utils.ImageType.PNG)) {
//                originalImage.compress(Bitmap.CompressFormat.PNG, 95, outputStream);
//            } else {
//                // If I don't know what type of image it is (or it is a JPEG), save as JPEG
//                originalImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
//                if (getSettings().isKeepJpegOrientation()) {
//                    setOrientation(originalOrientation, image);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            Timber.e("Couldn't find file to write to: %s", image);
//            e.printStackTrace();
//        }
//
//        Timber.d("Sometimes re-saving as JPEG might add exif data. Removing exif data with ExifInterface to try to delete it");
//        removeExifDataWithExifInterface(image, false);
//    }
//
//    private void removeExifDataWithExifInterface(File image, boolean fallback) {
//        /* First try to delete Exif data because it is the fastest way to do it.
//         * If this fails, open the image and save it again with the resave image
//         */
//        try {
//            ExifInterface exifInterface = new ExifInterface(image.toString());
//            for (String attribute : getExifAttributes()) {
//                String value = exifInterface.getAttribute(attribute);
//                if (getSettings().isKeepJpegOrientation() && attribute.equals(ExifInterface.TAG_ORIENTATION)) {
//                    Timber.d("Keep orientation is on: skipping ExifInterface.TAG_ORIENTATION attribute. Orientation is set to %s", value);
//                    continue;
//                }
//                if (value != null) {
//                    Timber.v("Exif attribute " + attribute + " is set. Removing (setting to null)");
//                    exifInterface.setAttribute(attribute, null);
//                }
//            }
//            exifInterface.saveAttributes();
//        } catch (IOException e) {
//            if (fallback) {
//                Timber.e(e, "Failed to remove exif data with ExifInterface. Falling back to re-saving image");
//                removeExifDataByResavingImage(image);
//            } else {
//                Timber.e(e, "Failed to remove exif data with ExifInterface.");
//            }
//        }
//    }
//
//    private void removeExifDataWithExifInterface(File image) {
//        removeExifDataWithExifInterface(image, true);
//    }
//
//    private String getOrientation(File image) {
//        String orientation = null;
//        try {
//            ExifInterface exifInterface = new ExifInterface(image.toString());
//            orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
//        } catch (IOException e) {
//            Timber.e(e, "Couldn't read orientation exif attribute for image '%s'", image);
//            e.printStackTrace();
//        }
//        return orientation;
//    }
//
//    private String setOrientation(String orientation, File image) {
//        // Somehow, this adds ImageLength, ImageWidth and LigtSource to the exif metadata
//        // Running remove exif data with ExifInterface after re-writing image to get rid of these
//        try {
//            Timber.d("Trying to set orientation for image '%s' to %s", image, orientation);
//            ExifInterface exifInterface = new ExifInterface(image.toString());
//            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
//            exifInterface.saveAttributes();
//        } catch (IOException e) {
//            Timber.e(e, "Couldn't save orientation exif tag for image '%s'", image);
//            e.printStackTrace();
//        }
//        return orientation;
//    }
//
//    private Utils getUtils() {
//        if (utils == null) {
//            utils = new Utils(context);
//        }
//        return utils;
//    }

    private static Set<String> getExifAttributes() {
        if (exifAttributes == null) {
            exifAttributes = new HashSet<>();

            // From my current Android SDK
            exifAttributes.add("FNumber");
            exifAttributes.add("ApertureValue");
            exifAttributes.add("Artist");
            exifAttributes.add("BitsPerSample");
            exifAttributes.add("BrightnessValue");
            exifAttributes.add("CFAPattern");
            exifAttributes.add("ColorSpace");
            exifAttributes.add("ComponentsConfiguration");
            exifAttributes.add("CompressedBitsPerPixel");
            exifAttributes.add("Compression");
            exifAttributes.add("Contrast");
            exifAttributes.add("Copyright");
            exifAttributes.add("CustomRendered");
            exifAttributes.add("DateTime");
            exifAttributes.add("DateTimeDigitized");
            exifAttributes.add("DateTimeOriginal");
            exifAttributes.add("DefaultCropSize");
            exifAttributes.add("DeviceSettingDescription");
            exifAttributes.add("DigitalZoomRatio");
            exifAttributes.add("DNGVersion");
            exifAttributes.add("ExifVersion");
            exifAttributes.add("ExposureBiasValue");
            exifAttributes.add("ExposureIndex");
            exifAttributes.add("ExposureMode");
            exifAttributes.add("ExposureProgram");
            exifAttributes.add("ExposureTime");
            exifAttributes.add("FileSource");
            exifAttributes.add("Flash");
            exifAttributes.add("FlashpixVersion");
            exifAttributes.add("FlashEnergy");
            exifAttributes.add("FocalLength");
            exifAttributes.add("FocalLengthIn35mmFilm");
            exifAttributes.add("FocalPlaneResolutionUnit");
            exifAttributes.add("FocalPlaneXResolution");
            exifAttributes.add("FocalPlaneYResolution");
            exifAttributes.add("GainControl");
            exifAttributes.add("GPSAltitude");
            exifAttributes.add("GPSAltitudeRef");
            exifAttributes.add("GPSAreaInformation");
            exifAttributes.add("GPSDateStamp");
            exifAttributes.add("GPSDestBearing");
            exifAttributes.add("GPSDestBearingRef");
            exifAttributes.add("GPSDestDistance");
            exifAttributes.add("GPSDestDistanceRef");
            exifAttributes.add("GPSDestLatitude");
            exifAttributes.add("GPSDestLatitudeRef");
            exifAttributes.add("GPSDestLongitude");
            exifAttributes.add("GPSDestLongitudeRef");
            exifAttributes.add("GPSDifferential");
            exifAttributes.add("GPSDOP");
            exifAttributes.add("GPSImgDirection");
            exifAttributes.add("GPSImgDirectionRef");
            exifAttributes.add("GPSLatitude");
            exifAttributes.add("GPSLatitudeRef");
            exifAttributes.add("GPSLongitude");
            exifAttributes.add("GPSLongitudeRef");
            exifAttributes.add("GPSMapDatum");
            exifAttributes.add("GPSMeasureMode");
            exifAttributes.add("GPSProcessingMethod");
            exifAttributes.add("GPSSatellites");
            exifAttributes.add("GPSSpeed");
            exifAttributes.add("GPSSpeedRef");
            exifAttributes.add("GPSStatus");
            exifAttributes.add("GPSTimeStamp");
            exifAttributes.add("GPSTrack");
            exifAttributes.add("GPSTrackRef");
            exifAttributes.add("GPSVersionID");
            exifAttributes.add("ImageDescription");
            exifAttributes.add("ImageLength");
            exifAttributes.add("ImageUniqueID");
            exifAttributes.add("ImageWidth");
            exifAttributes.add("InteroperabilityIndex");
            exifAttributes.add("ISOSpeedRatings");
            exifAttributes.add("JPEGInterchangeFormat");
            exifAttributes.add("JPEGInterchangeFormatLength");
            exifAttributes.add("LightSource");
            exifAttributes.add("Make");
            exifAttributes.add("MakerNote");
            exifAttributes.add("MaxApertureValue");
            exifAttributes.add("MeteringMode");
            exifAttributes.add("Model");
            exifAttributes.add("NewSubfileType");
            exifAttributes.add("OECF");
            exifAttributes.add("AspectFrame");
            exifAttributes.add("PreviewImageLength");
            exifAttributes.add("PreviewImageStart");
            exifAttributes.add("ThumbnailImage");
            exifAttributes.add("Orientation");
            exifAttributes.add("PhotometricInterpretation");
            exifAttributes.add("PixelXDimension");
            exifAttributes.add("PixelYDimension");
            exifAttributes.add("PlanarConfiguration");
            exifAttributes.add("PrimaryChromaticities");
            exifAttributes.add("ReferenceBlackWhite");
            exifAttributes.add("RelatedSoundFile");
            exifAttributes.add("ResolutionUnit");
            exifAttributes.add("RowsPerStrip");
            exifAttributes.add("ISO");
            exifAttributes.add("JpgFromRaw");
            exifAttributes.add("SensorBottomBorder");
            exifAttributes.add("SensorLeftBorder");
            exifAttributes.add("SensorRightBorder");
            exifAttributes.add("SensorTopBorder");
            exifAttributes.add("SamplesPerPixel");
            exifAttributes.add("Saturation");
            exifAttributes.add("SceneCaptureType");
            exifAttributes.add("SceneType");
            exifAttributes.add("SensingMethod");
            exifAttributes.add("Sharpness");
            exifAttributes.add("ShutterSpeedValue");
            exifAttributes.add("Software");
            exifAttributes.add("SpatialFrequencyResponse");
            exifAttributes.add("SpectralSensitivity");
            exifAttributes.add("StripByteCounts");
            exifAttributes.add("StripOffsets");
            exifAttributes.add("SubfileType");
            exifAttributes.add("SubjectArea");
            exifAttributes.add("SubjectDistance");
            exifAttributes.add("SubjectDistanceRange");
            exifAttributes.add("SubjectLocation");
            exifAttributes.add("SubSecTime");
            exifAttributes.add("SubSecTimeDigitized");
            exifAttributes.add("SubSecTimeOriginal");
            exifAttributes.add("ThumbnailImageLength");
            exifAttributes.add("ThumbnailImageWidth");
            exifAttributes.add("TransferFunction");
            exifAttributes.add("UserComment");
            exifAttributes.add("WhiteBalance");
            exifAttributes.add("WhitePoint");
            exifAttributes.add("XResolution");
            exifAttributes.add("YCbCrCoefficients");
            exifAttributes.add("YCbCrPositioning");
            exifAttributes.add("YCbCrSubSampling");
            exifAttributes.add("YResolution");

            // Get all fields that the concrete Android-Java implementation have and delete them
            Field[] fields = ExifInterface.class.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isPublic(field.getModifiers()) &&
                        Modifier.isStatic(field.getModifiers()) &&
                        Modifier.isFinal(field.getModifiers())) {

                    if (field.getType() == String.class) {
                        String attribute;
                        try {
                            attribute = (String) field.get(String.class);
                            exifAttributes.add(attribute);
                        } catch (IllegalAccessException e) {
                            Timber.e(e);
                        }
                    }
                }
            }
        }

        return exifAttributes;
    }
}
