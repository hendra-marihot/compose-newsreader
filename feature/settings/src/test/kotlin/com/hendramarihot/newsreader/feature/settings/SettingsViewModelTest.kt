package com.hendramarihot.newsreader.feature.settings

import app.cash.turbine.test
import com.hendramarihot.newsreader.domain.GetUserPreferencesUseCase
import com.hendramarihot.newsreader.domain.SetDarkModeUseCase
import com.hendramarihot.newsreader.domain.ToggleCategoryUseCase
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.model.UserPreferences
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase = mockk()
    private val setDarkModeUseCase: SetDarkModeUseCase = mockk(relaxUnitFun = true)
    private val toggleCategoryUseCase: ToggleCategoryUseCase = mockk(relaxUnitFun = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SettingsViewModel = SettingsViewModel(
        getUserPreferencesUseCase = getUserPreferencesUseCase,
        setDarkModeUseCase = setDarkModeUseCase,
        toggleCategoryUseCase = toggleCategoryUseCase,
    )

    @Test
    fun `exposes user preferences from use case`() = runTest(testDispatcher) {
        val prefs = UserPreferences(isDarkMode = true, selectedCategories = setOf(Category.TECHNOLOGY))
        every { getUserPreferencesUseCase() } returns MutableStateFlow(prefs)

        val viewModel = createViewModel()

        viewModel.preferences.test {
            skipItems(1)
            assertEquals(prefs, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDarkModeToggle calls use case`() = runTest(testDispatcher) {
        every { getUserPreferencesUseCase() } returns MutableStateFlow(UserPreferences())

        val viewModel = createViewModel()

        viewModel.onDarkModeToggle(true)
        advanceUntilIdle()

        coVerify { setDarkModeUseCase(true) }
    }

    @Test
    fun `onCategoryToggle calls use case when state changes`() = runTest(testDispatcher) {
        val prefs = UserPreferences(selectedCategories = setOf(Category.GENERAL))
        every { getUserPreferencesUseCase() } returns MutableStateFlow(prefs)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onCategoryToggle(category = Category.TECHNOLOGY, enabled = true)
        advanceUntilIdle()

        coVerify { toggleCategoryUseCase(Category.TECHNOLOGY) }
    }

    @Test
    fun `onCategoryToggle does not call use case when state unchanged`() = runTest(testDispatcher) {
        val prefs = UserPreferences(selectedCategories = setOf(Category.GENERAL))
        every { getUserPreferencesUseCase() } returns MutableStateFlow(prefs)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onCategoryToggle(category = Category.GENERAL, enabled = true)
        advanceUntilIdle()

        coVerify(exactly = 0) { toggleCategoryUseCase(any()) }
    }
}
