package flab.tickethub.support.error

import flab.tickethub.support.response.ErrorResult
import flab.tickethub.support.response.FieldData
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApiException::class)
    fun handelApiException(e: ApiException): ResponseEntity<ErrorResult<Any?>> {
        val errorCode = e.errorCode

        logging(
            errorCode = errorCode,
            throwable = e
        )

        return ErrorResult.of(e.errorCode, e.data)
    }

    /**
     * validation 예외 처리
     *
     * @param e validation 예외
     * @return 에러 필드를 포함한 Http 400 BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResult<Any?>> {
        logging(
            errorCode = ErrorCode.INVALID_INPUT_VALUE,
            throwable = e
        )

        val data = e.bindingResult.fieldErrors.map { FieldData.of(it) }

        return ErrorResult.of(ErrorCode.INVALID_INPUT_VALUE, data)
    }

    private fun logging(
        errorCode: ErrorCode,
        throwable: Throwable
    ) {
        when (errorCode.logLevel) {
            LogLevel.ERROR -> logger.error(
                "${throwable.javaClass.simpleName} : ${throwable.message}",
                throwable
            )

            LogLevel.WARN -> logger.warn(
                "${throwable.javaClass.simpleName} : ${throwable.message}",
                throwable
            )

            else -> logger.info(
                "${throwable.javaClass.simpleName} : ${throwable.message}",
                throwable
            )
        }
    }

}