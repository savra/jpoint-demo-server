package com.app.service

import com.app.model.InsuranceCompany
import jakarta.inject.Singleton
import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Singleton
class NetworkService {
    private val httpClient = HttpClient.newHttpClient()

    suspend fun getCalculation(insuranceCompany: InsuranceCompany): String {
        val request = HttpRequest.newBuilder(
            URI("http://localhost:8080/rest/insurance/${insuranceCompany.name.lowercase()}")
        ).build()

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await().body()
    }
}