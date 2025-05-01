# Composables a desarrollar

## A. General Buttons

Botones estándar para diversas acciones.

### `1. PrimaryButton`
* **Uso**: Acción principal en una pantalla o diálogo.
* **Parámetros clave**: `text: String`, `onClick: () -> Unit`, `modifier: Modifier = Modifier`, `enabled: Boolean = true`, `isLoading: Boolean = false` (opcional).
* **Recomendación**: Estilo llamativo usando colores primarios del tema.

### `2. SecondaryButton`
* **Uso**: Acción secundaria.
* **Parámetros clave**: `text: String`, `onClick: () -> Unit`, `modifier: Modifier = Modifier`, `enabled: Boolean = true`.
* **Recomendación**: Estilo menos prominente (ej. `OutlinedButton` o colores secundarios).

### `3. DangerButton`
* **Uso**: Acciones destructivas (eliminar, cancelar).
* **Parámetros clave**: `text: String`, `onClick: () -> Unit`, `modifier: Modifier = Modifier`, `enabled: Boolean = true`.
* **Recomendación**: Estilo distintivo usando colores de error/peligro (rojo).

### `4. GoogleSignButton`
* **Uso**: Botón específico para iniciar sesión con Google.
* **Parámetros clave**: `onClick: () -> Unit`, `modifier: Modifier = Modifier`, `enabled: Boolean = true`, `isLoading: Boolean = false` (opcional).
* **Recomendación**: Seguir guías de branding de Google.

### `5. CloseIconButton` (antes `CloseButton` basado en icono)
* **Uso**: Cerrar modales, diálogos, etc., usando un icono.
* **Parámetros clave**: `onClick: () -> Unit`, `modifier: Modifier = Modifier`, `enabled: Boolean = true`, `contentDescription: String? = "Cerrar"`.
* **Implementación**: Usar `IconButton` con `Icons.Default.Close`.

### `6. RoundedIconButton` (antes `RoundedButton -> Icon`)
* **Uso**: Botón de acción redondeado con un icono.
* **Parámetros clave**: `icon: ImageVector` (o `Painter`), `contentDescription: String?`, `onClick: () -> Unit`, `modifier: Modifier = Modifier`, `enabled: Boolean = true`, `backgroundColor: Color` (del tema).

---

## B. Form & inputs

Componentes para la entrada de datos del usuario.

### `7. FormContainer`
* **Uso**: Agrupar campos de formulario, aplicando espaciado/estilos comunes.
* **Parámetros clave**: `modifier: Modifier = Modifier`, `content: @Composable ColumnScope.() -> Unit`.
* **Implementación**: Usualmente un `Column` con padding.

### `8. InputText`
* **Uso**: Campo de texto genérico (base para otros).
* **Parámetros clave**: `value: String`, `onValueChange: (String) -> Unit`, `modifier: Modifier = Modifier`, `label: String?`, `placeholder: String?`, `leadingIcon: @Composable (() -> Unit)?`, `trailingIcon: @Composable (() -> Unit)?`, `isError: Boolean = false`, `supportingText: @Composable (() -> Unit)?`, `keyboardOptions: KeyboardOptions`, `visualTransformation: VisualTransformation`, `enabled: Boolean = true`.
* **Recomendación**: Usar `OutlinedTextField` o `TextField` de Material.

### `9. InputDate`
* **Uso**: Seleccionar una fecha.
* **Parámetros clave**: `value: LocalDate?` (o `String`), `onValueChange: (LocalDate?) -> Unit`, `modifier: Modifier = Modifier`, `label: String?`, `placeholder: String?`, `isError: Boolean`, `supportingText: @Composable (() -> Unit)?`, `enabled: Boolean`.
* **Implementación**: `InputText` no editable que abre un `DatePickerDialog` al hacer clic.

### `10. InputSelect`
* **Uso**: Seleccionar una opción de una lista (Dropdown).
* **Parámetros clave**: `options: List<T>`, `selectedOption: T?`, `onOptionSelected: (T) -> Unit`, `optionToString: (T) -> String`, `modifier: Modifier`, `label: String?`, `placeholder: String?`, `isError: Boolean`, `supportingText: @Composable (() -> Unit)?`, `enabled: Boolean`.
* **Recomendación**: Usar `ExposedDropdownMenuBox` de Material.

### `11. InputEmail`
* **Uso**: Campo específico para email.
* **Recomendación**: Configurar `InputText` con `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)` y validación específica.

### `12. InputPassword`
* **Uso**: Campo específico para contraseña.
* **Recomendación**: Configurar `InputText` con `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)`, `visualTransformation = PasswordVisualTransformation()`, y `trailingIcon` para visibilidad.

### `13. Switch`
* **Uso**: Interruptor para opciones booleanas.
* **Parámetros clave**: `checked: Boolean`, `onCheckedChange: (Boolean) -> Unit`, `modifier: Modifier`, `enabled: Boolean`, `thumbContent: (@Composable () -> Unit)?`.
* **Recomendación**: Usar `Switch` de Material 3, a menudo en un `Row` con un `Text` como etiqueta.

---

## C. Screen Components

