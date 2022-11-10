package com.waracle.test.data.boundary

import com.waracle.test.data.entities.CakeEntity
import com.waracle.test.domain.model.DomainCakeModel
import javax.inject.Inject

class DomainMapper @Inject constructor() {
    fun mapCakeEntityToDomainModel(cakeEntity: CakeEntity): DomainCakeModel {
        return DomainCakeModel(
            title = cakeEntity.title,
            desc = cakeEntity.desc,
            image = cakeEntity.image
        )
    }

}