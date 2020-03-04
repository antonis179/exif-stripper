<a href="https://www.buymeacoffee.com/AoCpEcg" target="_blank"><img src="https://bmc-cdn.nyc3.digitaloceanspaces.com/BMC-button-images/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>


# Exif Stripper #

Exif Stripper removes private information from your photos stored as image metadata (exif) so you can share them safely!

Download from [Play Store](https://play.google.com/store/apps/details?id=org.amoustakos.exifstripper)


## Setup ##

- Setup a signing key through android studio
- Setup a firebase project and add google-services.json as per instructions
- Add `_SENSITIVE_HIDE_FROM_GIT_` folder in project root
- Add password.properties in `_SENSITIVE_HIDE_FROM_GIT_` (the location path should point to your key):
    ```properties
    debug.key.location=
    debug.key.password=
    debug.key.alias=
    debug.key.alias.password=

    release.key.location=
    release.key.password=
    release.key.alias=
    release.key.alias.password=
    ```

## Other Instructions ##

### Dependency updates ###

To check available dependency updates you can run:
```
./gradlew dependencyUpdates
```

More info [here](https://github.com/ben-manes/gradle-versions-plugin)

## Build notes ##

### Gradle ###
The project specifies a local gradle.properties file which you should adjust to fit your specs.
Options `org.gradle.jvmargs` and `org.gradle.parallel.threads` are commented as you should set these
in your global `gradle.properties` file

## Donations ##

So you liked my code and want to buy me coffee and pizza? That's awesome!

- BTC: **19rqnHTiZNxeXVSCB2cckJeC92rm23nNrc**
- Paypal: **<https://www.paypal.me/neo179>**
- Buy me a coffee: **<https://www.buymeacoffee.com/AoCpEcg>**