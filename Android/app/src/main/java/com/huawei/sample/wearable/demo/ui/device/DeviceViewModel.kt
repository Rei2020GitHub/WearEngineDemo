package com.huawei.sample.wearable.demo.ui.device

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.sample.wearable.demo.model.Constant
import com.huawei.wearengine.HiWear
import com.huawei.wearengine.auth.Permission
import com.huawei.wearengine.common.WearEngineErrorCode
import com.huawei.wearengine.device.Device
import com.huawei.wearengine.monitor.MonitorItem
import com.huawei.wearengine.monitor.MonitorListener
import com.huawei.wearengine.p2p.PingCallback


class DeviceViewModel : ViewModel() {

    companion object {
        private val LOG_TAG = DeviceViewModel::class.java.simpleName

        private val PERMISSION_ARRAY = arrayOf(Permission.DEVICE_MANAGER, Permission.NOTIFY)

        private val MONITOR_CHARGE_STATUS_DETAIL: Map<Int, String> = mapOf(
            1 to "Charge start",
            2 to "Charge stop",
            3 to "Charge finish"
        )

        private val MONITOR_ITEM_LIST = listOf<MonitorItem>(MonitorItem.MONITOR_ITEM_LOW_POWER, MonitorItem.MONITOR_CHARGE_STATUS)
    }

    private val _textHasAvailableDevices = MutableLiveData<String>().apply {
        value = "Devices : Unknown"
    }
    val textHasAvailableDevices: LiveData<String> = _textHasAvailableDevices

    private val _textCheckPermission = MutableLiveData<String>().apply {
        value = "Permission : Unknown"
    }
    val textCheckPermission: LiveData<String> = _textCheckPermission

    private val _textDeviceName = MutableLiveData<String>().apply {
        value = "Device name : Unknown"
    }
    val textDeviceName: LiveData<String> = _textDeviceName

    private val _textDeviceUuid = MutableLiveData<String>().apply {
        value = "Device UUID : Unknown"
    }
    val textDeviceUuid: LiveData<String> = _textDeviceUuid

    private val _textDeviceAvailableStorage = MutableLiveData<String>().apply {
        value = "Available storage : Unknown"
    }
    val textDeviceAvailableStorage: LiveData<String> = _textDeviceAvailableStorage

    private val _textDeviceMonitorPowerStatus = MutableLiveData<String>().apply {
        value = "Power status : Unknown"
    }
    val textDeviceMonitorPowerStatus: LiveData<String> = _textDeviceMonitorPowerStatus

    private val _textDeviceMonitorChargeStatus = MutableLiveData<String>().apply {
        value = "Charge status : Unknown"
    }
    val textDeviceMonitorChargeStatus: LiveData<String> = _textDeviceMonitorChargeStatus

    private val _textDeviceMonitorItemWear = MutableLiveData<String>().apply {
        value = "Item wear : Unknown"
    }
    val textDeviceMonitorItemWear: LiveData<String> = _textDeviceMonitorItemWear

    private val _textDeviceMonitorItemPowerMode = MutableLiveData<String>().apply {
        value = "Item power mode : Unknown"
    }
    val textDeviceMonitorItemPowerMode: LiveData<String> = _textDeviceMonitorItemPowerMode

    private val _textDeviceType = MutableLiveData<String>().apply {
        value = "Device type : Unknown"
    }
    val textDeviceType: LiveData<String> = _textDeviceType

    private val _textIsAppInstalled = MutableLiveData<String>().apply {
        value = "Package name : Unknown"
    }
    val textIsAppInstalled: LiveData<String> = _textIsAppInstalled

    private val _textPing = MutableLiveData<String>().apply {
        value = "Ping : Unknown"
    }
    val textPing: LiveData<String> = _textPing
    
    private val _textLog = MutableLiveData<String>().apply {
        value = ""
    }
    val textLog: LiveData<String> = _textLog

    private var connectedDevice: Device? = null
    private var targetWatchAppPackageName: String? = null
    private var targetWatchAppFingerPrint: String? = null

    // デバイスがあるかどうかを確認する
    fun hasAvailableDevices(context: Context) {
        HiWear.getDeviceClient(context)
            .hasAvailableDevices()
            .addOnSuccessListener { result ->
                // デバイスがある
                if (result) {
                    _textHasAvailableDevices.postValue("Devices : Available")
                } else
                // デバイスがない
                {
                    _textHasAvailableDevices.postValue("Devices : Non available")
                }

                _textLog.value = _textLog.value + "update() addOnSuccessListener() result = $result" + "\n"
                Log.i(LOG_TAG, "update() addOnSuccessListener() result = $result")
            }
            .addOnFailureListener { execption ->
                _textHasAvailableDevices.postValue("Devices : Unknown")

                _textLog.value = _textLog.value + "update() addOnFailureListener() execption = $execption" + "\n"
                Log.e(LOG_TAG, "update() addOnFailureListener() : ", execption)
            }
    }

