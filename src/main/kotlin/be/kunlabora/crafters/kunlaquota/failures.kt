package be.kunlabora.crafters.kunlaquota

sealed interface Failure
sealed interface AddFailure : Failure
sealed interface ReadFailure : Failure
data class AddQuoteFailed(val message: String) : AddFailure
data class AddQuoteInvalid(val message: String) : AddFailure
data object FetchQuotesFailed : ReadFailure
