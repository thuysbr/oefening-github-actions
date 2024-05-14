package be.kunlabora.crafters.kunlaquota.service.domain

import be.kunlabora.crafters.kunlaquota.ShareFailure
import be.kunlabora.crafters.kunlaquota.service.Result

interface QuoteShareRepository {
    fun upsert(quoteId: QuoteId, quoteShareProvider: QuoteShareProvider): Result<ShareFailure, QuoteShare>
    fun find(quoteId: QuoteId): Result<ShareFailure, QuoteShare?>
}

typealias QuoteShareProvider = (QuoteId) -> QuoteShare
