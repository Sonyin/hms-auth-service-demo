HUAWEI AppGallery Connect Auth Service Demo
=====

AppGallery Connect provides a cloud-based auth service and SDKs to help you quickly build a secure and reliable user authentication system for your apps to verify user identity.
The AppGallery Connect auth service supports multiple authentication methods and is seamlessly integrated with other Serverless services to help you secure user data based on simple rules that you have defined.

Third-party accounts and others
------------

- HUAWEI ID
- HUAWEI Game Service account
- WeChat account
- QQ account
- Weibo account
- Email account
- Google account*
- Google Play Game account*
- Facebook account*
- Twitter account*
- Anonymous account
- Self-created account

Requirements
------------

 - [Android SDK](http://developer.android.com/sdk/index.html)
 - Android [7.0 (API 24) ](http://developer.android.com/tools/revisions/platforms.html#7.0)
 - Android SDK Tools
 - Android SDK Build tools 29.0.3
 - Android Support Repository
 - Android Support libraries
 - HMS AG Connect Library
 - HMS AG Connect Auth Service Library
 - HMS Game Service Library
 - WeChat SDK
 - Weibo SDK
 - QQ SDK
 - Facebook Login SDK
 - Twitter4j SDK
 - Retrofit

Reference
------------

 - [Huawei AppGallery Connect Auth Service](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-auth-service-introduction)
 - [How to add Twitter Login button to your Android app using Kotlin](https://johncodeos.com/how-to-add-twitter-login-button-to-your-android-app-using-kotlin)
 - [https://github.com/kongqw/WeChatHelper](https://github.com/kongqw/WeChatHelper)

 Preparations
 ------------
 
  - Registering as a Developer to [HUAWEI Developer](https://developer.huawei.com/consumer/en)
  - Import this project with Android Studio and change package name
  - Create an app by referring to [Creating a Project](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-get-started#createproject) and [Creating an App in the Project](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-get-started#createapp)
  - Set the data storage location
  - Enabling Required Services : Auth Service
  - Generating a Signing Certificate Fingerprint
  - Configuring the Signing Certificate Fingerprint
  - Get your agconnect-services.json file to the app root directory


Building
--------

To build, install and run a debug version, run this from the root of the project:

    ./gradlew app:assembleDebug
