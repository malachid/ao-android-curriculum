package com.aboutobjects.curriculum.readinglist.model

data class Author(
    val firstName: String? = null,
    val lastName: String? = null
) {
    companion object {
        const val UNKNOWN = "Unknown"

        fun from(name: String?): Author {
            return when {
                name.isNullOrEmpty()-> Author()
                name.contains(",") -> {
                    val index = name.indexOf(",")
                    Author(
                        firstName = name.substring(index + 1),
                        lastName = name.substring(0, index)
                    )
                }
                name.contains(" ") -> {
                    val index = name.indexOf(" ")
                    Author(
                        firstName = name.substring(0, index),
                        lastName = name.substring(index + 1)
                    )
                }
                else -> Author(lastName = name)
            }
        }
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