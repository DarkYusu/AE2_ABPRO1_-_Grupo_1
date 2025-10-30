package com.aplicaciones_android.ae2_abpro1___grupo_1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aplicaciones_android.ae2_abpro1___grupo_1.R
import com.aplicaciones_android.ae2_abpro1___grupo_1.adapter.UsuarioAdapter
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository
import com.aplicaciones_android.ae2_abpro1___grupo_1.viewmodel.UsuarioViewModel

class UsuariosListFragment : Fragment() {

    private lateinit var adapter: UsuarioAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var tvMessage: TextView
    private lateinit var viewModel: UsuarioViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_usuarios_list, container, false)

        recycler = view.findViewById<RecyclerView>(R.id.recyclerUsuarios)
        swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        tvMessage = view.findViewById<TextView>(R.id.tvMessage)

        adapter = UsuarioAdapter(
            onClick = { usuarioId ->
                if (usuarioId <= 0) return@UsuarioAdapter
                val bundle = Bundle().apply { putInt("usuario_id", usuarioId) }
                recycler.post {
                    try {
                        findNavController().navigate(R.id.action_usuariosListFragment_to_usuarioDetailFragment, bundle)
                    } catch (e: Exception) {
                        Log.w("UsuariosListFragment", "Navigation to detail failed", e)
                        try { findNavController().navigate(R.id.usuarioDetailFragment, bundle) } catch (_: Exception) {}
                    }
                }
            },
            onEdit = { usuario ->
                val ctx = requireContext()
                val dialogView = layoutInflater.inflate(R.layout.dialog_edit_usuario, null)
                val etNombre = dialogView.findViewById<android.widget.EditText>(R.id.etNombre)
                val etApellido = dialogView.findViewById<android.widget.EditText>(R.id.etApellido)
                val etEmail = dialogView.findViewById<android.widget.EditText>(R.id.etEmail)
                val etPerfil = dialogView.findViewById<android.widget.EditText>(R.id.etPerfilUrl)
                etNombre.setText(usuario.nombre)
                etApellido.setText(usuario.apellido)
                etEmail.setText(usuario.email)
                etPerfil.setText(usuario.perfilUrl)
                androidx.appcompat.app.AlertDialog.Builder(ctx)
                    .setTitle(R.string.edit_user)
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.btn_guardar)) { _, _ ->
                        val updated = com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario(
                            id = usuario.id,
                            nombre = etNombre.text.toString().trim(),
                            apellido = etApellido.text.toString().trim(),
                            email = etEmail.text.toString().trim(),
                            perfilUrl = etPerfil.text.toString().trim()
                        )
                        viewModel.updateUsuario(usuario.id, updated)
                    }
                    .setNegativeButton(getString(R.string.btn_cancelar), null)
                    .show()
            },
            onDelete = { usuario ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm_delete_title))
                    .setMessage(getString(R.string.confirm_delete_message))
                    .setNegativeButton(getString(R.string.btn_cancelar), null)
                    .setPositiveButton(getString(R.string.btn_eliminar)) { _, _ ->
                        // Eliminación optimista en UI: eliminar del adapter localmente
                        try {
                            val current = adapter.currentList.toList()
                            val updated = current.filter { it.id != usuario.id }
                            adapter.submitList(updated) {
                                Log.d("UsuariosListFragment", "Adapter updated optimistically after delete, newSize=${updated.size}")
                            }
                        } catch (e: Exception) {
                            Log.w("UsuariosListFragment", "Optimistic update failed", e)
                        }
                        // Llamada al ViewModel para eliminar en servidor; observer recargará la lista
                        viewModel.deleteUsuario(usuario.id)
                    }
                    .show()
            }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        val factory = UsuarioViewModel.Factory(UsuarioRepository())
        viewModel = ViewModelProvider(requireActivity(), factory).get(UsuarioViewModel::class.java)

        // Observers para operaciones de mutación: si se eliminó o actualizó un usuario,
        // recargar la lista para mantener la UI sincronizada.
        viewModel.deleteResult.observe(viewLifecycleOwner) { res ->
            when (res) {
                is com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource.Loading -> {
                    // opcional: mostrar indicador (usamos swipe)
                    swipe.isRefreshing = true
                }
                is com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource.Success -> {
                    swipe.isRefreshing = false
                    // recargar lista
                    viewModel.loadUsuarios()
                }
                is com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource.Error -> {
                    swipe.isRefreshing = false
                    // Mostrar mensaje de error sencillo
                    android.widget.Toast.makeText(requireContext(), "${res.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { res ->
            when (res) {
                is com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource.Loading -> swipe.isRefreshing = true
                is com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource.Success -> {
                    swipe.isRefreshing = false
                    viewModel.loadUsuarios()
                }
                is com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource.Error -> {
                    swipe.isRefreshing = false
                    android.widget.Toast.makeText(requireContext(), "${res.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        swipe.setOnRefreshListener {
            viewModel.loadUsuarios()
        }

        viewModel.usuarios.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    swipe.isRefreshing = true
                    tvMessage.visibility = View.GONE
                }
                is Resource.Success -> {
                    swipe.isRefreshing = false
                    val list = resource.data
                    // Enviar una nueva instancia y usar el callback de commit para asegurar
                    // que la actualización del diff haya finalizado antes de hacer acciones
                    adapter.submitList(list.toList()) {
                        Log.d("UsuariosListFragment", "submitList commit callback: list updated, size=${list.size}")
                    }
                    tvMessage.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    if (list.isEmpty()) tvMessage.text = getString(R.string.no_users)
                }
                is Resource.Error -> {
                    swipe.isRefreshing = false
                    tvMessage.visibility = View.VISIBLE
                    tvMessage.text = resource.message
                }
            }
        }

        viewModel.loadUsuarios()

        // Asegurar refresco al volver a este fragment (por ejemplo tras eliminar un usuario en detalle)
        // Usamos un LifecycleEventObserver moderno y lo añadimos al viewLifecycleOwner para
        // recibir el evento ON_RESUME y recargar la lista.
        val lifecycleObserver = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadUsuarios()
            }
        }
        viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        return view
    }

    override fun onStart() {
        super.onStart()
        // Iniciar refresco automático (polling) mientras el fragment está en primer plano
        try {
            viewModel.startAutoRefresh(5000L)
        } catch (e: Exception) {
            Log.w("UsuariosListFragment", "startAutoRefresh failed", e)
        }
    }

    override fun onStop() {
        super.onStop()
        // Detener el refresco automático para ahorrar recursos
        try {
            viewModel.stopAutoRefresh()
        } catch (e: Exception) {
            Log.w("UsuariosListFragment", "stopAutoRefresh failed", e)
        }
    }
}
