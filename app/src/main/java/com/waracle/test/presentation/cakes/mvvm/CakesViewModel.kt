package com.waracle.test.presentation.cakes.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waracle.test.domain.model.DomainCakeModel
import com.waracle.test.domain.sealed.DomainSealedResponse
import com.waracle.test.presentation.cakes.compose.CakeActionListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CakesViewModel @Inject constructor(
    private val cakesModel: CakesModel
) : ViewModel(), CakeActionListener {

    private var cakesList = emptyList<DomainCakeModel>()

    sealed class CakeIntention {
        object RequestCakeList : CakeIntention()
        object CancelDialog : CakeIntention()
        data class RequestDialog(val description: String) : CakeIntention()
    }

    sealed class CurrentUIState {
        object NotLoaded : CurrentUIState()
        object Loading : CurrentUIState()
        data class ShowCakesList(val data: List<DomainCakeModel>) : CurrentUIState()
        data class ShowCakeDetailsDialog(val data: List<DomainCakeModel>, val description: String) :
            CurrentUIState()

        data class Error(val errorCode: Int?, val errorMessages: String?) : CurrentUIState()
    }

    // StateFlows
    private val _cakesStateFlow = MutableStateFlow<CurrentUIState>(
        CurrentUIState.NotLoaded
    )

    val cakeStateFlow: StateFlow<CurrentUIState> = _cakesStateFlow

    private fun triggerCakeList() {
        viewModelScope.launch() {
            _cakesStateFlow.emit(CurrentUIState.Loading)
            when (val response = cakesModel.getCakesList()) {
                is DomainSealedResponse.Success -> {
                    if (!response.data.isNullOrEmpty()) {
                        cakesList = response.data.distinctBy { it.title }.sortedBy { it.title }
                        _cakesStateFlow.emit(CurrentUIState.ShowCakesList(data = cakesList))
                    } else {
                        _cakesStateFlow.emit(CurrentUIState.NotLoaded)
                    }
                }
                is DomainSealedResponse.Error -> {
                    _cakesStateFlow.emit(
                        CurrentUIState.Error(
                            response.error?.errorCode,
                            response.error?.errorMessage
                        )
                    )
                }
            }
        }
    }

    private fun triggerCakeDetailsDialog(description: String) {
        viewModelScope.launch {
            _cakesStateFlow.emit(
                CurrentUIState.ShowCakeDetailsDialog(
                    data = cakesList,
                    description = description
                )
            )
        }
    }

    private fun triggerCancelDialog() {
        viewModelScope.launch {
            _cakesStateFlow.emit(
                CurrentUIState.ShowCakesList(
                    data = cakesList
                )
            )
        }
    }

    override fun acceptNewIntention(intention: CakeIntention) {
        when (intention) {
            is CakeIntention.RequestCakeList -> {
                triggerCakeList()
            }
            is CakeIntention.RequestDialog -> {
                triggerCakeDetailsDialog(intention.description)
            }
            is CakeIntention.CancelDialog -> {
                triggerCancelDialog()
            }
        }
    }
}