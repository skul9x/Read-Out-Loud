package com.skul9x.readoutloud.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainSharedViewModel : ViewModel() {
    private val _summarizeEvent = MutableLiveData<String?>()
    val summarizeEvent: LiveData<String?> = _summarizeEvent

    private val _readAloudEvent = MutableLiveData<String?>()
    val readAloudEvent: LiveData<String?> = _readAloudEvent

    fun requestSummarize(text: String) {
        _summarizeEvent.value = text
    }

    fun clearSummarizeEvent() {
        _summarizeEvent.value = null
    }

    fun requestReadAloud(text: String) {
        _readAloudEvent.value = text
    }

    fun clearReadAloudEvent() {
        _readAloudEvent.value = null
    }
}
