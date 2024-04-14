package be.kunlabora.crafters.kunlaquota.service

import java.util.*

@Suppress("unused")
class EntityId<E> private constructor(val value: String) {
    companion object {
        fun <E> new(): EntityId<E> = EntityId(UUID.randomUUID().toString())
        fun <E> fromString(value: String): EntityId<E> = EntityId(UUID.fromString(value).toString())
    }
}