package com.yyxnb.yyxarch.http.exception

class ServerException(val errCode: Int, override val message: String) : RuntimeException(message)