package com.example.grupo_5_biosur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.grupo_5_biosur.ui.AdeliaApp
import com.example.grupo_5_biosur.ui.theme.Grupo_5_biosurTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Grupo_5_biosurTheme {
                AdeliaApp()
            }
        }
    }
}
