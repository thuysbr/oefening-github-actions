package be.kunlabora.crafters.kunlaquota

import be.kunlabora.crafters.kunlaquota.service.Either

interface CanReturnFailure {
    fun failOnNext(fn: String, failure: Failure)
}

class FailureStub : CanReturnFailure {
    private val registeredFailures: MutableMap<String, Failure> = mutableMapOf()

    override fun failOnNext(fn: String, failure: Failure) {
        registeredFailures.put(fn, failure)
    }

    operator fun <T> invoke(fn: String, block: () -> T): Either<Failure, T> {
        return registeredFailures[fn]
            ?.let { Either.Left(it) }
            ?: Either.Right(block())
    }
}