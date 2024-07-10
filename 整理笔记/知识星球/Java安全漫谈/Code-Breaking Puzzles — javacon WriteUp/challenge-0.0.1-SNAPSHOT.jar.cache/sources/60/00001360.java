package org.springframework.asm;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/asm/Handler.class */
final class Handler {
    final Label startPc;
    final Label endPc;
    final Label handlerPc;
    final int catchType;
    final String catchTypeDescriptor;
    Handler nextHandler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Handler(Label startPc, Label endPc, Label handlerPc, int catchType, String catchTypeDescriptor) {
        this.startPc = startPc;
        this.endPc = endPc;
        this.handlerPc = handlerPc;
        this.catchType = catchType;
        this.catchTypeDescriptor = catchTypeDescriptor;
    }

    Handler(Handler handler, Label startPc, Label endPc) {
        this(startPc, endPc, handler.handlerPc, handler.catchType, handler.catchTypeDescriptor);
        this.nextHandler = handler.nextHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Handler removeRange(Handler firstHandler, Label start, Label end) {
        if (firstHandler == null) {
            return null;
        }
        firstHandler.nextHandler = removeRange(firstHandler.nextHandler, start, end);
        int handlerStart = firstHandler.startPc.bytecodeOffset;
        int handlerEnd = firstHandler.endPc.bytecodeOffset;
        int rangeStart = start.bytecodeOffset;
        int rangeEnd = end == null ? Integer.MAX_VALUE : end.bytecodeOffset;
        if (rangeStart >= handlerEnd || rangeEnd <= handlerStart) {
            return firstHandler;
        }
        if (rangeStart <= handlerStart) {
            if (rangeEnd >= handlerEnd) {
                return firstHandler.nextHandler;
            }
            return new Handler(firstHandler, end, firstHandler.endPc);
        } else if (rangeEnd >= handlerEnd) {
            return new Handler(firstHandler, firstHandler.startPc, start);
        } else {
            firstHandler.nextHandler = new Handler(firstHandler, end, firstHandler.endPc);
            return new Handler(firstHandler, firstHandler.startPc, start);
        }
    }

    static int getExceptionTableLength(Handler firstHandler) {
        int length = 0;
        Handler handler = firstHandler;
        while (true) {
            Handler handler2 = handler;
            if (handler2 != null) {
                length++;
                handler = handler2.nextHandler;
            } else {
                return length;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getExceptionTableSize(Handler firstHandler) {
        return 2 + (8 * getExceptionTableLength(firstHandler));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putExceptionTable(Handler firstHandler, ByteVector output) {
        output.putShort(getExceptionTableLength(firstHandler));
        Handler handler = firstHandler;
        while (true) {
            Handler handler2 = handler;
            if (handler2 != null) {
                output.putShort(handler2.startPc.bytecodeOffset).putShort(handler2.endPc.bytecodeOffset).putShort(handler2.handlerPc.bytecodeOffset).putShort(handler2.catchType);
                handler = handler2.nextHandler;
            } else {
                return;
            }
        }
    }
}