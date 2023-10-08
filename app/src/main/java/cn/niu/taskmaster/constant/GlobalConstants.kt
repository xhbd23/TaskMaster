package cn.niu.taskmaster.constant

/**
 * 全局变量
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/07
 */

/**
 * 未初始化
 * */
const val VALUE_INT_NOT_INITIALIZED: Int = -100

/**
 * 未初始化
 * */
const val VALUE_LONG_NOT_INITIALIZED: Long = -100L


/** 普通优先级 */
const val PRIORITY_NORMAL = 1

/** 较低优先级 */
const val PRIORITY_LOW = 2

/** 中等优先级 */
const val PRIORITY_MEDIUM = 3

/** 高优先级 */
const val PRIORITY_HIGH = 4


const val ALERT_DEFAULT = 0

const val ALERT_MIN_5 = 1

const val ALERT_MIN_30 = 2

const val ALERT_HOUR_1 = 3

const val ALERT_DAY_1 = 4

const val REPEAT_MODE_NONE = 0

const val REPEAT_MODE_EVERYDAY = 1

const val REPEAT_MODE_TOW_DAYS = 2

const val REPEAT_MODE_EVERY_WEEK = 3

const val REPEAT_MODE_EVERY_MONTH = 4
