#!/bin/bash
set -e
echo "=== AimLabs EXE Packager ==="

if [ ! -f out/AimLabs.jar ]; then
    echo "JAR not found, building first..."
    bash build.sh
fi

# Windows .bat launcher
cat > out/AimLabs.bat << 'BAT'
@echo off
title AimLabs
java -jar "%~dp0AimLabs.jar" %*
if errorlevel 1 (
    echo.
    echo ERROR: Java 17+ is required.
    echo Download from https://adoptium.net
    pause
)
BAT

echo ""
echo "=== Packaging Complete ==="
echo "  out/AimLabs.jar   - 可执行JAR"
echo "  out/AimLabs.bat   - Windows启动器(双击运行)"
echo "  out/AimLabs.sh    - Linux/Mac启动器"
echo ""
echo "要生成真正的exe，在Windows上运行:"
echo "  jpackage --input out --main-jar AimLabs.jar --name AimLabs --type app-image"
ls -la out/
