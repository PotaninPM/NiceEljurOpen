package com.team.common.functions

fun String.centerEllipsis(maxLength: Int): String {
    if (this.length <= maxLength) return this
    val keep = maxLength / 2
    val start = this.take(keep)
    val end = this.takeLast(keep)
    return "$start...$end"
}