package com.huawei.sample.wearable.demo.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huawei.sample.wearable.demo.databinding.FragmentDeviceBinding

class DeviceFragment : Fragment() {

    private var _binding: FragmentDeviceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val deviceViewModel =
            ViewModelProvider(this).get(DeviceViewModel::class.java)

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textHasAvailableDevices: TextView = binding.textHasAvailableDevices
        deviceViewModel.textHasAvailableDevices.observe(viewLifecycleOwner) {
            textHasAvailableDevices.text = it
        }

        val textCheckPermission: TextView = binding.textCheckPermission
        deviceViewModel.textCheckPermission.observe(viewLifecycleOwner) {
            textCheckPermission.text = it
        }

        val textDeviceName: TextView = binding.textDeviceName
        deviceViewModel.textDeviceName.observe(viewLifecycleOwner) {
            textDeviceName.text = it
        }

        val textDeviceUuid: TextView = binding.textDeviceUuid
        deviceViewModel.textDeviceUuid.observe(viewLifecycleOwner) {
            textDeviceUuid.text = it
        }

        val textDeviceAvailableStorage: TextView = binding.textDeviceAvailableStorage
        deviceViewModel.textDeviceAvailableStorage.observe(viewLifecycleOwner) {
            textDeviceAvailableStorage.text = it
        }

        val textDeviceMonitorPowerStatus: TextView = binding.textDeviceMonitorPowerStatus
        deviceViewModel.textDeviceMonitorPowerStatus.observe(viewLifecycleOwner) {
            textDeviceMonitorPowerStatus.text = it
        }

        val textDeviceMonitorChargeStatus: TextView = binding.textDeviceMonitorChargeStatus
        deviceViewModel.textDeviceMonitorChargeStatus.observe(viewLifecycleOwner) {
            textDeviceMonitorChargeStatus.text = it
        }

        val textDeviceType: TextView = binding.textDeviceType
        deviceViewModel.textDeviceType.observe(viewLifecycleOwner) {
            textDeviceType.text = it
        }

        val textIsAppInstalled: TextView = binding.textIsAppInstalled
        deviceViewModel.textIsAppInstalled.observe(viewLifecycleOwner) {
            textIsAppInstalled.text = it
        }

        val textPing: TextView = binding.textPing
        deviceViewModel.textPing.observe(viewLifecycleOwner) {
            textPing.text = it
        }


        val textLog: TextView = binding.textLog
        deviceViewModel.textLog.observe(viewLifecycleOwner) {
            textLog.text = it
        }

        val buttonUpdate: Button = binding.buttonCheckStatus
        buttonUpdate.setOnClickListener {
            deviceViewModel.hasAvailableDevices(requireContext())
            deviceViewModel.checkPermission(requireContext())
            deviceViewModel.getConnectedDevice(requireContext())
        }

        return root
    }

    override fun onPause() {
        super.onPause()

        val deviceViewModel =
            ViewModelProvider(this).get(DeviceViewModel::class.java)

        deviceViewModel.unregisterMonitor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}