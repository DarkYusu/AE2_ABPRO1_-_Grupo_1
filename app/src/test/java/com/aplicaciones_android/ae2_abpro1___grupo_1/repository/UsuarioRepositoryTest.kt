package com.aplicaciones_android.ae2_abpro1___grupo_1.repository

import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario
import com.aplicaciones_android.ae2_abpro1___grupo_1.network.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioRepositoryTest {

    private val api = mockk<ApiService>()
    private val repo = UsuarioRepository(api)

    // Test: Cuando la API responde correctamente con una lista, el repositorio debe
    // devolver Resource.Success con la lista parseada.
    @Test
    fun `fetchUsuarios returns success when api responds successfully`() {
        runTest {
            val usuarios = listOf(Usuario(1, "A", "B", "a@b.com", null))
            coEvery { api.getUsuarios() } returns Response.success(usuarios)

            val res = repo.fetchUsuarios()

            assert(res is Resource.Success)
            val data = (res as Resource.Success).data
            assertEquals(1, data.size)
            coVerify { api.getUsuarios() }
        }
    }

    // Test: Si la llamada getLastId devuelve un error HTTP, el repositorio debe
    // devolver Resource.Error (cubrimos manejo de códigos != 2xx).
    @Test
    fun `getLastId returns error when api fails`() {
        runTest {
            coEvery { api.getLastId() } returns Response.error(500, "err".toResponseBody())

            val res = repo.getLastId()

            assert(res is Resource.Error)
            coVerify { api.getLastId() }
        }
    }

    // Test: createUsuario debe retornar Resource.Success cuando la API devuelve
    // un body con el usuario creado.
    @Test
    fun `createUsuario returns success when api returns body`() {
        runTest {
            val usuario = Usuario(2, "C", "D", "c@d.com", null)
            coEvery { api.createUsuario(usuario = usuario) } returns Response.success(usuario)

            val res = repo.createUsuario(usuario)

            assert(res is Resource.Success)
            assertEquals(usuario, (res as Resource.Success).data)
            coVerify { api.createUsuario(usuario = usuario) }
        }
    }

    // Test: simular IOException (error de red) y verificar que el repositorio
    // devuelve Resource.Error con mensaje que contiene 'Error de red'.
    @Test
    fun `fetchUsuarios returns error on IOException`() {
        runTest {
            coEvery { api.getUsuarios() } throws IOException("timeout")

            val res = repo.fetchUsuarios()

            assert(res is Resource.Error)
            val msg = (res as Resource.Error).message
            // Mensaje formateado en el repo: "Error de red: ${e.message}"
            assert(msg.contains("Error de red"))
            coVerify { api.getUsuarios() }
        }
    }
}
