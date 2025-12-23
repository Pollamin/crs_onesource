#!/bin/bash
# Build script for CRS OneSource OpenDock PO Validator
# ====================================================

set -e

echo "=========================================="
echo "Building CRS OpenDock PO Validator"
echo "=========================================="

# Configuration
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src/main/java"
LIB_DIR="$PROJECT_DIR/lib"
BUILD_DIR="$PROJECT_DIR/build"
DIST_DIR="$PROJECT_DIR/dist"
JAR_NAME="crs-validator.jar"

# Create directories
mkdir -p "$BUILD_DIR"
mkdir -p "$DIST_DIR"

# Check for Gson library
GSON_JAR="$LIB_DIR/gson-2.10.1.jar"
if [ ! -f "$GSON_JAR" ]; then
    echo "Downloading Gson library..."
    curl -L -o "$GSON_JAR" \
        "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
fi

# Build classpath
CLASSPATH="$GSON_JAR"

echo "Compiling Java sources..."
find "$SRC_DIR" -name "*.java" > "$BUILD_DIR/sources.txt"

javac -d "$BUILD_DIR" \
    -cp "$CLASSPATH" \
    -source 11 \
    -target 11 \
    @"$BUILD_DIR/sources.txt"

echo "Creating JAR file..."
cd "$BUILD_DIR"

# Create manifest
echo "Main-Class: com.pollaminllc.crs.Main" > MANIFEST.MF
echo "Class-Path: lib/gson-2.10.1.jar" >> MANIFEST.MF

# Package JAR
jar cfm "$DIST_DIR/$JAR_NAME" MANIFEST.MF \
    com/pollaminllc/crs/*.class \
    com/pollaminllc/crs/model/*.class \
    com/pollaminllc/crs/data/*.class \
    com/pollaminllc/crs/util/*.class

cd "$PROJECT_DIR"

# Copy dependencies
mkdir -p "$DIST_DIR/lib"
cp "$GSON_JAR" "$DIST_DIR/lib/"

# Copy config
cp config.properties "$DIST_DIR/"

# Copy run script
cp run.sh "$DIST_DIR/"

echo "=========================================="
echo "Build complete!"
echo "Output: $DIST_DIR/$JAR_NAME"
echo ""
echo "To run:"
echo "  cd $DIST_DIR && ./run.sh"
echo "=========================================="
