# SmartHealthMonitor
![Android CI](https://img.shields.io/badge/Android-API26+-green)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-MD3-blue)

Aplicación Android de monitoreo de salud personal en tiempo real.
Desarrollada como proyecto integrador -UNTG 9no Cuatrimestre 2026.
##Stack Tecnológico.

| Tecnología                   | Uso                                    |
|------------------------------|----------------------------------------|
| Kotlin + Jetpack Compose     | UI declarativa con Material Design 3   |
| Wearable + Data Layer API    | Comunicación Reloj a teléfono (BLE)    |
| Health Service API           | Sensor FC real en background (wear OS) |
| Room Database                | Historial persistente de lecturas FC   | 
| Jetpack Navigation           | NavHost entre 4 pantallas              |
| Github + Convential Commits | Control de versiones profesional       |


# Pantallas
|Pantallas|Descripción|
|---|---|
|LoginScreen|Autentificación con validación y State|
|DashboardScreen|FC y Pasos en tiempo real del wearable|
|HistorialScreen| Lecturas persistidas en Room con Flow Reactivo|
|AlertaScreen|AlertDialog MD3 + Snackbar de confirmación|



# Unidad II - Wear OS
| Pantallas           | Descripción                                         |
|---------------------|-----------------------------------------------------|
| WearDashboardScreen | FC en tiempo real con ScalingLazyColumn y Time Text |
| WearHistorialScreen | Lista con Rotary Input(corona del reloj)            |
| WearAlertaScreen    | Botones Circulares de confirmación                  |
| WearHistorialScreen | Hora + FC en el WatchFree nativo                    |

## Capturas de pantallas.

![Login](screenshots/login.png)
![Dashboard](screenshots/dashboard.png)
![Historial](screenshots/historial.png)
![Alerta](screenshots/alerta.png)

![WatchFace](screenshots/watchface.png)
![WearDashboard](screenshots/wear_dashboard.png)




# Unidad III - Teleivisión.

## Arquitectura — SmartHealth Monitor
 
```text
Sensor PPG (Wear OS)
    │  Health Services API
    ▼
PassiveListenerService (wear)
    │  MessageClient (BLE)
    ▼
WearListenerService (app)
    │  SmartHealthRepository
    ▼
StateFlow<Int> (fcActual)  ──────────────────────────────────┐
    │                                                        │
    ▼                                                        ▼
DashboardViewModel (app)              TvViewModel (tv)
    │  collectAsState()                    │  collectAsState()
    ▼                                        ▼
DashboardScreen (Compose)          TvCatalogScreen (Compose TV)
    └── CastButton ──► Chromecast (Remote Playback)
 
Room DB (LecturaFC)  ◄──  Repository  ──►  Flow<List<LecturaFC>>
                                                │
                          ┌─────────────────────┴──────────┐
                          ▼                                ▼
               HistorialScreen (app)        TvCatalogScreen (tv)
```

## Autor
Leonel Alejandro Torres Pérez - UTNG - leyotorres1501@gmail.com
