package io.mycat.fabric.phdc.ctrl.config;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.util.NestedServletException;

import io.mycat.fabric.phdc.exception.BuzException;


@ControllerAdvice
public class GlobalExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * rest 业务错误处理
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({Exception.class})
	@ResponseBody
	public ResponseContent handleRestRespErrorException(HttpServletRequest request, Exception ex) {
		logger.error("GlobalExceptionHandler 处理错误 => [ex->{}]",ex.getMessage());
	 	if (ex instanceof NestedServletException) {
	 		NestedServletException nestedServletException = (NestedServletException)ex;
	 		if (nestedServletException.getCause() instanceof OutOfMemoryError) {
	 			return new ResponseContent(ResponseCode.FAILED.getValue(), null,"服务器繁忙");
	 		}
		}
	 	BuzException exception = null;
		if (ex instanceof BuzException) {
			exception = (BuzException) ex;
			return new ResponseContent(exception.getCode(),null,exception.getMessage());
		}
		if (ex instanceof MultipartException) {
			return new ResponseContent(ResponseCode.FAILED,null);
		}
		return new ResponseContent(ResponseCode.FAILED, null);
	}
}
