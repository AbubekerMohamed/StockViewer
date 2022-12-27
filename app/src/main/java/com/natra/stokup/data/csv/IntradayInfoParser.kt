package com.natra.stokup.data.csv

import com.natra.stokup.data.mapper.toIntradayInfo
import com.natra.stokup.data.remote.dto.IntradayInfoDto
import com.natra.stokup.domain.model.CompanyListing
import com.natra.stokup.domain.model.IntradayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return with(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    val dto = IntradayInfoDto(timestamp,close.toDouble())
                    dto.toIntradayInfo()
                }
                .filter {
                    it.date.dayOfMonth == LocalDate.now().minusDays(4).dayOfMonth
                }
                .sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}