package com.natra.stokup.data.repository

import com.natra.stokup.data.csv.CSVParser
import com.natra.stokup.data.local.StockDatabase
import com.natra.stokup.data.mapper.toCompanyInfo
import com.natra.stokup.data.mapper.toCompanyListing
import com.natra.stokup.data.mapper.toCompanyListingEntity
import com.natra.stokup.data.remote.StockApi
import com.natra.stokup.domain.model.CompanyInfo
import com.natra.stokup.domain.model.CompanyListing
import com.natra.stokup.domain.model.IntradayInfo
import com.natra.stokup.domain.repository.StockRepository
import com.natra.stokup.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListing>,
    val intradayInfoParser: CSVParser<IntradayInfo>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))

            val localListings = dao.searchCompanyListing(query)
            emit(
                Resource.Success(
                    data = localListings.map { it.toCompanyListing() }
                )
            )

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data."))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't connect to internet. Please try again."))
                null
            }
            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao.searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val result = intradayInfoParser.parse(response.byteStream())
            Resource.Success(result)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load intraday info."
            )
        } catch (e: HttpException) {
            Resource.Error(
                message = "Couldn't load intraday info."
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load company info."
            )
        } catch (e: HttpException) {
            Resource.Error(
                message = "Couldn't load company info."
            )
        }
    }
}