# RiskScore

## API

The public API is [`RiskScoreCalculator`](riskscore/src/main/java/uk/nhs/riskscore/RiskScoreCalculator.kt), [`RiskScoreCalculatorConfiguration`](riskscore/src/main/java/uk/nhs/riskscore/RiskScoreCalculatorConfiguration.kt) and [`ScanInstance`](riskscore/src/main/java/uk/nhs/riskscore/ScanInstance.kt).
There is an example configuration at `RiskScoreCalculatorConfiguration.exampleConfiguration` taken from the [GAEN repo](https://github.com/nhsx).

## Usage

The risk score is computed from the list of [`ScanInstance`s](https://developers.google.com/android/reference/com/google/android/gms/nearby/exposurenotification/ScanInstance) associated with a single exposure window.

For a sample, data is available at https://github.com/nhsx/gaen_data-public. This data can be tested by running

```
./gradlew run
```

### Adding as a dependency

Include the following to the `repositories` block in your module's `build.gradle` file

```groovy
maven {
    url = 'https://maven.pkg.github.com/nhsx/riskscore-kt-public'
    credentials {
        username = project.findProperty("gpr.user") ?: System.getenv("PACKAGES_ACCESS_ACTOR")
        password = project.findProperty("gpr.key") ?: System.getenv("PACKAGES_ACCESS_TOKEN")
    }
}
```

Then you can use the riskscore-kt dependency as follows

```groovy
implementation "uk.nhs.covid19:riskscore-kt:$riskscore_version"
```

NB: Consumers of this library should bundle a Kotlin stdlib with API version 1.3.

## Versioning

This module uses semantic versioning.

## Release

To publish the package, simply create a release on GitHub and this will trigger a [workflow](.github/workflows/gradle-publish.yml) to publish the package.

Be sure to update the version number in [build.gradle](riskscore/build.gradle) to match the tag of the release.

## Dependencies

The module depends on [Apache commons-math v3](https://commons.apache.org/proper/commons-math/) for the Gamma distribution calculations.

## Testing

This module uses [kotest](https://github.com/kotest/kotest) for unit and property based testing. To use this in Intellij / Android studio install the kotest plugin.
