package com.waracle.test.presentation.cakes.compose

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.waracle.test.R
import com.waracle.test.domain.model.DomainCakeModel
import com.waracle.test.presentation.cakes.mvvm.CakesViewModel
import kotlinx.coroutines.flow.StateFlow

object CakeListScreen {

    @Composable
    fun initialise(
        currentUIStateFlow: StateFlow<CakesViewModel.CurrentUIState>,
        intentionListener: CakeActionListener
    ) {
        currentUIStateFlow.collectAsState().value.let { currentUIState ->
            when (currentUIState) {
                is CakesViewModel.CurrentUIState.Loading -> {
                    displayLoadingView()
                }
                is CakesViewModel.CurrentUIState.ShowCakesList -> {
                    showCakesList(
                        data = currentUIState.data,
                        intentionListener = intentionListener
                    )
                }
                is CakesViewModel.CurrentUIState.Error -> {
                    displayErrorView(errorMessage = currentUIState.errorMessages)
                }
                is CakesViewModel.CurrentUIState.NotLoaded -> {
                    displayEmptyView()
                }
                is CakesViewModel.CurrentUIState.ShowCakeDetailsDialog -> {
                    showCakesList(
                        data = currentUIState.data,
                        intentionListener = intentionListener
                    )
                    displayCakeDetailsDialog(dialogDisplay = true,
                        description = currentUIState.description,
                        onCancelButtonClick = {
                            intentionListener.acceptNewIntention(
                                CakesViewModel.CakeIntention.CancelDialog
                            )
                        })
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun showCakesList(data: List<DomainCakeModel>, intentionListener: CakeActionListener) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
            items(data.size) { item ->
                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .clickable {
                            intentionListener.acceptNewIntention(
                                CakesViewModel.CakeIntention.RequestDialog(data[item].desc)
                            )
                        },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = Uri.parse(data[item].image),
                        placeholder = painterResource(id = R.drawable.ic_baseline_image_24),
                        error = painterResource(id = R.drawable.ic_baseline_image_24),
                        contentDescription = data[item].title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(5.dp),
                        alignment = Alignment.Center
                    )

                    Text(
                        text = data[item].title,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(start = 5.dp, bottom = 5.dp)
                    )
                }
                Divider()
            }
        }
    }

    @Composable
    fun displayEmptyView() {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_perm_device_information_24),
                contentDescription = stringResource(id = R.string.message_not_loaded),
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))

            Text(text = stringResource(id = R.string.message_not_loaded))
        }
    }

    @Composable
    fun displayLoadingView() {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(id = R.string.label_loading))
        }
    }

    @Composable
    fun displayErrorView(errorMessage: String?) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_error_24),
                contentDescription = stringResource(id = R.string.message_not_loaded),
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))

            errorMessage?.let { Text(text = it) }
        }
    }

    @Composable
    fun displayCakeDetailsDialog(
        dialogDisplay: Boolean, description: String,
        onCancelButtonClick: () -> Unit
    ) {

        val showCustomDialogWithResult = remember {
            mutableStateOf(dialogDisplay)
        }

        if (showCustomDialogWithResult.value)
            Dialog(onDismissRequest = {}) {
                Surface(
                    shape = RoundedCornerShape(
                        16.dp
                    ),
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = stringResource(id = R.string.label_description),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = description,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                ),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_cancel),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            showCustomDialogWithResult.value =
                                                !showCustomDialogWithResult.value
                                            onCancelButtonClick()
                                        },
                                    color = colorResource(id = R.color.black)
                                )
                            }
                        }

                    }
                }
            }
    }

}