package com.purelab.enums.user

enum class SkinType(
    val rowValue: String
) {
    /** 未設定 */
    NOT_SETTING("NOT_SETTING"),
    /** 乾燥肌 */
    SRY_SKIN("SRY_SKIN"),
    /** 脂性肌（オイリー肌） */
    OILY_SKIN("OILY_SKIN"),
    /** 混合肌（インナードライ） */
    INNER_DRY("INNER_DRY"),
    /** 普通肌 */
    NORMAL_SKIN("NORMAL_SKIN"),
    /** その他 */
    OTHER("OTHER"),
}