/**
 * @file    Screen.kt
 * @ingroup ui_navigation // Grupo para elementos relacionados con la navegación
 * @brief   Define una clase sellada (`sealed class`) que representa todas las pantallas (destinos) posibles en la aplicación.
 *
 * @details Esta clase sellada, `Screen`, es fundamental para la navegación de tipo seguro
 * en Jetpack Compose. Cada objeto (`data object`) dentro de la clase representa
 * una pantalla única en la aplicación y encapsula su ruta de navegación como un [String].
 *
 * Organiza las pantallas en dos flujos principales:
 * 1.  **Flujo de Autenticación (`auth flow`):** Incluye pantallas como [Launch], [SignIn], [SignUp],
 * [Forgot], [Recover] (aunque esta última no parece estar implementada aún en `AppNavGraph`),
 * y [VerifyEmail].
 * 2.  **Flujo Principal de la Aplicación (`app con BottomBar`):** Incluye la pantalla contenedora
 * [Main] (que actúa como un hub para la navegación con barra inferior) y las pantallas
 * accesibles desde ella: [Home], [Emotions], [Habits], [Profile], y [Settings].
 *
 * El uso de una clase sellada para las rutas ofrece las siguientes ventajas:
 * - **Seguridad de Tipos:** Evita errores de escritura en las rutas al navegar.
 * - **Centralización:** Todas las rutas están definidas en un solo lugar.
 * - **Refactorización Fácil:** Cambiar una ruta solo requiere modificarla aquí.
 * - **Completitud en `when`:** El compilador puede verificar si se han manejado todos los casos de `Screen`.
 *
 * @see AppNavGraph El gráfico de navegación que utiliza estas rutas para definir los destinos.
 * @see NavHostController El controlador que utiliza estas rutas para realizar la navegación.
 */
// ui/navigation/Screen.kt
package com.app.tibibalance.ui.navigation

/**
 * @brief Clase sellada que representa las diferentes pantallas/destinos de la aplicación.
 * @details Cada objeto dentro de esta clase define una ruta única utilizada por el sistema
 * de navegación de Jetpack Compose.
 *
 * @param route El [String] que identifica de forma única la ruta de navegación para esta pantalla.
 */
sealed class Screen(val route: String) {
    /* auth flow: Pantallas relacionadas con el proceso de autenticación. */

    /** @brief La pantalla inicial de la aplicación, generalmente donde el usuario decide iniciar sesión o registrarse. */
    data object Launch       : Screen("launch")
    /** @brief Pantalla para que los usuarios existentes inicien sesión con sus credenciales. */
    data object SignIn       : Screen("sign_in")
    /** @brief Pantalla para que nuevos usuarios creen una cuenta. */
    data object SignUp       : Screen("sign_up")
    /** @brief Pantalla para iniciar el proceso de recuperación de contraseña (generalmente pidiendo el correo). */
    data object Forgot       : Screen("forgot_pass")
    /** @brief Pantalla para completar la recuperación de contraseña (potencialmente después de seguir un enlace de correo). */
    data object Recover      : Screen("recover_pass") // Nota: Esta ruta no está en AppNavGraph.kt actualmente.
    /** @brief Pantalla que se muestra después del registro para instruir al usuario a verificar su correo electrónico. */
    data object VerifyEmail  : Screen("verify_email")

    /* app con BottomBar: Pantallas principales de la aplicación accesibles después de la autenticación,
     * usualmente gestionadas por una barra de navegación inferior. */

    /**
     * @brief Pantalla contenedora principal que alberga la navegación con barra inferior.
     * @details Actúa como un "hub" o punto de entrada a las secciones principales de la app.
     * La ruta es "main".
     */
    data object Main         : Screen("main")
    /** @brief Pantalla de inicio principal de la aplicación, accesible desde la barra inferior. Ruta: "main/home". */
    data object Home         : Screen("main/home")
    /** @brief Pantalla para la gestión o visualización de emociones, accesible desde la barra inferior. Ruta: "main/emotions". */
    data object Emotions     : Screen("main/emotions")
    /** @brief Pantalla para la gestión o visualización de hábitos, accesible desde la barra inferior. Ruta: "main/habits". */
    data object Habits       : Screen("main/habits")
    /** @brief Pantalla para visualizar o editar el perfil del usuario, accesible desde la barra inferior. Ruta: "main/profile". */
    data object Profile      : Screen("main/profile")
    /** @brief Pantalla para los ajustes de la aplicación, accesible desde la barra inferior. Ruta: "main/settings". */
    data object Settings     : Screen("main/settings")

    // Se podrían añadir más objetos aquí para nuevas pantallas, por ejemplo:
    // data object HabitDetail : Screen("habit_detail/{habitId}") {
    //     fun createRoute(habitId: String) = "habit_detail/$habitId"
    // }

    /** @brief Pantalla para configurar notificaciones del usuario. de settings */
    data object NotificationSettings : Screen("main/settings/notifications")
    /** @brief Pantalla para Editar perfil del usuario. de settings */
    data object EditPersonal : Screen("main/settings/edit_profile")
    /** @brief Pantalla para cambiar contraseña */
    data object ChangePassword : Screen("changePassword")


}
