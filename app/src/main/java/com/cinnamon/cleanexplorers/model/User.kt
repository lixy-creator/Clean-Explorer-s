package com.cinnamon.cleanexplorers.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Fanny on 11/11/2020.
 **/
open class User : Parcelable {
    var id: String?
     var pseudo: String?
    var email: String?
    var token: String?


    constructor(id: String?, firstName: String?, email: String?, token:String?) {
        this.id = id
        this.pseudo = firstName
        this.email = email
        this.token=token
    }

    protected constructor(parcel: Parcel) {
        this.id = parcel.readString()
        this.pseudo = parcel.readString()
        this.email = parcel.readString()
        this.token=parcel.readString()


    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }



}