package com.hendramarihot.newsreader.domain

import javax.inject.Inject

class SetDarkModeUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        userPreferencesRepository.setDarkMode(enabled)
    }
}
