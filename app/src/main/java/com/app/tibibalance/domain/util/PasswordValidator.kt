package com.app.tibibalance.domain.util

object PasswordValidator {
    private val symbols = "!@#\$%^&*()-_=+[]{}|;:'\",.<>?/`~"

    /**
     * Devuelve `null` si la contraseña cumple con todos los requisitos:
     * - ≥ 8 caracteres
     * - Al menos una mayúscula
     * - Al menos una minúscula
     * - Al menos un dígito
     * - Al menos un símbolo
     *
     * O bien una cadena con TODOS los puntos incumplidos, cada uno en su línea.
     */
    fun validateStrength(password: String): String? {
        val errors = mutableListOf<String>()
        if (password.length < 8)           errors.add("• Mínimo 8 caracteres")
        if (!password.any(Char::isUpperCase)) errors.add("• Incluir mayúsculas (A-Z)")
        if (!password.any(Char::isLowerCase)) errors.add("• Incluir minúsculas (a-z)")
        if (!password.any(Char::isDigit))     errors.add("• Incluir números (0-9)")
        if (!password.any { symbols.contains(it) })
            errors.add("• Incluir símbolos (${symbols.take(5)}...)")

        return if (errors.isEmpty()) null
        else "La contraseña debe:\n" + errors.joinToString("\n")
    }
}
