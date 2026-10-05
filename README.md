SigaraTakip
===========

SigaraTakip is a lightweight Android application for tracking daily cigarette consumption.

Cigarettes can be logged directly from the application or from a persistent notification. Daily records are stored locally on the device.

Features
--------

- Daily cigarette tracking
- Quick logging from the notification panel
- Local data storage
- Automatic daily counter
- Works offline
- No account required
- Lightweight Android application

Building on Android with Termux
-------------------------------

SigaraTakip can be compiled directly on an Android device using Termux.

The current build system uses Termux together with a Debian environment through proot-distro.

1. Update Termux packages:

    pkg update
    pkg upgrade

2. Install the required Termux packages:

    pkg install git openjdk-21 zip apksigner proot-distro wget

3. Install Debian:

    proot-distro install debian

4. Clone SigaraTakip:

    cd ~
    git clone https://github.com/oyuzdenseniv2646-beep/SigaraTakip.git
    cd SigaraTakip

5. Create the required tool directories:

    mkdir -p ~/tools/aapt2
    mkdir -p ~/tools/r8
    mkdir -p ~/android-sdk/platforms/android-35

6. Required build files:

The build script expects the following files:

    ~/tools/aapt2/aapt2
    ~/tools/r8/r8.jar
    ~/android-sdk/platforms/android-35/android.jar

The AAPT2 binary must be executable:

    chmod +x ~/tools/aapt2/aapt2

7. Run the Termux setup script:

    chmod +x setup.sh
    ./setup.sh

The setup script checks the required Termux packages, Debian environment and Android build tools.

8. Build the application:

    ./build.sh

If the build completes successfully, the APK will be created at:

    ~/SigaraTakip/build/SigaraTakip.apk

The build script automatically creates a local debug signing key if one does not already exist.

Build Requirements
------------------

- Termux
- OpenJDK 21
- Debian through proot-distro
- AAPT2
- Android SDK 35 android.jar
- R8 / D8
- APKSigner
- zip
- Git

Planned Features
----------------

- Weekly and monthly statistics
- Consumption charts
- Cigarette cost tracking
- Time since the last cigarette
- Daily consumption goals
- Improved user interface
- Turkish and English language support
- Data export and backup

Contributing
------------

Contributions are welcome.

You can contribute by fixing bugs, improving the interface, adding new features, or suggesting improvements through GitHub Issues.

To contribute:

1. Fork the repository.
2. Create a branch for your changes.
3. Make your changes.
4. Commit your work.
5. Open a Pull Request.

License
-------

This project is licensed under the MIT License.
