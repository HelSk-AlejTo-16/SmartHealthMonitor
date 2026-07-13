package mx.utng.latp.smarthealthmonitort.mqtt

object MqttConfig {
    const val BROKER_URL  = "ssl://TU-CLUSTER.hivemq.cloud:8883"
    const val USERNAME    = "TU-USUARIO-HIVEMQ"
    const val PASSWORD    = "TU-CONTRASEÑA"

    const val TOPIC_FC    = "utng/smarthealthmonitor/fc"
    const val TOPIC_TV    = "utng/smarthealthmonitor/tv"
    const val TOPIC_ALERT = "utng/smarthealthmonitor/alerta"

    const val QOS = 1

    const val CLIENT_WEAR = "smarthealthmonitor-wear"
    const val CLIENT_APP  = "smarthealthmonitor-app"
    const val CLIENT_TV   = "smarthealthmonitor-tv"
}
