package com.natra.stokup.domain.repository

import com.natra.stokup.domain.model.CompanyInfo
import com.natra.stokup.domain.model.CompanyListing
import com.natra.stokup.domain.model.IntradayInfo
import com.natra.stokup.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(
        symbol: String
    ): Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>
}