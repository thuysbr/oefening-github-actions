package be.kunlabora.crafters.kunlaquota.service.domain

import be.kunlabora.crafters.kunlaquota.ShareFailure
import be.kunlabora.crafters.kunlaquota.service.Result

class QuoteShareRepositoryFake: QuoteShareRepository {
    private val quoteShares: MutableMap<QuoteId, QuoteShare> = mutableMapOf()

    override fun upsert(quoteId: QuoteId, quoteShareProvider: QuoteShareProvider): Result<ShareFailure, QuoteShare> {
        return quoteShares[quoteId]
            ?.let { Result.Ok(it) }
            ?: quoteShareProvider(quoteId)
                .also { quoteShares[quoteId] = it }
                .let { Result.Ok(it) }
    }

    override fun find(quoteId: QuoteId): Result<ShareFailure, QuoteShare?> {
        return Result.Ok(quoteShares[quoteId])
    }
}