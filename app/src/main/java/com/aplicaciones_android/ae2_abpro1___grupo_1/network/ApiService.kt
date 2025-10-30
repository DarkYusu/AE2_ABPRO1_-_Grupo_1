package com.aplicaciones_android.ae2_abpro1___grupo_1.network

import com.aplicaciones_android.ae2_abpro1___grupo_1.model.LastIdResponse
import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/{user}/usuarios")
    suspend fun getUsuarios(@Path("user") user: String = "antonio"): Response<List<Usuario>>

    @GET("/{user}/usuarios/{usuario_id}")
    suspend fun getUsuario(@Path("user") user: String = "antonio", @Path("usuario_id") id: Int): Response<Usuario>

    @GET("/{user}/usuarios/lastid")
    suspend fun getLastId(@Path("user") user: String = "antonio"): Response<LastIdResponse>

    @GET("/{user}/health")
    suspend fun health(@Path("user") user: String = "antonio"): Response<Map<String, Any>>

    @POST("/{user}/usuarios")
    suspend fun createUsuario(@Path("user") user: String = "antonio", @Body usuario: Usuario): Response<Usuario>

    @PUT("/{user}/usuarios/{usuario_id}")
    suspend fun updateUsuario(@Path("user") user: String = "antonio", @Path("usuario_id") id: Int, @Body usuario: Usuario): Response<Usuario>

    @PATCH("/{user}/usuarios/{usuario_id}")
    suspend fun patchUsuario(@Path("user") user: String = "antonio", @Path("usuario_id") id: Int, @Body usuario: Map<String, Any>): Response<Usuario>

    @DELETE("/{user}/usuarios/{usuario_id}")
    suspend fun deleteUsuario(@Path("user") user: String = "antonio", @Path("usuario_id") id: Int): Response<Unit>
}
