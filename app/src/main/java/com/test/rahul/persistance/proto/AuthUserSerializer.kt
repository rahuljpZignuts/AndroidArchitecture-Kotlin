package com.test.rahul.persistance.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.test.rahul.data.proto.AuthUser
import java.io.InputStream
import java.io.OutputStream

val Context.authUserDataStore: DataStore<AuthUser> by dataStore(
    fileName = "auth_user.pb",
    serializer = AuthUserSerializer,
)

object AuthUserSerializer : Serializer<AuthUser> {
    override val defaultValue: AuthUser = AuthUser.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AuthUser {
        try {
            return AuthUser.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AuthUser, output: OutputStream) = t.writeTo(output)
}
