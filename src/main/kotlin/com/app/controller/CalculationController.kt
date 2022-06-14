package com.app.controller

import com.app.dto.SuccessfulCalculationResponse
import com.app.model.InsuranceCompany
import com.app.service.CalculationService
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map

@Controller("/rest/calculation")
class CalculationController(private val calculationService: CalculationService, private val objectMapper: ObjectMapper) {
    @Post("/{insuranceCompany}")
    suspend fun getCalculation(insuranceCompany: InsuranceCompany): SuccessfulCalculationResponse {
        return calculationService.getCalculation(insuranceCompany)
    }

    @Post("/all")
    suspend fun getCalculations(): List<SuccessfulCalculationResponse> {
        return calculationService.getCalculations()
    }

    @Post("/parallel")
    suspend fun getCalculationsParallel(): List<SuccessfulCalculationResponse> {
        return calculationService.getCalculationsInParallel()
    }

    @Post("/stream", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun getCalculationsStream() = channelFlow {
        calculationService.getCalculations(channel)
    }.map { objectMapper.writeValueAsString(it) + "\n"}
}