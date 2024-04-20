package be.kunlabora.crafters.kunlaquota

sealed interface Failure
data class AddQuoteFailed(val message: String) : Failure
data object FetchQuotesFailed : Failure
