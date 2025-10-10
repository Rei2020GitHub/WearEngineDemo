import { MESSAGE_TYPE_VALUE_TEXT, MESSAGE_TYPE_VALUE_UNKNOWN } from './constant';
import { sendJsonString } from './wearEngineManager';
import fileUri from '@ohos.file.fileuri';
import featureAbility from '@ohos.ability.featureAbility';
const context = featureAbility.getContext();

// スマホ側から受信したメッセージの種類を取得する
const getMessageType = (message) => {
    if (null != message) {
        const jsonObject = JSON.parse(message);
        return jsonObject.type;
    }

    return MESSAGE_TYPE_VALUE_UNKNOWN;
}

// スマホ側から受信したメッセージの内容を取得する
const getMessageData = (message) => {
    if (null != message) {
        const jsonObject = JSON.parse(message);
        return jsonObject.data;
    }

    return null;
}

// ウォッチからスマホにテキストを送信する
const sendText = (text, callback) => {
    const jsonObject = {
        type: MESSAGE_TYPE_VALUE_TEXT,
        data: text
    }

    sendJsonString(JSON.stringify(jsonObject), callback)
}

// Internal://app/を絶対パスに変換する
const convertInternalToAbsolute = async (internalPath) => {
    let filePath = await context.getFilesDir() + '/' + internalPath.replace('internal://app/', '');
    return fileUri.getUriFromPath(filePath);
}

// Wearableで表示できる画像ファイルであるかチェックする
const isImageFile = (path) => {
    // 拡張子がjpgまたはpngである場合、Wearableで表示できる画像ファイルとみなす
    if (path.endsWith('.jpg') || path.endsWith('.png')) {
        return true;
    }
    return false;
}

export {
    getMessageType,
    getMessageData,
    sendText,
    convertInternalToAbsolute,
    isImageFile,
}