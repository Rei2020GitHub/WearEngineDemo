import { initP2pClient, unregisterReceiver } from '../../common/wearEngineManager.js';

import vibrator from '@system.vibrator';
import router from '@system.router';
import brightness from '@system.brightness';

import {
    ACTION_DOWN,
    ACTION_LEFT,
    ACTION_OPEN,
    ACTION_RIGHT,
    ACTION_UP, MESSAGE_TYPE_VALUE_ACTION, MESSAGE_TYPE_VALUE_TEXT,
    MOVE_MARGIN } from '../../common/constant.js';
import { getMessageData, getMessageType } from '../../common/util.js';

export default {
    data: {
        meLeft: (466 / 2) - (100 / 2),
        meTop: (466 / 2) - (100 / 2),
        message: 'Message',
    },
    onInit() {
        brightness.setKeepScreenOn({
            keepScreenOn: true,
            success: function () {
                console.log(`handling success`)
            },
            fail: function (data, code) {
            }
        })
    },
    onShow() {
        initP2pClient(this.messageReceiver());
    },
    onHide() {
        unregisterReceiver(this.messageReceiver());
    },
    onDestroy() {
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
                this.message = "Debug 1";

                // メッセージの種類がファイルの場合
                if (data && data.isFileType) {
                    console.log("messageReceiver() - onReceiveMessage : Type = File");

                    router.replace({
                        uri: 'pages/index/index'
                    })
                } else
                // メッセージの種類がファイル以外の場合
                {
                    console.log("messageReceiver() - onReceiveMessage : Type = text, data = " + data);

                    this.message = "Debug 2";

                    // テキストの場合
                    if (MESSAGE_TYPE_VALUE_TEXT == getMessageType(data)) {
                        router.replace({
                            uri: 'pages/index/index'
                        })
                    }
                    // アクションの場合
                    else if (MESSAGE_TYPE_VALUE_ACTION == getMessageType(data)) {
                        // メッセージの内容を取得する
                        var message = getMessageData(data);

                        if (ACTION_OPEN == message) {
                        }
                        else if (ACTION_UP == message) {
                            this.meTop = this.meTop - MOVE_MARGIN;

                            // バイブレーションを鳴らす
                            vibrator.vibrate(this.vibrateOption());
                        }
                        else if (ACTION_LEFT == message) {
                            this.meLeft = this.meLeft - MOVE_MARGIN;

                            // バイブレーションを鳴らす
                            vibrator.vibrate(this.vibrateOption());
                        }
                        else if (ACTION_RIGHT == message) {
                            this.meLeft = this.meLeft + MOVE_MARGIN;

                            // バイブレーションを鳴らす
                            vibrator.vibrate(this.vibrateOption());
                        }
                        else if (ACTION_DOWN == message) {
                            this.meTop = this.meTop + MOVE_MARGIN;

                            // バイブレーションを鳴らす
                            vibrator.vibrate(this.vibrateOption());
                        }
                    }
                }
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
    // 画面のスワイプイベント
    onSwipe(event) {
        // 左スワイプ
        if (event.direction === 'right') {
            router.replace({
                uri: 'pages/index/index'
            })
        }
        // 右スワイプ
        if (event.direction === 'left') {
            router.replace({
                uri: 'pages/index/index'
            })
        }
        // 上スワイプ
        if (event.direction === 'down') {
            router.replace({
                uri: 'pages/index/index'
            })
        }
        // 下スワイプ
        if (event.direction === 'up') {
            router.replace({
                uri: 'pages/index/index'
            })
        }
    },
    dragstartfunc(e) {
        this.start = e.globalX
    },
    dragendfunc(e) {
        var temp = e.globalX
        if(temp - this.start > 0) {
        }
    },
};