    // Wear Engineの権限があるかどうかを調べる
    fun checkPermission(context: Context) {
        HiWear.getAuthClient(context)
            .checkPermissions(PERMISSION_ARRAY)
            .addOnSuccessListener { resultArray ->
                var resultText = ""
                if (resultArray.size >= 2) {
                    resultText += "Basic device infomation = " + resultArray[0] + ", "
                    resultText += "Message notification = " + resultArray[1]
                }

                _textCheckPermission.postValue("Permission : $resultText")

                _textLog.value = _textLog.value + "checkPermission() addOnSuccessListener() result = $resultText\n"
                Log.i(LOG_TAG, "checkPermission() addOnSuccessListener() result = $resultText")
            }
            .addOnFailureListener { execption ->
                _textCheckPermission.postValue("Permission : Unknown")

                _textLog.value = _textLog.value + "checkPermission() addOnFailureListener() execption = $execption" + "\n"
                Log.e(LOG_TAG, "checkPermission() addOnFailureListener() : ", execption)
            }
    }

    // P2P通信ができるデバイスを取得する
    fun getConnectedDevice(context: Context) {
        HiWear.getDeviceClient(context)
            .bondedDevices
            .addOnSuccessListener { deviceList ->
                _textLog.value = _textLog.value + "getConnectedDevice() addOnSuccessListener() deviceList = " + deviceList.joinToString{it.name} + "\n"
                Log.i(LOG_TAG, "getConnectedDevice() addOnSuccessListener() deviceList = " + deviceList.joinToString{it.name})

                // 対象デバイスに対して、さらに接続中のデバイスを限定する
                // ※このサンプルでは、対象デバイスに接続中のデバイスの数が一つまでと仮定する
                deviceList.forEach { device ->
                    if (device.isConnected) {
                        connectedDevice = device
                        _textDeviceName.postValue("Device name : " + device.name)
                        _textDeviceUuid.postValue("Device UUID : " + device.uuid)

                        _textLog.value = _textLog.value + "getConnectedDevice() addOnSuccessListener() connected device = " + device.name + ", UUID = " + device.uuid + "\n"
                        Log.i(LOG_TAG, "getConnectedDevice() addOnSuccessListener() connected device = " + device.name + ", UUID = " + device.uuid)

                        // デバイスの空き容量（KB）を調べる
                        getAvailableKbytes(context)
                        // デバイスのバッテリー状態を調べる
                        queryMonitorPowerStatus(context)

                        // リスナーを登録する
                        registerMonitor(context, MONITOR_ITEM_LIST)
                    }
                }
            }
            .addOnFailureListener { execption ->
                _textDeviceName.postValue("Device name : Unknown")
                _textDeviceUuid.postValue("Device UUID : Unknown")

                _textLog.value = _textLog.value + "getConnectedDevice() addOnFailureListener() execption = $execption" + "\n"
                Log.e(LOG_TAG, "getConnectedDevice() addOnFailureListener() : ", execption)
            }
    }

