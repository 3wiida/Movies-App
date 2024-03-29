package com.ewida.rickmorti.utils.date_time_utils

import android.os.Build
import com.ewida.rickmorti.common.Keys.DAY_OF_MONTH
import com.ewida.rickmorti.common.Keys.DAY_OF_WEEK
import com.ewida.rickmorti.common.Keys.DAY_OF_YEAR
import com.ewida.rickmorti.common.Keys.MONTH
import com.ewida.rickmorti.common.Keys.YEAR
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {

    fun getDateDetails(date:String?,pattern:String,locale: Locale=Locale("en")):MutableMap<String,String>{
        val dateSections= mutableMapOf<String,String>()
        if(!date.isNullOrBlank()){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                val formatter=DateTimeFormatter.ofPattern(pattern)
                val dateDetails=LocalDate.parse(date,formatter)
                dateSections[YEAR]=dateDetails.year.toString()
                dateSections[MONTH]=dateDetails.month.toString()
                dateSections[DAY_OF_MONTH]=dateDetails.dayOfMonth.toString()
                dateSections[DAY_OF_WEEK]=dateDetails.dayOfWeek.toString()
                dateSections[DAY_OF_YEAR]=dateDetails.dayOfYear.toString()
            }
        }

        return dateSections
    }

    fun getMovieDuration(durationInMin:Int?):String{
        var result=""
        if(durationInMin!=null && durationInMin!=0){
            val hours=(durationInMin/60)
            val min=durationInMin-(hours*60)
            result="$hours h $min min"
        }
        return result
    }

}