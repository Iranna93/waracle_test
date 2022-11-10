package com.waracle.test.presentation.cakes.mvvm

import com.waracle.test.domain.model.DomainCakeModel
import com.waracle.test.domain.sealed.DomainSealedResponse
import com.waracle.test.domain.usecase.cakes.GetCakeListUseCase
import javax.inject.Inject

class CakesModel @Inject constructor(
    private val getCakeListUseCase: GetCakeListUseCase
) {
    suspend fun getCakesList(): DomainSealedResponse<List<DomainCakeModel>> {
        return getCakeListUseCase.execute()
    }
}