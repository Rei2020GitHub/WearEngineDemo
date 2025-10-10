# ファーウェイWear Engineのサンプルプロジェクト
ファーウェイのWear Engine SDKはファーウェイのスマートウォッチとAndroid・iOSとの間の通信ライブラリです。通信ライブラリなので、スマホ側だけに実装してもウォッチ側だけに実装しても、Wear Engineの機能は当然十分に試すことができません。そのため、本サンプルプロジェクトを利用するのに、スマホ側とウォッチ側のプロジェクトを両方参考にしたほうが、理解度が高まると思います。

また、Wear Engine SDKを使用するのに、まずファーウェイ開発者のコンソールでWear Engineサービスを開通する必要があります。Wear Engineサービスの開通が完了してから、Wear Engine SDKの実装を開始することをお勧めします。

本サンプルは以下の４つのプロジェクトによって構成されています。

| プロジェクト | 内容 |
| --- | --- |
| Android | Wear Engineを実装したAndroidのプロジェクト |
| BinDemo | 画像をbinファイルに変換するプロジェクト |
| SmartWatch/JS | Wear Engineを実装したWearableのプロジェクト |
| SportWatch/JS | Wear Engineを実装したLite Wearableのプロジェクト |

AndroidプロジェクトはWear Engine SDKを実装したAndroidのプロジェクトです。

SportWatch/JSプロジェクトはWear Engine SDKを実装したLite Wearableのプロジェクトです。

SmartWatch/JSプロジェクトは、SportWatch/JSプロジェクトをもとにし、Wearableで動作できるように手を加えたプロジェクトです。

上記の３つのプロジェクトを用いて、ファーウェイウォッチとAndroidとの間の、双方向のテキスト通信とファイル送受信を実演します。

現時点のLite Wearableでは、表示できる画像のフォーマットはbinであり、一般的に使われない形式です。そのため、Lite Wearableに画像を送信して表示させることをやる場合、binファイルを送るしかありません。BinDemoプロジェクトでは、画像をbinファイルに変換する手順が示されます。

## ファーウェイWear Engine SDKの実装
こちらの[ガイド](./Guide.md)をご参照ください

## 開発環境
* Android Studio Giraffe | 2022.3.1 Patch 2
* DevEco Studio 5.1.0 Release
## テスト環境
### Android
| 項目 | 内容 |
| --- | --- |
| デバイス名 | HUAWEI P30 lite |
| 機種 | MAR-LX2J |
### Wearable
| 項目 | 内容 |
| --- | --- |
| デバイス名 | HUAWEI WATCH 5-25B |
| モデル名 | HUAWEI WATCH 5 |
| モデル番号 | RTS-AL00 |
| HarmonyOS | 5.1.0 |
| ソフトウェアバージョン | 5.1.0.205(SP1C900E101R1P100) |
| OpenHarmonyバージョン | OpenHarmony 5.0.1 |
### Lite Wearable
| 項目 | 内容 |
| --- | --- |
| デバイス名 | HUAWEI WATCH ATM-6D2 |
| モデル名 | HUAWEI WATCH ATM |
| モデル番号 | ATM-B29 |
## 参考資料
* Wear Engine - Android Phone App Development (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/phone-dev-0000001086797354)
* Wear Engine - iOS Phone App Development (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/phone-dev-ios-0000001874113366)
* Wearable App Development (JS) (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/fitnesswatch-dev-0000001051423561)
* Applying for the Wear Engine Service (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/applying-wearengine-0000001050777982)
* Lite Smart Wearables (https://developer.huawei.com/consumer/en/doc/best-practices/bpta-lite-wearable-guide)
* Wear Engine SDK (Mobile Phones) (https://developer.huawei.com/consumer/en/doc/connectivity-Library/phone-sdk-cn-0000001656844234)
* Wear Engine Reference (https://developer.huawei.com/consumer/en/doc/connectivity-References/api-description-0000001111724474)