package cn.niu.taskmaster.util

import cn.niu.taskmaster.constant.VALUE_NOT_INITIALIZED

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/07
 */
object TodoUtils {

    /**
     * 检查是否已初始化
     * @param value
     * */
    fun checkInitialized(value: Long): Boolean {
        return value != VALUE_NOT_INITIALIZED.toLong()
    }

}