package com.cinnamon.cleanexplorers.utils.extension

import java.util.*


fun Date.getAge(): Int {
    val dob = Calendar.getInstance()
    dob.time = this
    val today = Calendar.getInstance()
    var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
    if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
        age--
    }
    return age
}