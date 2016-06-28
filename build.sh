set -e
cd ../java-api-client
./gradlew install
cd $OLDPWD
cd ../android-capture-sdk
./gradlew install
cd $OLDPWD
./gradlew build
adb install -r app/build/outputs/apk/app-debug.apk
