package com.example.tfgproject.ui.toolbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToolbarViewModel : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    fun setTitle(title: String) {
        _title.value = title
    }

    // Aquí puedes añadir más lógica relacionada con la Toolbar si es necesario.
}
