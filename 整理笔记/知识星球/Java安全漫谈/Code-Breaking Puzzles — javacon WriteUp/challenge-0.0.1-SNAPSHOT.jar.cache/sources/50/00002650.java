package org.springframework.web.servlet.mvc.method.annotation;

import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseEntityExceptionHandler.class */
public abstract class ResponseEntityExceptionHandler {
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    protected static final Log pageNotFoundLogger = LogFactory.getLog("org.springframework.web.servlet.PageNotFound");
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class, MissingPathVariableException.class, MissingServletRequestParameterException.class, ServletRequestBindingException.class, ConversionNotSupportedException.class, TypeMismatchException.class, HttpMessageNotReadableException.class, HttpMessageNotWritableException.class, MethodArgumentNotValidException.class, MissingServletRequestPartException.class, BindException.class, NoHandlerFoundException.class, AsyncRequestTimeoutException.class})
    public final ResponseEntity<Object> handleException(Exception ex, WebRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
            return handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex, headers, status, request);
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            HttpStatus status2 = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            return handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) ex, headers, status2, request);
        } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
            HttpStatus status3 = HttpStatus.NOT_ACCEPTABLE;
            return handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException) ex, headers, status3, request);
        } else if (ex instanceof MissingPathVariableException) {
            HttpStatus status4 = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleMissingPathVariable((MissingPathVariableException) ex, headers, status4, request);
        } else if (ex instanceof MissingServletRequestParameterException) {
            HttpStatus status5 = HttpStatus.BAD_REQUEST;
            return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, headers, status5, request);
        } else if (ex instanceof ServletRequestBindingException) {
            HttpStatus status6 = HttpStatus.BAD_REQUEST;
            return handleServletRequestBindingException((ServletRequestBindingException) ex, headers, status6, request);
        } else if (ex instanceof ConversionNotSupportedException) {
            HttpStatus status7 = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleConversionNotSupported((ConversionNotSupportedException) ex, headers, status7, request);
        } else if (ex instanceof TypeMismatchException) {
            HttpStatus status8 = HttpStatus.BAD_REQUEST;
            return handleTypeMismatch((TypeMismatchException) ex, headers, status8, request);
        } else if (ex instanceof HttpMessageNotReadableException) {
            HttpStatus status9 = HttpStatus.BAD_REQUEST;
            return handleHttpMessageNotReadable((HttpMessageNotReadableException) ex, headers, status9, request);
        } else if (ex instanceof HttpMessageNotWritableException) {
            HttpStatus status10 = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleHttpMessageNotWritable((HttpMessageNotWritableException) ex, headers, status10, request);
        } else if (ex instanceof MethodArgumentNotValidException) {
            HttpStatus status11 = HttpStatus.BAD_REQUEST;
            return handleMethodArgumentNotValid((MethodArgumentNotValidException) ex, headers, status11, request);
        } else if (ex instanceof MissingServletRequestPartException) {
            HttpStatus status12 = HttpStatus.BAD_REQUEST;
            return handleMissingServletRequestPart((MissingServletRequestPartException) ex, headers, status12, request);
        } else if (ex instanceof BindException) {
            HttpStatus status13 = HttpStatus.BAD_REQUEST;
            return handleBindException((BindException) ex, headers, status13, request);
        } else if (ex instanceof NoHandlerFoundException) {
            HttpStatus status14 = HttpStatus.NOT_FOUND;
            return handleNoHandlerFoundException((NoHandlerFoundException) ex, headers, status14, request);
        } else if (ex instanceof AsyncRequestTimeoutException) {
            HttpStatus status15 = HttpStatus.SERVICE_UNAVAILABLE;
            return handleAsyncRequestTimeoutException((AsyncRequestTimeoutException) ex, headers, status15, request);
        } else {
            throw ex;
        }
    }

    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        pageNotFoundLogger.warn(ex.getMessage());
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
            HttpServletResponse response = servletWebRequest.getResponse();
            if (response != null && response.isCommitted()) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Async request timed out");
                    return null;
                }
                return null;
            }
        }
        return handleExceptionInternal(ex, null, headers, status, webRequest);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }
        return new ResponseEntity<>(body, (MultiValueMap<String, String>) headers, status);
    }
}