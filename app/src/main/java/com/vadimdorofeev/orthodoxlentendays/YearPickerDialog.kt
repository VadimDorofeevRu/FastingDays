package com.vadimdorofeev.orthodoxlentendays

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate


class YearPickerDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = Common.selectedDate.value ?: Common.today

        val numberPicker = NumberPicker(activity).apply {
            minValue = 1900 // Только XX и XXI века, до и после даты смещаются
            maxValue = 2099
            value = date.year
        }

        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle(R.string.dialog_year_title)
        builder.setView(numberPicker)
        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            Common.selectedDate.value = LocalDate.of(numberPicker.value, date.monthValue, date.dayOfMonth)
            dismiss()
        }
        builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> dismiss() }
        return builder.create()
    }

}