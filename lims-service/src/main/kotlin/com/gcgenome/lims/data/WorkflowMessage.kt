package com.gcgenome.lims.data

import java.time.LocalDateTime

data class WorkflowMessage(
    var request: WorkflowRequest,
    var process: String,
    var type: String
)
data class WorkflowRequest(
    var requester: WorkflowRequester,
    var service: WorkflowService,
    var samples: List<WorkflowSample>,
    var dateReception: LocalDateTime?
)
data class WorkflowRequester(
    var code: String
)
data class WorkflowService(
    var id: String
)
data class WorkflowSample (
    var id: Long
)

