package blooom.cinnamon.cleanexplorers.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Fanny on 11/11/2020.
 **/
open class User : Parcelable {
    var id: String?
     var firstName: String?
    var lastName: String?
    var email: String?


    constructor(id: String?, firstName: String?, lastName: String?, email: String?) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }

    protected constructor(parcel: Parcel) {
        this.id = parcel.readString()
        this.firstName = parcel.readString()
        this.lastName = parcel.readString()
        this.email = parcel.readString()


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