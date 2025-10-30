package com.aplicaciones_android.ae2_abpro1___grupo_1.repository

import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario
import com.aplicaciones_android.ae2_abpro1___grupo_1.network.RetrofitClient
import retrofit2.HttpException
import java.io.IOException

class UsuarioRepository(private val api: com.aplicaciones_android.ae2_abpro1___grupo_1.network.ApiService = RetrofitClient.apiService) {

    suspend fun fetchUsuarios(): Resource<List<Usuario>> {
        return try {
            val response = api.getUsuarios()
            if (response.isSuccessful) {
                val body: List<Usuario> = response.body() ?: emptyList()
                Resource.Success(body)
            } else {
                Resource.Error("Servidor: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Error de red: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Error HTTP: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    suspend fun getLastId(): Resource<com.aplicaciones_android.ae2_abpro1___grupo_1.model.LastIdResponse> {
        return try {
            val response = api.getLastId()
            if (response.isSuccessful) {
                val body: com.aplicaciones_android.ae2_abpro1___grupo_1.model.LastIdResponse? = response.body()
                if (body != null) Resource.Success(body) else Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("Servidor: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Error de red: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Error HTTP: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    suspend fun createUsuario(usuario: Usuario): Resource<Usuario> {
        return try {
            val response = api.createUsuario(usuario = usuario)
            if (response.isSuccessful) {
                val body: Usuario? = response.body()
                if (body != null) Resource.Success(body) else Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("Servidor: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Error de red: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Error HTTP: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    suspend fun getUsuario(id: Int): Resource<Usuario> {
        return try {
            val response = api.getUsuario(id = id)
            if (response.isSuccessful) {
                val body: Usuario? = response.body()
                if (body != null) Resource.Success(body) else Resource.Error("Usuario no encontrado")
            } else {
                Resource.Error("Servidor: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Error de red: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Error HTTP: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    suspend fun updateUsuario(id: Int, usuario: Usuario): Resource<Usuario> {
        return try {
            val response = api.updateUsuario(id = id, usuario = usuario)
            if (response.isSuccessful) {
                val body: Usuario? = response.body()
                if (body != null) Resource.Success(body) else Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("Servidor: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Error de red: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Error HTTP: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    suspend fun deleteUsuario(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteUsuario(id = id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Servidor: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Error de red: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Error HTTP: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error desconocido: ${e.message}")
        }
    }
}
