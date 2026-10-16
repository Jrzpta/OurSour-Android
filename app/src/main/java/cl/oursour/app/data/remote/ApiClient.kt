package cl.oursour.app.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // En el EMULADOR, 10.0.2.2 significa "el computador donde corre el emulador".
    // Ahí está tu Spring Boot (localhost:8080 del PC).
    // En un celular físico usa la IP de tu PC en la red WiFi, p. ej. "http://192.168.1.50:8080/".
    const val BASE_URL = "http://10.0.2.2:8080/"

    fun create(tokenProvider: () -> String?): OurSourApi {
        // Agrega "Authorization: Bearer <token>" a cada petición si hay sesión iniciada.
        val authInterceptor = Interceptor { chain ->
            val token = tokenProvider()
            val request = if (token != null) {
                chain.request().newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }

        // Muestra en Logcat (filtro "OkHttp") cada petición y respuesta. Solo para desarrollo.
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OurSourApi::class.java)
    }
}
