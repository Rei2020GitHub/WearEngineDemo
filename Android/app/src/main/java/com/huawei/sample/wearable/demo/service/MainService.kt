package com.huawei.sample.wearable.demo.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import com.huawei.sample.wearable.demo.model.Constant
import com.huawei.sample.wearable.demo.ui.data.DataFragment
import com.huawei.sample.wearable.demo.ui.file.FileFragment
import com.huawei.wearengine.HiWear
import com.huawei.wearengine.common.WearEngineErrorCode
import com.huawei.wearengine.device.Device
import com.huawei.wearengine.p2p.CancelFileTransferCallBack
import com.huawei.wearengine.p2p.FileIdentification
import com.huawei.wearengine.p2p.P2pClient
import com.huawei.wearengine.p2p.Receiver
import com.huawei.wearengine.p2p.SendCallback
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets


class MainService : Service() {

    companion object {
        private val LOG_TAG = MainService::class.java.simpleName

        ////////////////////////////////////////////////////////////////
        // フラグメントとサービスの通信コマンド
        // （Wear Engineと関係ない。フラグメントとサービスのやり取り専用）
        ////////////////////////////////////////////////////////////////

        // スマホからウォッチへのテキスト送信についてのコマンド

        const val MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH = 1
        const val MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH_SUCCESS = 2
        const val MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH_FAIL = 3

        // ウォッチからスマホへのテキスト送信についてのコマンド

        const val MESSAGE_SEND_TEXT_FROM_WATCH_TO_PHONE = 4

        // スマホからウォッチへのファイル送信についてのコマンド

        const val MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_START = 5
        const val MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_PROGRESS = 6
        const val MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_SUCCESS = 7
        const val MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL = 8

        // スマホからウォッチへのファイル送信停止についてのコマンド

        const val MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH = 9
        const val MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_SUCCESS = 10
        const val MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL = 11
        const val MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_RESULT = 12

        // ウォッチからスマホへのファイル送信についてのコマンド

        const val MESSAGE_SEND_FILE_FROM_WATCH_TO_PHONE = 13

        const val MESSAGE_TYPE_KEY = "type"
        const val MESSAGE_TYPE_VALUE_TEXT = 1
        const val MESSAGE_TYPE_VALUE_JSON_STRING = 2
        const val MESSAGE_DATA_KEY = "data"

        const val KEY_DATA = "Data"
    }

