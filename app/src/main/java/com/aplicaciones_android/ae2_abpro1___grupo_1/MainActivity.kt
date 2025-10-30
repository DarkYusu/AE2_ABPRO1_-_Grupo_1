package com.aplicaciones_android.ae2_abpro1___grupo_1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener NavHostFragment y NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Asignar el navGraph programáticamente
        navController.setGraph(R.navigation.nav_graph)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        // Inflar el menú (era eliminado del XML para evitar errores de análisis); hacerlo aquí para que se muestre
        bottomNav.inflateMenu(com.aplicaciones_android.ae2_abpro1___grupo_1.R.menu.bottom_nav_menu)
        NavigationUI.setupWithNavController(bottomNav, navController)

        // conectar toolbar para que muestre el título del destino
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        NavigationUI.setupWithNavController(toolbar, navController)
    }
}