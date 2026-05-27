package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.model.Category
import javax.inject.Inject

class ToggleCategoryUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(category: Category) {
        userPreferencesRepository.toggleCategory(category)
    }
}
