package com.acquaintsoft.kotlinapi.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Meta {

    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("code")
    @Expose
    var code: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("count")
    @Expose
    var count: Int? = null
    @SerializedName("order_status")
    @Expose
    var orderStatus: String? = null
}
