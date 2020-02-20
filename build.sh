#!/bin/sh

gradle build
cd cli
gradle compileKotlinMacos
gradle linkReleaseExecutableMacos
gradle compilekotlinLinux
gradle linkReleaseExecutableLinux
cd ..
mkdir release
cp build/libs/ktgen.jar release/
cp cli/build/bin/macos/releaseExecutable/cli.kexe release/ktgen_mac
cp cli/build/bin/linux/releaseExecutable/cli.kexe release/ktgen_lin


