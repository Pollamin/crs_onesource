#!/bin/bash
# Run script for CRS OneSource OpenDock PO Validator
# ==================================================

# Find script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Check if running from dist or project root
if [ -f "$SCRIPT_DIR/crs-validator.jar" ]; then
    # Running from dist directory
    JAR_FILE="$SCRIPT_DIR/crs-validator.jar"
    LIB_DIR="$SCRIPT_DIR/lib"
elif [ -f "$SCRIPT_DIR/dist/crs-validator.jar" ]; then
    # Running from project root
    JAR_FILE="$SCRIPT_DIR/dist/crs-validator.jar"
    LIB_DIR="$SCRIPT_DIR/dist/lib"
else
    echo "Error: crs-validator.jar not found. Please run build.sh first."
    exit 1
fi

# Build classpath
CLASSPATH="$JAR_FILE:$LIB_DIR/*"

# Java options (adjust as needed)
JAVA_OPTS="-Xms64m -Xmx256m"

# Run the validator
echo "Starting CRS OpenDock PO Validator..."
exec java $JAVA_OPTS -cp "$CLASSPATH" com.pollaminllc.crs.Main "$@"
