# Estructura de Directorios UI en Android Studio con Jetpack Compose

Esta documentación describe una estructura recomendada para organizar el código de la interfaz de usuario (UI) en un proyecto Android utilizando Jetpack Compose, centrándose específicamente en los directorios `ui/components` y `ui/preview`. Esta estructura promueve la modularidad, reutilización, mantenibilidad y facilita las pruebas unitarias y de UI.

## Contexto General

app/
└── src/
    └── main/
        └── java/
            └── com/
                └── app/
                    └── tibibalance/
                    ├── data/
                    ├── domain/
                    ├── di/
                    └── ui/  <-- Directorio base para la UI
                        ├── components/
                        ├── preview/
                        ├── screens/
                        ├── theme/
                        ├── navigation/
## Directorio `ui/components`

### Propósito

El directorio `ui/components` está destinado a albergar **elementos de UI reutilizables y atómicos (o de bajo nivel de composición)**. Estos componentes son la base fundamental sobre la cual se construyen pantallas más complejas. Idealmente, estos componentes deberían ser:

1.  **Sin estado (Stateless):** Reciben datos y callbacks como parámetros y no gestionan su propio estado interno complejo.
3.  **Foco único:** Cada componente realiza una tarea específica de UI.

### Organización Interna

La convención principal es **crear un archivo `.kt` separado para cada función `@Composable` que define un componente**. El nombre del archivo debe coincidir exactamente (respetando mayúsculas y minúsculas) con el nombre de la función `@Composable` principal que contiene.

└── ui/
    └── components/
        ├── PrimaryButton.kt         # Contiene @Composable fun PrimaryButton(...)


## Directorio `ui/preview`


El directorio ui/preview contiene funciones @Composable específicamente anotadas con @Preview. El propósito fundamental de este directorio es facilitar la visualización y el desarrollo iterativo de los componentes de UI (ui/components) y pantallas completas (ui/screens) directamente en el panel de vista previa de Android Studio, sin necesidad de ejecutar la aplicación completa en un emulador o dispositivo físico.Organización InternaLa organización puede variar, pero un enfoque común es tener archivos .kt que agrupen previews para componentes relacionados o para un componente específico si tiene muchas variantes de previsualización. Los nombres de archivo suelen incluir el sufijo Previews o Preview.└── ui/
    ├── components/
    │   ├── CustomButton.kt
    │   └── InputField.kt
    │   └── ...
    └── preview/
        ├── ButtonPreviews.kt     # Contiene @Preview para CustomButton
        ├── InputFieldPreviews.kt # Contiene @Preview para InputField
        ├── ComponentPreviews.kt  # Alternativa: Agrupa previews de varios componentes pequeños
        ├── LoginScreenPreview.kt # Contiene @Preview para la pantalla de Login
        └── ...
