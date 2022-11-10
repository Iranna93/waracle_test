package com.waracle.test.data.repositories

import com.waracle.test.data.Constants
import com.waracle.test.data.boundary.DomainMapper
import com.waracle.test.data.source.remote.RetrofitService
import com.waracle.test.domain.model.DomainCakeModel
import com.waracle.test.domain.repositories.DomainCakesRepository
import com.waracle.test.domain.sealed.DomainErrorResponse
import com.waracle.test.domain.sealed.DomainSealedResponse

class CakesRepositoryImpl(
    private val retrofitService: RetrofitService,
    private val domainMapper: DomainMapper
) : DomainCakesRepository {
    override suspend fun getCakesList(): DomainSealedResponse<List<DomainCakeModel>> {
        //TODO - we can get the custom parameters from presentation through usecases
        val listCakes = retrofitService.getCakesList(Constants.CAKE_LIST_DATA_SOURCE).map {
            //TODO - and we can save the data into local db - Room or plain SQLITE
            domainMapper.mapCakeEntityToDomainModel(it)
        }

        return if (listCakes.isNullOrEmpty()) {
            DomainSealedResponse.Error(DomainErrorResponse(12, "Not available"))
        } else {
            DomainSealedResponse.Success(data = listCakes)
        }
    }
}