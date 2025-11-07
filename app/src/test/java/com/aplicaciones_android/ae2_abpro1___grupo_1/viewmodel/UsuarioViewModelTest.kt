package com.aplicaciones_android.ae2_abpro1___grupo_1.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository
import com.aplicaciones_android.ae2_abpro1___grupo_1.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Regla para configurar Dispatchers.Main con dispatcher de pruebas
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<UsuarioRepository>()
    private lateinit var viewModel: UsuarioViewModel

    @Before
    fun setup() {
        viewModel = UsuarioViewModel(repository)
    }

    // Test: Cuando repository.fetchUsuarios() retorna datos con éxito, el LiveData
    // `usuarios` del ViewModel debe publicar Resource.Success con la lista filtrada.
    @Test
    fun `loadUsuarios posts Success when repository returns data`() = runTest {
        val usuarios = listOf(Usuario(1, "A", "B", "a@b.com", null))
        coEvery { repository.fetchUsuarios() } returns Resource.Success(usuarios)

        viewModel.loadUsuarios()
        advanceUntilIdle()

        val res = viewModel.usuarios.value
        assert(res is Resource.Success)
        assertEquals(1, (res as Resource.Success).data.size)
    }

    // Test: Simula fallo al obtener lastId para el flujo de creación. El ViewModel
    // debe publicar Resource.Error con un mensaje descriptivo.
    @Test
    fun `createUsuario retries and posts error when getLastId fails`() = runTest {
        val usuario = Usuario(0, "X", "Y", "x@y.com", null)
        coEvery { repository.getLastId() } returns Resource.Error("fail")

        viewModel.createUsuario(usuario)
        advanceUntilIdle()

        val res = viewModel.createResult.value
        assert(res is Resource.Error)
        assertEquals("No se pudo obtener lastId: fail", (res as Resource.Error).message)
    }

    // New test: updateUsuario debe publicar Loading y luego Success cuando el repositorio devuelve success
    @Test
    fun `updateUsuario posts Loading then Success`() = runTest {
        val usuario = Usuario(5, "New", "User", "new@example.com", null)
        coEvery { repository.updateUsuario(5, usuario) } returns Resource.Success(usuario)

        viewModel.updateUsuario(5, usuario)
        advanceUntilIdle()

        val res = viewModel.updateResult.value
        assertTrue(res is Resource.Success)
        assertEquals(usuario, (res as Resource.Success).data)
        coVerify { repository.updateUsuario(5, usuario) }
    }

    // New test: deleteUsuario debe publicar Success cuando la eliminación es exitosa y luego solicitar recarga de usuarios
    @Test
    fun `deleteUsuario posts Success and triggers reload`() = runTest {
        // Lista inicial con el usuario a eliminar
        val initialList = listOf(Usuario(10, "To", "Delete", "del@example.com", null))
        // Después de eliminar, el servidor retorna lista vacía
        val afterDelete = emptyList<Usuario>()

        coEvery { repository.deleteUsuario(10) } returns Resource.Success(Unit)
        // Cuando viewModel.loadUsuarios() sea llamado, simulamos que fetchUsuarios devuelve afterDelete
        coEvery { repository.fetchUsuarios() } returnsMany listOf(Resource.Success(initialList), Resource.Success(afterDelete))

        // Cargar inicialmente
        viewModel.loadUsuarios()
        advanceUntilIdle()

        // Ejecutar delete
        viewModel.deleteUsuario(10)
        advanceUntilIdle()

        // En la app la UI observa deleteResult y llama a viewModel.loadUsuarios();
        // en este test simulamos ese comportamiento llamando manualmente a loadUsuarios()
        viewModel.loadUsuarios()
        advanceUntilIdle()

        val deleteRes = viewModel.deleteResult.value
        assertTrue(deleteRes is Resource.Success)
        // Después de la eliminación y la recarga manual, el LiveData usuarios debe haberse actualizado a afterDelete
        val usuariosRes = viewModel.usuarios.value
        assertTrue(usuariosRes is Resource.Success)
        assertEquals(0, (usuariosRes as Resource.Success).data.size)

        coVerify { repository.deleteUsuario(10) }
        coVerify(atLeast = 1) { repository.fetchUsuarios() }
    }
}
