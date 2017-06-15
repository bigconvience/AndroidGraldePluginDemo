#!/usr/bin/env bash
./gradlew clean assembleDebug
adb install app/build/outputs/apk/app-debug.apk