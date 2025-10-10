package com.huawei.sample.wearable.demo.ui.file

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FileViewModel : ViewModel() {
    companion object {
        private val LOG_TAG = FileViewModel::class.java.simpleName
    }

    private val _selectPath = MutableLiveData<String>().apply {
        value = ""
    }
    val selectPath: LiveData<String> = _selectPath

    private val _textData = MutableLiveData<String>().apply {
        value = ""
    }
    val textData: LiveData<String> = _textData

    fun setSelectPath(path: String) {
        _selectPath.postValue(path)
    }

    fun setText(text: String) {
        _textData.postValue(text)
    }

    fun addText(text: String) {
        if (textData.value.isEmpty()) {
            _textData.postValue(text)
        } else {
            _textData.postValue(textData.value + "\n" + text)
        }
    }
}