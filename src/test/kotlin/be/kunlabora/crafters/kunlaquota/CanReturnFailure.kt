package be.kunlabora.crafters.kunlaquota

interface CanReturnFailure {
    fun failOnNext(fn: String, failure: Failure)
}