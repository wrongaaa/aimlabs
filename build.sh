#!/bin/bash
# AimLabs 构建脚本

set -e

echo "=== AimLabs Build Script ==="

# 清理
rm -rf build out
mkdir -p build out

# 编译
echo "[1/3] Compiling..."
find src -name "*.java" > sources.txt
javac -d build @sources.txt
rm sources.txt
echo "  Compiled successfully."

# 打包JAR
echo "[2/3] Packaging JAR..."
jar cfm out/AimLabs.jar MANIFEST.MF -C build .
echo "  Created out/AimLabs.jar"

# 创建可执行启动脚本
echo "[3/3] Creating launcher scripts..."

# Windows .bat
cat > out/AimLabs.bat << 'BAT'
@echo off
title AimLabs
java -jar AimLabs.jar
if errorlevel 1 (
    echo.
    echo Java is required to run AimLabs.
    echo Please install Java 17+ from https://adoptium.net
    pause
)
BAT

# Linux/Mac .sh
cat > out/AimLabs.sh << 'SH'
#!/bin/bash
java -jar AimLabs.jar
SH
chmod +x out/AimLabs.sh

echo ""
echo "=== Build Complete ==="
echo "Output: out/AimLabs.jar"
echo "Run: java -jar out/AimLabs.jar"
