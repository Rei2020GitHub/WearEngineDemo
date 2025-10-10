// WearableのWear Engine SDK
import { P2pClient, PeerDeviceClient, Builder, Message } from './sdk/wearable/wearengine';

import { WEAR_ENGINE_CONST, OS_TYPE_ANDROID, OS_TYPE_IOS } from './constant.js';

var p2pClient = null;

// P2pClientのオブジェクトを生成する
const initP2pClient = (receiver) => {
    const peerDeviceClient = new PeerDeviceClient();

    // スマホの種類を取得する
    peerDeviceClient.getPeerDevice({
        onSuccess: (data) => {
            // Android
            if (OS_TYPE_ANDROID == data.peerDevice.mOsType) {
                // P2pClientのオブジェクトを生成する
                p2pClient = new P2pClient();
                p2pClient.setPeerPkgName(WEAR_ENGINE_CONST.package_name.android);
                p2pClient.setPeerFingerPrint(WEAR_ENGINE_CONST.finger_print.android);

                // レシーバーを登録する
                if (null != receiver) {
                    p2pClient.registerReceiver(receiver);
                }
            } else if
            // iOS
            (OS_TYPE_IOS == data.peerDevice.mOsType) {
                // P2pClientのオブジェクトを生成する
                p2pClient = new P2pClient();
                p2pClient.setPeerPkgName(WEAR_ENGINE_CONST.package_name.ios);
                p2pClient.setPeerFingerPrint(WEAR_ENGINE_CONST.finger_print.ios);

                // レシーバーを登録する
                if (null != receiver) {
                    p2pClient.registerReceiver(receiver);
                }
            }
        },
        onFailure: () => {
            console.log("getPeerDevice fail")
        },
    })
}

// スマホと通信できるか確認する（Pingする）
const ping = (callback) => {
    p2pClient.ping(callback);
}

// スマホにJson Stringを送信する
const sendJsonString = (jsonString, callback) => {
    var builderClient = new Builder();
    builderClient.setDescription(jsonString);

    var sendMessage = new Message();
    sendMessage.builder = builderClient;

    p2pClient.send(sendMessage, callback);
}

// スマホにファイルを送信する
const sendFile = (filepath, mode, mode2, callback) => {
    if (null == filepath || null == mode || null == mode2 || null == p2pClient) {
        return;
    }

    var file = {
        "name" : filepath,
        "mode" : mode,
        "mode2" : mode2,
    };

    var builderClient = new Builder();
    builderClient.setPayload(file);

    var sendMessage = new Message();
    sendMessage.builder = builderClient;

    p2pClient.send(sendMessage, callback);
}

// レシーバーを解除する
const unregisterReceiver = (callback) => {
    if (null != p2pClient) {
        p2pClient.unregisterReceiver(callback);
    }
}

export {
    initP2pClient,
    ping,
    sendJsonString,
    sendFile,
    unregisterReceiver,
}