package be.kunlabora.crafters.kunlaquota

import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.Either.Left

interface CanReturnFailure<in F: Failure> {
    fun failOnNext(fn: String, failure: F)
}

class FailureStub<F: Failure> : CanReturnFailure<F> {
    private val registeredFailures: MutableMap<String, F> = mutableMapOf()

    override fun failOnNext(fn: String, failure: F) {
        registeredFailures[fn] = failure
    }

    operator fun <T> invoke(fn: String, block: () -> Either<F, T>): Either<F, T> {
        return registeredFailures[fn]
            ?.let { Left(it) }
            ?: block()
    }
}