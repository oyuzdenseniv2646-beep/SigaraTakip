#!/data/data/com.termux/files/usr/bin/bash
set -e

echo "======================================"
echo "       SIGARATAKIP TERMUX SETUP"
echo "======================================"
echo

if ! command -v pkg >/dev/null 2>&1; then
    echo "Error: This setup script must be run inside Termux."
    exit 1
fi

echo "[1/6] Updating package information..."
pkg update

echo
echo "[2/6] Installing required packages..."
pkg install -y git openjdk-21 zip apksigner proot-distro

echo
echo "[3/6] Checking Debian environment..."

if ! proot-distro login debian -- true >/dev/null 2>&1; then
    echo "Debian is not installed."
    echo "Installing Debian..."
    proot-distro install debian
else
    echo "Debian is already installed."
fi

echo
echo "[4/6] Creating tool directories..."

mkdir -p "$HOME/tools/aapt2"
mkdir -p "$HOME/tools/r8"
mkdir -p "$HOME/android-sdk/platforms/android-35"

echo
echo "[5/6] Checking Android build tools..."

MISSING=0

if [ -f "$HOME/tools/aapt2/aapt2" ]; then
    chmod +x "$HOME/tools/aapt2/aapt2"
    echo "AAPT2: OK"
else
    echo "AAPT2: MISSING"
    MISSING=1
fi

if [ -f "$HOME/tools/r8/r8.jar" ]; then
    echo "R8/D8: OK"
else
    echo "R8/D8: MISSING"
    MISSING=1
fi

if [ -f "$HOME/android-sdk/platforms/android-35/android.jar" ]; then
    echo "Android 35 android.jar: OK"
else
    echo "Android 35 android.jar: MISSING"
    MISSING=1
fi

echo
echo "[6/6] Final check..."

if [ "$MISSING" -ne 0 ]; then
    echo
    echo "Setup is not complete."
    echo
    echo "The following build files must exist:"
    echo
    echo "  $HOME/tools/aapt2/aapt2"
    echo "  $HOME/tools/r8/r8.jar"
    echo "  $HOME/android-sdk/platforms/android-35/android.jar"
    echo
    echo "See README.md for build tool information."
    exit 1
fi

echo "Checking AAPT2..."

if ! proot-distro login debian -- bash -lc \
    "'$HOME/tools/aapt2/aapt2' version" >/dev/null 2>&1; then
    echo "Error: AAPT2 could not run inside Debian."
    exit 1
fi

echo "Checking D8..."

if ! java -cp "$HOME/tools/r8/r8.jar" \
    com.android.tools.r8.D8 --version >/dev/null 2>&1; then
    echo "Error: D8 could not start."
    exit 1
fi

chmod +x "$(dirname "$0")/build.sh"

echo
echo "======================================"
echo "          SETUP COMPLETE"
echo "======================================"
echo
echo "Build SigaraTakip with:"
echo
echo "  ./build.sh"
echo
