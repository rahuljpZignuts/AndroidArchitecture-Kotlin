package com.test.rahul.persistance.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.test.rahul.data.proto.Authentication
import java.io.InputStream
import java.io.OutputStream

val Context.authenticationDataStore: DataStore<Authentication> by dataStore(
    fileName = "authentication.pb",
    serializer = AuthenticationSerializer,
)

object AuthenticationSerializer : Serializer<Authentication> {
    override val defaultValue: Authentication = Authentication.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Authentication {
        try {
            return Authentication.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Authentication, output: OutputStream) = t.writeTo(output)
}
