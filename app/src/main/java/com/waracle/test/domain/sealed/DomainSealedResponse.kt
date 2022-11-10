package com.waracle.test.domain.sealed

sealed class DomainSealedResponse<T>(
    val data: T? = null,
    val error: DomainErrorResponse? = null
) {
    class Success<T>(data: T) : DomainSealedResponse<T>(data)
    class Error<T>(error: DomainErrorResponse?, data: T? = null) :
        DomainSealedResponse<T>(data, error)
}
