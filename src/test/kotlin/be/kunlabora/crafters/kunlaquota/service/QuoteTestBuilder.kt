package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.service.domain.Quote

fun aDefaultQuote(
    name: String = "snarf",
    text: String = "snarf snarf",
) = Quote(
    id = EntityId.new(),
    name = name,
    text = text
)