package com.rodolfonavalon.canadatransit.model.transit.response

import com.rodolfonavalon.canadatransit.model.transit.Meta

abstract class MetaResponse<MODEL> {
    abstract val meta: Meta
    abstract val response: List<MODEL>
}
