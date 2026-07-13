package mx.utng.latp.smarthealthmonitor.mqtt

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MqttAppService(
    private val context  : Context,
    private val fcFlow   : MutableStateFlow<Int>   // actualiza el Repository
) {
    private var client: MqttAsyncClient? = null

    fun connect() {
        client = MqttAsyncClient(
            MqttConfig.BROKER_URL,
            MqttConfig.CLIENT_APP, 
            MemoryPersistence()
        )

        val options = MqttConnectOptions().apply {
            userName = MqttConfig.USERNAME
            password = MqttConfig.PASSWORD.toCharArray()
            isCleanSession = true
            socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
        }

        // Callback de mensajes entrantes
        client?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String, msg: MqttMessage) {
                when (topic) {
                    MqttConfig.TOPIC_FC -> handleFcMessage(msg)
                }
            }
            override fun connectionLost(cause: Throwable?) {
                android.util.Log.w("MQTT_APP","Conexión perdida: ${cause?.message}")
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        client?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                // Suscribirse al topic de FC del reloj
                client?.subscribe(MqttConfig.TOPIC_FC, MqttConfig.QOS)
                android.util.Log.d("MQTT_APP","✅ Conectado y suscrito a ${MqttConfig.TOPIC_FC}")
            }
            override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                android.util.Log.e("MQTT_APP","❌ Error: ${ex?.message}")
            }
        })
    }

    private fun handleFcMessage(msg: MqttMessage) {
        val fcMsg = Json.decodeFromString<FcMessage>(String(msg.payload))

        // 1. Actualizar el StateFlow del Repository
        fcFlow.value = fcMsg.bpm

        // 2. Re-publicar al topic TV con formato enriquecido
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val tvMsg = TvMessage(bpm=fcMsg.bpm, estado=fcMsg.estado, hora=hora)
        val tvPayload = Json.encodeToString(tvMsg).toByteArray()
        val tvMqtt = MqttMessage(tvPayload).apply {
            qos = MqttConfig.QOS; isRetained = true
        }
        client?.publish(MqttConfig.TOPIC_TV, tvMqtt)
        android.util.Log.d("MQTT_APP","🔁 Re-publicado al TV: ${fcMsg.bpm} bpm")
    }

    fun disconnect() { client?.disconnect() }
}
