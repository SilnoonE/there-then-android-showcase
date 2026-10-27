package showcase.therethen

// 실제 구현의 일부 원칙을 일반화한 독립 JVM 예제입니다.
// 제품 모델, 백업 형식, Android 공유 구현과는 다릅니다.
data class Companion(val id: String, val nickname: String)
data class PlacePoint(val latitude: Double?, val longitude: Double?) {
    fun isValid(): Boolean = when {
        latitude == null && longitude == null -> true
        latitude == null || longitude == null -> false
        else -> latitude.isFinite() && longitude.isFinite() &&
            latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
}
data class Visit(
    val title: String,
    val companionIds: Set<String>,
    val note: String
)
data class ShareChoice(val includeNames: Boolean, val includeNote: Boolean)

fun visitsWith(personId: String, visits: List<Visit>): List<Visit> =
    visits.filter { personId in it.companionIds }

fun shareCaption(visit: Visit, people: List<Companion>, choice: ShareChoice): String {
    val names = if (choice.includeNames) {
        people.filter { it.id in visit.companionIds }.joinToString(" · ") { it.nickname }
    } else ""
    return listOf(visit.title, names, if (choice.includeNote) visit.note else "")
        .filter { it.isNotBlank() }.joinToString("\n")
}

fun main() {
    val friends = listOf(Companion("demo-a", "동행 A"), Companion("demo-b", "동행 B"))
    val visit = Visit("가상의 작은 식탁", setOf("demo-a"), "샘플 메모")
    val wish = Visit("다음에 가볼 곳", setOf("demo-b"), "")

    check(visitsWith("demo-a", listOf(visit, wish)) == listOf(visit))
    check(visitsWith("missing", listOf(visit)).isEmpty())

    // 이름이 바뀌어도 기록 연결은 유지됩니다.
    val renamed = friends.map { if (it.id == "demo-a") it.copy(nickname = "바꾼 별명") else it }
    check("바꾼 별명" in shareCaption(visit, renamed, ShareChoice(true, false)))

    // 좌표가 없거나 올바른 쌍인 경우와 잘못된 입력을 구분합니다.
    check(PlacePoint(null, null).isValid())
    check(PlacePoint(0.0, 0.0).isValid())
    check(PlacePoint(-90.0, 180.0).isValid())
    check(!PlacePoint(0.0, null).isValid())
    check(!PlacePoint(91.0, 0.0).isValid())
    check(!PlacePoint(0.0, Double.NaN).isValid())
    check(!PlacePoint(Double.POSITIVE_INFINITY, 0.0).isValid())

    check(shareCaption(visit, friends, ShareChoice(false, false)) == visit.title)
    check(shareCaption(visit, friends, ShareChoice(true, false)) == "가상의 작은 식탁\n동행 A")
    check(shareCaption(visit, friends, ShareChoice(false, true)) == "가상의 작은 식탁\n샘플 메모")
    check(shareCaption(visit, friends, ShareChoice(true, true)) == "가상의 작은 식탁\n동행 A\n샘플 메모")
    println("PASS: companion links, coordinate validation, and four sharing choices")
}
