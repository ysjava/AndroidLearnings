package com.learndm.androidlearnings.datetime

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters
import java.util.Date


/**
 * 日期时间API介绍
 * JDK 8中增加了一套全新的日期时间API，这套API设计合理，是线程安全的。新的日期及时间API位于 java.time 包 中，下面是一些关键类。
 *
 * LocalDate ：表示日期，包含年月日，格式为 2019-10-16
 * LocalTime ：表示时间，包含时分秒，格式为 16:38:54.158549300
 * LocalDateTime ：表示日期时间，包含年月日，时分秒，格式为 2018-09-06T15:33:56.750
 * DateTimeFormatter ：日期时间格式化类
 * Instant：时间戳，表示一个特定的时间瞬间
 * Duration：用于计算2个时间(LocalTime，时分秒)的距离
 * Period：用于计算2个日期(LocalDate，年月日)的距离
 * ZonedDateTime ：包含时区的时间
 * */

fun main() {
    val a = System.currentTimeMillis()

    Thread.sleep(2000)
    val b = System.currentTimeMillis()
//    println(b-a)
    dateTimeTest()
}

fun timeTest() {
    //获取一个指定的时间
    val time = LocalTime.of(16, 9, 10)
    println(time)
    //获取当前时间
    val now = LocalTime.now()
    println(now)

    println("时间信息: hour:${now.hour}")
}

fun dateTest() {
    // 1.创建指定的日期
    val date1: LocalDate = LocalDate.of(2021, 5, 6)
    println("date1 = $date1")

    // 2.得到当前的日期
    val now = LocalDate.now()
    println("now = $now")

    // 3.根据LocalDate对象获取对应的日期信息
    println("year：" + now.year)
}

fun dateTimeTest() {
    val now: LocalDateTime = LocalDateTime.now()
    println(now)

    // 修改日期时间  对日期时间的修改，对已存在的LocalDate对象，创建了它模板,
    // 并不会修改原来的信息
    val new: LocalDateTime = now.withYear(1998)
    println(now)
    println(new)

    // 在当前日期时间的基础上 加上或者减去指定的时间
    println("两天后: ${now.plusDays(2)}")
    println("十年后: ${now.plusYears(10)}")
    println("一月后: ${now.plusMonths(1)}")
    println("三小时后: ${now.plusHours(3)}")

}

//日期时间比较
fun test1() {
    val date1 = LocalDate.now()
    val date2 = LocalDate.of(2033, 9, 10)
    //date1是否在date2以后, 还有isEqual,isBefore
    println(date1.isAfter(date2))
}

fun test2() {
    val now = LocalDateTime.now()
    // 指定格式  使用系统默认的格式 2021-05-27T16:16:38.139
    val isoLocalDateTime: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    // 将日期时间转换为字符串
    val format = now.format(isoLocalDateTime)
    println("format = $format")

    // 通过 ofPattern 方法来指定特定的格式
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val format1 = now.format(dateTimeFormatter)
    // 2021-05-27 16:16:38
    println("format1 = $format1")

    // 将字符串解析为一个 日期时间类型
    val parse = LocalDateTime.parse("1997-05-06 22:45:16", dateTimeFormatter)

    // parse = 1997-05-06T22:45:16
    println("parse = $parse")
}

fun test3() {
    val now: Instant = Instant.now()
    println("now = $now")
    // 获取从1970年一月一日 00:00:00 到现在的 纳秒
    println(now.nano)
    Thread.sleep(5)
    val now1 = Instant.now()
    println("耗时：" + (now1.nano - now.nano))
}

fun test4(){
    // 计算时间差
    val now = LocalTime.now()
    val time = LocalTime.of(22, 48, 59)
    println("now = $now")
    // 通过Duration来计算时间差
    val duration: Duration = Duration.between(now, time)
    println(duration.toDays())
    println(duration.toHours())
    println(duration.toMinutes())
    println(duration.toMillis())

    // 计算日期差
    val nowDate = LocalDate.now()
    val date = LocalDate.of(1997, 12, 6)
    val period: Period = Period.between(date, nowDate)
    println(period.years)
    println(period.months)
    println(period.days)
}

fun test5(){
    val now = LocalDateTime.now()
    // 将当前的日期调整到下个月的一号
    val adJuster = TemporalAdjuster { temporal ->
        val dateTime = temporal as LocalDateTime
        val nextMonth = dateTime.plusMonths(1).withDayOfMonth(1)
        println("nextMonth = $nextMonth")
        nextMonth
    }
    // 我们可以通过TemporalAdjusters 来实现
    // val nextMonth = now.with(adJuster);
    val nextMonth = now.with(TemporalAdjusters.firstDayOfNextMonth())
    println("now = $now")
    println("nextMonth = $nextMonth")
}

fun test6(){
    //所有时区
//    ZoneId.getAvailableZoneIds().forEach {
//        println(it)
//    }

    // 获取当前时间 中国使用的 东八区的时区，比标准时间早8个小时
    val now = LocalDateTime.now()
    println("now = $now")

    // 获取标准时间
    val bz = ZonedDateTime.now(Clock.systemUTC())
    println("bz = $bz")

    // 使用计算机默认的时区，创建日期时间
    val now1 = ZonedDateTime.now()
    println("now1 = $now1")

    // 使用指定的时区创建日期时间
    val now2 = ZonedDateTime.now(ZoneId.of("America/Marigot"))
    println("now2 = $now2")
}