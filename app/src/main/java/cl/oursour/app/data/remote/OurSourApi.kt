package cl.oursour.app.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Cada función corresponde a un endpoint del backend Spring Boot. */
interface OurSourApi {

    // CU-07
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    // CU-01
    @GET("api/ingredients")
    suspend fun ingredients(): List<IngredientDto>

    @POST("api/ingredients")
    suspend fun createIngredient(@Body body: IngredientRequest): IngredientDto

    @PUT("api/me/ingredients")
    suspend fun saveMyIngredients(@Body body: IngredientIdsRequest): List<IngredientDto>

    // CU-02 y CU-03
    @POST("api/recipes/search")
    suspend fun searchRecipes(@Body body: RecipeSearchRequest): RecipeSearchResponse

    @GET("api/recipes/{id}")
    suspend fun recipe(
        @Path("id") id: Long,
        @Query("ingredientIds") ingredientIds: List<Long>
    ): RecipeDetailDto

    // CU-04
    @GET("api/cart")
    suspend fun cart(): CartDto

    @POST("api/cart/items")
    suspend fun addToCart(@Body body: AddToCartRequest): CartDto

    @PUT("api/cart/items/{productId}")
    suspend fun updateQuantity(
        @Path("productId") productId: Long,
        @Body body: QuantityRequest
    ): CartDto

    @DELETE("api/cart/items/{productId}")
    suspend fun removeFromCart(@Path("productId") productId: Long): CartDto

    // CU-08
    @POST("api/orders/checkout")
    suspend fun checkout(@Body body: CheckoutRequest): OrderDto

    @GET("api/orders")
    suspend fun orders(): List<OrderDto>
}
