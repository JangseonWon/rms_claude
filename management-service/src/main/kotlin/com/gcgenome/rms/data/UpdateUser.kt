package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateUser(
    val id: String,
    val name: String?,
    val password: Password?,
    val role: String?,
    val type: String?,
    val email: String?,
    @JsonProperty("phone_number")
    val phoneNumber: String?,
    val state: String?
){
    companion object{
        data class Password(
            @JsonProperty("newPassword")
            var password: String,
            @JsonProperty("confirmPassword")
            val passwordConfirm: String,
        )
    }
}
