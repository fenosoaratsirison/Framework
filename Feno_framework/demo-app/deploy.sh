#!/bin/bash
# Compile la demo-app et produit build/demo-app.war
set -e

APP_NAME="demo-app"
BUILD_DIR="build"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/WEB-INF/classes"
mkdir -p "$BUILD_DIR/WEB-INF/lib"

echo "Compilation..."
find src/main/java -name "*.java" > sources.txt

javac -cp "lib/jakarta.servlet-api.jar:lib/mg-framework.jar" \
    -d "$BUILD_DIR/WEB-INF/classes" \
    @sources.txt

rm sources.txt

echo "Copie des JSP et du web.xml..."
cp -r src/main/webapp/WEB-INF/* "$BUILD_DIR/WEB-INF/"

echo "Copie du framework dans WEB-INF/lib..."
cp lib/mg-framework.jar "$BUILD_DIR/WEB-INF/lib/"

echo "Creation du WAR..."
cd "$BUILD_DIR"
jar -cf "../${APP_NAME}.war" .
cd ..

echo "OK : ${APP_NAME}.war"
echo "(A copier dans webapps/ de Tomcat pour deployer)"
