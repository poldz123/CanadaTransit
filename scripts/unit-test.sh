#!/bin/bash
# This script will run the unit test and test coverage for the application.
# The script should display the total coverage for the CI to parse it that
# will generate a badge.
# TODO: Make this test to be runnable in small and full mode.
#       small = Makes the test smaller and run only the most important tests
#       full = Runs all of the test for the application.
./gradlew :app:testDebugUnitTest jacocoTestReport