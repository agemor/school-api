package kr.go.neis.api

object ScheduleParser {

    /**
     * 웹에서 가져온 데이터를 바탕으로 학사일정을 파싱합니다.
     */
    fun parse(rawData: String): List<Schedule> {

        if (rawData.length < 1)
            throw NEISException("불러온 데이터가 올바르지 않습니다.")

        val monthlySchedule = ArrayList<Schedule>()

        /*
         파싱 편의를 위해 모든 공백을 제거합니다.
         일정 텍스트에는 공백이 들어가지 않으므로, 파싱 결과에는 영향을 주지 않습니다.
         */
        val rawData = rawData.replace("\\s+".toRegex(), "")

        val chunk = rawData.split("textL\">".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            for (i in 1 until chunk.size) {
                var trimmed = before(chunk[i], "</div>")
                val date = before(after(trimmed, ">"), "</em>")

                // 빈 공간은 파싱하지 않습니다.
                if (date.isEmpty()) continue

                // 일정을 가져옵니다.
                val schedule = StringBuilder()
                while (trimmed.contains("<strong>")) {
                    val name = before(after(trimmed, "<strong>"), "</strong>")
                    schedule.append(name)
                    schedule.append("\n")
                    trimmed = after(trimmed, "</strong>")
                }
                monthlySchedule.add(Schedule(schedule.toString()))
            }
            return monthlySchedule

        } catch (e: Exception) {
            throw NEISException("학사일정 정보 파싱에 실패했습니다. 최신 버전의 API로 업데이트해 주세요.")
        }
    }
}