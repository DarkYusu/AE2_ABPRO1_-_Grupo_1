package com.aplicaciones_android.ae2_abpro1___grupo_1.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido") val apellido: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("perfil_url") val perfilUrl: String?
)

// Respuesta del endpoint /usuarios/lastid
data class LastIdResponse(
    @SerializedName("last_id") val lastId: Int,
    @SerializedName("next_id") val nextId: Int
)
