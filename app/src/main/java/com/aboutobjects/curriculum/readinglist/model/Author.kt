package com.aboutobjects.curriculum.readinglist.model

data class Author(
    val firstName: String? = null,
    val lastName: String? = null
) {
    companion object {
        const val UNKNOWN = "Unknown"
    }

    fun displayName(): String? {
        return when {
            firstName == null && lastName == null -> UNKNOWN
            firstName == null -> lastName
            lastName == null -> firstName
            else -> "$firstName $lastName"
        }
    }
}