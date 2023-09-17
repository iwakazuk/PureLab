package com.purelab.enums.user

enum class Sex(
    val rowValue: String
) {
    /** 未設定 */
    NOT_SETTING("NOT_SETTING"),
    /** 男性 */
    MAN("MAN"),
    /** 女性 */
    WOMAN("WOMAN"),
    /** その他 */
    OTHER("OTHER"),
}