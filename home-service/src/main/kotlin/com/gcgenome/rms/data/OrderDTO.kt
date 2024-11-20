package com.gcgenome.rms.data

import java.util.UUID

data class OrderDTO(
    var id: UUID? = null,
    var user: UserDTO? = null
)
