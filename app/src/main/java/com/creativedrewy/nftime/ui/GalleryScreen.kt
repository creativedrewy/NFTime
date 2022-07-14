package com.creativedrewy.nftime.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nftime.viewmodel.GalleryViewModel
import com.creativedrewy.nftime.viewmodel.Loading
import com.creativedrewy.nftime.viewmodel.NftViewProps

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

    val viewState by viewModel.viewState.collectAsState()
    val isLoading = viewState is Loading

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PhotoGrid(
            items = viewState.listItems
        )
    }
}

@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
fun PhotoGrid(items: List<NftViewProps>) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2)
    ) {
        items(items) { nft ->
            PhotoItem(nft = nft)
        }
    }
}

@Composable
fun PhotoItem(nft: NftViewProps) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Red)
    ) {

    }
}