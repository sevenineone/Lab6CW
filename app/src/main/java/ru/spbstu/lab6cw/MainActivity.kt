package ru.spbstu.lab6cw

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var backgroundThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        textSecondsElapsed.text = getString(R.string.text, secondsElapsed)
    }

    override fun onStart() {
        Log.i("thread", "started")
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        startTime = System.currentTimeMillis()
        backgroundThread = Thread {
            try {
                while (!Thread.currentThread().isInterrupted) {
                    Thread.sleep(1000)
                    Log.i("thread", "running")
                    textSecondsElapsed.post {
                        textSecondsElapsed.text = getString(
                            R.string.text,
                            secondsElapsed + ((System.currentTimeMillis() - startTime) / 1000)
                        )
                    }
                }
            } catch (e: InterruptedException) {
                Log.i("thread", "stoped")
            }
        }
        backgroundThread.start()
        super.onStart()
    }

    override fun onStop() {
        Log.i("thread", "stopped, thread: " + Thread.getAllStackTraces().size)
        backgroundThread.interrupt()
        endTime = System.currentTimeMillis()
        secondsElapsed += ((endTime - startTime) / 1000).toInt()
        with(sharedPrefs.edit()) {
            putInt(SECONDS, secondsElapsed)
            apply()
        }
        super.onStop()
    }

    companion object {
        const val SECONDS = "Seconds elapsed"
    }

}