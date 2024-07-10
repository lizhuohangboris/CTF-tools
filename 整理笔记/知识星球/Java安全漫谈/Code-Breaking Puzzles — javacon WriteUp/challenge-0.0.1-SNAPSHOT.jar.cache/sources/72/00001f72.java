package org.springframework.expression.spel.standard;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.cglib.core.Constants;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompiledExpression;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/standard/SpelCompiler.class */
public final class SpelCompiler implements Opcodes {
    private static final int CLASSES_DEFINED_LIMIT = 100;
    private ChildClassLoader ccl;
    private final AtomicInteger suffixId = new AtomicInteger(1);
    private static final Log logger = LogFactory.getLog(SpelCompiler.class);
    private static final Map<ClassLoader, SpelCompiler> compilers = new ConcurrentReferenceHashMap();

    private SpelCompiler(@Nullable ClassLoader classloader) {
        this.ccl = new ChildClassLoader(classloader);
    }

    @Nullable
    public CompiledExpression compile(SpelNodeImpl expression) {
        if (expression.isCompilable()) {
            if (logger.isDebugEnabled()) {
                logger.debug("SpEL: compiling " + expression.toStringAST());
            }
            Class<? extends CompiledExpression> clazz = createExpressionClass(expression);
            if (clazz != null) {
                try {
                    return (CompiledExpression) ReflectionUtils.accessibleConstructor(clazz, new Class[0]).newInstance(new Object[0]);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Failed to instantiate CompiledExpression", ex);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("SpEL: unable to compile " + expression.toStringAST());
            return null;
        }
        return null;
    }

    private int getNextSuffix() {
        return this.suffixId.incrementAndGet();
    }

    @Nullable
    private Class<? extends CompiledExpression> createExpressionClass(SpelNodeImpl expressionToCompile) {
        String className = "spel/Ex" + getNextSuffix();
        ClassWriter cw = new ExpressionClassWriter();
        cw.visit(49, 1, className, null, "org/springframework/expression/spel/CompiledExpression", null);
        MethodVisitor mv = cw.visitMethod(1, Constants.CONSTRUCTOR_NAME, "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/springframework/expression/spel/CompiledExpression", Constants.CONSTRUCTOR_NAME, "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        MethodVisitor mv2 = cw.visitMethod(1, "getValue", "(Ljava/lang/Object;Lorg/springframework/expression/EvaluationContext;)Ljava/lang/Object;", null, new String[]{"org/springframework/expression/EvaluationException"});
        mv2.visitCode();
        CodeFlow cf = new CodeFlow(className, cw);
        try {
            expressionToCompile.generateCode(mv2, cf);
            CodeFlow.insertBoxIfNecessary(mv2, cf.lastDescriptor());
            if ("V".equals(cf.lastDescriptor())) {
                mv2.visitInsn(1);
            }
            mv2.visitInsn(176);
            mv2.visitMaxs(0, 0);
            mv2.visitEnd();
            cw.visitEnd();
            cf.finish();
            byte[] data = cw.toByteArray();
            return loadClass(StringUtils.replace(className, "/", "."), data);
        } catch (IllegalStateException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(expressionToCompile.getClass().getSimpleName() + ".generateCode opted out of compilation: " + ex.getMessage());
                return null;
            }
            return null;
        }
    }

    private Class<? extends CompiledExpression> loadClass(String name, byte[] bytes) {
        if (this.ccl.getClassesDefinedCount() > 100) {
            this.ccl = new ChildClassLoader(this.ccl.getParent());
        }
        return this.ccl.defineClass(name, bytes);
    }

    public static SpelCompiler getCompiler(@Nullable ClassLoader classLoader) {
        SpelCompiler spelCompiler;
        ClassLoader clToUse = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
        synchronized (compilers) {
            SpelCompiler compiler = compilers.get(clToUse);
            if (compiler == null) {
                compiler = new SpelCompiler(clToUse);
                compilers.put(clToUse, compiler);
            }
            spelCompiler = compiler;
        }
        return spelCompiler;
    }

    public static boolean compile(Expression expression) {
        return (expression instanceof SpelExpression) && ((SpelExpression) expression).compileExpression();
    }

    public static void revertToInterpreted(Expression expression) {
        if (expression instanceof SpelExpression) {
            ((SpelExpression) expression).revertToInterpreted();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/standard/SpelCompiler$ChildClassLoader.class */
    public static class ChildClassLoader extends URLClassLoader {
        private static final URL[] NO_URLS = new URL[0];
        private int classesDefinedCount;

        public ChildClassLoader(@Nullable ClassLoader classLoader) {
            super(NO_URLS, classLoader);
            this.classesDefinedCount = 0;
        }

        int getClassesDefinedCount() {
            return this.classesDefinedCount;
        }

        public Class<?> defineClass(String name, byte[] bytes) {
            Class<?> clazz = super.defineClass(name, bytes, 0, bytes.length);
            this.classesDefinedCount++;
            return clazz;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/standard/SpelCompiler$ExpressionClassWriter.class */
    public class ExpressionClassWriter extends ClassWriter {
        public ExpressionClassWriter() {
            super(3);
        }

        @Override // org.springframework.asm.ClassWriter
        protected ClassLoader getClassLoader() {
            return SpelCompiler.this.ccl;
        }
    }
}