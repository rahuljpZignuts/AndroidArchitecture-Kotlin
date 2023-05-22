
# Android Application Architecture - Kotlin

This Project demonstrates how to structure your app following recommended best practices.

However the recommendations and best practices present in this project can be applied to a broad spectrum of apps to allow them to scale, improve quality and robustness, and make them easier to test. However, you should treat them as guidelines and adapt them to your requirements as needed.


## Modern App Architecture

This Modern App Architecture encourages using the following techniques, among others:

- A reactive and layered architecture.
- Unidirectional Data Flow (UDF) in all layers of the app.
- A UI layer with state holders to manage the complexity of the UI.
- Coroutines and flows.
- Dependency injection best practices.

## General Best Practices

The following recommendations aren't mandatory, in most cases following them makes your code base more robust, testable, and maintainable in the long run:

**Don't store data in app components.**

Avoid designating your app's entry points—such as activities, services, and broadcast receivers—as sources of data. Instead, they should only coordinate with other components to retrieve the subset of data that is relevant to that entry point. Each app component is rather short-lived, depending on the user's interaction with their device and the overall current health of the system.

**Reduce dependencies on Android classes.**

Your app components should be the only classes that rely on Android framework SDK APIs such as `Context`, or `Toast`. Abstracting other classes in your app away from them helps with testability and reduces `coupling` within your app.

**Create well-defined boundaries of responsibility between various modules in your app.**

For example, don't spread the code that loads data from the network across multiple classes or packages in your code base. Similarly, don't define multiple unrelated responsibilities—such as data caching and data binding—in the same class.

**Expose as little as possible from each module.**

For example, don't be tempted to create a shortcut that exposes an internal implementation detail from a module. You might gain a bit of time in the short term, but you are then likely to incur technical debt many times over as your codebase evolves.

**Consider how to make each part of your app testable in isolation.**

For example, having a well-defined API for fetching data from the network makes it easier to test the module that persists that data in a local database. If instead, you mix the logic from these two modules in one place, or distribute your networking code across your entire code base, it becomes much more difficult—if not impossible—to test effectively.

## Code Quality

Code quality is a measurement of how high or low the value of a specific set of code, program or software is. Typically, a code is high quality if the lines of code are easy to interpret and if the developer documented the code. High-quality code often meets these common parameters:

- Functional
- Consistent
- Easy to understand
- Meets clients' needs
- Testable
- Reusable
- Free of bugs and errors
- Secure
- Well documented

When a developer writes low-quality code, it can create issues with security, code errors and functionality.

Here are some additional reasons code quality is important:
- **Enhances code readability**
- **Improves the level of program sustainability**
- **Increases transferability**

## Features

This Project attempts to use the latest cutting edge libraries and tools. As a summary:

