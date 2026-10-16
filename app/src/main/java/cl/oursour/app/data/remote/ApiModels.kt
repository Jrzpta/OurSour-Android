package cl.oursour.app.data.remote

// Estos modelos son el espejo de los DTOs del backend (clase Dtos.java).
// Los nombres de los campos deben ser IGUALES a los del JSON.

// ---- Autenticación (CU-07) ----
data class RegisterRequest(val email: String, val password: String, val fullName: String)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val token: String, val email: String, val fullName: String, val role: String)

// ---- Ingredientes (CU-01) ----
data class IngredientDto(val id: Long, val name: String, val category: String)
data class IngredientRequest(val name: String, val category: String)
data class IngredientIdsRequest(val ingredientIds: List<Long>)

// ---- Productos ----
data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val active: Boolean
)

// ---- Recetas (CU-02 y CU-03) ----
data class RecipeSearchRequest(val ingredientIds: List<Long>, val minMatch: Int? = null)

data class RecipeMatchDto(
    val recipeId: Long,
    val title: String,
    val matchPercent: Int,
    val matchedCount: Int,
    val totalCount: Int,
    val missingIngredients: List<String>,
    val product: ProductDto?
)

data class RecipeSearchResponse(
    val recipes: List<RecipeMatchDto>,
    val suggestedProducts: List<ProductDto>,
    val message: String?
)

data class IngredientLine(val name: String, val quantity: String?, val available: Boolean)

data class RecipeDetailDto(
    val id: Long,
    val title: String,
    val description: String?,
    val steps: List<String>,
    val ingredients: List<IngredientLine>,
    val product: ProductDto?
)

// ---- Carro (CU-04) ----
data class AddToCartRequest(val productId: Long, val quantity: Int? = null)
data class QuantityRequest(val quantity: Int)
data class CartItemDto(
    val productId: Long,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)
data class CartDto(
    val items: List<CartItemDto>,
    val subtotal: Double,
    val shippingCost: Double,
    val total: Double
)

// ---- Despacho, pago y pedido (CU-08) ----
data class CheckoutRequest(val address: String, val city: String, val cardNumber: String)
data class OrderItemDto(
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)
data class OrderDto(
    val id: Long,
    val status: String,
    val subtotal: Double,
    val shippingCost: Double,
    val total: Double,
    val address: String,
    val city: String,
    val paymentReference: String?,
    val createdAt: String,
    val items: List<OrderItemDto>
)

// ---- Formato de error uniforme del backend ----
data class ApiError(val status: Int?, val message: String?, val fieldErrors: Map<String, String>?)
