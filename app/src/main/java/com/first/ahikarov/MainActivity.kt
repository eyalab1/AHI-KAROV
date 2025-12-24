package com.first.ahikarov

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.first.ahikarov.databinding.ActivityMainBinding
import com.first.ahikarov.peaceofmind.PeaceOfMindFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // אתחול ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        
        enableEdgeToEdge()
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // פתרון זמני: הצגת הפרגמנט שלך מיד עם פתיחת האפליקציה
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, PeaceOfMindFragment())
                        .commit()
                }
    }
}