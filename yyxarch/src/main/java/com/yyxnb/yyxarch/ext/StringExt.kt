package com.yyxnb.yyxarch.ext

/**
 * Description: 字符串处理相关
 */

/**
 * 是否是手机号
 */
fun String.isPhone() = "(\\+\\d+)?1[34589]\\d{9}$".toRegex().matches(this)

/**
 * 是否是邮箱地址
 */
fun String.isEmail() = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?".toRegex().matches(this)

/**
 * 是否是身份证号码
 */
fun String.isIDCard() = "[1-9]\\d{16}[a-zA-Z0-9]".toRegex().matches(this)

/**
 * 是否是中文字符
 */
fun String.isChinese() = "^[\u4E00-\u9FA5]+$".toRegex().matches(this)

/**
 * 判断两字符串是否相等
 */
fun Boolean.equalsIgnoreCase(a: String, b: String) = a === b || b != null && a.length == b.length && a.regionMatches(0, b, 0, b.length, ignoreCase = true)

/**
 * 判断字符串是否为null或全为空格
 */
fun Boolean.isSpace(s: String) = s == null || s.trim { it <= ' ' }.length == 0;

/**
 * 首字母大写
 */
fun String.upperFirstLetter() = let {
    if (it.isEmpty() || !Character.isLowerCase(it[0])) {
        it
    } else (it[0].toInt() - 32).toChar().toString() + it.substring(1)
}

/**
 * 首字母小写
 */
fun String.lowerFirstLetter() = let {
    if (it.isEmpty() || !Character.isUpperCase(it[0])) {
        it
    } else (it[0].toInt() + 32).toChar().toString() + it.substring(1)
}

/**
 * 反转字符串
 */
fun String.reverse(): String {
    let {
        val len = it.length
        if (len <= 1) {
            return it
        }
        val mid = len shr 1
        val chars = it.toCharArray()
        var c: Char
        for (i in 0 until mid) {
            c = chars[i]
            chars[i] = chars[len - i - 1]
            chars[len - i - 1] = c
        }
        return String(chars)
    }

}

