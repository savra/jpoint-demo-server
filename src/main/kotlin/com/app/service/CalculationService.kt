package com.app.service

import com.app.dto.CalculationResponse
import com.app.model.InsuranceCompany
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import java.math.BigDecimal
import kotlin.random.Random

interface ICalculationService {
    suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse
    suspend fun getAllCalculations(): List<CalculationResponse>
    suspend fun getAllCalculationsInParallel(): List<CalculationResponse>
}

interface IWsCalculationService {
    suspend fun getAllCalculations(channel: SendChannel<CalculationResponse>)
}

@Singleton
class CalculationService : ICalculationService, IWsCalculationService {
    override suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse {
        val price = when (insuranceCompany) {
            InsuranceCompany.FIRST -> {
                Random.nextInt(3_000, 8_000)
            }
            InsuranceCompany.SECOND -> {
                Random.nextInt(3_500, 7_000)
            }
            InsuranceCompany.THIRD -> {
                Random.nextInt(4_000, 9_000)
            }
        }.let(::BigDecimal)

        delay(Random.nextLong(1_000, 8_000))

        return CalculationResponse(price, insuranceCompany)
    }

    override suspend fun getAllCalculations(): List<CalculationResponse> {
        return InsuranceCompany.values().map { getCalculation(it) }
    }

    override suspend fun getAllCalculationsInParallel(): List<CalculationResponse> = coroutineScope {
        val jobs = InsuranceCompany.values().map { async { getCalculation(it) } }.toTypedArray()

        awaitAll(*jobs)
    }

    override suspend fun getAllCalculations(channel: SendChannel<CalculationResponse>) = coroutineScope {
        InsuranceCompany.values().forEach { insuranceCompany ->
            launch {
                val result = getCalculation(insuranceCompany)
                channel.send(result)
            }
        }
    }
}
