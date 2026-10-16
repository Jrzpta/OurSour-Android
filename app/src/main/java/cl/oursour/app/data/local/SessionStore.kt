package cl.oursour.app.data.local

import android.content.Context
import cl.oursour.app.data.remote.AuthResponse

/** Guarda el token JWT y el nombre del usuario para no pedir login en cada apertura. */
class SessionStore(context: Context) {

    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    val token: String? get() = prefs.getString("token", null)
    val fullName: String get() = prefs.getString("fullName", "") ?: ""

    fun save(auth: AuthResponse) {
        prefs.edit()
            .putString("token", auth.token)
            .putString("fullName", auth.fullName)
            .putString("role", auth.role)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
