package com.waracle.test.presentation.cakes.compose

import com.waracle.test.presentation.cakes.mvvm.CakesViewModel


interface CakeActionListener {
    fun acceptNewIntention(intention: CakesViewModel.CakeIntention)
}