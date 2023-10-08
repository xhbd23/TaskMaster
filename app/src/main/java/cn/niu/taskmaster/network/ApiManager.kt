package cn.niu.taskmaster.network

import com.li.li_network.okhttp.RetrofitManager
import com.li.utils.framework.ext.common.moshi
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/08
 */
object ApiManager {
    // 一言 api
    val yiyanApi by lazy {
        RetrofitManager.create(
            RetrofitManager.buildInstance("https://v1.hitokoto.cn/") {
                this.addConverterFactory(MoshiConverterFactory.create(
                ).asLenient())
                client(RetrofitManager.initOkHttpClient(null) {
                    this
                })
            },
            YiYanService::class.java
        )
    }
}