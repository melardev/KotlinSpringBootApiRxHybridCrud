package com.melardev.spring.restcrud.dtos.responses

class SuccessResponse @JvmOverloads constructor(message: String) : AppResponse(true) {

    init {
        addFullMessage(message)
    }
}
