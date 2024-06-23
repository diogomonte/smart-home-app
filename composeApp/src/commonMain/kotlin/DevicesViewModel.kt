import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.prepareGet
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class DeviceUiState(val devices: List<Device> = emptyList())

data class DeviceLiveData(val data: String = "")

class DevicesViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(DeviceUiState())
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()

    private val _deviceLiveData = MutableStateFlow(DeviceLiveData())
    val deviceLiveData: StateFlow<DeviceLiveData> = _deviceLiveData.asStateFlow()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private val cioHttpClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        }
    }

    init {
        updateDevices()
    }

    suspend fun listDevices(): List<Device> {
        return httpClient.get("http://raspberrypi:8090/devices").body()
    }

    suspend fun liveData(deviceId: String) {
        cioHttpClient.prepareGet (
            urlString = "http://raspberrypi:8090/devices/$deviceId/events",
            block = {
                headers {
                    append(HttpHeaders.Connection, "keep-alive")
                    append(HttpHeaders.ContentType, "application/stream+json")
                }
            }
        ).execute { httpResponse ->
            var i = 0;
            val channel: ByteReadChannel = httpResponse.body()
            val sb = StringBuilder()
            while (!channel.isClosedForRead) {
                sb.append(channel.readUTF8Line() ?: "")
                _deviceLiveData.update {
                    println(sb.toString())
                    it.copy(data = sb.toString())
                }
            }
        }
    }

    fun deviceLiveData(deviceId: String) {
        viewModelScope.launch {
            liveData(deviceId)
        }
    }

    fun updateDevices() {
        viewModelScope.launch {
            val devices = listDevices()
            _uiState.update {
                it.copy(devices = devices)
            }
        }
    }
}