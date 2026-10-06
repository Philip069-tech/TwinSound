# TwinSound v0.2

This version automatically scans Android's exposed audio outputs and distinguishes ordinary Bluetooth A2DP from Bluetooth LE Audio.

Important platform limitation:
- Android supports synchronized audio sharing for compatible Bluetooth LE Audio devices.
- Ordinary third-party apps cannot universally force two arbitrary A2DP speakers to play the same stream.
- Android's system output routing / manufacturer features may expose multi-output or grouped routes.
- TwinSound detects what Android exposes and refuses to claim dual playback when the OS does not support it.

This is intentionally an honest capability detector rather than an app that pretends it can bypass Android's audio-routing restrictions.

Official references:
https://developer.android.com/develop/connectivity/bluetooth/ble-audio/overview
https://developer.android.com/media/routing


## Build from an iPad
This repository includes a GitHub Actions workflow at `.github/workflows/build-apk.yml`.
After uploading the project to GitHub, open Actions -> Build TwinSound APK -> Run workflow.
When it finishes, open the workflow run and download the `TwinSound-debug-apk` artifact.
