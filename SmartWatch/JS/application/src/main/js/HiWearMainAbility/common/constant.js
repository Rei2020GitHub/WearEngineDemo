export const THIS_PACKAGE_NAME = 'com.huawei.sample.wearable.sportwatch.demo';

export const WEAR_ENGINE_CONST = {
    // スマホ側のアプリのパッケージ名
    'package_name' : {
        // Android側のアプリのパッケージ名
        'android' : 'com.huawei.sample.wearable.demo',
        // iOS側のアプリのパッケージ名
        'ios' : 'com.huawei.sample.wearable.demo'
    },
    // フィンガープリントについて、こちらをご参照ください
    // https://developer.huawei.com/consumer/cn/blog/topic/03188242414955227
    //
    // 例：
    // Android側が使うkeystoreファイル（jksファイル）に対して、次のコマンドを使って、SHA256の値を取得する
    // keytool -list -v -keystore [jksファイル]
    //
    // コンソールで次の結果が表示されるとする
    //
    // SHA256: 8D:8B:79:09:D1:AC:B2:D3:E0:31:12:72:B0:A3:75:B0:13:23:64:25:91:46:65:2A:AE:46:F5:3F:A6:2A:43:29
    //
    // フィンガープリントが8D8B7909D1ACB2D3E0311272B0A375B0132364259146652AAE46F53FA62A4329になる
    'finger_print' : {
        'android' : '8D8B7909D1ACB2D3E0311272B0A375B0132364259146652AAE46F53FA62A4329',
        'ios' : '8D8B7909D1ACB2D3E0311272B0A375B0132364259146652AAE46F53FA62A4329'
    }
};

// スマホの種類
// Android
export const OS_TYPE_ANDROID = 1;
// iOS
export const OS_TYPE_IOS = 2;

// ウォッチが送信するファイルの種類
// テキスト
export const FILE_MODE_TEXT = 'text';
// バイナリ
export const FILE_MODE_BINARY = 'binary';

// ウォッチが送信するファイルの属性
export const FILE_MODE2_READ = 'R';
export const FILE_MODE2_WRITE = 'W';
export const FILE_MODE2_READ_WRITE = 'RW';

// スマホ側から受信したメッセージの種類
// 不明
export const MESSAGE_TYPE_VALUE_UNKNOWN = 0;
// テキスト
export const MESSAGE_TYPE_VALUE_TEXT = 1;
// Json String
export const MESSAGE_TYPE_VALUE_JSON_STRING = 2;
