package com.natra.stokup.presentation.company_info

import com.natra.stokup.domain.model.CompanyInfo
import com.natra.stokup.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
