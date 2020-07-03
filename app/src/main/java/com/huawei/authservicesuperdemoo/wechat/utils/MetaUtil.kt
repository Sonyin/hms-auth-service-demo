package com.huawei.authservicesuperdemoo.wechat.utils

import android.content.Context
import android.content.pm.PackageManager

object MetaUtil {

    private fun getAppMetaData(context: Context, key: String): String? {
        if (key.isEmpty()) {
            return null
        }
        return try {
            context.packageManager?.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                ?.metaData?.get(key)?.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun getWeChatAppId(context: Context): String? {
        return getAppMetaData(context, "wechat_app_id")
    }

    fun getWeChatSecret(context: Context): String? {
        return getAppMetaData(context, "wechat_app_secret")
    }
}