# ファーウェイWear Engineのサンプルプロジェクト（Wearable）
## 開発環境
* DevEco Studio 5.1.0 Release
## テスト環境
### Wearable
| 項目 | 内容 |
| --- | --- |
| デバイス名 | HUAWEI WATCH 5-25B |
| モデル名 | HUAWEI WATCH 5 |
| モデル番号 | RTS-AL00 |
| HarmonyOS | 5.1.0 |
| ソフトウェアバージョン | 5.1.0.205(SP1C900E101R1P100) |
| OpenHarmonyバージョン | OpenHarmony 5.0.1 |
## Wear Engine (Wearable)実装の注意事項
* Wear Engine SDKでは、コールバックを使う部分があります。jsのページで、コールバックで受け取った値を表示する際、コールバック関数の後ろに「.bind(this)」を追加しないと、値が反映されないことがあります。
* ウォッチがファイルを受信したら、internal://app/[ファイル名]というファイルパスが渡されます。しかし、画像として表示するときに、絶対パスを指定しないと表示できません。変換方法について、util.jsのconvertInternalToAbsolute()をご参照ください。
## 参考資料
* Wearable App Development (JS) (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/fitnesswatch-dev-0000001051423561)
* Lite Smart Wearables (https://developer.huawei.com/consumer/en/doc/best-practices/bpta-lite-wearable-guide)
* Wear Engine Reference (https://developer.huawei.com/consumer/en/doc/connectivity-References/api-description-0000001111724474)