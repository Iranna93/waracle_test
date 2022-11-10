package com.waracle.test.domain.usecase.cakes

import com.waracle.test.domain.model.DomainCakeModel
import com.waracle.test.domain.repositories.DomainCakesRepository
import com.waracle.test.domain.sealed.DomainSealedResponse
import com.waracle.test.domain.usecase.BaseUseCase
import javax.inject.Inject

class GetCakeListUseCase @Inject constructor(
    private val domainCakesRepository: DomainCakesRepository
) : BaseUseCase<DomainSealedResponse<List<DomainCakeModel>>>() {
    override suspend fun execute(): DomainSealedResponse<List<DomainCakeModel>> {
        return domainCakesRepository.getCakesList()
    }
}