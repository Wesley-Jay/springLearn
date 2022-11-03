package com.example.product.service.exceptionHandle;

import com.example.product.entity.CustomException;
import com.example.product.entity.ResultEnum;
import com.example.product.entity.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author wsj
 * @implNote 异常处理类
 * @date 2022/11/3
 */
@ControllerAdvice
public class ExceptionHandle {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
    private final static String  DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(HttpServletRequest request, Exception e) {
        if (e instanceof CustomException) {
            CustomException customException = (CustomException) e;
            return ResultUtil.error(customException.getCode(),customException.getMessage());
        } else {
            ResultEnum systemException = ResultEnum.SystemException;
            return ResultUtil.error(systemException.getCode(),systemException.getDescribe());
        }
    }
}
