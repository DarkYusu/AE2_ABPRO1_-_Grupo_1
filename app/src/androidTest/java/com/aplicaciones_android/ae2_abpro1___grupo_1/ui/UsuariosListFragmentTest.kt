package com.aplicaciones_android.ae2_abpro1___grupo_1.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.aplicaciones_android.ae2_abpro1___grupo_1.MainActivity
import com.aplicaciones_android.ae2_abpro1___grupo_1.R
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.allOf

@RunWith(AndroidJUnit4::class)
@LargeTest
class UsuariosListFragmentTest {

    @Test
    fun fragment_shows_recycler_and_swipe() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // Esperar que el recycler exista
            onView(withId(R.id.recyclerUsuarios)).check(matches(isDisplayed()))
            onView(withId(R.id.swipeRefresh)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun activity_shows_topAppBar() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.topAppBar)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun activity_shows_bottomNav() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.bottomNav)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun activity_has_nav_host_fragment() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // Hay dos FragmentContainerView con el mismo id; escogemos el que es hijo directo del layout raíz "main".
            onView(allOf(withId(R.id.nav_host_fragment), withParent(withId(R.id.main))))
                .check(matches(isDisplayed()))
        }
    }
}
