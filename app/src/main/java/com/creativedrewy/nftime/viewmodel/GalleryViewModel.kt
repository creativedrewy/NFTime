package com.creativedrewy.nftime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.metaplex.lib.modules.nfts.NftClient
import com.metaplex.lib.modules.nfts.models.JsonMetadata
import com.metaplex.lib.modules.nfts.models.NFT
import com.metaplex.lib.solana.SolanaConnectionDriver
import com.solana.core.PublicKey
import com.solana.networking.RPCEndpoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<NftGalleryViewState>(Empty())

    val viewState: StateFlow<NftGalleryViewState>
        get() = _state

    fun loadNfts() {
        val ownerPublicKey = PublicKey("8hEeWszgrA2XkRK4GH6zL4Qq5wJBwotwsB6VEweD8YEQ")
        val solanaConnection = SolanaConnectionDriver(RPCEndpoint.mainnetBetaSolana)
        val solanaIdentityDriver = ReadOnlyIdentityDriver(ownerPublicKey, solanaConnection.solanaRPC)
        val storageDriver = OkHttpSharedStorageDriver()

        val metaplex = Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)

        viewModelScope.launch {
            val myNfts = metaplex.nft.findAllByOwner(ownerPublicKey)

            val props = myNfts.take(10).map { nft ->
                val jsonMeta = nft.loadMeta(metaplex)

                NftViewProps(
                    name = nft.name,
                    displayImageUrl = jsonMeta.image ?: ""
                )
            }

            _state.value = Completed(props)
        }
    }
}

suspend fun NftClient.findAllByOwner(key: PublicKey): List<NFT> =
    suspendCoroutine { cont ->
        findAllByOwner(key) { result ->
            result.onSuccess {
                cont.resume(it.filterNotNull())
            }

            result.onFailure { throw it }
        }
    }

suspend fun NFT.loadMeta(plex: Metaplex): JsonMetadata =
    suspendCoroutine { cont ->
        this.metadata(plex) { result ->
            result.onSuccess { jsonMeta ->
                cont.resume(jsonMeta)
            }

            result.onFailure { throw it }
        }
    }