package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.service.Either.Left
import be.kunlabora.crafters.kunlaquota.service.Either.Right
import java.util.*

@Suppress("unused")
class EntityId<E> private constructor(val value: String) {
    companion object {
        fun <E> new(): EntityId<E> = EntityId(UUID.randomUUID().toString())
        fun <E> fromString(value: String): EntityId<E> = EntityId(UUID.fromString(value).toString())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityId<*>

        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString() = "EntityId($value)"
}

//marker interface
interface Command

sealed class Either<out F, out T> {
    data class Left<F>(val value: F) : Either<F, Nothing>()
    data class Right<T>(val value: T) : Either<Nothing, T>()

    fun valueOrThrow() = when (this) {
        is Left -> error("You expected a Right, but got a Left of ${this.value}")
        is Right -> this.value
    }

    fun <R> map(block: (T) -> R): Either<F, R> =
        when (this) {
            is Left -> this
            is Right -> Right(block(this.value))
        }

    fun <R> flatMap(block: (T) -> Either<@UnsafeVariance F,R>): Either<F, R> =
        when (this) {
            is Left -> this
            is Right -> block(this.value)
        }

    fun recover(recovery: (F) -> @UnsafeVariance T): Either<T, T> =
        when (this) {
            is Left -> Right(recovery(this.value))
            is Right -> this
        }
}


fun <T> Either<T, T>.get(): T =
    when (this) {
        is Left -> this.value
        is Right -> this.value
    }
