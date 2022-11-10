@file:OptIn(ExperimentalCoroutinesApi::class)

package com.waracle.test.presentation.cakes.mvvm

import com.waracle.test.domain.model.DomainCakeModel
import com.waracle.test.domain.sealed.DomainErrorResponse
import com.waracle.test.domain.sealed.DomainSealedResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CakesViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun prepareMockData(): List<DomainCakeModel> {
        return listOf(
            DomainCakeModel(
                title = "Lemon cheesecake",
                desc = "A cheesecake made of lemon",
                image = "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"
            ),
            DomainCakeModel(
                title = "victoria sponge",
                desc = "sponge with jam",
                image = "https://upload.wikimedia.org/wikipedia/commons/0/05/111rfyh.jpg"
            ),
            DomainCakeModel(
                title = "victoria sponge",
                desc = "sponge with jam",
                image = "https://upload.wikimedia.org/wikipedia/commons/0/05/111rfyh.jpg"
            ),

            )
    }

    private fun verifyErrorCakesData(
        mockCakeData: List<DomainCakeModel>,
        resultData: List<DomainCakeModel>,
    ) {
        assertEquals(2, resultData.size)
        assertEquals(mockCakeData[0].title, resultData[0].title)
        assertEquals(mockCakeData[0].image, resultData[0].image)
    }

    @Test
    fun cakeViewModel_RequestCakeList_Success() = runTest {
        val modelCakeMock = mockk<CakesModel>()
        val mockData = prepareMockData()
        val testScope = CoroutineScope(dispatcher)

        coEvery {
            modelCakeMock.getCakesList()
        } returns DomainSealedResponse.Success(data = mockData)

        val viewModel = CakesViewModel(
            cakesModel = modelCakeMock
        )

        var initialResultList: List<CakesViewModel.CurrentUIState>? = null

        testScope.launch {
            viewModel.acceptNewIntention(
                intention = CakesViewModel.CakeIntention.RequestCakeList
            )
            initialResultList = viewModel.cakeStateFlow.take(2).toList()
        }.join()
        assertEquals(true, initialResultList?.isNotEmpty())

        initialResultList?.let { resultsList ->
            assertEquals(
                true,
                resultsList[0] is CakesViewModel.CurrentUIState.NotLoaded
            )
            assertEquals(true, resultsList[1] is CakesViewModel.CurrentUIState.ShowCakesList)

            val resultData =
                resultsList[1] as CakesViewModel.CurrentUIState.ShowCakesList

            verifyErrorCakesData(
                mockCakeData = mockData,
                resultData = resultData.data,
            )
        }
    }

    @Test
    fun cakeViewModel_RequestCakeList_NotLoaded() = runTest {
        val modelCakeMock = mockk<CakesModel>()
        val testScope = CoroutineScope(dispatcher)

        coEvery {
            modelCakeMock.getCakesList()
        } returns DomainSealedResponse.Success(data = emptyList())

        val viewModel = CakesViewModel(
            cakesModel = modelCakeMock
        )

        var initialResultList: CakesViewModel.CurrentUIState? = null

        testScope.launch {
            viewModel.acceptNewIntention(
                intention = CakesViewModel.CakeIntention.RequestCakeList
            )
            initialResultList = viewModel.cakeStateFlow.value
        }.join()

        assertEquals(true, initialResultList is CakesViewModel.CurrentUIState.NotLoaded)
    }

    @Test
    fun cakeViewModel_RequestCakeList_Error() = runTest {
        val modelCakeMock = mockk<CakesModel>()
        val testScope = CoroutineScope(dispatcher)

        coEvery {
            modelCakeMock.getCakesList()
        } returns DomainSealedResponse.Error(
            data = emptyList(),
            error = DomainErrorResponse(errorMessage = "Testing", errorCode = 11)
        )

        val viewModel = CakesViewModel(
            cakesModel = modelCakeMock
        )

        var initialResultList: List<CakesViewModel.CurrentUIState>? = null

        testScope.launch {
            viewModel.acceptNewIntention(
                intention = CakesViewModel.CakeIntention.RequestCakeList
            )
            initialResultList = viewModel.cakeStateFlow.take(2).toList()
        }.join()
        assertEquals(true, initialResultList?.isNotEmpty())

        initialResultList?.let { resultsList ->
            assertEquals(
                true,
                resultsList[0] is CakesViewModel.CurrentUIState.NotLoaded
            )
            assertEquals(
                true,
                resultsList[1] is CakesViewModel.CurrentUIState.Error
            )
        }
    }
}