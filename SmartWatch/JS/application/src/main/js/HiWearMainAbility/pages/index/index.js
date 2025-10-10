import { initP2pClient, ping, sendFile, unregisterReceiver } from '../../common/wearEngineManager.js';
import { FILE_MODE2_READ, FILE_MODE_BINARY, MESSAGE_TYPE_VALUE_TEXT } from '../../common/constant.js';
import { convertInternalToAbsolute, getMessageData, getMessageType, isImageFile, sendText } from '../../common/util.js';

let filePath;
let fileInternalPath;

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

        initP2pClient(this.messageReceiver());
    },
    onReady() {

    },
    onShow() {
    },
    onDestroy() {
        unregisterReceiver(this.messageReceiver());
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
            onReceiveMessage: async function (data) {
                // メッセージの種類がファイルの場合
                if (data && data.isFileType) {
                    console.log("messageReceiver() - onReceiveMessage : Type = File");

                    // internal://app/のファイルパスを保存する
                    fileInternalPath = data.name;
                    // internal://app/のファイルパスを絶対パスに変換する
                    filePath = await convertInternalToAbsolute(data.name);

                    // ファイル名を表示する
                    this.message = filePath;

                    // 受信したファイルの拡張子がjpgまたはpngの場合、画像ファイルと仮定し、表示する
                    if (isImageFile(filePath)) {
                        // 絶対パスで画像ファイルを表示する
                        this.imageSrc = filePath;
                    }
                } else {
                    console.log("messageReceiver() - onReceiveMessage : Type = text, data = " + data);
                    if (MESSAGE_TYPE_VALUE_TEXT == getMessageType(data)) {
                        // メッセージの内容を取得する
                        var message = getMessageData(data);
                        // メッセージの内容を表示する
                        this.message = message;
                        // スマホに返事する
                        sendText("Received: " + message, this.sendTextCallback());
                    }
                }
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
    onClickButtonPing() {
        ping(this.pingCallback());
    },
    onClickButtonSendText() {
        sendText("Send Text", this.sendTextCallback());
    },
    onClickButtonSendFile() {
        // internal://app/のファイルパスを渡し、ファイルをスマホに送信する
        sendFile(fileInternalPath, FILE_MODE_BINARY, FILE_MODE2_READ, this.sendFileCallback());
    }
};
