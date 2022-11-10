package com.waracle.test.presentation.cakes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import com.waracle.test.R
import com.waracle.test.presentation.cakes.compose.CakeListScreen
import com.waracle.test.presentation.cakes.mvvm.CakesViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CakesActivity : AppCompatActivity() {

    private val cakesViewModel: CakesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CakeListScreen.initialise(
                    currentUIStateFlow = cakesViewModel.cakeStateFlow,
                    intentionListener = cakesViewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cakesViewModel.acceptNewIntention(CakesViewModel.CakeIntention.RequestCakeList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            cakesViewModel.acceptNewIntention(CakesViewModel.CakeIntention.RequestCakeList)
            return super.onOptionsItemSelected(item)
        }
        return false
    }
}