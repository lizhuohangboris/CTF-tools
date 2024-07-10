package org.springframework.remoting.caucho;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;
import org.springframework.util.CommonsLogWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/caucho/HessianExporter.class */
public class HessianExporter extends RemoteExporter implements InitializingBean {
    public static final String CONTENT_TYPE_HESSIAN = "application/x-hessian";
    private SerializerFactory serializerFactory = new SerializerFactory();
    @Nullable
    private HessianRemoteResolver remoteResolver;
    @Nullable
    private Log debugLogger;
    @Nullable
    private HessianSkeleton skeleton;

    public void setSerializerFactory(@Nullable SerializerFactory serializerFactory) {
        this.serializerFactory = serializerFactory != null ? serializerFactory : new SerializerFactory();
    }

    public void setSendCollectionType(boolean sendCollectionType) {
        this.serializerFactory.setSendCollectionType(sendCollectionType);
    }

    public void setAllowNonSerializable(boolean allowNonSerializable) {
        this.serializerFactory.setAllowNonSerializable(allowNonSerializable);
    }

    public void setRemoteResolver(HessianRemoteResolver remoteResolver) {
        this.remoteResolver = remoteResolver;
    }

    public void setDebug(boolean debug) {
        this.debugLogger = debug ? this.logger : null;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        prepare();
    }

    public void prepare() {
        checkService();
        checkServiceInterface();
        this.skeleton = new HessianSkeleton(getProxyForService(), getServiceInterface());
    }

    public void invoke(InputStream inputStream, OutputStream outputStream) throws Throwable {
        Assert.notNull(this.skeleton, "Hessian exporter has not been initialized");
        doInvoke(this.skeleton, inputStream, outputStream);
    }

    protected void doInvoke(HessianSkeleton skeleton, InputStream inputStream, OutputStream outputStream) throws Throwable {
        HessianInput hessianInput;
        HessianOutput hessianOutput;
        ClassLoader originalClassLoader = overrideThreadContextClassLoader();
        try {
            InputStream isToUse = inputStream;
            OutputStream osToUse = outputStream;
            if (this.debugLogger != null && this.debugLogger.isDebugEnabled()) {
                PrintWriter debugWriter = new PrintWriter(new CommonsLogWriter(this.debugLogger));
                InputStream hessianDebugInputStream = new HessianDebugInputStream(inputStream, debugWriter);
                OutputStream hessianDebugOutputStream = new HessianDebugOutputStream(outputStream, debugWriter);
                hessianDebugInputStream.startTop2();
                hessianDebugOutputStream.startTop2();
                isToUse = hessianDebugInputStream;
                osToUse = hessianDebugOutputStream;
                if (debugWriter != null) {
                    if (0 != 0) {
                        debugWriter.close();
                    } else {
                        debugWriter.close();
                    }
                }
            }
            if (!isToUse.markSupported()) {
                isToUse = new BufferedInputStream(isToUse);
                isToUse.mark(1);
            }
            int code = isToUse.read();
            if (code == 72) {
                int major = isToUse.read();
                int minor = isToUse.read();
                if (major != 2) {
                    throw new IOException("Version " + major + '.' + minor + " is not understood");
                }
                hessianInput = new Hessian2Input(isToUse);
                hessianOutput = new Hessian2Output(osToUse);
                hessianInput.readCall();
            } else if (code == 67) {
                isToUse.reset();
                hessianInput = new Hessian2Input(isToUse);
                hessianOutput = new Hessian2Output(osToUse);
                hessianInput.readCall();
            } else if (code == 99) {
                int major2 = isToUse.read();
                isToUse.read();
                hessianInput = new HessianInput(isToUse);
                if (major2 >= 2) {
                    hessianOutput = new Hessian2Output(osToUse);
                } else {
                    hessianOutput = new HessianOutput(osToUse);
                }
            } else {
                throw new IOException("Expected 'H'/'C' (Hessian 2.0) or 'c' (Hessian 1.0) in hessian input at " + code);
            }
            hessianInput.setSerializerFactory(this.serializerFactory);
            hessianOutput.setSerializerFactory(this.serializerFactory);
            if (this.remoteResolver != null) {
                hessianInput.setRemoteResolver(this.remoteResolver);
            }
            skeleton.invoke(hessianInput, hessianOutput);
            try {
                hessianInput.close();
                isToUse.close();
            } catch (IOException e) {
            }
            try {
                hessianOutput.close();
                osToUse.close();
            } catch (IOException e2) {
            }
        } finally {
            resetThreadContextClassLoader(originalClassLoader);
        }
    }
}