package com.hendramarihot.newsreader.feature.home.navigation

import kotlinx.serialization.serializer
import org.junit.jupiter.api.Test

class HomeRouteSerializationTest {

    @Test
    fun `HomeRoute resolves a serializer for type-safe navigation`() {
        // Type-safe Navigation Compose calls serializer<HomeRoute>() when building
        // the NavGraph. Without the kotlinx-serialization compiler plugin applied to
        // this module, the reified lookup falls back to reflection and throws
        // SerializationException at runtime — crashing the app on launch.
        serializer<HomeRoute>()
    }
}
