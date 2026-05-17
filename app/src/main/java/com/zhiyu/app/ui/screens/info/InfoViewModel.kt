package com.zhiyu.app.ui.screens.info

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class InfoUiState(
    val currentTime: String = "",
    val currentDate: String = "",
    val weekday: String = "",
    val countdownText: String = "",
    val countdownVisible: Boolean = false
)

class InfoViewModel : ViewModel() {

    val uiState: Flow<InfoUiState> = flow {
        while (true) {
            val now = LocalTime.now()
            val today = LocalDate.now()
            val timeStr = DateTimeFormatter.ofPattern("HH:mm:ss").format(now)
            val dateStr = DateTimeFormatter.ofPattern("yyyy年M月d日").format(today)
            val weekStr = weekdayCn(today.dayOfWeek)
            val countdown = countdownText(now)

            emit(
                InfoUiState(
                    currentTime = timeStr,
                    currentDate = dateStr,
                    weekday = weekStr,
                    countdownText = countdown,
                    countdownVisible = countdown.isNotEmpty()
                )
            )
            delay(1000)
        }
    }

    private fun weekdayCn(day: DayOfWeek): String = when (day) {
        DayOfWeek.MONDAY -> "星期一"
        DayOfWeek.TUESDAY -> "星期二"
        DayOfWeek.WEDNESDAY -> "星期三"
        DayOfWeek.THURSDAY -> "星期四"
        DayOfWeek.FRIDAY -> "星期五"
        DayOfWeek.SATURDAY -> "星期六"
        DayOfWeek.SUNDAY -> "星期日"
    }

    private fun countdownText(now: LocalTime): String {
        val today = LocalDate.now()
        val dow = today.dayOfWeek
        val isWeekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY
        if (isWeekend) return "周末愉快"

        val workEnd = LocalTime.of(18, 0)
        return if (now.isBefore(workEnd)) {
            val remaining = ChronoUnit.SECONDS.between(now, workEnd)
            val h = remaining / 3600
            val m = (remaining % 3600) / 60
            val s = remaining % 60
            "下班倒计时: ${h}时${m}分${s}秒"
        } else {
            "已下班"
        }
    }
}
