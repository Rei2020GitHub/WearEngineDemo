package com.huawei.sample.wearable.demo.model

class Constant {
    companion object {
        // フィンガープリントについて、こちらをご参照ください
        // https://developer.huawei.com/consumer/cn/blog/topic/03188242414955227

        // Wearable側のアプリのパッケージ名
        const val WEARABLE_APP_PACKAGE_NAME: String = "com.huawei.sample.wearable.smartwatch.demo"

        // Wearableアプリのフィンガープリント = {WearableアプリのAPP ID}
        // {WearableアプリのAPP ID} = [AppGallery Connect] → [Development and services] → [{あなたのプロジェクト}] → [Project settings] → [App:{あなたのWearableアプリ}] → [General information] → [App information] → [App ID]
        const val WEARABLE_APP_FINGER_PRINT: String = "6917582307755932208"

        // Lite wearable側のアプリのパッケージ名
        const val LITE_WEARABLE_APP_PACKAGE_NAME = "com.huawei.sample.wearable.sportwatch.demo"

        // Lite wearableアプリのフィンガープリント
        // https://developer.huawei.com/consumer/en/doc/connectivity-guides/signature-0000001053969657
        // 生成手順：
        // 1. Lite wearableのプロジェクトで使われるcerの証明書を開き、最後のBEGIN CERTIFICATEのブロックをコピーし、新しいcerファイルを作る
        // 2. (1)で作られたcerファイルを開き、「公開キー」の値をコピーする
        // 3. base64ツール（例：https://www.tomeko.net/online_tools/hex_to_base64.php）を使い、公開キーの値をbase64に変換する
        // 4. Lite wearable側のアプリのフィンガープリント = {Lite wearable側のアプリのパッケージ名}_{公開キーのbase64の値}
        // 例：
        // cerの証明書の最後のBEGIN CERTIFICATEのブロックが下記のものと仮定する
        /*
        -----BEGIN CERTIFICATE-----
        MIICxzCCAkygAwIBAgIOCfv/3LHg1mZhaFIPE1EwCgYIKoZIzj0EAwMwYDEzMDEG
        A1UEAwwqSHVhd2VpIENCRyBEZXZlbG9wZXIgUmVsYXRpb25zIENBIEcyIERSQSAz
        MQ0wCwYDVQQLDARIU0hLMQ0wCwYDVQQKDARIU0hLMQswCQYDVQQGEwJDTjAeFw0y
        NTA3MTQwNzA1MTNaFw0yNjAxMTAwNzA1MTNaMIGLMQswCQYDVQQGEwJKUDEdMBsG
        A1UECgwUU2h1IFRhbmcgUmF5bW9uZCBMZWUxHDAaBgNVBAsMEzQ5OTg2MDY5OTI4
        OTAxNDgzOTgxPzA9BgNVBAMMNlNodSBUYW5nIFJheW1vbmQgTGVlKDQ5OTg2MDY5
        OTI4OTAxNDgzOTgpXCxEZXZlbG9wbWVudDBZMBMGByqGSM49AgEGCCqGSM49AwEH
        A0IABK8eWhXXp0mpV/J/oBxLbozRqh4fHvPukJJC8ScCXMau4RsoTlGtX1DnGJPV
        bUWY5rgAhABfxd85ldTxCKNLMjmjgb0wgbowHQYDVR0OBBYEFOm+aUa8DflEV1a9
        LPcRhxr6g+idMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUser/VGBMG9+RG0/H
        kCc9dK3zD7kwRQYDVR0fBD4wPDA6oDigNoY0aHR0cDovL2NybC5jbG91ZC5odWF3
        ZWkuY29tL0h1YXdlaUNCR0hEUkcyRFJBY3JsLmNybDAOBgNVHQ8BAf8EBAMCB4Aw
        EwYDVR0lBAwwCgYIKwYBBQUHAwMwCgYIKoZIzj0EAwMDaQAwZgIxALGucCY1j7bK
        HbD6pyJswFXyb+j2xVsFfVOeLXFwraAMvfJYULEVmYttYrROOeY1cAIxAOcSFdpt
        HEBLwEsgzVtYAXyRZYqtdG0fyjU6/zJ0zjslXGPZrnnQbHjXhEurDNOZMg==
        -----END CERTIFICATE-----
        */
        // 公開キーの値 = 04af1e5a15d7a749a957f27fa01c4b6e8cd1aa1e1f1ef3ee909242f127025cc6aee11b284e51ad5f50e71893d56d4598e6b80084005fc5df3995d4f108a34b3239
        // base64の値 = BK8eWhXXp0mpV/J/oBxLbozRqh4fHvPukJJC8ScCXMau4RsoTlGtX1DnGJPVbUWY5rgAhABfxd85ldTxCKNLMjk=
        // {Lite wearable側のアプリのパッケージ名}_{公開キーのbase64の値} = com.huawei.sample.wearable.sportwatch.demo_BK8eWhXXp0mpV/J/oBxLbozRqh4fHvPukJJC8ScCXMau4RsoTlGtX1DnGJPVbUWY5rgAhABfxd85ldTxCKNLMjk=
        const val LITE_WEARABLE_APP_FINGER_PRINT = LITE_WEARABLE_APP_PACKAGE_NAME + "_" + "BK8eWhXXp0mpV/J/oBxLbozRqh4fHvPukJJC8ScCXMau4RsoTlGtX1DnGJPVbUWY5rgAhABfxd85ldTxCKNLMjk="
    }
}