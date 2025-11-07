package com.aplicaciones_android.ae2_abpro1___grupo_1.repository

import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario
import com.aplicaciones_android.ae2_abpro1___grupo_1.network.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioRepositoryMockWebServerTest {

    // MockWebServer se usa para simular respuestas HTTP reales de la API.
    // Esto permite verificar la integración Retrofit -> Repository sin realizar
    // llamadas de red externas.

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: ApiService
    private lateinit var repo: UsuarioRepository

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Creamos el repositorio con la instancia de ApiService apuntando al MockWebServer
        repo = UsuarioRepository(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `fetchUsuarios parses successful response`() = runTest {
        // Preparar respuesta JSON que devuelve una lista de usuarios
        val body = """
            [
              {"id":1,"nombre":"Ana","apellido":"Lopez","email":"ana@example.com","perfil_url":null}
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(body)
        )

        // Llamar al método del repositorio
        val result = repo.fetchUsuarios()

        // Verificar que recibimos Resource.Success con los datos parseados
        assertTrue(result is Resource.Success)
        val data = (result as Resource.Success).data
        assertEquals(1, data.size)
        assertEquals("Ana", data[0].nombre)
    }

    @Test
    fun `getLastId handles server error`() = runTest {
        // Simulamos un error 500 del servidor para el endpoint /usuarios/lastid
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500).setBody("Internal Server Error")
        )

        val result = repo.getLastId()

        // Debe retornar Resource.Error con mensaje informativo
        assertTrue(result is Resource.Error)
        val msg = (result as Resource.Error).message
        assertTrue(msg.contains("Servidor"))
    }

    @Test
    fun `createUsuario returns body when server returns created user`() = runTest {
        val requestBody = """
            {"id":2,"nombre":"Pedro","apellido":"Gomez","email":"pedro@example.com","perfil_url":null}
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(requestBody)
        )

        val usuario = Usuario(2, "Pedro", "Gomez", "pedro@example.com", null)
        val res = repo.createUsuario(usuario)

        assertTrue(res is Resource.Success)
        assertEquals("Pedro", (res as Resource.Success).data.nombre)
    }

    // New test: respuesta 200 con body nulo -> Retrofit convertirá a null y el repo
    // debe devolver Resource.Error con mensaje "Respuesta vacía del servidor" en getLastId/createUsuario.
    @Test
    fun `getLastId returns error when body is null`() = runTest {
        // Simulamos 200 pero sin body (por ejemplo: servidor retorna contenido vacío)
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("")
        )

        val res = repo.getLastId()

        assertTrue(res is Resource.Error)
        val msg = (res as Resource.Error).message
        assertTrue(msg.contains("Respuesta vacía") || msg.isNotEmpty())
    }
}
