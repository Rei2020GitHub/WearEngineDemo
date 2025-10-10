# ファーウェイWear Engineのサンプルプロジェクト（Lite Wearable）
## 開発環境
* DevEco Studio 5.1.0 Release
## テスト環境
### Lite Wearable
| 項目 | 内容 |
| --- | --- |
| デバイス名 | HUAWEI WATCH ATM-6D2 |
| モデル名 | HUAWEI WATCH ATM |
| モデル番号 | ATM-B29 |
## Wear Engine (Lite Wearable)実装の注意事項
* Wear Engine SDKでは、コールバックを使う部分があります。jsのページで、コールバックで受け取った値を表示する際、コールバック関数の後ろに「.bind(this)」を追加しないと、値が反映されないことがあります。
* 画像は通常jpgやpngなどのフォーマットになりますが、Lite Wearableではjpgやpngを受信しても表示できません。画像をbinフォーマットに変換してから、Lite Wearableに送信する必要があります。binフォーマットについては本サンプルのBinDemoプロジェクトをご参照ください。
## 参考資料
* Wearable App Development (JS) (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/fitnesswatch-dev-0000001051423561)
* Lite Smart Wearables (https://developer.huawei.com/consumer/en/doc/best-practices/bpta-lite-wearable-guide)
* Wear Engine Reference (https://developer.huawei.com/consumer/en/doc/connectivity-References/api-description-0000001111724474)