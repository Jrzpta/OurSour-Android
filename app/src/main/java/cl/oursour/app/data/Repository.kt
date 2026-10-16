package cl.oursour.app.data

import cl.oursour.app.data.local.MyIngredientDao
import cl.oursour.app.data.local.MyIngredientEntity
import cl.oursour.app.data.local.SessionStore
import cl.oursour.app.data.remote.*
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

/** Error ya traducido a un mensaje que se puede mostrar al usuario. */
class ApiException(val code: Int, message: String) : Exception(message)

/**
 * Única puerta de acceso a datos para los ViewModels:
 * combina la API (Retrofit), la base local (Room) y la sesión.
 */
class Repository(
    private val api: OurSourApi,
    private val dao: MyIngredientDao,
    private val session: SessionStore
) {
    private val gson = Gson()

    // ---------- Sesión (CU-07) ----------
    val userName: String get() = session.fullName
    val isLoggedIn: Boolean get() = session.token != null

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        safe { api.login(LoginRequest(email.trim(), password)) }.onSuccess { session.save(it) }

    suspend fun register(fullName: String, email: String, password: String): Result<AuthResponse> =
        safe { api.register(RegisterRequest(email.trim(), password, fullName.trim())) }
            .onSuccess { session.save(it) }

    suspend fun logout() {
        session.clear()
        dao.clear()
    }

    // ---------- Ingredientes (CU-01) ----------
    suspend fun ingredients(): Result<List<IngredientDto>> = safe { api.ingredients() }

    suspend fun createIngredient(name: String, category: String): Result<IngredientDto> =
        safe { api.createIngredient(IngredientRequest(name.trim(), category)) }

    suspend fun localSelection(): List<MyIngredientEntity> = dao.getAll()

    /** Primero guarda en el teléfono (Room); luego intenta copiarlo al backend. */
    suspend fun saveSelection(selected: List<IngredientDto>): Result<Unit> {
        dao.replaceAll(selected.map { MyIngredientEntity(it.id, it.name, it.category) })
        return safe {
            api.saveMyIngredients(IngredientIdsRequest(selected.map { it.id }))
            Unit
        }
    }

    // ---------- Recetas (CU-02 y CU-03) ----------
    suspend fun searchRecipes(ids: List<Long>): Result<RecipeSearchResponse> =
        safe { api.searchRecipes(RecipeSearchRequest(ids)) }

    suspend fun recipeDetail(id: Long, ids: List<Long>): Result<RecipeDetailDto> =
        safe { api.recipe(id, ids) }

    // ---------- Carro (CU-04) ----------
    suspend fun cart(): Result<CartDto> = safe { api.cart() }
    suspend fun addToCart(productId: Long): Result<CartDto> =
        safe { api.addToCart(AddToCartRequest(productId, 1)) }
    suspend fun updateQuantity(productId: Long, quantity: Int): Result<CartDto> =
        safe { api.updateQuantity(productId, QuantityRequest(quantity)) }
    suspend fun removeFromCart(productId: Long): Result<CartDto> =
        safe { api.removeFromCart(productId) }

    // ---------- Pedido (CU-08) ----------
    suspend fun checkout(request: CheckoutRequest): Result<OrderDto> = safe { api.checkout(request) }
    suspend fun orders(): Result<List<OrderDto>> = safe { api.orders() }

    // ---------- Manejo de errores ----------
    private suspend fun <T> safe(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: HttpException) {
            Result.failure(ApiException(e.code(), errorMessage(e)))
        } catch (e: IOException) {
            Result.failure(
                ApiException(0, "No hay conexión con el servidor. Verifica que el backend esté corriendo.")
            )
        }

    private fun errorMessage(e: HttpException): String {
        val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
        val parsed = try { gson.fromJson(body, ApiError::class.java) } catch (_: Exception) { null }
        val fields = parsed?.fieldErrors?.values?.joinToString(", ")
        return when {
            e.code() == 401 && parsed?.message == null ->
                "Sesión inválida o expirada. Inicia sesión nuevamente."
            !fields.isNullOrBlank() -> "${parsed?.message}: $fields"
            parsed?.message != null -> parsed.message
            else -> "Error ${e.code()}"
        }
    }
}
