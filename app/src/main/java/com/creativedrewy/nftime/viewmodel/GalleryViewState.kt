package com.creativedrewy.nftime.viewmodel

sealed class NftGalleryViewState(
    val listItems: List<NftViewProps>
)

class Empty : NftGalleryViewState(listOf())
class Loading : NftGalleryViewState(listOf())

class Completed(
    val items: List<NftViewProps>
) : NftGalleryViewState(items)

data class NftViewProps(
    val name: String = "",
    val description: String = "",
    val displayImageUrl: String = "",
    val videoUrl: String = "",
    val assetType: AssetType = Image,
    val assetUrl: String = "",
    val isPending: Boolean = true
)

sealed class AssetType

object Model3d : AssetType()
object Image : AssetType()
object ImageAndVideo : AssetType()