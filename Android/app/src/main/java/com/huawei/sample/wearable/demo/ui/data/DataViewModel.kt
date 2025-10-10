package com.huawei.sample.wearable.demo.ui.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class DataViewModel : ViewModel() {

    companion object {
        private val LOG_TAG = DataViewModel::class.java.simpleName
    }

    private val _textData = MutableLiveData<String>().apply {
        value = ""
    }
    val textData: LiveData<String> = _textData

    fun addText(text: String) {
        if (textData.value.isEmpty()) {
            _textData.postValue(text)
        } else {
            _textData.postValue(textData.value + "\n" + text)
        }
    }
}