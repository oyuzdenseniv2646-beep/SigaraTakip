#!/data/data/com.termux/files/usr/bin/bash
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
SRC="$ROOT/app/src/main"
BUILD="$ROOT/build"

ANDROID_JAR="$HOME/android-sdk/platforms/android-35/android.jar"

AAPT2="/data/data/com.termux/files/home/tools/aapt2/aapt2"
P_ROOT="$ROOT"
P_SRC="$P_ROOT/app/src/main"
P_BUILD="$P_ROOT/build"
P_ANDROID_JAR="/data/data/com.termux/files/home/android-sdk/platforms/android-35/android.jar"

echo "======================================"
echo "       SIGARA TAKIP APK BUILDER"
echo "======================================"

echo
echo "[1/8] Build temizleniyor..."

rm -rf "$BUILD"

mkdir -p "$BUILD/compiled"
mkdir -p "$BUILD/generated"
mkdir -p "$BUILD/classes"
mkdir -p "$BUILD/dex"

echo
echo "[2/8] Resources derleniyor..."

proot-distro login debian -- bash -lc "
set -e

'$AAPT2' compile \
    --dir '$P_SRC/res' \
    -o '$P_BUILD/compiled/resources.zip'
"

echo
echo "[3/8] Resources + Manifest linkleniyor..."

proot-distro login debian -- bash -lc "
set -e

'$AAPT2' link \
    -I '$P_ANDROID_JAR' \
    --manifest '$P_SRC/AndroidManifest.xml' \
    --java '$P_BUILD/generated' \
    --min-sdk-version 23 \
    --target-sdk-version 35 \
    -o '$P_BUILD/base.apk' \
    '$P_BUILD/compiled/resources.zip'
"

echo
echo "[4/8] Java kaynaklari derleniyor..."

find "$SRC/java" "$BUILD/generated" \
    -type f \
    -name '*.java' \
    > "$BUILD/sources.txt"

echo "Java dosyasi sayisi:"
wc -l "$BUILD/sources.txt"

javac \
    --release 8 \
    -g:none \
    -encoding UTF-8 \
    -classpath "$ANDROID_JAR" \
    -d "$BUILD/classes" \
    @"$BUILD/sources.txt"

echo
echo "[5/8] DEX olusturuluyor..."

find "$BUILD/classes" \
    -type f \
    -name '*.class' \
    > "$BUILD/classes.txt"

echo "Class dosyasi sayisi:"
wc -l "$BUILD/classes.txt"

java -cp "$HOME/tools/r8/r8.jar" com.android.tools.r8.D8 \
    --lib "$ANDROID_JAR" \
    --min-api 23 \
    --output "$BUILD/dex" \
    @"$BUILD/classes.txt"

test -f "$BUILD/dex/classes.dex"

echo
echo "[6/8] DEX APK'ya ekleniyor..."

cp "$BUILD/base.apk" "$BUILD/unsigned.apk"

cd "$BUILD/dex"

zip -q \
    "$BUILD/unsigned.apk" \
    classes.dex

cd "$ROOT"

echo
echo "[7/8] Imza anahtari kontrol ediliyor..."

if [ ! -f "$ROOT/debug.keystore" ]; then

    echo "Debug imza anahtari olusturuluyor..."

    keytool \
        -genkeypair \
        -keystore "$ROOT/debug.keystore" \
        -storepass android \
        -alias androiddebugkey \
        -keypass android \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -dname "CN=SigaraTakip,O=Cano,C=TR"
fi

echo
echo "[8/8] APK imzalaniyor..."

rm -f "$BUILD/SigaraTakip.apk"

apksigner sign \
    --ks "$ROOT/debug.keystore" \
    --ks-key-alias androiddebugkey \
    --ks-pass pass:android \
    --key-pass pass:android \
    --out "$BUILD/SigaraTakip.apk" \
    "$BUILD/unsigned.apk"

echo
echo "Imza kontrol ediliyor..."

apksigner verify \
    --verbose \
    "$BUILD/SigaraTakip.apk"

echo
echo "======================================"
echo "          BUILD TAMAMLANDI"
echo "======================================"
echo
echo "APK:"
echo "$BUILD/SigaraTakip.apk"
echo

ls -lh "$BUILD/SigaraTakip.apk"
