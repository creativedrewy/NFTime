package com.creativedrewy.nftime.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nftime.theme.NFTimeTheme
import com.creativedrewy.nftime.viewmodel.GalleryViewModel

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.loadNfts()
        }
    )

    Greeting(name = "Andrew")
}

@Composable
fun Greeting(
    name: String
) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NFTimeTheme {
        Greeting("Android")
    }
}