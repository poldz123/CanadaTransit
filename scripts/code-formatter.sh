#!/bin/bash
# This script runs a automatic code formatter for kotlin files. This will
# crawl to the entire kotlin project files.
#
# IMPORTANT: This is only used locally and shall not be used within the CI since the
#            thing this script only does is formatting code which unrelated with the CI.
./gradlew detektFormat