Componentes estructurales o complejos para pantallas.

### `14. Header`
* **Uso**: Barra superior de la aplicación.
* **Parámetros clave**: `title: String`, `modifier: Modifier`, `navigationIcon: @Composable (() -> Unit)?`, `actions: @Composable RowScope.() -> Unit`.
* **Recomendación**: Usar `TopAppBar` de Material.

### `15. BottomNavBar`
* **Uso**: Barra de navegación inferior.
* **Parámetros clave**: `items: List<NavBarItem>` (`route`, `label`, `icon`), `currentRoute: String?`, `onItemSelected: (String) -> Unit`, `modifier: Modifier`.
* **Recomendación**: Usar `NavigationBar` y `NavigationBarItem` (`NavBarButton`) de Material.

### `16. Modal`
* **Uso**: Diálogo o Bottom Sheet superpuesto.
* **Parámetros clave**: `showModal: Boolean` (o `isVisible`), `onDismissRequest: () -> Unit`, `modifier: Modifier`, `content: @Composable () -> Unit`.
* **Recomendación**: Usar `Dialog` o `ModalBottomSheetLayout`.
    * **`ModalCloseButton`**: Instancia de `CloseIconButton` dentro del `content`.
    * **`ModalHeader`**: Composable interno con `title: String`, `modifier: Modifier`, `actions: @Composable RowScope.() -> Unit`.

### `17. ModalTabs`
* **Uso**: Pestañas dentro de un modal o cualquier contenedor.
* **Parámetros clave**: `tabs: List<String>` (o data objects), `selectedTabIndex: Int`, `onTabSelected: (Int) -> Unit`, `modifier: Modifier`, `tabContent: @Composable (index: Int) -> Unit`.
* **Recomendación**: Usar `TabRow` o `ScrollableTabRow`.

### `18. ModalInfoContainer`
* **Uso**: Contenedor estilizado para información dentro de un modal.
* **Parámetros clave**: `modifier: Modifier`, `content: @Composable ColumnScope.() -> Unit`.
* **Implementación**: `Column` o `Box` con estilo (padding, background) del tema.

### `19. ImageContainer`
* **Uso**: Mostrar una imagen (local o remota).
* **Parámetros clave**: `imageUrl: String` (o `data: Any`), `contentDescription: String?`, `modifier: Modifier`, `placeholder: Painter?`, `error: Painter?`, `contentScale: ContentScale`.
* **Recomendación**: Usar librerías como Coil o Glide.

### `20. ProfileContainer`
* **Uso**: Mostrar información de perfil de usuario.
* **Parámetros clave**: `user: UserData`, `modifier: Modifier`, `onEditClick: (() -> Unit)?`.
* **Implementación**: Usa `ImageContainer`, `Text`, y opcionalmente `EditButton`.
    * **`EditButton`**: Botón (`TextButton` o `IconButton` con `Icons.Default.Edit`) para iniciar la edición. Parámetros: `onClick: () -> Unit`, `modifier: Modifier`.

### `21. IconContainer`
* **Uso**: Mostrar un icono vectorial o painter.
* **Parámetros clave**: `icon: ImageVector` (o `Painter`), `contentDescription: String?`, `modifier: Modifier`, `tint: Color`.
* **Recomendación**: Usar el Composable `Icon` estándar.
    * **`EditButton` (en `IconContainer`)**: Si es para editar la entidad representada por el icono, usar el mismo `EditButton` cerca.

### `22. StatContainer`
* **Uso**: Mostrar una estadística simple (valor y etiqueta).
* **Parámetros clave**: `label: String`, `value: String` (o `Int`), `modifier: Modifier`, `icon: @Composable (() -> Unit)?`.
* **Implementación**: `Column { Text(value); Text(label) }` estilizado.

### `23. Graph`
* **Uso**: Mostrar gráficos.
* **Parámetros clave**: `data: ChartData` (depende de la librería), `modifier: Modifier`.
* **Recomendación**: Usar una librería de gráficos especializada (ej. Vico, MPAndroidChart wrappers).

---

## D. Info aux

Componentes para mostrar información auxiliar resumida.

### `24. HabitContainer`
* **Uso**: Mostrar miniatura/resumen de un hábito.
* **Parámetros clave**: `habit: HabitData`, `modifier: Modifier`, `onClick: (() -> Unit)?`.
* **Implementación**: `Card` o `Row` estilizado, usando `IconContainer`, `Text`, etc.

### `25. AchievementContainer`
* **Uso**: Mostrar información de un logro (bloqueado/desbloqueado, progreso).
* **Parámetros clave**: `achievement: AchievementData`, `modifier: Modifier`.
* **Implementación**: `Card` o `Row` estilizado, usando `IconContainer`, `Text`, y `ProgressBar`. Estilo puede variar según estado (`isUnlocked`).
    * **`ProgressBar`**: Ver descripción abajo.

### `26. ProgressBar`
* **Uso**: Indicador de progreso lineal.
* **Parámetros clave**: `progress: Float` (0.0f a 1.0f), `modifier: Modifier`, `color: Color`, `trackColor: Color`.
* **Recomendación**: Usar `LinearProgressIndicator` de Material 3. Para circular, `CircularProgressIndicator`.

