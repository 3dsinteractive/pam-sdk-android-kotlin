package ai.pams.android.kotlin.utils

import android.annotation.SuppressLint
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

class DateUtils {
    companion object {

        fun getDateFormat() = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        fun hourFromGMT(): Int {
            val now = LocalDateTime.now()
            val zone = ZoneId.of("GMT0")
            val zoneOffSet = zone.rules.getOffset(now)
            return zoneOffSet.totalSeconds / 3600
        }

        fun now(): LocalDateTime {
            return LocalDateTime.now()
        }

        fun localDateTimeFromString(dateString: String): LocalDateTime {
            val zdt = ZonedDateTime.parse(dateString, getDateFormat())
            val localZdt = zdt.withZoneSameInstant(ZoneId.systemDefault())
            return localZdt.toLocalDateTime()
        }

        fun toDateStringForUI(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
            return formatter.format(date)
        }

        fun toDateStringForUI(dateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")
            return formatter.format(dateTime)
        }

        fun toOnlyTimeStringForUI(date: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            return formatter.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun timeStampToTimeString(timeStamp: Long, pattern: String): String {
            val sdf = java.text.SimpleDateFormat(pattern)
            val date = java.util.Date(timeStamp)

            return sdf.format(date)
        }

        fun localDateToServerFormat(date: LocalDate): String {
            val zdt = ZonedDateTime.of(date, LocalTime.MIDNIGHT, ZoneId.of("GMT0"))
            val formatter = getDateFormat()
            return formatter.format(zdt)
        }

        fun convertGMT0StringToLocalDateTime(dateTimeString: String?): LocalDateTime? {
            if (dateTimeString == null || dateTimeString == "") {
                return null
            }

            val format = getDateFormat()
            val dt = LocalDateTime.parse(dateTimeString, format)
            val zdt = ZonedDateTime.of(dt, ZoneId.of("GMT0"))

            val systemZoneDateTime = zdt.withZoneSameInstant(ZoneId.systemDefault())

            return systemZoneDateTime.toLocalDateTime()
        }

        fun getGMT0TimeStamp(): String {
            val zdt = ZonedDateTime.now(ZoneId.systemDefault())
            val formatter = getDateFormat()
            return formatter.format(zdt)
        }

        private fun set2Digit(m: Int) = if (m <= 9) {
            "0$m"
        } else {
            "$m"
        }
    }
}