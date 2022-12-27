package com.natra.stokup.di

import com.natra.stokup.data.csv.CSVParser
import com.natra.stokup.data.csv.CompanyListingsParser
import com.natra.stokup.data.csv.IntradayInfoParser
import com.natra.stokup.data.repository.StockRepositoryImpl
import com.natra.stokup.domain.model.CompanyListing
import com.natra.stokup.domain.model.IntradayInfo
import com.natra.stokup.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}