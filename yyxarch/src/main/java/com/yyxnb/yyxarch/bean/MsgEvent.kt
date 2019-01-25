package com.yyxnb.yyxarch.bean

/**
 * RxBus data
 */
class MsgEvent {
    var id: Int = 0
    var status: String? = null
    var msg: Any? = null

    constructor() {}

    constructor(id: Int, msg: Any) {
        this.id = id
        this.msg = msg
    }

    constructor(status: String, msg: Any) {
        this.status = status
        this.msg = msg
    }
}