package dev.dotworld.logger

import android.content.Context
import androidx.annotation.Keep
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.Flattener2
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @Author: Naveen Sakthivel
 * @Date: 08-05-2022
 * Copyright (c) 2022 Dotworld Technologies. All rights reserved.
 */
@Keep
object Log {

    fun init(context: Context, logcat: Boolean) {
        try {
            val config = LogConfiguration.Builder()
                .build()

            val androidPrinter: Printer =
                AndroidPrinter(true)

            val dir = context.filesDir.path

            val filePrinter: Printer =
                FilePrinter.Builder(dir)
                    .backupStrategy(
                        FileSizeBackupStrategy2(
                            10 * 1024 * 1024, // 10 MB
                            5
                        )
                    )
                    .fileNameGenerator(DateFileNameGenerator())
                    .cleanStrategy(FileLastModifiedCleanStrategy(6 * 24 * 60 * 60 * 1000)) // Rotate every 10 days
                    .flattener(DWFlattener())
                    .build()

            if (logcat) {
                XLog.init(
                    config,
                    filePrinter,
                    androidPrinter
                )
            } else {
                XLog.init(
                    config,
                    filePrinter,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun v(tag: String, msg: String) {
        XLog.tag(tag).v(msg)
    }

    @JvmStatic
    fun v(tag: String, msg: String, tr: Throwable?) {
        XLog.tag(tag).v("$msg\n${tr?.stackTraceToString()}")
    }

    @JvmStatic
    fun d(tag: String, msg: String?) {
        XLog.tag(tag).d(msg)
    }

    @JvmStatic
    fun d(tag: String, msg: String, tr: Throwable?) {
        XLog.tag(tag).d("$msg\n${tr?.stackTraceToString()}")
    }

    @JvmStatic
    fun i(tag: String, msg: String?) {
        XLog.tag(tag).i(msg)
    }

    @JvmStatic
    fun i(tag: String, msg: String, tr: Throwable?) {
        XLog.tag(tag).i("$msg\n${tr?.stackTraceToString()}")
    }

    @JvmStatic
    fun w(tag: String, msg: String?) {
        XLog.tag(tag).w(msg)
    }

    @JvmStatic
    fun w(tag: String, msg: String, tr: Throwable?) {
        XLog.tag(tag).w("$msg\n${tr?.stackTraceToString()}")
    }

    @JvmStatic
    fun w(tag: String, tr: Throwable?) {
        XLog.tag(tag).w("${tr?.stackTraceToString()}")
    }

    @JvmStatic
    fun e(tag: String, msg: String?) {
        XLog.tag(tag).e(msg)
    }

    @JvmStatic
    fun e(tag: String, msg: String, tr: Throwable?) {
        XLog.tag(tag).e("$msg\n${tr?.stackTraceToString()}")
    }
}

class DWFlattener : Flattener2 {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun flatten(
        timeMillis: Long,
        logLevel: Int,
        tag: String,
        message: String?
    ): CharSequence {
        val time = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeMillis), TimeZone
                .getDefault().toZoneId()
        )
        return "${time.format(formatter)}\t[${LogLevel.getShortLevelName(logLevel)}]\t${tag}\t$message"
    }
}