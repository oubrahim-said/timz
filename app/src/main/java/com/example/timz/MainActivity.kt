package com.example.timz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup

class MainActivity : AppCompatActivity() {
    lateinit var buttonGroup: MaterialButtonToggleGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buttonGroup = findViewById(R.id.buttonGroup)
        val handler = Handler(Looper.getMainLooper())

        changeFragment(TeachersFragment())
        buttonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val fragment = when (checkedId) {
                    R.id.btnOne -> TeachersFragment()
                    R.id.btnTwo -> FirstYearFragment()
                    R.id.btnThree -> SecondYearFragment()
                    else -> null
                }

                fragment?.let { frag ->
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                        if (currentFragment == null || currentFragment::class != frag::class) {
                            changeFragment(frag)
                        }
                    },500)
                }
            }
        }
    }
    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

    }
}