# ファーウェイWear Engineのサンプルプロジェクト（Android）
## 開発環境
* Android Studio Giraffe | 2022.3.1 Patch 2
## テスト環境
### Android
| 項目 | 内容 |
| --- | --- |
| デバイス名 | HUAWEI P30 lite |
| 機種 | MAR-LX2J |
## Wear Engine (Android)実装の注意事項
* Wear Engineを実装するときに、P2pClientに正しいパッケージ名とフィンガープリントを渡す必要があります。特にフィンガープリントについてちょっとわかりにくいです。Constant.ktにフィガープリントの取得手順が書かれているので、それを参考にしていただければと思います。
## 参考資料
* Wear Engine - Android Phone App Development (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/phone-dev-0000001086797354)