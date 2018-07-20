package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * @author Uraka.Lee
 */
object HeaderHandler : MarkHandler {

    override fun handleHeader(sourceMark: String): String {
        return if (sourceMark.length < 6) "$sourceMark${Mark.H.defaultMark}" else Mark.H.defaultMark
    }

    override fun handleList(sourceMark: String): String {
        return Mark.LI.defaultMark
    }
}