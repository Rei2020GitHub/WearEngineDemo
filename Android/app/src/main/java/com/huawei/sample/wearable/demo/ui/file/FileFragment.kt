package com.huawei.sample.wearable.demo.ui.file

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huawei.sample.wearable.demo.R
import com.huawei.sample.wearable.demo.databinding.FragmentFileBinding
import com.huawei.sample.wearable.demo.model.UriUtil
import com.huawei.sample.wearable.demo.service.MainService
import org.json.JSONObject
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files


class FileFragment : Fragment() {

    companion object {
        private val LOG_TAG = FileFragment::class.java.simpleName

        private val PERMISSION_LIST: List<String> = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
    }

    private var _binding: FragmentFileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var messenger: Messenger? = null

    private lateinit var fileViewModel: FileViewModel

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messenger = Messenger(service)
            Log.i(LOG_TAG, "onServiceConnected() name = ${name?.shortClassName}")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            messenger = null
            Log.i(LOG_TAG, "onServiceDisconnected() name = ${name?.shortClassName}")
        }
    }

    inner class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            Log.i(LOG_TAG, "handleMessage() message.what = " + message.what)

            when (message.what) {
                MainService.MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_PROGRESS,
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            fileViewModel.addText("Send progress: " + jsonObject.getString(MainService.MESSAGE_DATA_KEY))
                        }
                    }
                MainService.MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_SUCCESS,
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            fileViewModel.addText("Send success: " + jsonObject.getString(MainService.MESSAGE_DATA_KEY))
                        }
                        showSendButton()
                    }
                MainService.MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL,
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            fileViewModel.addText("Send fail: " + jsonObject.getString(MainService.MESSAGE_DATA_KEY))
                        }
                        showSendButton()
                    }
                MainService.MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_SUCCESS
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            fileViewModel.addText("Stop send success: " + jsonObject.getString(MainService.MESSAGE_DATA_KEY))
                        }
                        showSendButton()
                    }
                MainService.MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_FAIL,
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            fileViewModel.addText("Stop send fail: " + jsonObject.getString(MainService.MESSAGE_DATA_KEY))
                        }
                        showSendButton()
                    }
                MainService.MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH_RESULT
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            fileViewModel.addText("Stop send result: " + jsonObject.getString(MainService.MESSAGE_DATA_KEY))
                        }
                        showSendButton()
                    }
                MainService.MESSAGE_SEND_FILE_FROM_WATCH_TO_PHONE,
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            val path = jsonObject.getString(MainService.MESSAGE_DATA_KEY)
                            fileViewModel.addText("Receive file: " + path)
                            showImage(path)
                        }
                    }
                else
                    -> {
                        Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}")
                        super.handleMessage(message)
                    }
            }
        }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Activity.RESULT_OK == result.resultCode) {
            result.data?.let { data ->
                val path = UriUtil.getPath(requireContext(), data.data as Uri)
                path?.let { path ->
                    if (path.indexOf("storage") >= 0) {
                        fileViewModel.setSelectPath(path)
                        fileViewModel.addText(path)

                        val inputData: TextView = binding.inputData
                        inputData.text = path
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.choose_from_storage), Toast.LENGTH_SHORT).show()
                    }
                } ?:let {
                    Toast.makeText(requireContext(), getString(R.string.choose_from_storage), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

    }

    private lateinit var fileFragmentMessenger: Messenger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fileFragmentMessenger = Messenger(IncomingHandler(requireContext()))

        fileViewModel = ViewModelProvider(this).get(FileViewModel::class.java)

        _binding = FragmentFileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val buttonSelectFile: Button = binding.buttonSelectFile
        buttonSelectFile.setOnClickListener {
            selectImageFile()
        }

        val textData: TextView = binding.textData
        fileViewModel.textData.observe(viewLifecycleOwner) {
            textData.text = it
        }

        val buttonSendFile: Button = binding.buttonSendFile
        buttonSendFile.setOnClickListener {
            sendFile(fileViewModel.selectPath.value)
        }

        val buttonStopSend: Button = binding.buttonStopSend
        buttonStopSend.setOnClickListener {
            stopSend(fileViewModel.selectPath.value)
        }

        showSendButton()
        connectService()

        return root
    }

    override fun onStart() {
        super.onStart()
        checkAndAskPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun connectService() {
        Log.i(LOG_TAG, "connectService() messenger = $messenger")
        if (null == messenger) {
            val intent = Intent(requireContext(), MainService::class.java)
                .apply {
                    putExtra(FileFragment::class.java.simpleName, fileFragmentMessenger)
                }

            requireActivity().applicationContext.startService(intent)
            Log.i(LOG_TAG, "connectService() startService")

            requireActivity().applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            Log.i(LOG_TAG, "connectService() bindService Context.BIND_AUTO_CREATE, extra = " + FileFragment::class.java.simpleName)
        }
    }

    private fun disconnectService() {
        messenger?.let { messenger ->
            requireActivity().unbindService(serviceConnection)
        }
    }

    private fun checkAndAskPermission() {
        context?.let { context ->
            val permissionList: MutableList<String> = mutableListOf()

            PERMISSION_LIST.forEach { permission ->
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission)
                }
            }

            if (!permissionList.isNullOrEmpty()) {
                requestPermissionLauncher.launch(permissionList.toTypedArray())
            }
        }
    }

    private fun selectImageFile() {
        val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
        }
        activityResultLauncher.launch(intent)
    }

    private fun sendFile(path: String) {
        messenger?.let { messenger ->
            val message = Message.obtain(null, MainService.MESSAGE_SEND_FILE_FROM_PHONE_TO_WATCH_START)
                .apply {
                    data = Bundle().apply {
                        putString(MainService.KEY_DATA, path)
                        fileViewModel.addText("Start send: " + path)
                    }
                }
            try {
                messenger.send(message)
                showStopButton()
            } catch (remoteException: RemoteException) {
                Log.e(LOG_TAG, "sendFile() exception : ", remoteException)
            }
        }
    }

    private fun stopSend(path: String) {
        messenger?.let { messenger ->
            val message = Message.obtain(null, MainService.MESSAGE_STOP_SEND_FILE_FROM_PHONE_TO_WATCH)
                .apply {
                    data = Bundle().apply {
                        putString(MainService.KEY_DATA, path)
                        fileViewModel.addText("Start stop send: " + path)
                    }
                }
            try {
                messenger.send(message)
                showSendButton()
            } catch (remoteException: RemoteException) {
                Log.e(LOG_TAG, "stopSend() exception : ", remoteException)
            }
        }
    }

    private fun showSendButton() {
        val buttonSendFile: Button = binding.buttonSendFile
        val buttonStopSend: Button = binding.buttonStopSend
        buttonSendFile.visibility = View.VISIBLE
        buttonStopSend.visibility = View.GONE
    }

    private fun showStopButton() {
        val buttonSendFile: Button = binding.buttonSendFile
        val buttonStopSend: Button = binding.buttonStopSend
        buttonStopSend.visibility = View.VISIBLE
        buttonSendFile.visibility = View.GONE
    }

    private fun showImage(path: String) {
        val imageData: ImageView = binding.imageData
        imageData.setImageURI(Uri.parse(path))
    }
}