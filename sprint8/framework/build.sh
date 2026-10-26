#!/bin/bash
# Compile le framework et produit build/mg-framework.jar
set -e

BUILD_DIR="build"
JAR_NAME="mg-framework.jar"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

echo "Compilation du framework..."
find src -name "*.java" > sources.txt

javac -cp "lib/jakarta.servlet-api.jar" -d "$BUILD_DIR/classes" @sources.txt

rm sources.txt

echo "Creation du jar..."
cd "$BUILD_DIR/classes"
jar -cf "../$JAR_NAME" .
cd ../..

echo "OK : $BUILD_DIR/$JAR_NAME"
