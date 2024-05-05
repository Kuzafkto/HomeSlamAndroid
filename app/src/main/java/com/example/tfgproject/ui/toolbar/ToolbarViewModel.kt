package com.example.tfgproject.ui.toolbar
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ToolbarViewModel : ViewModel() {
    private val _title = MutableStateFlow("Titulo Inicial")
    val title: StateFlow<String> = _title

    fun setTitle(title: String) {
        _title.value = title
    }
}
