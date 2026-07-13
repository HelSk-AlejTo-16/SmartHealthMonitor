package mx.utng.latp.smarthealthmonitor.mqtt

object MqttConfig {
    // ⚠️ Reemplaza con los datos de TU cluster HiveMQ
    const val BROKER_URL  = "5e214de2dce64bbc97371dd3e04de438.s1.eu.hivemq.cloud"
    const val USERNAME    = "Leo1234"  // del Access Management
    const val PASSWORD    = "utng1234"

    // Topics del proyecto
    const val TOPIC_FC    = "utng/smarthealthmonitor/fc"
    const val TOPIC_TV    = "utng/smarthealthmonitor/tv"
    const val TOPIC_ALERT = "utng/smarthealthmonitor/alerta"

    // QoS: 0=best effort, 1=at least once, 2=exactly once
    const val QOS = 1

    // Client IDs únicos por dispositivo
    const val CLIENT_WEAR = "smarthealthmonitor-wear"
    const val CLIENT_APP  = "smarthealthmonitor-app"
    const val CLIENT_TV   = "smarthealthmonitor-tv"
}
