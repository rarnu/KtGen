#!/bin/sh

gradle build
cd cli
gradle build
cd ..
mkdir release
cp build/libs/ktgen.jar release/
cp cli/build/bin/macos/mainReleaseExecutable/main.kexe release/ktgen

