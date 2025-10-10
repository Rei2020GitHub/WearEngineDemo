# ファーウェイWear Engineのサンプルプロジェクト
Huawei's Wear Engine SDK is a communication library between Huawei smartwatches and Android and iOS. Because it is a communication library, you cannot fully test Wear Engine's functionality whether you implement it only on the smartphone or only on the watch. Therefore, when using this sample project, it is recommended that you refer to both the smartphone and watch projects to gain a better understanding.

To use the Wear Engine SDK, you must first enable the Wear Engine service in the Huawei Developer Console. We recommend that you start implementing the Wear Engine SDK only after the Wear Engine service is enabled.

This sample consists of the following four projects:

| Project | Content |
| --- | --- |
| Android | Android projects that implement Wear Engine |
| BinDemo | Project to convert images to bin files |
| SmartWatch/JS | Wearable project implementing Wear Engine |
| SportWatch/JS | Lite Wearable project with Wear Engine |

The Android project is an Android project that implements the Wear Engine SDK.

The SportWatch/JS project is a Lite Wearable project that implements the Wear Engine SDK.

The SmartWatch/JS project is based on the SportWatch/JS project and has been modified to work with Wearable.

Using the three projects above, we will demonstrate two-way text communication and file transfer between a Huawei Watch and Android.

Currently, the only image format that Lite Wearable can display is bin, which is not a commonly used format. Therefore, if you want to send an image to Lite Wearable and display it, you have no choice but to send a bin file. The BinDemo project shows the steps to convert an image to a bin file.


## Development environment
* Android Studio Giraffe | 2022.3.1 Patch 2
* DevEco Studio 5.1.0 Release
## Test environment
### Android
| Item | Content |
| --- | --- |
| Device Name | HUAWEI P30 lite |
| Model | MAR-LX2J |
### Wearable
| Item | Content |
| --- | --- |
| Device Name | HUAWEI WATCH 5-25B |
| Model Name | HUAWEI WATCH 5 |
| Model | RTS-AL00 |
| HarmonyOS | 5.1.0 |
| Software Version | 5.1.0.205(SP1C900E101R1P100) |
| OpenHarmony Version | OpenHarmony 5.0.1 |
### Lite Wearable
| Item | Content |
| --- | --- |
| Device Name | HUAWEI WATCH ATM-6D2 |
| Model Name | HUAWEI WATCH ATM |
| Model | ATM-B29 |
## Reference
* Wear Engine - Android Phone App Development (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/phone-dev-0000001086797354)
* Wear Engine - iOS Phone App Development (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/phone-dev-ios-0000001874113366)
* Wearable App Development (JS) (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/fitnesswatch-dev-0000001051423561)
* Applying for the Wear Engine Service (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/applying-wearengine-0000001050777982)
* Lite Smart Wearables (https://developer.huawei.com/consumer/en/doc/best-practices/bpta-lite-wearable-guide)
* Wear Engine SDK (Mobile Phones) (https://developer.huawei.com/consumer/en/doc/connectivity-Library/phone-sdk-cn-0000001656844234)
* Wear Engine Reference (https://developer.huawei.com/consumer/en/doc/connectivity-References/api-description-0000001111724474)