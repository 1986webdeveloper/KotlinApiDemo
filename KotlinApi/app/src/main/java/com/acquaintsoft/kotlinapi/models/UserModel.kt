package com.acquaintsoft.kotlinapi.models

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by chetan on 27/11/17.
 */

class UserModel {

    @SerializedName("meta")
    @Expose
    lateinit var meta: Meta
    @SerializedName("data")
    @Expose
    lateinit var userDetails: UserDetails


    class UserDetails : Parcelable {

        @SerializedName("id")
        @Expose
        lateinit var id: String
        @SerializedName("name")
        @Expose
        lateinit var name: String
        @SerializedName("mobile")
        @Expose
        lateinit var mobile: String
        @SerializedName("email")
        @Expose
        lateinit var email: String
        @SerializedName("user_image")
        @Expose
        lateinit var userImage: String


        constructor() {

        }

        protected constructor(`in`: Parcel) {
            id = `in`.readString()
            name = `in`.readString()
            mobile = `in`.readString()
            email = `in`.readString()
            userImage = `in`.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(id)
            parcel.writeString(name)
            parcel.writeString(mobile)
            parcel.writeString(email)
            parcel.writeString(userImage)
        }

        companion object {


            @JvmField
            val CREATOR: Parcelable.Creator<UserDetails> = object : Parcelable.Creator<UserDetails> {
                override fun createFromParcel(`in`: Parcel): UserDetails {
                    return UserDetails(`in`)
                }

                override fun newArray(size: Int): Array<UserDetails?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }

}
