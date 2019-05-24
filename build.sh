#!/bin/sh

gradle build
cd cli
gradle compileKotlinMacos
gradle linkReleaseExecutableMacos
cd ..
mkdir release
cp build/libs/ktgen.jar release/
cp cli/build/bin/macos/releaseExecutable/cli.kexe release/ktgen

