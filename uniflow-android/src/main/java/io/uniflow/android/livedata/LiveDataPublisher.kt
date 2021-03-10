package io.uniflow.android.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import io.uniflow.core.flow.DataPublisher
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain

open class LiveDataPublisher(
    defaultState: UIState,
    private val tag: String
) : DataPublisher {

    internal val _states = MutableLiveData<UIState>()
    val states: LiveData<UIState> = _states
    internal val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>> = _events

    init {
        defaultState(defaultState)
    }

    fun defaultState(defaultState: UIState) {
        _states.value = defaultState
    }

    override suspend fun getState(): UIState = _states.value ?: error("No state in LiveData")
    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
        onMain(immediate = true) {
            UniFlowLogger.debug("$tag --> $state")
            _states.value = state
        }
    }

    override suspend fun publishEvent(event: UIEvent) {
        onMain(immediate = true) {
            UniFlowLogger.debug("$tag --> $event")
            _events.value = Event(content = event)
        }
    }
}
fun liveDataPublisher(defaultState: UIState, tag: String) = LiveDataPublisher(defaultState, tag)