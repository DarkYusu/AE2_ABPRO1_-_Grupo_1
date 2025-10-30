package com.aplicaciones_android.ae2_abpro1___grupo_1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aplicaciones_android.ae2_abpro1___grupo_1.R
import com.aplicaciones_android.ae2_abpro1___grupo_1.model.Usuario

class UsuarioAdapter(
    private val onClick: (Int) -> Unit,
    private val onEdit: (Usuario) -> Unit,
    private val onDelete: (Usuario) -> Unit
) : ListAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder>(DIFF) {

    init {
        // Indicamos que el adaptador tiene IDs estables para evitar problemas de reconcilación tras eliminación
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view, onClick, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UsuarioViewHolder(itemView: View,
                             private val onClick: (Int) -> Unit,
                             private val onEdit: (Usuario) -> Unit,
                             private val onDelete: (Usuario) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById<TextView>(R.id.tvId)
        private val tvNombre: TextView = itemView.findViewById<TextView>(R.id.tvNombre)
        private val tvApellido: TextView = itemView.findViewById<TextView>(R.id.tvApellido)
        private val btnEditar: View? = itemView.findViewById(R.id.btnItemEditar)
        private val btnEliminar: View? = itemView.findViewById(R.id.btnItemEliminar)

        fun bind(usuario: Usuario) {
            tvId.text = itemView.context.getString(R.string.label_id_full, usuario.id)

            val nombre = usuario.nombre?.trim().orEmpty()
            val apellido = usuario.apellido?.trim().orEmpty()
            val email = usuario.email?.trim().orEmpty()

            tvNombre.text = if (nombre.isNotEmpty()) nombre else "Sin nombre"
            tvApellido.text = if (apellido.isNotEmpty()) apellido else "Sin apellido"
            val tvEmail = itemView.findViewById<TextView>(R.id.tvEmail)
            tvEmail?.text = if (email.isNotEmpty()) email else "Sin email"

            // Asegurar que la vista sea clickeable y loggear el click para depuración
            itemView.isClickable = true
            itemView.setOnClickListener {
                try {
                    Log.d("UsuarioAdapter", "Item clicked usuarioId=${usuario.id}")
                    onClick(usuario.id)
                } catch (e: Exception) {
                    Log.e("UsuarioAdapter", "Error handling item click", e)
                }
            }

            // Botón editar: invoca onEdit con la entidad completa
            btnEditar?.setOnClickListener {
                try {
                    Log.d("UsuarioAdapter", "Editar clicked usuarioId=${usuario.id}")
                    onEdit(usuario)
                } catch (e: Exception) {
                    Log.e("UsuarioAdapter", "Error handling edit click", e)
                }
            }

            // Botón eliminar: invoca onDelete con la entidad completa
            btnEliminar?.setOnClickListener {
                try {
                    Log.d("UsuarioAdapter", "Eliminar clicked usuarioId=${usuario.id}")
                    onDelete(usuario)
                } catch (e: Exception) {
                    Log.e("UsuarioAdapter", "Error handling delete click", e)
                }
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Usuario>() {
            override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean = oldItem == newItem
        }
    }

    override fun getItemId(position: Int): Long {
        // Devolver el id del usuario como long para IDs estables
        return try {
            getItem(position).id.toLong()
        } catch (_: Exception) {
            RecyclerView.NO_ID
        }
    }
}
