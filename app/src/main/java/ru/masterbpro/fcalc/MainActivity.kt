package ru.masterbpro.fcalc

import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.ComponentActivity
import ru.masterbpro.fcalc.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var bindingClass: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculatePWC()
            }
        }
        bindingClass.distanceFirst.addTextChangedListener(textWatcher)
        bindingClass.distanceSecond.addTextChangedListener(textWatcher)
        bindingClass.timeFirst.addTextChangedListener(textWatcher)
        bindingClass.timeSecond.addTextChangedListener(textWatcher)
        bindingClass.pulseFirst.addTextChangedListener(textWatcher)
        bindingClass.pulseSecond.addTextChangedListener(textWatcher)

        bindingClass.timeFirst.setOnClickListener { showTimePickerDialog(bindingClass.timeFirst) }
        bindingClass.timeSecond.setOnClickListener { showTimePickerDialog(bindingClass.timeSecond) }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                editText.setText(
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        selectedHour,
                        selectedMinute
                    )
                )
            }, 0, 0, true
        )
        timePickerDialog.setTitle("Выберите минуты и секунды")
        timePickerDialog.show()
    }

    private fun calculatePWC() {
        val oneLap = 45.81 // meter

        try {
            val distanceFirst = bindingClass.distanceFirst.text.toString().toDoubleOrNull()
            val timeFirst = getSecondFromString(bindingClass.timeFirst.text.toString()).toDouble()
            val pulseFirst = bindingClass.pulseFirst.text.toString().toDoubleOrNull()

            val distanceSecond = bindingClass.distanceSecond.text.toString().toDoubleOrNull()
            val timeSecond = getSecondFromString(bindingClass.timeSecond.text.toString()).toDouble()
            val pulseSecond = bindingClass.pulseSecond.text.toString().toDoubleOrNull()

            if (distanceFirst != null && timeFirst != 0.0 && pulseFirst != null && distanceSecond != null && timeSecond != 0.0 && pulseSecond != null) {
                val V1 = (distanceFirst * oneLap) / timeFirst
                val V2 = (distanceSecond * oneLap) / timeSecond
                val PWC = V1 + (V2 - V1) * ((42.5 - pulseFirst) / (pulseSecond - pulseFirst))
                val PWCFormatted = "%.2f".format(PWC)
                bindingClass.pwcView.text = "PWC: $PWCFormatted"
            } else {
                bindingClass.pwcView.text = "PWC: 0.0"
            }
        } catch (except: Exception) {
            // pass
        }
    }

    private fun getSecondFromString(timeString: String): Int {
        val (minutes, seconds) = timeString.split(":").map { it.toInt() }
        return (minutes * 60) + seconds
    }
}