---

## E. Text

Componentes para mostrar texto con estilos consistentes.

### `27. Title`, `Subtitle`, `Caption`, `Description`
* **Uso**: Mostrar texto con estilos tipográficos predefinidos.
* **Parámetros clave**: `text: String`, `modifier: Modifier`, `color: Color`, `textAlign: TextAlign?`, `maxLines: Int`, `overflow: TextOverflow`.
* **Recomendación**: Mapear a estilos de `MaterialTheme.typography` (ej. `h4`, `h6`, `caption`, `body1`) en lugar de definir estilos manualmente.

### `28. Alert`
* **Uso**: Mostrar mensajes de estado o error (ej. en formularios).
* **Parámetros clave**: `text: String`, `modifier: Modifier`, `severity: AlertSeverity` (Enum: Error, Warning, Info, Success), `icon: @Composable (() -> Unit)?`.
* **Recomendación**: Estilo (color, icono) debe depender del `severity`.

## F. Orden de Desarrollo

### Día 1: Fundamentales y Preview de `FeatureLaunchScreen`
* **Objetivo**: Construir los bloques UI más básicos y permitir la previsualización de la pantalla inicial (`FeatureLaunchScreen`).
* **Componentes (en orden de prioridad):**
    1.  **Text (`Title`, `Subtitle`, `Caption`, `Description`) (27)**: Indispensable para cualquier texto. Implementar usando `MaterialTheme.typography`.
    2.  **`IconContainer` (21)**: Necesario para iconos.
    3.  **`PrimaryButton` (1)**: Requerido para `FeatureLaunchScreen`.
    4.  **`SecondaryButton` (2)**: Requerido para `FeatureLaunchScreen`.
    5.  **`ImageContainer` (19)**: Requerido para `FeatureLaunchScreen` y Auth. Configurar con librería (Coil/Glide).

### Día 2: Componentes de Autenticación y Preview de `AuthScreens`
* **Objetivo**: Añadir componentes de formulario y estructura para permitir la previsualización de pantallas como `LoginFeatureScreen`, `RegisterFeatureScreen`, etc.
* **Componentes (en orden de prioridad):**
    6.  **`InputText` (8)**: Base para campos de texto de formularios.
    7.  **`FormContainer` (7)**: Para agrupar campos en formularios de Auth.
    8.  **`Header` (14)**: Estructura superior para pantallas de Auth.
    9.  **`GoogleSignButton` (4)**: Botón específico de Auth.
    10. **`Alert` (28)**: Para mostrar errores de validación en formularios.
    11. **`InputEmail` (11)**: Configuración de `InputText` para email.
    12. **`InputPassword` (12)**: Configuración de `InputText` para contraseña.

### Día 3: App Principal, Navegación y Componentes Avanzados
* **Objetivo**: Completar la biblioteca con componentes para la navegación principal, visualización de datos, controles adicionales, modales y elementos complejos necesarios para el resto de la aplicación (ej. `HomeFeatureScreen`, `ProfileFeatureScreen`).
* **Componentes (en orden de prioridad):**
    13. **`Switch` (13)**: Control booleano común.
    14. **`CloseIconButton` (5)**: Para cerrar modales/diálogos.
    15. **`RoundedIconButton` (6)**: Botón de icono genérico.
    16. **`DangerButton` (3)**: Botón para acciones destructivas.
    17. **`BottomNavBar` (15)**: Navegación principal inferior.
    18. **`ProgressBar` (26)**: Indicador de progreso.
    19. **`StatContainer` (22)**: Para mostrar estadísticas simples.
    20. **`InputDate` (9)**: Campo de fecha (requiere `DatePickerDialog`).
    21. **`InputSelect` (10)**: Campo de selección (requiere `ExposedDropdownMenuBox`).
    22. **`Modal` (16)** (con `ModalCloseButton`, `ModalHeader`): Base para diálogos/bottom sheets.
    23. **`ProfileContainer` (20)**: Contenedor específico para perfil.
    24. **`HabitContainer` (24)**: Contenedor específico para hábitos.
    25. **`AchievementContainer` (25)**: Contenedor específico para logros.
    26. **`ModalInfoContainer` (18)**: Contenedor estilizado para modales.
    27. **`ModalTabs` (17)**: Pestañas para modales/contenedores.
    28. **`Graph` (23)**: Gráficos (requiere librería externa).

## Recomendaciones Generales

* **`Modifier`**: Incluir `modifier: Modifier = Modifier` en todos los Composables.
* **Estado (State Hoisting)**: Elevar el estado a los Composables padres. Pasar `value` y `onValueChange` a inputs, y `onClick` a botones.
* **Tema (Theme)**: Usar `MaterialTheme` para colores, tipografía y formas. Evitar estilos hardcodeados.
* **Accesibilidad**: Proveer `contentDescription` para elementos no textuales (iconos, imágenes).
* **Layouts**: Utilizar `Column`, `Row`, `Box`, `LazyColumn`, etc., para estructurar los componentes.


