# Assessment Test

## Introduction
Assessment used for evaluating technical skills on Android development.

There is one approach for two different companies and assessments:
* Company A: [Marvel Database](https://marvel.fandom.com/wiki/Marvel_Database) via its public MediaWiki API.
* Company B: [Randomuser](https://randomuser.me/) data origin.

Each provider is implemented in a separate flavor, while most app behavior is shared.
See [Architecture](architecture.md) for the module map, dependency boundaries, data
flow, source layout, and libraries.

## Reviewers considerations

Bugs, issues, bad practices, wrong implementations and all spaghetti code you find: [open an issue](https://github.com/r2bapps/assessment/issues/new/choose) please!

The assessment will be alive and well be improved for learning purposes.

## Building

The project uses JDK 17, Gradle 9.6.0, Android Gradle Plugin 9.5.1, Kotlin 2.2.10,
and Android SDK Platform 36. Open the project in Android Studio with JDK 17 selected,
or run `./gradlew` with `JAVA_HOME` pointing to a JDK 17 installation.

The app targets Android 16 (API 36) and supports Android 7.1 (API 25) and later.
