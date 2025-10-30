package com.aplicaciones_android.ae2_abpro1___grupo_1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aplicaciones_android.ae2_abpro1___grupo_1.R
import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository
import com.aplicaciones_android.ae2_abpro1___grupo_1.viewmodel.UsuarioViewModel

class RegistroUsuarioFragment : Fragment() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPerfilUrl: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var tvResult: TextView

    private lateinit var viewModel: UsuarioViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_registro_usuario, container, false)
        etNombre = view.findViewById(R.id.etNombre)
        etApellido = view.findViewById(R.id.etApellido)
        etEmail = view.findViewById(R.id.etEmail)
        etPerfilUrl = view.findViewById(R.id.etPerfilUrl)
        btnRegistrar = view.findViewById(R.id.btnRegistrar)
        tvResult = view.findViewById(R.id.tvResult)

        val factory = UsuarioViewModel.Factory(UsuarioRepository())
        viewModel = ViewModelProvider(requireActivity(), factory).get(UsuarioViewModel::class.java)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val perfil = etPerfilUrl.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty()) {
                tvResult.text = getString(R.string.msg_nombre_email_requeridos)
                return@setOnClickListener
            }

            // id en 0 para que el servidor asigne
            val nuevo = Usuario(id = 0, nombre = nombre, apellido = apellido, email = email, perfilUrl = perfil)
            viewModel.createUsuario(nuevo)
        }

        viewModel.createResult.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Resource.Loading -> {
                    tvResult.text = getString(R.string.msg_registrando)
                }
                is Resource.Success -> {
                    val u = res.data
                    tvResult.text = getString(R.string.msg_registrado, u.nombre ?: "", u.email ?: "")
                    // limpiar campos
                    etNombre.text.clear()
                    etApellido.text.clear()
                    etEmail.text.clear()
                    etPerfilUrl.text.clear()
                    // refrescar lista global
                    viewModel.loadUsuarios()
                }
                is Resource.Error -> {
                    tvResult.text = getString(R.string.msg_error, res.message)
                }
            }
        }

        return view
    }
}
