package be.kunlabora.crafters.kunlaquota.service

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

    fun valueOrThrow() = when(this) {
        is Left -> error("You expected a Right, but got a Left of ${this.value}")
        is Right -> this.value
    }
}