    // フラグメントから受信したメッセージの処理
    inner class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                // スマホからウォッチへのテキスト送信
                MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH -> {
                    // メッセージから送信内容のテキストを取得する
                    val text = message.data.getString(KEY_DATA)
                    text?.let { text ->
                        // テキストを送信する
                        sendText(text)
                    }
                    Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")
                }
                // スマホからウォッチへのファイル送信
                MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_START -> {
                    // メッセージから送信内容のファイルパスを取得する
                    val path = message.data.getString(KEY_DATA)
                    path?.let { path ->
                        // ファイルパスを渡してファイルを送信する
                        sendFile(path)
                    }
                    Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, path = $path")
                }
                // スマホからウォッチへのファイル送信停止
                MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH -> {
                    // メッセージから送信内容のファイルパスを取得する
                    val path = message.data.getString(KEY_DATA)
                    path?.let { path ->
                        // ファイル送信を停止する
                        stopSendFile(path)
                    }
                    Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, path = $path")
                }
                // その他
                else -> {
                    Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}")
                    super.handleMessage(message)
                }
            }
        }
    }

    private lateinit var p2pClient: P2pClient

    private lateinit var messenger: Messenger

    private var connectedDevice: Device? = null
    private var targetWatchAppPackageName: String? = null
    private var targetWatchAppFingerPrint: String? = null

    private var dataFragmentMessenger: Messenger? = null
    private var fileFragmentMessenger: Messenger? = null

    // ウォッチ側から受信したメッセージ
    private val receiver = object : Receiver {
        override fun onReceiveMessage(p2pMessage: com.huawei.wearengine.p2p.Message) {
            when (p2pMessage.type) {
                // メッセージの種類：データ
                com.huawei.wearengine.p2p.Message.MESSAGE_TYPE_DATA -> {
                    Log.i(LOG_TAG, "onReceiveMessage() data = " + p2pMessage.data.toString(Charsets.UTF_8) + ", description = " + p2pMessage.description)

                    // メッセージのデータ内容を取得する
                    val jsonString = p2pMessage.data.toString(Charsets.UTF_8)

                    // データ内容をフラグメントに転送する
                    noticeDataTypeResult(MESSAGE_SEND_TEXT_FROM_WATCH_TO_PHONE, jsonString)
                }
                // メッセージの種類：ファイル
                com.huawei.wearengine.p2p.Message.MESSAGE_TYPE_FILE -> {
                    Log.i(LOG_TAG, "onReceiveMessage() absolutePath = " + p2pMessage.file.absolutePath)

                    // 受信したファイルの絶対パスをフラグメントに転送する
                    noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_WATCH_TO_PHONE, p2pMessage.file.absolutePath)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(LOG_TAG, "onCreate()")

        // フラグメントから受信したメッセージを処理するメッセンジャーを生成する
        messenger = Messenger(IncomingHandler(applicationContext))

        // Wear Engineの初期化
        wearEngineInit()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(LOG_TAG, "onBind()")

        intent?.let { intent ->
            // DataFragmentに送信するメッセンジャーを登録する
            if (intent.hasExtra(DataFragment::class.java.simpleName)) {
                dataFragmentMessenger = intent.getParcelableExtra(DataFragment::class.java.simpleName)
                Log.i(LOG_TAG, "onBind() dataFragmentMessenger = $dataFragmentMessenger")
            }
            // FileFragmentに送信するメッセンジャーを登録する
            if (intent.hasExtra(FileFragment::class.java.simpleName)) {
                fileFragmentMessenger = intent.getParcelableExtra(FileFragment::class.java.simpleName)
                Log.i(LOG_TAG, "onBind() fileFragmentMessenger = $fileFragmentMessenger")
            }
        }

        return messenger.binder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)

        Log.i(LOG_TAG, "onRebind()")

        intent?.let { intent ->
            // DataFragmentに送信するメッセンジャーを登録する
            if (intent.hasExtra(DataFragment::class.java.simpleName)) {
                dataFragmentMessenger = intent.getParcelableExtra(DataFragment::class.java.simpleName)
                Log.i(LOG_TAG, "onRebind() dataFragmentMessenger = $dataFragmentMessenger")
            }
            // FileFragmentに送信するメッセンジャーを登録する
            if (intent.hasExtra(FileFragment::class.java.simpleName)) {
                fileFragmentMessenger = intent.getParcelableExtra(FileFragment::class.java.simpleName)
                Log.i(LOG_TAG, "onRebind() fileFragmentMessenger = $fileFragmentMessenger")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(LOG_TAG, "onStartCommand()")

        intent?.let { intent ->
            // DataFragmentに送信するメッセンジャーを登録する
            if (intent.hasExtra(DataFragment::class.java.simpleName)) {
                dataFragmentMessenger = intent.getParcelableExtra(DataFragment::class.java.simpleName)
                Log.i(LOG_TAG, "onStartCommand() dataFragmentMessenger = $dataFragmentMessenger")
            }
            // FileFragmentに送信するメッセンジャーを登録する
            if (intent.hasExtra(FileFragment::class.java.simpleName)) {
                fileFragmentMessenger = intent.getParcelableExtra(FileFragment::class.java.simpleName)
                Log.i(LOG_TAG, "onStartCommand() fileFragmentMessenger = $fileFragmentMessenger")
            }
        }

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, "onDestroy()")

        // Wear Engineのレシーバーを解除する
        HiWear.getP2pClient(this).unregisterReceiver(receiver)
            .addOnSuccessListener {
                Log.i(LOG_TAG, "onDestroy() unregisterReceiver() addOnSuccessListener")
            }
            .addOnFailureListener { execption ->
                Log.e(LOG_TAG, "onDestroy() unregisterReceiver() addOnFailureListener : ", execption)
            }
    }

    // Wear Engineの初期化
    private fun wearEngineInit() {
        // P2pClientを生成する
        p2pClient = HiWear.getP2pClient(applicationContext)

        // 登録済みのデバイスを取得する
        HiWear.getDeviceClient(applicationContext)
            .bondedDevices
            .addOnSuccessListener { deviceList ->
                Log.i(LOG_TAG, "init() addOnSuccessListener() deviceList = " + deviceList.joinToString{it.name})

                // 登録済みのデバイスに対して、以下の処理を行う
                deviceList.forEach { device ->
                    // 接続中のデバイスに対して、以下の処理を行う
                    if (device.isConnected) {
                        connectedDevice = device

                        Log.i(LOG_TAG, "init() addOnSuccessListener() connected device = " + device.name + ", UUID = " + device.uuid)

                        // デバイスに指定のアプリ（パッケージ）がインストール済みか調べる
                        // まずWearable側のアプリのパッケージ名で調べる
                        p2pClient
                            .isAppInstalled(connectedDevice, Constant.WEARABLE_APP_PACKAGE_NAME)
                            .addOnSuccessListener { isAppInstalled ->
                                // Wearableのアプリがインストール済みの場合
                                if (isAppInstalled) {
                                    targetWatchAppPackageName = Constant.WEARABLE_APP_PACKAGE_NAME
                                    targetWatchAppFingerPrint = Constant.WEARABLE_APP_FINGER_PRINT

                                    // Wearable側のアプリのパッケージ名を設定する
                                    p2pClient.setPeerPkgName(targetWatchAppPackageName)
                                    // Wearable側のアプリのフィンガープリントを設定する
                                    p2pClient.setPeerFingerPrint(targetWatchAppFingerPrint)
                                    // Wear Engineのレシーバーを登録する
                                    p2pClient.registerReceiver(connectedDevice, receiver)
                                    Log.i(LOG_TAG, "init() registerReceiver()")

                                    Log.i(LOG_TAG, "init() addOnSuccessListener() Wearable (Smart watch)")
                                    Log.i(LOG_TAG, "init() addOnSuccessListener() ${Constant.WEARABLE_APP_PACKAGE_NAME} = $isAppInstalled")
                                } else {
                                    // Lite Wearable側のアプリのパッケージ名で調べる
                                    p2pClient
                                        .isAppInstalled(connectedDevice, Constant.LITE_WEARABLE_APP_PACKAGE_NAME)
                                        .addOnSuccessListener { isAppInstalled ->
                                            // Lite Wearableのアプリがインストール済みの場合
                                            if (isAppInstalled) {
                                                targetWatchAppPackageName = Constant.LITE_WEARABLE_APP_PACKAGE_NAME
                                                targetWatchAppFingerPrint = Constant.LITE_WEARABLE_APP_FINGER_PRINT

                                                // Lite Wearable側のアプリのパッケージ名を設定する
                                                p2pClient.setPeerPkgName(targetWatchAppPackageName)
                                                // Lite Wearable側のアプリのフィンガープリントを設定する
                                                p2pClient.setPeerFingerPrint(targetWatchAppFingerPrint)
                                                // Lite Wear Engineのレシーバーを登録する
                                                p2pClient.registerReceiver(connectedDevice, receiver)
                                                Log.i(LOG_TAG, "init() registerReceiver()")

                                                Log.i(LOG_TAG, "init() addOnSuccessListener() Lite wearable (Sport watch)")
                                                Log.i(LOG_TAG, "init() addOnSuccessListener() ${Constant.LITE_WEARABLE_APP_PACKAGE_NAME} = $isAppInstalled")
                                            }
                                        }
                                        .addOnFailureListener { execption ->
                                            Log.e(LOG_TAG, "init() addOnFailureListener() : ", execption)
                                        }
                                }
                            }
                            .addOnFailureListener { execption ->
                                Log.e(LOG_TAG, "init() addOnFailureListener() : ", execption)
                            }

                        return@addOnSuccessListener
                    }
                }
            }
            .addOnFailureListener { execption ->
                Log.e(LOG_TAG, "init() addOnFailureListener() : ", execption)
            }
    }

    // テキストを送信する
    private fun sendText(text: String) {
        val jsonObject = JSONObject().apply {
            this.put(MESSAGE_TYPE_KEY, MESSAGE_TYPE_VALUE_TEXT)
            this.put(MESSAGE_DATA_KEY, text)
        }
        // テキストをJsonStringに変換し、デバイスに送信する
        sendJsonString(jsonObject.toString())
    }

    // テキストをJsonStringに変換し、ウォッチに送信する
    private fun sendJsonString(jsonString: String) {
        if (jsonString.isBlank()) {
            return
        }

        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                Log.i(LOG_TAG, "sendJsonString() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                // メッセージを生成する
                val sendMessage = com.huawei.wearengine.p2p.Message.Builder()
                    .setPayload(jsonString.toByteArray(StandardCharsets.UTF_8))
                    .build()

                // メッセージをウォッチに送信する
                p2pClient
                    .send(connectedDevice, sendMessage, object : SendCallback {
                        override fun onSendResult(resultCode: Int) {
                            // 送信成功
                            if (WearEngineErrorCode.ERROR_CODE_COMM_SUCCESS == resultCode) {
                                Log.i(LOG_TAG, "sendJsonString() onSendResult() succeeded - jsonString = $jsonString, resultCode = $resultCode, targetWatchAppPackageName = $targetWatchAppPackageName, targetWatchAppFingerPrint = $targetWatchAppFingerPrint")

                                // ウォッチ側から受信した送信結果をフラグメントに反映させる
                                noticeDataTypeResult(MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH_SUCCESS, jsonString)
                            } else
                            // 送信失敗
                            {
                                Log.i(LOG_TAG, "sendJsonString() onSendResult() failed - jsonString = $jsonString, resultCode = $resultCode ${WearEngineErrorCode.getErrorMsgFromCode(resultCode)}, targetWatchAppPackageName = $targetWatchAppPackageName, targetWatchAppFingerPrint = $targetWatchAppFingerPrint")

                                // ウォッチ側から受信した送信結果をフラグメントに反映させる
                                noticeDataTypeResult(MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH_FAIL, "")
                            }
                        }

                        override fun onSendProgress(progress: Long) {
                            Log.i(LOG_TAG, "sendJsonString() onSendProgress() progress = $progress")
                        }
                    })
                    .addOnSuccessListener {
                        Log.i(LOG_TAG, "sendJsonString() addOnSuccessListener()")
                    }
                    .addOnFailureListener { execption ->
                        Log.e(LOG_TAG, "sendJsonString() addOnFailureListener() : ", execption)

                        // 送信失敗
                        // 送信結果をフラグメントに反映させる
                        noticeDataTypeResult(MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH_FAIL, "")
                    }
            }
        } ?: let {
            Log.i(LOG_TAG, "sendJsonString() connectedDevice is null")
        }
    }

    // ファイルをウォッチに送信する
    private fun sendFile(path: String) {
        // ファイルパスが空の場合
        if (path.isBlank()) {
            // エラーをフラグメントに反映させる
            noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
            return
        }

        val file = File(path)
        // ファイルパスが開けない場合
        if (!file.exists()) {
            Log.i(LOG_TAG, "sendFile() $path not exist")
            // エラーをフラグメントに反映させる
            noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
            return
        }

        // ファイルではない場合
        if (!file.isFile) {
            Log.i(LOG_TAG, "sendFile() $path not file")
            // エラーをフラグメントに反映させる
            noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
            return
        }

        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                Log.i(LOG_TAG, "sendFile() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                // メッセージを生成する
                val sendMessage = com.huawei.wearengine.p2p.Message.Builder()
                    .setPayload(file)
                    .build()

                p2pClient
                    .send(connectedDevice, sendMessage, object : SendCallback {
                        override fun onSendResult(resultCode: Int) {
                            // 送信成功
                            if (WearEngineErrorCode.ERROR_CODE_COMM_SUCCESS == resultCode) {
                                Log.i(LOG_TAG, "sendFile() onSendResult() succeeded - path = $path, resultCode = $resultCode, targetWatchAppPackageName = $targetWatchAppPackageName, targetWatchAppFingerPrint = $targetWatchAppFingerPrint")

                                // ウォッチ側から受信した送信結果をフラグメントに反映させる
                                noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_SUCCESS, path)
                            } else
                            // 送信失敗
                            {
                                Log.i(LOG_TAG, "sendFile() onSendResult() failed - path = $path, resultCode = $resultCode ${WearEngineErrorCode.getErrorMsgFromCode(resultCode)}, targetWatchAppPackageName = $targetWatchAppPackageName, targetWatchAppFingerPrint = $targetWatchAppFingerPrint")

                                // ウォッチ側から受信した送信結果をフラグメントに反映させる
                                noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
                            }
                        }

                        override fun onSendProgress(progress: Long) {
                            Log.i(LOG_TAG, "sendFile() onSendProgress() progress = $progress")

                            // ファイル送信進捗をフラグメントに反映させる
                            noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_PROGRESS, progress.toString())
                        }
                    })
                    .addOnSuccessListener {
                        Log.i(LOG_TAG, "sendFile() addOnSuccessListener()")
                    }
                    .addOnFailureListener { execption ->
                        Log.e(LOG_TAG, "sendFile() addOnFailureListener() : ", execption)

                        // 送信失敗
                        // 送信結果をフラグメントに反映させる
                        noticeFileTypeResult(MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
                    }
            }
        } ?: let {
            Log.i(LOG_TAG, "sendFile() connectedDevice is null")
        }
    }

    // ファイル送信停止
    private fun stopSendFile(path: String) {
        // ファイルパスが空の場合
        if (path.isBlank()) {
            // エラーをフラグメントに反映させる
            noticeFileTypeResult(MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
            return
        }

        val file = File(path)
        // ファイルパスが開けない場合
        if (!file.exists()) {
            Log.i(LOG_TAG, "stopSendFile() $path not exist")
            // エラーをフラグメントに反映させる
            noticeFileTypeResult(MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
            return
        }

        // ファイルではない場合
        if (!file.isFile) {
            Log.i(LOG_TAG, "stopSendFile() $path not file")
            // エラーをフラグメントに反映させる
            noticeFileTypeResult(MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
            return
        }

        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                Log.i(LOG_TAG, "stopSendFile() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                val fileIdentification = FileIdentification.Builder()
                    .setFile(file)
                    .build()

                // ファイル送信停止を送信する
                p2pClient
                    .cancelFileTransfer(connectedDevice, fileIdentification, object : CancelFileTransferCallBack {
                        override fun onCancelFileTransferResult(errCode: Int) {
                            Log.i(LOG_TAG, "stopSendFile() onCancelFileTransferResult() path = $path, errCode = $errCode")

                            // ファイル送信停止結果をフラグメントに反映させる
                            noticeFileTypeResult(MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_RESULT, "path = $path, errCode = $errCode")
                        }
                    })
                    .addOnSuccessListener {
                        Log.i(LOG_TAG, "stopSendFile() addOnSuccessListener()")

                        // ファイル送信停止結果をフラグメントに反映させる
                        noticeFileTypeResult(MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_SUCCESS, path)
                    }
                    .addOnFailureListener { execption ->
                        Log.e(LOG_TAG, "stopSendFile() addOnFailureListener() : ", execption)

                        // ファイル送信停止結果をフラグメントに反映させる
                        noticeFileTypeResult(MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL, path)
                    }
            }
        } ?: let {
            Log.i(LOG_TAG, "stopSendFile() connectedDevice is null")
        }
    }

    // DataFragmentに結果を反映させる
    private fun noticeDataTypeResult(type: Int, text: String) {
        dataFragmentMessenger?.let { dataFragmentMessenger ->
            val replyMessage = Message.obtain(null, type)
                .apply {
                    data = Bundle().apply {
                        putString(KEY_DATA, text)
                    }
                }
            try {
                dataFragmentMessenger.send(replyMessage)
                Log.i(LOG_TAG, "noticeDataTypeResult() type = $type, text = $text")
            } catch (remoteException: RemoteException) {
                Log.e(LOG_TAG, "sendMessage() exception : ", remoteException)
            }
        }
    }

    // FileFragmentに結果を反映させる
    private fun noticeFileTypeResult(type: Int, text: String) {
        fileFragmentMessenger?.let { fileFragmentMessenger ->
            val jsonObject = JSONObject().apply {
                this.put(MESSAGE_TYPE_KEY, MESSAGE_TYPE_VALUE_TEXT)
                this.put(MESSAGE_DATA_KEY, text)
            }

            val replyMessage = Message.obtain(null, type)
                .apply {
                    data = Bundle().apply {
                        putString(KEY_DATA, jsonObject.toString())
                    }
                }
            try {
                fileFragmentMessenger.send(replyMessage)
                Log.i(LOG_TAG, "noticeFileTypeResult() type = $type, text = $text")
            } catch (remoteException: RemoteException) {
                Log.e(LOG_TAG, "sendMessage() exception : ", remoteException)
            }
        }
    }
}