package cl.oursour.app

import android.app.Application
import android.content.Context
import cl.oursour.app.data.Repository
import cl.oursour.app.data.local.AppDatabase
import cl.oursour.app.data.local.SessionStore
import cl.oursour.app.data.remote.ApiClient

/** Contenedor simple de dependencias: se crea una sola vez al iniciar la app. */
class AppContainer(context: Context) {
    private val session = SessionStore(context)
    private val api = ApiClient.create { session.token }
    private val db = AppDatabase.build(context)
    val repository = Repository(api, db.myIngredientDao(), session)
}

class OurSourApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
