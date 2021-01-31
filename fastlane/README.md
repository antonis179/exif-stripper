fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android alpha
```
fastlane android alpha
```
Submit [flavor_env] build to Alpha. Usage fastlane alpha --env [flavor_env]
### android beta
```
fastlane android beta
```
Promote [flavor_env] to Beta. Usage fastlane beta --env [flavor_env]
### android release_10
```
fastlane android release_10
```
Promote [flavor_env] to Production (10%). Usage fastlane release --env [flavor_env]
### android release
```
fastlane android release
```
Promote [flavor_env] to Production (100%). Usage fastlane release --env [flavor_env]

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
