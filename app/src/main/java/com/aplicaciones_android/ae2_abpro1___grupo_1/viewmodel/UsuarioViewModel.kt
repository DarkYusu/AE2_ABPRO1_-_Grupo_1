package com.aplicaciones_android.ae2_abpro1___grupo_1.viewmodel

import androidx.lifecycle.*
import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repository: com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository = com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository()) : ViewModel() {

    private val _usuarios = MutableLiveData<Resource<List<Usuario>>>(Resource.Loading)
    val usuarios: LiveData<Resource<List<Usuario>>> = _usuarios

    private val _createResult = MutableLiveData<Resource<Usuario>>()
    val createResult: LiveData<Resource<Usuario>> = _createResult

    private val _usuarioDetail = MutableLiveData<Resource<Usuario>>()
    val usuarioDetail: LiveData<Resource<Usuario>> = _usuarioDetail

    private val _updateResult = MutableLiveData<Resource<Usuario>>()
    val updateResult: LiveData<Resource<Usuario>> = _updateResult

    private val _deleteResult = MutableLiveData<Resource<Unit>>()
    val deleteResult: LiveData<Resource<Unit>> = _deleteResult

    // Conjunto local de ids que han sido eliminados (optimisticamente) en la UI.
    // Se usa para filtrar la lista recibida del servidor hasta que el backend refleje la eliminación.
    private val deletedIds = mutableSetOf<Int>()

    // Job para polling automático
    private var pollingJob: kotlinx.coroutines.Job? = null

    /**
     * Inicia un refresco automático periódico de usuarios.
     * Llama a `loadUsuarios()` cada [intervalMs] milisegundos.
     */
    fun startAutoRefresh(intervalMs: Long = 5000L) {
        // evitar múltiples jobs
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    fetchAndPostUsuarios()
                } catch (e: Exception) {
                    // registrar pero ignorar para no cancelar el loop
                }
                kotlinx.coroutines.delay(intervalMs)
            }
        }
    }

    /**
     * Detiene el refresco automático si estaba activo.
     */
    fun stopAutoRefresh() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun loadUsuarios() {
        _usuarios.postValue(Resource.Loading)
        viewModelScope.launch {
            try {
                fetchAndPostUsuarios()
            } catch (e: Exception) {
                _usuarios.postValue(Resource.Error("Error al cargar usuarios: ${e.message}"))
            }
        }
    }

    // Lógica común para obtener usuarios del repositorio y publicar el resultado,
    // aplicando el filtrado por deletedIds.
    private suspend fun fetchAndPostUsuarios() {
        val result = repository.fetchUsuarios()
        when (result) {
            is Resource.Success -> {
                val all = result.data
                // Log para depuración: tamaño recibido
                try {
                    android.util.Log.d("UsuarioViewModel", "fetchAndPostUsuarios: received ${all.size} usuarios")
                } catch (_: Exception) { }
                val remainingDeleted = deletedIds.filter { id -> all.any { it.id == id } }.toMutableSet()
                val filtered = all.filter { it.id !in remainingDeleted }
                deletedIds.clear()
                deletedIds.addAll(remainingDeleted)
                _usuarios.postValue(Resource.Success(filtered))
            }
            is Resource.Error -> {
                android.util.Log.w("UsuarioViewModel", "fetchAndPostUsuarios: error=${result.message}")
                _usuarios.postValue(result)
            }
            is Resource.Loading -> _usuarios.postValue(Resource.Loading)
        }
    }

    fun createUsuario(usuario: Usuario) {
        _createResult.postValue(Resource.Loading)
        viewModelScope.launch {
            val maxAttempts = 3

            // función local para obtener next id y crear
            suspend fun obtainAndCreate(): Resource<Usuario> {
                when (val last = repository.getLastId()) {
                    is Resource.Success -> {
                        val nextId = last.data.nextId
                        val usuarioConId = usuario.copy(id = nextId)
                        return repository.createUsuario(usuarioConId)
                    }
                    is Resource.Error -> {
                        return Resource.Error("No se pudo obtener lastId: ${last.message}")
                    }
                    else -> return Resource.Error("No se pudo obtener lastId")
                }
            }

            var attempt = 0
            var finalResult: Resource<Usuario> = Resource.Error("No se intentó crear")
            while (attempt < maxAttempts) {
                attempt++
                finalResult = obtainAndCreate()
                if (finalResult is Resource.Success) break
                // Si error contiene 'ya existe' (mensaje del servidor), reintentamos tras delay
                val msg = when (finalResult) {
                    is Resource.Error -> finalResult.message
                    else -> ""
                }
                if (msg.contains("ya existe") || msg.contains("ID")) {
                    // pequeño retraso y reintentar
                    delay(500)
                    continue
                } else {
                    // error distinto, no reintentar
                    break
                }
            }

            _createResult.postValue(finalResult)
        }
    }

    fun loadUsuario(id: Int) {
        _usuarioDetail.postValue(Resource.Loading)
        viewModelScope.launch {
            val result = repository.getUsuario(id)
            _usuarioDetail.postValue(result)
        }
    }

    fun updateUsuario(id: Int, usuario: Usuario) {
        _updateResult.postValue(Resource.Loading)
        viewModelScope.launch {
            val result = repository.updateUsuario(id, usuario)
            _updateResult.postValue(result)
        }
    }

    fun deleteUsuario(id: Int) {
        _deleteResult.postValue(Resource.Loading)
        viewModelScope.launch {
            val result = repository.deleteUsuario(id)
            // Si la eliminación fue exitosa, marcar el id como eliminado localmente
            if (result is Resource.Success) {
                try {
                    deletedIds.add(id)
                } catch (_: Exception) { }
            }
            _deleteResult.postValue(result)
        }
    }

    // Factory para facilitar la inyección en Activity
    class Factory(private val repository: com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository = com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository()) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
                return UsuarioViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
