# Onfido Capture SDK for Android: Example app

This is repository contains sample code for an Android application intended to showcase how the [Onfido Android SDK](https://github.com/onfido/onfido-android-sdk) should be used.

This apk has two launcher apps. One is a demo flow that tries to emulate a bank app, the other one is debug app with multiple init options for the sdk flow.

In order to use the API (and the SDK) you'll need an Onfido account and an API token that you can get from the settings screen.
You will have to place the api token inside of a file called `secrets` and place it at the root of the project.
The `secrets` file should have the following content:
```
onfido_api_token=YOUR_TOKEN_HERE
```