- Developed entirely in [Kotlin](https://kotlinlang.org/)
- Benefits from advanced features of Kotlin like [flows](https://developer.android.com/kotlin/flow) and [coroutines](https://developer.android.com/kotlin/coroutines)
- Depends on most of the [Architecture Components](https://developer.android.com/topic/libraries/architecture/): Room, LiveData and lifecycle-aware components
- Uses [Android Data Binding Library](https://developer.android.com/topic/libraries/data-binding) and [View Binding](https://developer.android.com/topic/libraries/view-binding) extensively
- Provides dependency injection through [Hilt Android](https://developer.android.com/training/dependency-injection/hilt-android)
- Handles navigation using [Android navigation component](https://developer.android.com/guide/navigation)
- Stores data in [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) using [protocol buffers](https://developers.google.com/protocol-buffers)

## Development Setup

First off, it requires latest stable version of [Android Studio](https://developer.android.com/studio) (Dolphin | 2021.3.1 or newer) to be able to build the app.

### Compiling Requirements

#### Build Tools and Compile SDK
The project requires build tools version `33.0.0` and compile tools from Android SDK `33` to build this project. Complete requirements can be seen in project level [`build.gradle`](build.gradle#L23). If one faces error compiling this project, please install **Android SDK Platform 33 (Android 13)** manually from `Preferences` -> `Appearance & Behavior` -> `System Settings` -> `Android SDK`

#### Gradle Plugin
The project requires Android [`gradle plugin`](build.gradle#L37) version `8.0.1` and [`gradle`](gradle/wrapper/gradle-wrapper.properties#L3) version `8.0`.


## Quick Start Project

The Project Packages names and App Name can be set automatically across the every project location where it should change using gradle task named as [`quick_start.gradle`](app/quick_start.gradle#L1)

Refer to [`build.gradle`](app/build.gradle#L246-L247) and edit two define fields named as `newPackageName` and `rootProjectName`and Run task by tapping Play Button from left of `task` defined in android studio.

Example :

```
def newPackageName = "com.rahul.kotlin.architecture"
def rootProjectName = "Android Kotlin Architecture"

```

This will create / change project directories as `com/rahul/kotlin/architecture` and change app name to `Android Kotlin Architecture` with affecting code as well.
### Flavors

Flavor can be described as combination of requirements to meet specific set of needs. The flavors used by this app can be broken down as follows:

#### Product Flavors

Product flavors are designed to identify each environment separately.

- `production` Production server *(Live version)*
- `staging` Staging server *(Used for stable testing purposes)*
- `dev` Dev server *(Used for early testing purposes)*
- `local` Local server *(The flavor is intended to be used for testing on local servers or same machine for debugging severe issues)*

#### Build Types

Builds types are designed to identify build configurations separately.
- `debug` Build types intended to be used for developers while development
- `internal` Build types intended to be used when sharing builds internally with QA and other teams
- `release` Build types intended to be released on Play Store. It should only be used with combination of `production`.

The following table describes basic differences between these build types:

| Feature | debug | internal | release |
| ------ | :------: | :------: | :------: |
| Debuggable | ✔ | ✘ | ✘ |
| Logs Enabled | ✔ | ✔ | ✘ |
| Crashlytics Enabled | ✘ | ✔ | ✔ |

`debug` and `internal` build types have suffix **`.internal`** appended to their application id so that they can be differentiated from release builds.

#### Build Variants

The final build created after combination of `Product Flavor` and `Build Type` is known as `Build Variant`. Each variant created reflects the properties mentioned in above section. Here are few examples from the variants used by this app:

- `stagingDebug` *Debug* build pointing to *staging* server to be used by developers for testing and development purpose
- `stagingInternal` *Internal* build pointing to *staging* server with debugging disabled to be used by QA and other team members before release for testing with *staging* server
- `productionRelease` *Release* build pointing to *production* server to be released on Play Store

#### Ignored Variants

The build configurations file [`build.gradle`](app/build.gradle#L152-L160) includes a map that skips some variants to reduce the compile time for this project by ignoring some of the build combinations that have no use. The following example describes how it works:

```
    // true to skip variant, default fallback for missing variants is false
    def variantFilterSkipMap = [
            stagingDebug      : false, // Does not skip this variant
            stagingRelease    : true,  // Skips this variant
            // Other variants
    ]
```
### Signing the APKs

The build configurations file [`build.gradle`](app/build.gradle) contains signing configurations for each build so that builds created from any machine has same signing and can be installed over previous version of this project.

`storeConfig` can be used to generate signed apk for Play Store. This expects file containing information for signing at following relative path from this file:

> ../config/signing/dev.json

The file should contain JSON in following format:

```
{
  "android": {
    "storeFile": "path/filename.jks",
    "storePassword": "store_password",
    "keyAlias": "alias",
    "keyPassword": "alias_password"
  }
}
```

There are four properties that are read from the properties file:

- storeFile (relative path to keystore file)
- storePassword (Store password)
- keyAlias (Name of alias used for this app)
- keyPassword  (Alias password)


### APK (Android Package Kit) File Names

Refer to  [`build.gradle`](app/build.gradle#L166-L170) file starting with `applicationVariants.all`:

Each APK file name is named with following convention

> App-V[versionName].[versionCode]-[variantName].apk

To differentiate all variants, each variant file is given a different name so it clearly specifies the configurations and environment it was built for with *app version* included.

e.g. *App-V1.0.1.100_ProductionRelease.apk* is release ready APK with version name *1.0.1*, version code *100* and is pointing to *production* server.

## Application Versioning

The application uses [Semantic Versioning](https://semver.org/spec/v2.0.0.html) for version names which is described as follows:

> Given a version number MAJOR.MINOR.PATCH, increment the:

- **MAJOR** version when one make incompatible API changes,
- **MINOR** version when one add functionality in a backwards compatible manner, and
- **PATCH** version when one make backwards compatible bug fixes.

While the version code should be incremented every time when the build is shared with QA and/or other teams after any kind of change.


## Branch Recommendation

Branch names in this repository are based on following conventions:

- Regular branch names should be preceded by name of developer actively maintaining that branch e.g. `ninja/branch` refers to the branch maintained by developer named *Ninja*
- Developer name may be the actual name, nickname, initials or GitHub username
- Feature name with developer name e.g. `ninja/payment` refers to the branch that contains fixes/improvements for Payment module maintained by *Ninja*
- For fix in any branch, branch name preceded by developer name e.g. `ninja/develop` refers to the branch which contains fix(es) that should go in `develop` branch
- `develop` Active branch for all kind of development
- `release` Branch that contains latest code for next release. Once rolled out it should be back merged into `develop`
- `main` Branch that contains most stable code and is live on Google Play. Any hot fix for this branch should also be back merged into `develop` and `release` **(if necessary)**
- Branches should be merged into `develop` or `release` by creating Pull Requests and after approval of **at least 1 reviewer**
- Merged branches should be deleted immediately after PR is merged/closed
- Stale branches should be deleted if they are no longer useful
## Contributing

All developers use the same Github repo for development. Every developer should create a branch from the required branch. Once development is complete, the code should be merged back to the base branch using Pull Request. PRs should be merged using squash and merge option. Once merged, the developer should delete their branch to minimize stale branches within the repository.