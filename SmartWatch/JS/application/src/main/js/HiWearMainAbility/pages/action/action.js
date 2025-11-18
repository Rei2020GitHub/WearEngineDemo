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
import { getMessageData, getMessageType, setWindowKeepScreenOn } from '../../common/util.js';

const MAX_WIDTH = 466;
const MAX_HEIGHT = 466;

let imageLeft = 0;
let imageTop = 0;
let imageWidth = 466;
let imageHeight = 466;

export default {
    data: {
        meLeft: 0,
        meTop: 0,
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
    async onShow() {
        await setWindowKeepScreenOn(true);
        initP2pClient(this.messageReceiver());

        this.drawCanvas();
    },
    async onHide() {
        await setWindowKeepScreenOn(false);
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
    // 拡大
    onClickUiZoomIn() {
        imageLeft -= 5;
        imageTop -= 5;
        imageWidth += 10;
        imageHeight += 10;
        this.drawCanvas();
    },
    //　縮小
    onClickUiZoomOut() {
        imageLeft += 5;
        imageTop += 5;
        imageWidth -= 10;
        imageHeight -= 10;
        this.drawCanvas();
    },
    // 画像の描画
    drawCanvas() {
        const el = this.$refs.canvas;
        var ctx = el.getContext('2d');

        var img = new Image();
        img.src = 'common/image/image.jpg';
        img.onload = function () {
            console.log('Image load success');
            ctx.fillStyle = '#FF000000';
            ctx.fillRect(0, 0, MAX_WIDTH, MAX_HEIGHT);
            ctx.drawImage(img, imageLeft, imageTop, imageWidth, imageHeight);
        };
        img.onerror = function () {
            console.error('Image load fail');
        };
    },
};
