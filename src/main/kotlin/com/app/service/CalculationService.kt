package com.app.service

import com.app.dto.SuccessfulCalculationResponse
import com.app.model.InsuranceCompany
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Singleton
class CalculationService(private val networkService: NetworkService,
                         private val objectMapper: ObjectMapper) {
    suspend fun getCalculation(insuranceCompany: InsuranceCompany) : SuccessfulCalculationResponse {
        val response = networkService.getCalculation(insuranceCompany)

        return objectMapper.readValue<SuccessfulCalculationResponse>(response)
    }

    suspend fun getCalculations() : List<SuccessfulCalculationResponse> {
        return InsuranceCompany.values().map{getCalculation(it)}
    }

    suspend fun getCalculationsInParallel() : List<SuccessfulCalculationResponse> = coroutineScope {
        val jobs = InsuranceCompany.values().map { async { getCalculation(it) } }.toTypedArray()
        awaitAll(*jobs)
    }

    suspend fun getCalculations(channel: SendChannel<SuccessfulCalculationResponse>) = coroutineScope {
        InsuranceCompany.values().forEach { insuranceCompany ->
            launch {
                val result = getCalculation(insuranceCompany)
                channel.send(result)
            }
        }
    }
}