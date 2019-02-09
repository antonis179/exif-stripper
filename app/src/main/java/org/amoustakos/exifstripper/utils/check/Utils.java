package org.amoustakos.exifstripper.utils.check;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import androidx.core.content.ContextCompat;
import timber.log.Timber;

public class Utils {
    public enum ImageType {JPG, PNG, BMP, GIF, TIFF, UNKNOWN}

    private Context context;

    Utils(Context context) {
        this.context = context;
    }

    public boolean isPermissionGranted() {
        boolean granted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            granted = permissionCheck == PackageManager.PERMISSION_GRANTED;
        }

        return granted;
    }

    public File copyToCacheDir(Uri imageUri) {
        Timber.d("Copying image '%s' to cache dir", imageUri.getPath());

        File imagesCacheDir = new File(context.getCacheDir() + "/images");
        imagesCacheDir.mkdir();

        File destination = new File(String.format("%s/img_eggsif_%s.%s",
                imagesCacheDir,
                Math.abs(new Random().nextLong()),
                getImageType(imageUri).name().toLowerCase()));

        Timber.d("Temporary file name: %s", destination);
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(imageUri);
                outputStream = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];
                int bytesCount;
                while ((bytesCount = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bytesCount);
                }
            } catch (IOException e) {
                Timber.e(e,"Error copying file to cache dir");
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Timber.e(e, "Error closing streams while copying file while copying file to cache dir");
            e.printStackTrace();
        }

        return destination;
    }

    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String getMagicNumbers(InputStream inputStream) {
        final int BYTES_TO_READ = 8;
        byte[] magicBytes = new byte[BYTES_TO_READ];
        int bytesRead;

        try {
            bytesRead = inputStream.read(magicBytes, 0, BYTES_TO_READ);
            inputStream.close();
        } catch (IOException e) {
            Timber.e("An error ocurred while trying to read the file. Supposing it is not an image");
            e.printStackTrace();
            return "";
        }

        if (bytesRead != BYTES_TO_READ) {
            Timber.e("Failed to read the first %s bytes for file", BYTES_TO_READ);
            return "";
        }

        String magicBytesAsHexString = bytesToHex(magicBytes);
        Timber.d("First %s bytes: %s", BYTES_TO_READ, magicBytesAsHexString);

        return magicBytesAsHexString;
    }

    private ImageType getImageType(InputStream inputStream) {
        String magicNumbers = getMagicNumbers(inputStream);

        ImageType imageType;

        if (magicNumbers.startsWith("FFD8")) {
            imageType = ImageType.JPG;
            Timber.d("It's a JPEG image");
        } else if (magicNumbers.startsWith("89504E470D0A1A0A")) {
            imageType = ImageType.PNG;
            Timber.d("It's a PNG image");
        } else if (magicNumbers.startsWith("424D")) {
            imageType = ImageType.BMP;
            Timber.d("It's a BMP image");
        } else if (magicNumbers.startsWith("474946383961") ||
                magicNumbers.startsWith("474946383761")) {
            imageType = ImageType.GIF;
            Timber.d("It's a GIF image");
        } else if (magicNumbers.startsWith("49492A00") ||
                magicNumbers.startsWith("4D4D002A")) {
            imageType = ImageType.TIFF;
            Timber.d("It's a TIFF image");
        } else {
            imageType = ImageType.UNKNOWN;
            Timber.d("It's (probably) not an image. Failed to recognize type.");
        }

        return imageType;
    }

    public ImageType getImageType(Uri uri) {
        Timber.d("Getting ImageType from uri '%s'...", uri);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            ImageType imageType = getImageType(inputStream);
            inputStream.close();
            return imageType;
        } catch (FileNotFoundException e) {
            Timber.e(e, "Couldn't open input stream from content resolver for uri '%s'", uri);
            e.printStackTrace();
            return ImageType.UNKNOWN;
        } catch (IOException e) {
            Timber.e(e, "Couldn't close input stream from content resolver for uri '%s'", uri);
            e.printStackTrace();
            return ImageType.UNKNOWN;
        }
    }

    public ImageType getImageType(File file) {
        Timber.d("Getting ImageType from file '%s'...", file);
        try {
            InputStream inputStream = new FileInputStream(file);
            return getImageType(inputStream);
        } catch (FileNotFoundException e) {
            Timber.e(e, "Couldn't open input stream from content resolver for file '%s'", file);
            e.printStackTrace();
            return ImageType.UNKNOWN;
        }
    }

    public boolean isImage(Uri uri) {
        Timber.d("Checking if uri '%s' corresponds to an image...", uri);
        return getImageType(uri) != ImageType.UNKNOWN;
    }
}
