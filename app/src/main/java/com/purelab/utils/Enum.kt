package com.purelab.utils

import android.content.Context

enum class Category(val rowValue: String) {
    /** 洗顔料 */
    FACIAL_WASH("FACIAL_WASH"),

    /** クレンジング */
    CLEANSING("CLEANSING"),

    /** 化粧水 */
    LOTION("LOTION"),

    /** パック・フェイスマスク */
    PACKS("PACKS"),

    /** 目元・口元スペシャルケア */
    SPECIAL_CARE("SPECIAL_CARE"),

    /** その他スキンケア */
    OTHER_SKIN_CARE("OTHER_SKIN_CARE"),

    /** 日焼け・UVケア・サンオイル */
    TANNING("TANNING"),

    /** 口紅・グロス・リップライナー */
    LIPSTICK("LIPSTICK"),

    /** チーク */
    BLUSH("BLUSH"),

    /** ファンデーション */
    FOUNDATION("FOUNDATION"),

    /** 化粧下地・コンシーラー */
    MAKEUP_BASES("MAKEUP_BASES"),

    /** フェイスパウダー */
    FACE_POWDERS("FACE_POWDERS"),

    /** その他メイクアップ */
    OTHER_MAKEUP("OTHER_MAKEUP"),

    /** ヘアケア・スタイリング */
    HAIR_CARE("HAIR_CARE"),

    /** ボディケア・オーラルケア */
    BODY_CARE("BODY_CARE"),

    /** その他 */
    OTHER("OTHER"),

    /** 乳液・クリーム */
    EMULSIONS_AND_CREAMS("EMULSIONS_AND_CREAMS"),

    /** 保湿ジェル */
    MOISTURIZING_GELS("MOISTURIZING_GELS"),

    /** フェイスオイル */
    FACE_OILS("FACE_OILS"),

    /** 保湿ミスト・スプレー */
    MOISTURIZING_MISTS("MOISTURIZING_MISTS"),

    /** ナイトケアクリーム */
    NIGHT_CARE_CREAM("NIGHT_CARE_CREAM"),

    /** 美容液 */
    SERUMS("SERUMS"),

    /** オールインワン */
    ALL_IN_ONES("ALL_IN_ONES"),

    /** ピーリング・ゴマージュ */
    PEELINGS("PEELINGS"),

    /** リップクリーム */
    LIP_BALM("LIP_BALM"),

    /** デオドラント・制汗剤 */
    DEODORANTS("DEODORANTS"),
}

val Enum<*>.typeName: String
    get() {
        return this.javaClass.simpleName
    }

fun Enum<*>.toEnumString(context: Context, suffix: String? = null): String {
    var key = "utils.Enum." + this.typeName + "." + this.name
    suffix?.let {
        key += ".$it"
    }

    val resources = context.resources
    val resId = resources.getIdentifier(key, "string", context.packageName)

    return if (resId == 0) {
        key
    } else {
        resources.getString(resId)
    }
}
