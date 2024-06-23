import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Device(
    @SerialName("Id")
    val id: String = "",
    @SerialName("DeviceId")
    val deviceId: String = "",
    @SerialName("DeviceType")
    val deviceType: String = "",
    @SerialName("Status")
    val status: String = "",
)