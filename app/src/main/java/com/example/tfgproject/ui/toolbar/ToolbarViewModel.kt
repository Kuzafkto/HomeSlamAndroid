package com.example.tfgproject.ui.toolbar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for managing the toolbar title.
 */
class ToolbarViewModel : ViewModel() {
    private val _title = MutableStateFlow("Initial Title")
    val title: StateFlow<String> = _title

    /**
     * Sets the title for the toolbar.
     *
     * @param title The new title to be displayed.
     */
    fun setTitle(title: String) {
        _title.value = title
    }
}
