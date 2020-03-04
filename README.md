CanadaTransit
[![pipeline status](https://gitlab.com/poldz123/CanadaTransit/badges/develop/pipeline.svg)](https://gitlab.com/poldz123/CanadaTransit/commits/develop)
[![coverage report](https://gitlab.com/poldz123/CanadaTransit/badges/develop/coverage.svg)](https://gitlab.com/poldz123/CanadaTransit/commits/develop)
===

Android mobile application for Bus tracking across Canada. That uses the [TransitLand API](https://transit.land/) to fetch for the bus schedules for each of the transit line operators.

## Getting Started

### Prerequisites
- Install the latest [android studio](https://developer.android.com/studio)
- Install the [java jdk 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html#A1097257)

### Libraries
Most used libraries within the application which a developer must have knowledge with.

- [Kotlin](https://kotlinlang.org/docs/tutorials/)
- [RxJava](https://github.com/ReactiveX/RxJava)
- [RxKotlin](https://github.com/ReactiveX/RxKotlin)
- [Room Database](https://developer.android.com/training/data-storage/room/index.html)

## Running the tests

Unit tests is an essential part to the development of the application. A good developer should test the code the've made to eliminate bug in production. This project has good amount of unit tests that mostly covered the source code.

You can either use the script through the terminal or command-line or run the test within android studio.
After the unit test, it will then print the code coverage percentile which tell us how much code are covered by the test.

### Terminal or Command line
Make sure that you are at the project's root.
```
./scripts/unit-test.sh
```

## Running the static analysis

Static analysis is way to analyze the source code for code smells. Its main purpose is to check for buggy code, code format, or even suggest for a good code. After the analysis it will create a report which will contain a list of things that needed to be fixed, so make sure that they are all addressed.

Static analysis can only be executed through the terminal or command-line with a script.
```
./scripts/static-analysis.sh
```

## Running the code formatter

Code formatter is a script that will automatically formats the source code based on the rules. It uses a default [ktlint](https://ktlint.github.io/) code formatter which is a list of standard to format the code in kotlin.

Code formatter can only be executed through the terminal or command-line with a script.
```
./scripts/code-formatter.sh