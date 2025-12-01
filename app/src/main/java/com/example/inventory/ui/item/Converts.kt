package com.example.inventory.ui.item

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    // Convierte la Lista de Strings a un Texto JSON para guardar en la BD
    @TypeConverter
    fun fromList(value: List<String>?): String {
        // Si la lista es nula, guardamos un JSON de lista vacía "[]"
        return Gson().toJson(value ?: emptyList<String>())
    }

    // Convierte el Texto JSON de la BD a una Lista de Strings para usar en la App
    @TypeConverter
    fun toList(value: String?): List<String> {
        // Si el valor es nulo o vacío, devolvemos una lista vacía para no romper la app
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            Gson().fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}