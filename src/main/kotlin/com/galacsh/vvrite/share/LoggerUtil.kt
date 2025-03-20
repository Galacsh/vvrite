package com.galacsh.vvrite.share

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun logger(ref: () -> Unit): Logger {
    return LoggerFactory.getLogger(ref.javaClass.enclosingClass ?: ref.javaClass)
}

fun Logger.error(msg: () -> String) {
    if (isErrorEnabled) error(msg())
}

fun Logger.warn(msg: () -> String) {
    if (isWarnEnabled) warn(msg())
}

fun Logger.info(msg: () -> String) {
    if (isInfoEnabled) info(msg())
}

fun Logger.debug(msg: () -> String) {
    if (isDebugEnabled) debug(msg())
}

fun Logger.trace(msg: () -> String) {
    if (isTraceEnabled) trace(msg())
}
