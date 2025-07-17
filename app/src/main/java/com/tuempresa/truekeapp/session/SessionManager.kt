package com.tuempresa.truekeapp.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SessionManager {
    private val _sessionExpired = MutableSharedFlow<Unit>(replay = 0)
    val sessionExpired = _sessionExpired.asSharedFlow()

    suspend fun emitSessionExpired() {
        _sessionExpired.emit(Unit)
    }
}
