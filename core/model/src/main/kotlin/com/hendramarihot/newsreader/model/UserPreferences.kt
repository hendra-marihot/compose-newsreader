package com.hendramarihot.newsreader.model

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val selectedCategories: Set<Category> = setOf(Category.GENERAL),
)
