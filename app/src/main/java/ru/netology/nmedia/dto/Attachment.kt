package ru.netology.nmedia.dto

data class Attachment(
    val url: String,
    val type: AttachmentTypes,
//    val description: String? = null,
)

enum class AttachmentTypes {
    IMAGE
}
