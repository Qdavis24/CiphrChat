package com.example.ciphrchat.services

import android.util.Log
import com.example.ciphrchat.services.models.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object AuthApiService {
    private const val BASE_URL = "http://10.0.2.2:5000/auth"

    suspend fun register(username: String, password: String): AuthResponse? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = URL("$BASE_URL/register").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = JSONObject().apply {
                put("username", username)
                put("password", password)
            }.toString()

            connection.outputStream.use { it.write(body.toByteArray()) }

            val stream = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val json = JSONObject(BufferedReader(InputStreamReader(stream)).readText())
            AuthResponse(
                message = json.optString("message"),
                token = json.optString("token").takeIf { it.isNotEmpty() }
            )
        } catch (e: Exception) {
            Log.d("ERROR", "AuthApiService::register ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }

    suspend fun login(username: String, password: String): AuthResponse? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = URL("$BASE_URL/login").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = JSONObject().apply {
                put("username", username)
                put("password", password)
            }.toString()

            connection.outputStream.use { it.write(body.toByteArray()) }

            val stream = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val json = JSONObject(BufferedReader(InputStreamReader(stream)).readText())
            AuthResponse(
                message = json.optString("message"),
                token = json.optString("token").takeIf { it.isNotEmpty() }
            )
        } catch (e: Exception) {
            Log.d("ERROR", "AuthApiService::login ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }

    suspend fun getOnlineUsers(): List<String> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = URL("http://10.0.2.2:5000/users/connected").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val stream = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val json = JSONObject(BufferedReader(InputStreamReader(stream)).readText())
            val usersArray = json.getJSONArray("connected_users")
            (0 until usersArray.length()).map { usersArray.getString(it) }
        } catch (e: Exception) {
            Log.d("ERROR", "AuthApiService::getOnlineUsers ${e.message}")
            emptyList()
        } finally {
            connection?.disconnect()
        }
    }
}
