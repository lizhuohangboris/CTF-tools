package org.springframework.aop.aspectj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJWeaverMessageHandler.class */
public class AspectJWeaverMessageHandler implements IMessageHandler {
    private static final String AJ_ID = "[AspectJ] ";
    private static final Log logger = LogFactory.getLog("AspectJ Weaver");

    public boolean handleMessage(IMessage message) throws AbortException {
        IMessage.Kind messageKind = message.getKind();
        if (messageKind == IMessage.DEBUG) {
            if (logger.isDebugEnabled()) {
                logger.debug(makeMessageFor(message));
                return true;
            }
            return false;
        } else if (messageKind == IMessage.INFO || messageKind == IMessage.WEAVEINFO) {
            if (logger.isInfoEnabled()) {
                logger.info(makeMessageFor(message));
                return true;
            }
            return false;
        } else if (messageKind == IMessage.WARNING) {
            if (logger.isWarnEnabled()) {
                logger.warn(makeMessageFor(message));
                return true;
            }
            return false;
        } else if (messageKind == IMessage.ERROR) {
            if (logger.isErrorEnabled()) {
                logger.error(makeMessageFor(message));
                return true;
            }
            return false;
        } else if (messageKind == IMessage.ABORT && logger.isFatalEnabled()) {
            logger.fatal(makeMessageFor(message));
            return true;
        } else {
            return false;
        }
    }

    private String makeMessageFor(IMessage aMessage) {
        return AJ_ID + aMessage.getMessage();
    }

    public boolean isIgnoring(IMessage.Kind messageKind) {
        return false;
    }

    public void dontIgnore(IMessage.Kind messageKind) {
    }

    public void ignore(IMessage.Kind kind) {
    }
}