package mx.utng.latp.smarthealthmonitort.mqtt

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttTvSubscriber(
    private val tvFlow  : MutableStateFlow<TvMessage?>   // actualiza el ViewModel
) {
    private var client: MqttAsyncClient? = null

    fun connect() {
        client = MqttAsyncClient(
            MqttConfig.BROKER_URL,
            MqttConfig.CLIENT_TV, 
            MemoryPersistence()
        )

        client?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String, msg: MqttMessage) {
                if (topic == MqttConfig.TOPIC_TV) {
                    val tvMsg = Json.decodeFromString<TvMessage>(String(msg.payload))
                    tvFlow.value = tvMsg
                    android.util.Log.d("MQTT_TV","📺 Recibido: ${tvMsg.bpm} bpm")
                }
            }
            override fun connectionLost(cause: Throwable?) {}
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        val options = MqttConnectOptions().apply {
            userName = MqttConfig.USERNAME
            password = MqttConfig.PASSWORD.toCharArray()
            isCleanSession = true
            socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
        }

        client?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                client?.subscribe(MqttConfig.TOPIC_TV, MqttConfig.QOS)
                android.util.Log.d("MQTT_TV","✅ TV suscrita a ${MqttConfig.TOPIC_TV}")
            }
            override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                android.util.Log.e("MQTT_TV","❌ Error: ${ex?.message}")
            }
        })
    }
    fun disconnect() { client?.disconnect() }
}
