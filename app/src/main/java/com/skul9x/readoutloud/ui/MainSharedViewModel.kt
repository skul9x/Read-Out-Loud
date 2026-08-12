package com.skul9x.readoutloud.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainSharedViewModel : ViewModel() {
    private val _summarizeEvent = MutableLiveData<String?>()
    val summarizeEvent: LiveData<String?> = _summarizeEvent

    fun requestSummarize(text: String) {
        _summarizeEvent.value = text
    }

    fun clearSummarizeEvent() {
        _summarizeEvent.value = null
    }
}
