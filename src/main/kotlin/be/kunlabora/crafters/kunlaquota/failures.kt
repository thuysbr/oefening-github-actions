package be.kunlabora.crafters.kunlaquota

sealed interface Failure
data object AddQuoteFailed : Failure
data object FetchQuotesFailed : Failure
