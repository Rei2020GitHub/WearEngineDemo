package com.huawei.sample.wearable.demo.ui.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ActionViewModel : ViewModel() {

    companion object {
        private val LOG_TAG = ActionViewModel::class.java.simpleName
    }

    private val _textLog = MutableLiveData<String>().apply {
        value = ""
    }
    val textLog: LiveData<String> = _textLog

    fun addLog(text: String) {
        textLog.value?.let {
            if (it.isEmpty()) {
                _textLog.postValue(text)
            } else {
                _textLog.postValue(textLog.value + "\n" + text)
            }
        }
    }
}