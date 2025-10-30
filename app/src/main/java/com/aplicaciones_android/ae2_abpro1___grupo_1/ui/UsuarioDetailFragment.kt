package com.aplicaciones_android.ae2_abpro1___grupo_1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aplicaciones_android.ae2_abpro1___grupo_1.R
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.Resource
import com.aplicaciones_android.ae2_abpro1___grupo_1.repository.UsuarioRepository
import com.aplicaciones_android.ae2_abpro1___grupo_1.viewmodel.UsuarioViewModel

class UsuarioDetailFragment : Fragment() {

    private var usuarioId: Int = 0
    private lateinit var tvId: TextView
    private lateinit var etNombre: TextView
    private lateinit var etApellido: TextView
    private lateinit var etEmail: TextView
    private lateinit var etPerfilUrl: TextView
    private lateinit var tvResult: TextView

    private lateinit var viewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuarioId = it.getInt("usuario_id", 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_usuario_detail, container, false)
        tvId = view.findViewById(R.id.tvId)
        etNombre = view.findViewById(R.id.etNombre)
        etApellido = view.findViewById(R.id.etApellido)
        etEmail = view.findViewById(R.id.etEmail)
        etPerfilUrl = view.findViewById(R.id.etPerfilUrl)
        tvResult = view.findViewById(R.id.tvResult)

        val factory = UsuarioViewModel.Factory(UsuarioRepository())
        viewModel = ViewModelProvider(requireActivity(), factory).get(UsuarioViewModel::class.java)

        viewModel.usuarioDetail.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Resource.Loading -> tvResult.text = getString(R.string.msg_cargando)
                is Resource.Success -> {
                    val u = res.data
                    tvId.text = getString(R.string.label_id, u.id)
                    etNombre.text = u.nombre
                    etApellido.text = u.apellido
                    etEmail.text = u.email
                    etPerfilUrl.text = u.perfilUrl
                    tvResult.text = ""
                }
                is Resource.Error -> tvResult.text = getString(R.string.msg_error, res.message)
            }
        }

        // cargar el usuario
        viewModel.loadUsuario(usuarioId)

        return view
    }
}
