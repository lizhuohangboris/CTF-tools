package org.jboss.logging;

import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Slf4jLogger.class */
final class Slf4jLogger extends Logger {
    private static final long serialVersionUID = 8685757928087758380L;
    private final org.slf4j.Logger logger;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Slf4jLogger(String name, org.slf4j.Logger logger) {
        super(name);
        this.logger = logger;
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isEnabled(Logger.Level level) {
        if (level == Logger.Level.TRACE) {
            return this.logger.isTraceEnabled();
        }
        if (level == Logger.Level.DEBUG) {
            return this.logger.isDebugEnabled();
        }
        return infoOrHigherEnabled(level);
    }

    private boolean infoOrHigherEnabled(Logger.Level level) {
        if (level == Logger.Level.INFO) {
            return this.logger.isInfoEnabled();
        }
        if (level == Logger.Level.WARN) {
            return this.logger.isWarnEnabled();
        }
        if (level == Logger.Level.ERROR || level == Logger.Level.FATAL) {
            return this.logger.isErrorEnabled();
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x002c A[Catch: Throwable -> 0x009f, TryCatch #0 {Throwable -> 0x009f, blocks: (B:6:0x000d, B:9:0x001a, B:10:0x0023, B:12:0x002c, B:13:0x003c, B:15:0x0043, B:16:0x0053, B:18:0x005a, B:21:0x0071, B:23:0x0078, B:24:0x0088, B:26:0x008f, B:20:0x0061, B:8:0x0013), top: B:30:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x003c A[Catch: Throwable -> 0x009f, TryCatch #0 {Throwable -> 0x009f, blocks: (B:6:0x000d, B:9:0x001a, B:10:0x0023, B:12:0x002c, B:13:0x003c, B:15:0x0043, B:16:0x0053, B:18:0x005a, B:21:0x0071, B:23:0x0078, B:24:0x0088, B:26:0x008f, B:20:0x0061, B:8:0x0013), top: B:30:0x000d }] */
    @Override // org.jboss.logging.Logger
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void doLog(org.jboss.logging.Logger.Level r5, java.lang.String r6, java.lang.Object r7, java.lang.Object[] r8, java.lang.Throwable r9) {
        /*
            r4 = this;
            r0 = r4
            r1 = r5
            boolean r0 = r0.isEnabled(r1)
            if (r0 == 0) goto La1
            r0 = r8
            if (r0 == 0) goto L13
            r0 = r8
            int r0 = r0.length     // Catch: java.lang.Throwable -> L9f
            if (r0 != 0) goto L1a
        L13:
            r0 = r7
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch: java.lang.Throwable -> L9f
            goto L23
        L1a:
            r0 = r7
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch: java.lang.Throwable -> L9f
            r1 = r8
            java.lang.String r0 = java.text.MessageFormat.format(r0, r1)     // Catch: java.lang.Throwable -> L9f
        L23:
            r10 = r0
            r0 = r5
            org.jboss.logging.Logger$Level r1 = org.jboss.logging.Logger.Level.INFO     // Catch: java.lang.Throwable -> L9f
            if (r0 != r1) goto L3c
            r0 = r4
            org.slf4j.Logger r0 = r0.logger     // Catch: java.lang.Throwable -> L9f
            r1 = r10
            r2 = r9
            r0.info(r1, r2)     // Catch: java.lang.Throwable -> L9f
            goto L9c
        L3c:
            r0 = r5
            org.jboss.logging.Logger$Level r1 = org.jboss.logging.Logger.Level.WARN     // Catch: java.lang.Throwable -> L9f
            if (r0 != r1) goto L53
            r0 = r4
            org.slf4j.Logger r0 = r0.logger     // Catch: java.lang.Throwable -> L9f
            r1 = r10
            r2 = r9
            r0.warn(r1, r2)     // Catch: java.lang.Throwable -> L9f
            goto L9c
        L53:
            r0 = r5
            org.jboss.logging.Logger$Level r1 = org.jboss.logging.Logger.Level.ERROR     // Catch: java.lang.Throwable -> L9f
            if (r0 == r1) goto L61
            r0 = r5
            org.jboss.logging.Logger$Level r1 = org.jboss.logging.Logger.Level.FATAL     // Catch: java.lang.Throwable -> L9f
            if (r0 != r1) goto L71
        L61:
            r0 = r4
            org.slf4j.Logger r0 = r0.logger     // Catch: java.lang.Throwable -> L9f
            r1 = r10
            r2 = r9
            r0.error(r1, r2)     // Catch: java.lang.Throwable -> L9f
            goto L9c
        L71:
            r0 = r5
            org.jboss.logging.Logger$Level r1 = org.jboss.logging.Logger.Level.DEBUG     // Catch: java.lang.Throwable -> L9f
            if (r0 != r1) goto L88
            r0 = r4
            org.slf4j.Logger r0 = r0.logger     // Catch: java.lang.Throwable -> L9f
            r1 = r10
            r2 = r9
            r0.debug(r1, r2)     // Catch: java.lang.Throwable -> L9f
            goto L9c
        L88:
            r0 = r5
            org.jboss.logging.Logger$Level r1 = org.jboss.logging.Logger.Level.TRACE     // Catch: java.lang.Throwable -> L9f
            if (r0 != r1) goto L9c
            r0 = r4
            org.slf4j.Logger r0 = r0.logger     // Catch: java.lang.Throwable -> L9f
            r1 = r10
            r2 = r9
            r0.debug(r1, r2)     // Catch: java.lang.Throwable -> L9f
        L9c:
            goto La1
        L9f:
            r10 = move-exception
        La1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.logging.Slf4jLogger.doLog(org.jboss.logging.Logger$Level, java.lang.String, java.lang.Object, java.lang.Object[], java.lang.Throwable):void");
    }

    @Override // org.jboss.logging.Logger
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        if (isEnabled(level)) {
            try {
                String text = parameters == null ? String.format(format, new Object[0]) : String.format(format, parameters);
                if (level == Logger.Level.INFO) {
                    this.logger.info(text, thrown);
                } else if (level == Logger.Level.WARN) {
                    this.logger.warn(text, thrown);
                } else if (level == Logger.Level.ERROR || level == Logger.Level.FATAL) {
                    this.logger.error(text, thrown);
                } else if (level == Logger.Level.DEBUG) {
                    this.logger.debug(text, thrown);
                } else if (level == Logger.Level.TRACE) {
                    this.logger.debug(text, thrown);
                }
            } catch (Throwable th) {
            }
        }
    }
}