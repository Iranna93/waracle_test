package com.waracle.test.domain.repositories

import com.waracle.test.domain.model.DomainCakeModel
import com.waracle.test.domain.sealed.DomainSealedResponse

interface DomainCakesRepository {
    suspend fun getCakesList(): DomainSealedResponse<List<DomainCakeModel>>
}