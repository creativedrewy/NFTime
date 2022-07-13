package com.creativedrewy.nftime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.metaplex.lib.solana.SolanaConnectionDriver
import com.solana.core.PublicKey
import com.solana.networking.RPCEndpoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    fun loadNfts() {
        val ownerPublicKey = PublicKey("8hEeWszgrA2XkRK4GH6zL4Qq5wJBwotwsB6VEweD8YEQ")
        val solanaConnection = SolanaConnectionDriver(RPCEndpoint.mainnetBetaSolana)
        val solanaIdentityDriver = ReadOnlyIdentityDriver(ownerPublicKey, solanaConnection.solanaRPC)
        val storageDriver = OkHttpSharedStorageDriver()

        val metaplex = Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)

        metaplex.nft.findAllByOwner(ownerPublicKey) { result ->
            result.onSuccess { nfts ->
                nfts.filterNotNull().forEach {
                    Log.v("Andrew", "Your NFT name: ${ it.name }")
                }
            }
        }
    }

}