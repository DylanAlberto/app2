package com.example.app2

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pattern = Regex("([a-zA-ZñÑ\\s])*")
        val patternLastName = Regex ("([a-zA-ZñÑ\\s])*\\s([a-zA-ZñÑ]){2,27}");
        etDpd.setOnClickListener {
            showDatePickerDialog()
        }
        btnSend.setOnClickListener{
            //Valida que los campos no esten vacíos
            if (etDpd.text.isEmpty() || etName.text.isEmpty() || etLastName.text.isEmpty()) makeText(this,getString(R.string.fillAllError), LENGTH_SHORT).show()
            //Valida que los campos no sean espacios en blanco
            else if (etDpd.text.isBlank() || etName.text.isBlank() || etLastName.text.isBlank()) makeText(this,getString(R.string.blankSpacesError), LENGTH_SHORT).show()
            //Valida que no tenga otros caracteres
            else if (!pattern.matches(etName.text) || !pattern.matches(etLastName.text)) makeText(this,getString(R.string.onlyChar), LENGTH_SHORT).show()
            //Valida que al menos haya 2 caracteres para calcular el RFC
            else if (etName.text.length < 2 || etLastName.text.length < 2) makeText(this, getString(R.string.minChar), LENGTH_SHORT).show()
            //Valida que hayan ingresado dos apellidos
            else if (!patternLastName.matches(etLastName.text)) makeText(this, getString(R.string.surnamesError), LENGTH_SHORT).show()
            //los datos estan correctos, podemos trabajar con ellos
            else calcData(this,etName.text.toString(),etLastName.text.toString(),etDpd.text.toString())
        }
    }
    private fun calcData(ctx: Context ,names: String, surnames: String, dateRaw: String) {
        val namesSplited = names.split("\\s".toRegex()).map { it.trim() }
        val surnamesSplited = surnames.split("\\s".toRegex()).map { it.trim() }
        val dateSplited = dateRaw.split("/".toRegex()).map { it.trim() }
        val name = namesSplited.component1().toUpperCase()
        val lastName = surnamesSplited.component1().toUpperCase()
        val secLastname = surnamesSplited.component2().toUpperCase()
        val day = dateSplited.component1()
        val month = dateSplited.component2()
        val year = dateSplited.component3()

        //validar fechas para signo del zodiaco

        val zodiac = if ((month.toInt() == 1 && day.toInt() in 21..31) || (month.toInt() == 2 && day.toInt() in 1..19)) getString(R.string.acuario)
        else if ((month.toInt() == 2 && day.toInt() in 20..28) || (month.toInt() == 3 && day.toInt() in 1..20)) getString(R.string.piscis)
        else if ((month.toInt() == 3 && day.toInt() in 21..31) || (month.toInt() == 4 && day.toInt() in 1..20)) getString(R.string.aries)
        else if ((month.toInt() == 4 && day.toInt() in 21..30) || (month.toInt() == 5 && day.toInt() in 1..20)) getString(R.string.tauro)
        else if ((month.toInt() == 5 && day.toInt() in 21..31) || (month.toInt() == 6 && day.toInt() in 1..22)) getString(R.string.geminis)
        else if ((month.toInt() == 6 && day.toInt() in 23..30) || (month.toInt() == 7 && day.toInt() in 1..22)) getString(R.string.cancer)
        else if ((month.toInt() == 7 && day.toInt() in 23..31) || (month.toInt() == 8 && day.toInt() in 1..22)) getString(R.string.leo)
        else if ((month.toInt() == 8 && day.toInt() in 23..31) || (month.toInt() == 9 && day.toInt() in 1..22)) getString(R.string.virgo)
        else if ((month.toInt() == 9 && day.toInt() in 23..30) || (month.toInt() == 10 && day.toInt() in 1..22)) getString(R.string.libra)
        else if ((month.toInt() == 10 && day.toInt() in 23..31) || (month.toInt() == 11 && day.toInt() in 1..22)) getString(R.string.escorpion)
        else if ((month.toInt() == 11 && day.toInt() in 23..30) || (month.toInt() == 12 && day.toInt() in 1..20)) getString(R.string.sagitario)
        else getString(R.string.capricornio)


        //Impresion final
        val rfc = "RFC: ${lastName.take(2)}${secLastname.take(1)}${name.take(1)}${year.takeLast(2)}$month$day\n" +
                "${getString(R.string.zodiac)}: $zodiac"
        makeText(ctx,rfc, LENGTH_SHORT).show()
    }
    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // Se suma 1 porque en los meses comenzamos a contar desde 1 y no desde 0
            var selectedDate = " "

            if (day < 10 && month + 1 < 10) selectedDate = "0$day/0${month + 1}/$year"
            else if (month + 1 < 10) selectedDate = "$day/0${month + 1}/$year"
            else if (day < 10) selectedDate = "0$day/${month + 1}/$year"
            else selectedDate = "$day/${month + 1}/$year"

            etDpd.setText(selectedDate)
            etDpd.gravity = Gravity.CENTER_HORIZONTAL;
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }


}
class DatePickerFragment : DialogFragment() {

    private var listener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Fecha actual
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Selected date (initial value)
        val datePickerDialog = DatePickerDialog(activity!!, listener, year - 18, month, day)

        // Fecha minima y maxima
        c.set(Calendar.YEAR, year - 80)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        c.set(Calendar.YEAR, year - 18)
        datePickerDialog.datePicker.maxDate = c.timeInMillis

        return datePickerDialog
    }

    companion object {
        fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerFragment {
            val fragment = DatePickerFragment()
            fragment.listener = listener
            return fragment
        }
    }

}
