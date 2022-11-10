package com.waracle.test.domain.usecase

abstract class BaseUseCase<P> {

    abstract suspend fun execute(): P

}