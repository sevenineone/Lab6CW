package ru.spbstu.lab6cw

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivityC : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        textSecondsElapsed.text = getString(R.string.text, secondsElapsed)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(1000)
                    Log.i("coroutine", "running")
                    textSecondsElapsed.post {
                        textSecondsElapsed.text = getString(
                            R.string.text,
                            secondsElapsed + ((System.currentTimeMillis() - startTime) / 1000)
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("coroutine", "started")
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        startTime = System.currentTimeMillis()
    }

    override fun onStop() {
        super.onStop()
        Log.i("coroutine", "stopped, threads: " + Thread.getAllStackTraces().size)
        endTime = System.currentTimeMillis()
        secondsElapsed += ((endTime - startTime) / 1000).toInt()
        with(sharedPrefs.edit()) {
            putInt(SECONDS, secondsElapsed)
            apply()
        }
    }

    companion object {
        const val SECONDS = "Seconds elapsed"
    }
}