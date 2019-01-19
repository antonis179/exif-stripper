## Thanks ##

- https://github.com/sellmair/disposer for lifecycle disposer!

## Coming soon ##

- Networking: All available options will be added to okhttp/retrofit option classes
- ViewModel / LiveData / WorkManager
- NetworkCapabilities utility
- Realm restructuring
- Proguard / Gradle restructuring

### Frozen due to unresolved bugs ###
- AndroidX migration


## Build notes ##

### Gradle ###
The project specifies a local gradle.properties file which you should adjust to fit your specs.
Options `org.gradle.jvmargs` and `org.gradle.parallel.threads` are commented as you should set these
in your global `gradle.properties` file

### AAR generation ###

To generate an aar for every module in the project run one of the following commands at project root:
-   ```bash
    ./gradlew assemble
    ```
-   ```bash
    ./gradlew assembleDebug
    ```
-   ```bash
    ./gradlew assembleRelease
    ```

The libraries will be stored in `/root/genLibs`

### Mainframer

This project takes a lot to build so when working on a slow machine I prefer to use a remote machine for my builds. Mainframer does just this. You can set it up via docker on a more powerful machine and sync using ssh/rsync. Additionally there is a studio plugin (yay!).

- Setup Docker using this [README](mainframer_docker/README.md) file
- **NOTE:** If you are using my signing setup make sure you use relative paths in `_SENSITIVE_HIDE_FROM_GIT_` properties files (e.g. `../_SENSITIVE_HIDE_FROM_GIT_/key`)


## Technologies / Architecture ##

### Architecture ###
MVP is used as the architecture.

### Technologies / Libraries ###

- Kotlin
- Dagger 2
- RXJava / RXKotlin
- Retrofit / Okhttp
- Realm
- Firebase (performance, fcm, crashlytics)
- Event Bus
- Timber
- Butterknife

##### Deprecated / to be removed #####

- Firebase Job Dispatcher

​	
## Setup ##

### Without Firebase ###

- Remove signing configuration from app level gradle
    ```groovy
    signingConfigs {
            debug {
                ...
            }

            release {
                ...
            }
        }


        buildTypes {
            debug {
                //...
                signingConfig signingConfigs.debug
            }
            release {
                //...
                signingConfig signingConfigs.release
            }
        }
    ```



- Remove all firebase implementation
	- From project level gradle:
        ```groovy
        //Analytics
        classpath "com.google.gms:google-services:${FIREBASE}"
        classpath "com.google.firebase:firebase-plugins:${FIREBASE_PERFORMANCE}"
        ```
	- From app level gradle
        ```groovy
        apply plugin: 'com.google.firebase.firebase-perf'
        //...
        //Analytics
        implementation "com.google.firebase:firebase-core:${FIREBASE_CORE}"
        implementation "com.google.firebase:firebase-perf:${FIREBASE_PERF}"
        implementation "com.crashlytics.sdk.android:crashlytics:${CRASHLYTICS}"
        //...
        apply plugin: 'com.google.gms.google-services'
        ```
	- Remove `FCMService` (from manifest as well)
	- Remove Firebase metadata from manifest
        ```xml
        <application>
            <!-- FCM -->
            <meta-data
                android:name="com.google.firebase.messaging.default_notification_channel_id"
                android:value="@string/notif_default_channel_id"/>

            <meta-data
                android:name="com.google.firebase.messaging.default_notification_icon"
                android:resource="@android:drawable/ic_menu_info_details" />

            <meta-data
                android:name="com.google.firebase.messaging.default_notification_color"
                android:resource="@color/accent" />
        </application>
        ```
	
### With Firebase ###

- Setup a signing key through android studio
- Setup a firebase project and add google-services.json as per instructions
- Add `_SENSITIVE_HIDE_FROM_GIT_` folder in project root
- Add password.properties in `_SENSITIVE_HIDE_FROM_GIT_`:
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
gradlew dependencyUpdates
```

More info [here](https://github.com/ben-manes/gradle-versions-plugin)


## Donations ##

So you liked my code and want to buy me coffee and pizza? That's awesome!

- BTC: **1JaV93dtuy13gw4u1wHeDvnzfJ2oAGFprF**
- Paypal: **https://www.paypal.me/neo179**