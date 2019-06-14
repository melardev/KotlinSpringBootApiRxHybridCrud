package com.melardev.spring.restcrud.dtos.responses

class ErrorResponse(errorMessage: String) : AppResponse(false) {

    init {
        addFullMessage(errorMessage)
    }

}