    // デバイスの空き容量（KB）を調べる
    private fun getAvailableKbytes(context: Context) {
        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                _textDeviceAvailableStorage.postValue("Available storage : Unknown")

                _textLog.value = _textLog.value + "getAvailableKbytes() connectedDevice.isConnected = $connectedDevice.isConnected\n"
                Log.i(LOG_TAG, "getAvailableKbytes() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                HiWear.getDeviceClient(context)
                    .getAvailableKbytes(connectedDevice)
                    .addOnSuccessListener { kb ->
                        _textDeviceAvailableStorage.postValue("Available storage : $kb KB = ${kb / 1024} MB = ${kb / 1024 / 1024} GB")

                        _textLog.value = _textLog.value + "getAvailableKbytes() addOnSuccessListener() Available KB = $kb\n"
                        Log.i(LOG_TAG, "getAvailableKbytes() addOnSuccessListener() Available KB = $kb")

                        checkApp(context)
                    }
                    .addOnFailureListener { execption ->
                        _textDeviceAvailableStorage.postValue("Available storage : Unknown")

                        _textLog.value = _textLog.value + "getAvailableKbytes() addOnFailureListener() execption = $execption" + "\n"
                        Log.e(LOG_TAG, "getAvailableKbytes() addOnFailureListener() : ", execption)
                    }
            }
        } ?: let {
            _textDeviceAvailableStorage.postValue("Available storage : Unknown")

            _textLog.value = _textLog.value + "getAvailableKbytes() connectedDevice is null\n"
            Log.i(LOG_TAG, "getAvailableKbytes() connectedDevice is null")
        }
    }

    // デバイスのバッテリー状態を調べる
    private fun queryMonitorPowerStatus(context: Context) {
        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                _textDeviceMonitorPowerStatus.postValue("Power status : Unknown")

                _textLog.value = _textLog.value + "queryMonitorPowerStatus() connectedDevice.isConnected = $connectedDevice.isConnected\n"
                Log.i(LOG_TAG, "queryMonitorPowerStatus() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                HiWear.getMonitorClient(context)
                    .query(connectedDevice, MonitorItem.MONITOR_POWER_STATUS)
                    .addOnSuccessListener { monitorData ->
                        _textDeviceMonitorPowerStatus.postValue("Power status : ${monitorData.asInt()}")

                        _textLog.value = _textLog.value + "queryMonitorPowerStatus() addOnSuccessListener() Power status = ${monitorData.asInt()}\n"
                        Log.i(LOG_TAG, "queryMonitorPowerStatus() addOnSuccessListener() Power status = ${monitorData.asInt()}")

                        // さらに、デバイスの充電状態を調べる
                        queryMonitorChargeStatus(context)
                    }
                    .addOnFailureListener { execption ->
                        _textDeviceMonitorPowerStatus.postValue("Power status : Unknown")

                        _textLog.value = _textLog.value + "queryMonitorPowerStatus() addOnFailureListener() execption = $execption" + "\n"
                        Log.e(LOG_TAG, "queryMonitorPowerStatus() addOnFailureListener() : ", execption)

                        // さらに、デバイスの充電状態を調べる
                        queryMonitorChargeStatus(context)
                    }
            }
        } ?: let {
            _textDeviceMonitorPowerStatus.postValue("Power status : Unknown")

            _textLog.value = _textLog.value + "queryMonitorPowerStatus() connectedDevice is null\n"
            Log.i(LOG_TAG, "queryMonitorPowerStatus() connectedDevice is null")
        }
    }

    // デバイスの充電状態を調べる
    private fun queryMonitorChargeStatus(context: Context) {
        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                _textDeviceMonitorChargeStatus.postValue("Charge status : Unknown")

                _textLog.value = _textLog.value + "queryMonitorChargeStatus() connectedDevice.isConnected = $connectedDevice.isConnected\n"
                Log.i(LOG_TAG, "queryMonitorChargeStatus() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                HiWear.getMonitorClient(context)
                    .query(connectedDevice, MonitorItem.MONITOR_CHARGE_STATUS)
                    .addOnSuccessListener { monitorData ->
                        val result = MONITOR_CHARGE_STATUS_DETAIL[monitorData.asInt()]
                        result?.let { let ->
                            _textDeviceMonitorChargeStatus.postValue("Charge status : $result")
                        } ?: let {
                            _textDeviceMonitorChargeStatus.postValue("Charge status : Unknown")
                        }

                        _textLog.value = _textLog.value + "queryMonitorChargeStatus() addOnSuccessListener() Power status = ${monitorData.asInt()}\n"
                        Log.i(LOG_TAG, "queryMonitorChargeStatus() addOnSuccessListener() Power status = ${monitorData.asInt()}")
                    }
                    .addOnFailureListener { execption ->
                        _textDeviceMonitorChargeStatus.postValue("Charge status : Unknown")

                        _textLog.value = _textLog.value + "queryMonitorChargeStatus() addOnFailureListener() execption = $execption" + "\n"
                        Log.e(LOG_TAG, "queryMonitorChargeStatus() addOnFailureListener() : ", execption)
                    }
            }
        } ?: let {
            _textDeviceMonitorChargeStatus.postValue("Charge status : Unknown")

            _textLog.value = _textLog.value + "queryMonitorChargeStatus() connectedDevice is null\n"
            Log.i(LOG_TAG, "queryMonitorChargeStatus() connectedDevice is null")
        }
    }

    // デバイスの状態を監視するリスナー
    val monitorListener = MonitorListener { errorCode, monitorItem, monitorData ->
        when (monitorItem.name) {
            // バッテリー状態
            MonitorItem.MONITOR_ITEM_LOW_POWER.name -> {
                _textDeviceMonitorPowerStatus.postValue("Power status : ${monitorData.asInt()}")
            }
            // 充電状態
            MonitorItem.MONITOR_CHARGE_STATUS.name -> {
                val result = MONITOR_CHARGE_STATUS_DETAIL[monitorData.asInt()]
                result?.let { let ->
                    _textDeviceMonitorChargeStatus.postValue("Charge status : $result")
                } ?: let {
                    _textDeviceMonitorChargeStatus.postValue("Charge status : Unknown")
                }
            }
            else -> {

            }
        }
    }

    // リスナーを登録する
    private fun registerMonitor(context: Context, monitorItemArray: List<MonitorItem>) {
        unregisterMonitor()

        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                _textLog.value = _textLog.value + "registerMonitor() connectedDevice.isConnected = $connectedDevice.isConnected\n"
                Log.i(LOG_TAG, "registerMonitor() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                HiWear.getMonitorClient(context)
                    .register(connectedDevice, monitorItemArray, monitorListener)
                    .addOnSuccessListener {
                        _textLog.value = _textLog.value + "registerMonitor() addOnSuccessListener()\n"
                        Log.i(LOG_TAG, "registerMonitor() addOnSuccessListener()")
                    }
                    .addOnFailureListener { execption ->
                        _textLog.value = _textLog.value + "registerMonitor() addOnFailureListener() execption = $execption" + "\n"
                        Log.e(LOG_TAG, "registerMonitor() addOnFailureListener() : ", execption)
                    }
            }
        } ?: let {
            _textLog.value = _textLog.value + "registerMonitor() connectedDevice is null\n"
            Log.i(LOG_TAG, "registerMonitor() connectedDevice is null")
        }
    }

    // リスナーを解除する
    fun unregisterMonitor() {
        _textLog.value = _textLog.value + "unregisterMonitor()\n"
        Log.i(LOG_TAG, "unregisterMonitor()")
    }

    // デバイスに対象アプリがインストール済みか調べる
    private fun checkApp(context: Context) {
        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                _textLog.value = _textLog.value + "checkApp() connectedDevice.isConnected = $connectedDevice.isConnected\n"
                Log.i(LOG_TAG, "checkApp() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                // まず、デバイスにWearableのアプリがインストール済みか調べる
                HiWear.getP2pClient(context)
                    .isAppInstalled(connectedDevice, Constant.WEARABLE_APP_PACKAGE_NAME)
                    .addOnSuccessListener { isAppInstalled ->
                        // インストール済みの場合
                        if (isAppInstalled) {
                            _textDeviceType.postValue("Device type : Wearable (Smart watch)")

                            _textLog.value = _textLog.value + "checkApp() addOnSuccessListener() Wearable (Smart watch)\n"
                            Log.i(LOG_TAG, "checkApp() addOnSuccessListener() Wearable (Smart watch)")

                            _textIsAppInstalled.postValue(Constant.WEARABLE_APP_PACKAGE_NAME + " : Installed")

                            _textLog.value = _textLog.value + "checkApp() addOnSuccessListener() ${Constant.WEARABLE_APP_PACKAGE_NAME} = $isAppInstalled\n"
                            Log.i(LOG_TAG, "checkApp() addOnSuccessListener() ${Constant.WEARABLE_APP_PACKAGE_NAME} = $isAppInstalled")

                            // デバイスがWearableであるとする
                            // Wearable側のアプリのパッケージ名とフィンガープリントを使う
                            targetWatchAppPackageName = Constant.WEARABLE_APP_PACKAGE_NAME
                            targetWatchAppFingerPrint = Constant.WEARABLE_APP_FINGER_PRINT

                            // デバイスとの通信を試みる（Pingをしてみる）
                            targetWatchAppPackageName?.let { packageName ->
                                ping(context, packageName)
                            }
                        } else {
                            // デバイスにLite Wearableのアプリがインストール済みか調べる
                            HiWear.getP2pClient(context)
                                .isAppInstalled(connectedDevice, Constant.LITE_WEARABLE_APP_PACKAGE_NAME)
                                .addOnSuccessListener { isAppInstalled ->
                                    // インストール済みの場合
                                    if (isAppInstalled) {
                                        _textDeviceType.postValue("Device type : Lite wearable (Sport watch)")

                                        _textLog.value = _textLog.value + "checkApp() addOnSuccessListener() Lite wearable (Sport watch)\n"
                                        Log.i(LOG_TAG, "checkApp() addOnSuccessListener() Lite wearable (Sport watch)")

                                        _textIsAppInstalled.postValue(Constant.LITE_WEARABLE_APP_PACKAGE_NAME + " : Installed")

                                        _textLog.value = _textLog.value + "checkApp() addOnSuccessListener() ${Constant.LITE_WEARABLE_APP_PACKAGE_NAME} = $isAppInstalled\n"
                                        Log.i(LOG_TAG, "checkApp() addOnSuccessListener() ${Constant.LITE_WEARABLE_APP_PACKAGE_NAME} = $isAppInstalled")

                                        // デバイスがLite Wearableであるとする
                                        // Lite Wearable側のアプリのパッケージ名とフィンガープリントを使う
                                        targetWatchAppPackageName = Constant.LITE_WEARABLE_APP_PACKAGE_NAME
                                        targetWatchAppFingerPrint = Constant.LITE_WEARABLE_APP_FINGER_PRINT

                                        // デバイスとの通信を試みる（Pingをしてみる）
                                        targetWatchAppPackageName?.let { packageName ->
                                            ping(context, packageName)
                                        }
                                    } else
                                    // デバイスがWearableかLite Wearableか特定できない
                                    {
                                        _textDeviceType.postValue("Device type : Unknown")
                                        _textIsAppInstalled.postValue(Constant.WEARABLE_APP_PACKAGE_NAME + " & " + Constant.LITE_WEARABLE_APP_PACKAGE_NAME + " : Not exist")
                                    }
                                }
                                .addOnFailureListener { execption ->
                                    _textDeviceType.postValue("Device type : Unknown")
                                    _textIsAppInstalled.postValue(Constant.WEARABLE_APP_PACKAGE_NAME + " & " + Constant.LITE_WEARABLE_APP_PACKAGE_NAME + " : Not exist")

                                    _textLog.value = _textLog.value + "checkApp() addOnFailureListener() execption = $execption" + "\n"
                                    Log.e(LOG_TAG, "checkApp() addOnFailureListener() : ", execption)
                                }
                        }
                    }
                    .addOnFailureListener { execption ->
                        _textDeviceType.postValue("Device type : Unknown")
                        _textIsAppInstalled.postValue(Constant.WEARABLE_APP_PACKAGE_NAME + " & " + Constant.LITE_WEARABLE_APP_PACKAGE_NAME + " : Not exist")

                        _textLog.value = _textLog.value + "checkApp() addOnFailureListener() execption = $execption" + "\n"
                        Log.e(LOG_TAG, "checkApp() addOnFailureListener() : ", execption)
                    }
            }
        } ?: let {
            _textDeviceType.postValue("Device type : Unknown")
            _textIsAppInstalled.postValue(Constant.WEARABLE_APP_PACKAGE_NAME + " & " + Constant.LITE_WEARABLE_APP_PACKAGE_NAME + " : Not exist")

            Log.i(LOG_TAG, "checkApp() connectedDevice is null")
        }
    }

    // デバイスとの通信を試みる（Pingをしてみる）
    private fun ping(context: Context, packageName: String) {
        connectedDevice?.let { connectedDevice ->
            if (!connectedDevice.isConnected) {
                _textLog.value = _textLog.value + "ping() connectedDevice.isConnected = $connectedDevice.isConnected\n"
                Log.i(LOG_TAG, "ping() connectedDevice.isConnected = $connectedDevice.isConnected")
            } else {
                HiWear.getP2pClient(context)
                    .setPeerPkgName(packageName)
                    .ping(connectedDevice, object : PingCallback {
                        override fun onPingResult(errCode: Int) {
                            _textPing.postValue("Ping : $errCode - ${WearEngineErrorCode.getErrorMsgFromCode(errCode)}")
                        }
                    })
                    .addOnSuccessListener {
                        _textLog.value = _textLog.value + "ping() addOnSuccessListener()\n"
                        Log.i(LOG_TAG, "ping() addOnSuccessListener()")
                    }
                    .addOnFailureListener { execption ->
                        _textPing.postValue("Ping : Unknown")

                        _textLog.value = _textLog.value + "ping() addOnFailureListener() execption = $execption" + "\n"
                        Log.e(LOG_TAG, "ping() addOnFailureListener() : ", execption)
                    }
            }
        } ?: let {
            _textPing.postValue("Ping : Unknown")

            _textLog.value = _textLog.value + "ping() connectedDevice is null\n"
            Log.i(LOG_TAG, "ping() connectedDevice is null")
        }
    }
}