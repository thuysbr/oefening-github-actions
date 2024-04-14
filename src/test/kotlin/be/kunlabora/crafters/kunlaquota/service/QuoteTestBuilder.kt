package be.kunlabora.crafters.kunlaquota.service

fun aDefaultQuote(
    name: String = "snarf",
    text: String = "snarf snarf",
) = Quote(
    id = EntityId.new(),
    name = name,
    text = text
)