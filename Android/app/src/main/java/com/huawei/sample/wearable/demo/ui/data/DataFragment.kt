package com.huawei.sample.wearable.demo.ui.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huawei.sample.wearable.demo.databinding.FragmentDataBinding
import com.huawei.sample.wearable.demo.service.MainService
import org.json.JSONObject

class DataFragment : Fragment() {

    companion object {
        private val LOG_TAG = DataFragment::class.java.simpleName
    }

    private var _binding: FragmentDataBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var messenger: Messenger? = null

    private lateinit var dataViewModel: DataViewModel

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
                MainService.MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH_SUCCESS,
                MainService.MESSAGE_SEND_TEXT_FROM_WATCH_TO_PHONE
                    -> {
                        val text = message.data.getString(MainService.KEY_DATA)
                        text?.let { text ->
                            Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}, text = $text")

                            val jsonObject = JSONObject(text)
                            dataViewModel.addText(jsonObject.getString(MainService.MESSAGE_DATA_KEY))
                        }
                    }
                MainService.MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH_FAIL
                    -> {
                        Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}")
                        dataViewModel.addText("Send text fail")
                    }
                else
                    -> {
                        Log.i(LOG_TAG, "handleMessage() message.what = ${message.what}")
                        super.handleMessage(message)
                    }
            }
        }
    }

    private lateinit var dataFragmentMessenger: Messenger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataFragmentMessenger = Messenger(IncomingHandler(requireContext()))

        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        _binding = FragmentDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textData: TextView = binding.textData
        dataViewModel.textData.observe(viewLifecycleOwner) {
            textData.text = it
        }

        val buttonSendText: Button = binding.buttonSendText
        buttonSendText.setOnClickListener {
            val inputData: EditText = binding.inputData
            if (inputData.text.toString().trim().isNotEmpty()) {
                sendText(inputData.text.toString().trim())
                inputData.setText("")
            }
        }

        connectService()

        return root
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
                    putExtra(DataFragment::class.java.simpleName, dataFragmentMessenger)
                }

            requireActivity().applicationContext.startService(intent)
            Log.i(LOG_TAG, "connectService() startService")

            requireActivity().applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            Log.i(LOG_TAG, "connectService() bindService Context.BIND_AUTO_CREATE, extra = " + DataFragment::class.java.simpleName)
        }
    }

    private fun disconnectService() {
        messenger?.let { messenger ->
            requireActivity().unbindService(serviceConnection)
        }
    }

    private fun sendText(text: String) {
        messenger?.let { messenger ->
            val message = Message.obtain(null, MainService.MESSAGE_SEND_TEXT_FROM_PHONE_TO_WATCH)
                .apply {
                    data = Bundle().apply {
                        putString(MainService.KEY_DATA, text)
                    }
                }
            try {
                messenger.send(message)
            } catch (remoteException: RemoteException) {
                Log.e(LOG_TAG, "sendText() exception : ", remoteException)
            }
        }
    }
}