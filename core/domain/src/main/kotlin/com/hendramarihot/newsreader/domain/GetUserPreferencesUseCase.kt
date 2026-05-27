package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<UserPreferences> =
        userPreferencesRepository.userPreferences
}
