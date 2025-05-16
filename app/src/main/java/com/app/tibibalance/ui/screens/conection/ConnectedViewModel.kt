package com.app.tibibalance.ui.screens.conection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.wear.WearConnectionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectedViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        // Al arrancar, comprobamos la conexión
        viewModelScope.launch {
            _isConnected.value = WearConnectionHelper.isWearConnected(context)
        }
    }

    /** Llama de nuevo para refrescar el estado */
    fun refresh() {
        viewModelScope.launch {
            _isConnected.value = WearConnectionHelper.isWearConnected(context)
        }
    }
}