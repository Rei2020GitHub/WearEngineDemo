import { MESSAGE_TYPE_VALUE_TEXT, MESSAGE_TYPE_VALUE_UNKNOWN } from './constant';
import { sendJsonString } from './wearEngineManager';

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

// Binファイルかチェックする
// Lite Wearableではbinの画像しか表示できない
const isBinFile = (path) => {
    // 拡張子がbinの場合、Binファイルとみなす
    if (path.endsWith('.bin')) {
        return true;
    }
    return false;
}

// jsonファイルかチェックする
const isJsonFile = (path) => {
    // 拡張子がjsonの場合、jsonファイルとみなす
    if (path.endsWith('.json')) {
        return true;
    }
    return false;
}

// txtファイルかチェックする
const isTxtFile = (path) => {
    // 拡張子がtxtの場合、txtファイルとみなす
    if (path.endsWith('.txt')) {
        return true;
    }
    return false;
}

// 半角文字数をカウントする
const halfCharTotal = (str) => {
    let total = 0;
    for (let i = 0; i < str.length; i++) {
        if (str.charCodeAt(i) <= 127) {
            total += 1;
        }
    }
    return total;
}

// 全角文字数をカウントする
const fullCharTotal = (str) => {
    let total = 0;
    for (let i = 0; i < str.length; i++) {
        if (str.charCodeAt(i) > 127) {
            total += 1;
        }
    }
    return total;
}

export {
    getMessageType,
    getMessageData,
    sendText,
    isBinFile,
    isJsonFile,
    isTxtFile,
    halfCharTotal,
    fullCharTotal,
}