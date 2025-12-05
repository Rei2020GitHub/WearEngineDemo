import { initP2pClient, ping, sendFile, unregisterReceiver } from '../../common/wearEngineManager.js';
import { FILE_MODE2_READ, FILE_MODE_BINARY,
    MESSAGE_TYPE_VALUE_ACTION,
    MESSAGE_TYPE_VALUE_TEXT } from '../../common/constant.js';
import { getMessageData, getMessageType, isBinFile, isJsonFile, isTxtFile, sendText } from '../../common/util.js';

import file from '@system.file';
import vibrator from '@system.vibrator';
import router from '@system.router';
import brightness from '@system.brightness';

export default {
    data: {
        imageSrc: '',
        message: '',
        buttonPingValue: '',
        buttonSendTextValue: '',
        buttonSendFileValue: '',
    },
    onInit() {
        this.imageSrc = '';
        this.message = this.$t('strings.message');
        this.buttonPingValue = this.$t('strings.ping');
        this.buttonSendTextValue = this.$t('strings.sendtext');
        this.buttonSendFileValue = this.$t('strings.sendfile');
    },
    onReady() {
    },
    onShow() {
        brightness.setKeepScreenOn(this.setKeepScreenOnOptions(true));
        initP2pClient(this.messageReceiver());
    },
    onHide() {
        brightness.setKeepScreenOn(this.setKeepScreenOnOptions(false));
        unregisterReceiver(this.messageReceiver());
    },
    onDestroy() {
    },
    // Pingのコールバック
    pingCallback() {
        return {
            onSuccess: function() {
                console.log("pingCallback() - onSuccess");
            }.bind(this),
            onFailure: function() {
                console.log("pingCallback() - onFailure");
            }.bind(this),
            onPingResult: function(resultCode) {
                console.log("pingCallback() - onPingResult : " + resultCode.data + " - " + resultCode.code);
                this.message = 'Ping : ' + resultCode.code;
            }.bind(this),
        }
    },
    // スマホから送信されるメッセージを受信するレシーバー
    messageReceiver() {
        return {
            onSuccess: function() {
                console.log("messageReceiver() - onSuccess")
            }.bind(this),
            onFailure :function() {
                console.log("messageReceiver() - onFailure")
            }.bind(this),
            onReceiveMessage: function (data) {
                // バイブレーションを鳴らす
                vibrator.vibrate(this.vibrateOption());

                // メッセージの種類がファイルの場合
                if (data && data.isFileType) {
                    console.log("messageReceiver() - onReceiveMessage : Type = File");

                    // 受信したファイルの拡張子がbinの場合、画像ファイルと仮定し、表示する
                    if (isBinFile(data.name)) {
                        // ファイル名を表示する
                        this.message = data.name;

                        // 画像を表示する
                        this.imageSrc = data.name;
                    }
                    // 受信したファイルがjsonまたはtxtの場合
                    else if (isJsonFile(data.name) || isTxtFile(data.name)) {
                        // テキストファイルとしてデータを読み込む
                        file.readText(this.readTextCallback(data.name));
                    } else {
                        // ファイル名を表示する
                        this.message = data.name;
                    }
                } else
                // メッセージの種類がファイル以外の場合
                {
                    console.log("messageReceiver() - onReceiveMessage : Type = text, data = " + data);

                    // テキストの場合
                    if (MESSAGE_TYPE_VALUE_TEXT == getMessageType(data)) {
                        // メッセージの内容を取得する
                        var message = getMessageData(data);
                        // メッセージの内容を表示する
                        this.message = message;
                        // スマホに返事する
                        sendText("Received: " + message, this.sendTextCallback());
                    }
                    // アクションの場合
                    else if (MESSAGE_TYPE_VALUE_ACTION == getMessageType(data)) {
                        router.replace({
                            uri: 'pages/action/action'
                        })
                    }
                }
            }.bind(this),
        }
    },
    // テキストファイルを読み込むときのコールバック
    readTextCallback(uri) {
        return {
            uri: uri,
            success: function(fileData) {
                // ファイル名を表示する
                this.message = fileData.name;

                // ファイル内容を表示する
                this.message = fileData.text;
            }.bind(this),
            fail: function(fileData, code) {
                console.error('call readText fail callback fail, code: ' + code + ', fileData: ' + fileData);
            }.bind(this),
        }
    },
    // スマホにテキストを送信するときのコールバック
    sendTextCallback() {
        return {
            onSuccess: function() {
                console.log("sendTextCallback() - onSuccess")
            }.bind(this),
            onFailure: function() {
                console.log("sendTextCallback() - onFailure")
            }.bind(this),
            onSendResult: function(resultCode) {
                console.log("Send text: " + resultCode.data + " - " + resultCode.code);
            }.bind(this),
        }
    },
    // スマホにファイルを送信するときのコールバック
    sendFileCallback() {
        return {
            onSuccess: function() {
                console.log("sendFileCallback() - onSuccess")
            }.bind(this),
            onFailure: function() {
                console.log("sendFileCallback() - onFailure")
            }.bind(this),
            onSendResult: function(resultCode) {
                console.log("Send file: " + resultCode.data + " - " + resultCode.code);
                this.message = "Send file: " + resultCode.data + " - " + resultCode.code;
            }.bind(this),
            onSendProgress: function(count) {
                console.log("Progress" + count);
                console.log("Send file progress: " + count);
                this.message = "Send file progress: " + count;
            }.bind(this),
        }
    },
    // バイブレーションを鳴らすパラメータ
    vibrateOption() {
        return {
            mode: 'short',
            success: function(ret) {
                console.log('vibrate is successful');
            }.bind(this),
            fail: function(ret) {
                console.log('vibrate is failed');
            }.bind(this),
            complete: function(ret) {
                console.log('vibrate is completed');
            }.bind(this),
        }
    },
    // スリープさせないコールバック
    setKeepScreenOnOptions(keepScreenOn) {
        return {
            keepScreenOn: keepScreenOn,
            success: function () {
                console.log(`handling success`)
            },
            fail: function (data, code) {
            }
        }
    },
    onClickButtonPing() {
        ping(this.pingCallback());

        // バイブレーションを鳴らす
        vibrator.vibrate(this.vibrateOption());
    },
    onClickButtonSendText() {
        sendText("Send Text", this.sendTextCallback());
    },
    onClickButtonSendFile() {
        sendFile(this.imageSrc, FILE_MODE_BINARY, FILE_MODE2_READ, this.sendFileCallback());
    },
    // 画面のスワイプイベント
    onSwipe(event) {
        // 左スワイプ
        if (event.direction === 'right') {
            router.replace({
                uri: 'pages/info/info'
            })
        }
        // 右スワイプ
        if (event.direction === 'left') {
            router.replace({
                uri: 'pages/action/action'
            })
        }
        // 上スワイプ
        if (event.direction === 'down') {
            router.replace({
                uri: 'pages/heavyPage/heavyPage'
            })
        }
        // 下スワイプ
        if (event.direction === 'up') {
            router.replace({
                uri: 'pages/action/action'
            })
        }
    },
};