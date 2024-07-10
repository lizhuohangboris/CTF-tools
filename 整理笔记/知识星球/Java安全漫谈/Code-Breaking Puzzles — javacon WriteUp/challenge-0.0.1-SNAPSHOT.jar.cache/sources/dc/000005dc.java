package javax.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELProcessor.class */
public class ELProcessor {
    private static final Set<String> PRIMITIVES = new HashSet();
    private static final String[] EMPTY_STRING_ARRAY;
    private final ELManager manager = new ELManager();
    private final ELContext context = this.manager.getELContext();
    private final ExpressionFactory factory = ELManager.getExpressionFactory();

    static {
        PRIMITIVES.add("boolean");
        PRIMITIVES.add("byte");
        PRIMITIVES.add("char");
        PRIMITIVES.add("double");
        PRIMITIVES.add("float");
        PRIMITIVES.add("int");
        PRIMITIVES.add("long");
        PRIMITIVES.add("short");
        EMPTY_STRING_ARRAY = new String[0];
    }

    public ELManager getELManager() {
        return this.manager;
    }

    public Object eval(String expression) {
        return getValue(expression, Object.class);
    }

    public Object getValue(String expression, Class<?> expectedType) {
        ValueExpression ve = this.factory.createValueExpression(this.context, bracket(expression), expectedType);
        return ve.getValue(this.context);
    }

    public void setValue(String expression, Object value) {
        ValueExpression ve = this.factory.createValueExpression(this.context, bracket(expression), Object.class);
        ve.setValue(this.context, value);
    }

    public void setVariable(String variable, String expression) {
        if (expression == null) {
            this.manager.setVariable(variable, null);
            return;
        }
        ValueExpression ve = this.factory.createValueExpression(this.context, bracket(expression), Object.class);
        this.manager.setVariable(variable, ve);
    }

    public void defineFunction(String prefix, String function, String className, String methodName) throws ClassNotFoundException, NoSuchMethodException {
        if (prefix == null || function == null || className == null || methodName == null) {
            throw new NullPointerException(Util.message(this.context, "elProcessor.defineFunctionNullParams", new Object[0]));
        }
        Class<?> clazz = this.context.getImportHandler().resolveClass(className);
        if (clazz == null) {
            clazz = Class.forName(className, true, Util.getContextClassLoader());
        }
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new ClassNotFoundException(Util.message(this.context, "elProcessor.defineFunctionInvalidClass", className));
        }
        MethodSignature sig = new MethodSignature(this.context, methodName, className);
        if (function.length() == 0) {
            function = sig.getName();
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals(sig.getName())) {
                if (sig.getParamTypeNames() == null) {
                    this.manager.mapFunction(prefix, function, method);
                    return;
                } else if (sig.getParamTypeNames().length != method.getParameterTypes().length) {
                    continue;
                } else if (sig.getParamTypeNames().length == 0) {
                    this.manager.mapFunction(prefix, function, method);
                    return;
                } else {
                    Class<?>[] types = method.getParameterTypes();
                    String[] typeNames = sig.getParamTypeNames();
                    if (types.length == typeNames.length) {
                        boolean match = true;
                        int i = 0;
                        while (true) {
                            if (i >= types.length) {
                                break;
                            }
                            if (i == types.length - 1 && method.isVarArgs()) {
                                String typeName = typeNames[i];
                                if (typeName.endsWith("...")) {
                                    if (!typeName.substring(0, typeName.length() - 3).equals(types[i].getName())) {
                                        match = false;
                                    }
                                } else {
                                    match = false;
                                }
                            } else if (!types[i].getName().equals(typeNames[i])) {
                                match = false;
                                break;
                            }
                            i++;
                        }
                        if (match) {
                            this.manager.mapFunction(prefix, function, method);
                            return;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        throw new NoSuchMethodException(Util.message(this.context, "elProcessor.defineFunctionNoMethod", methodName, className));
    }

    public void defineFunction(String prefix, String function, Method method) throws NoSuchMethodException {
        if (prefix == null || function == null || method == null) {
            throw new NullPointerException(Util.message(this.context, "elProcessor.defineFunctionNullParams", new Object[0]));
        }
        int modifiers = method.getModifiers();
        if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
            throw new NoSuchMethodException(Util.message(this.context, "elProcessor.defineFunctionInvalidMethod", method.getName(), method.getDeclaringClass().getName()));
        }
        this.manager.mapFunction(prefix, function, method);
    }

    public void defineBean(String name, Object bean) {
        this.manager.defineBean(name, bean);
    }

    private static String bracket(String expression) {
        return "${" + expression + "}";
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELProcessor$MethodSignature.class */
    private static class MethodSignature {
        private final String name;
        private final String[] parameterTypeNames;

        public MethodSignature(ELContext context, String methodName, String className) throws NoSuchMethodException {
            int paramIndex = methodName.indexOf(40);
            if (paramIndex == -1) {
                this.name = methodName.trim();
                this.parameterTypeNames = null;
                return;
            }
            String returnTypeAndName = methodName.substring(0, paramIndex).trim();
            int wsPos = -1;
            int i = 0;
            while (true) {
                if (i < returnTypeAndName.length()) {
                    if (!Character.isWhitespace(returnTypeAndName.charAt(i))) {
                        i++;
                    } else {
                        wsPos = i;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (wsPos == -1) {
                throw new NoSuchMethodException();
            }
            this.name = returnTypeAndName.substring(wsPos).trim();
            String paramString = methodName.substring(paramIndex).trim();
            if (!paramString.endsWith(")")) {
                throw new NoSuchMethodException(Util.message(context, "elProcessor.defineFunctionInvalidParameterList", paramString, methodName, className));
            }
            String paramString2 = paramString.substring(1, paramString.length() - 1).trim();
            if (paramString2.length() == 0) {
                this.parameterTypeNames = ELProcessor.EMPTY_STRING_ARRAY;
                return;
            }
            this.parameterTypeNames = paramString2.split(",");
            ImportHandler importHandler = context.getImportHandler();
            for (int i2 = 0; i2 < this.parameterTypeNames.length; i2++) {
                String parameterTypeName = this.parameterTypeNames[i2].trim();
                int dimension = 0;
                int bracketPos = parameterTypeName.indexOf(91);
                if (bracketPos > -1) {
                    String parameterTypeNameOnly = parameterTypeName.substring(0, bracketPos).trim();
                    while (bracketPos > -1) {
                        dimension++;
                        bracketPos = parameterTypeName.indexOf(91, bracketPos + 1);
                    }
                    parameterTypeName = parameterTypeNameOnly;
                }
                boolean varArgs = false;
                if (parameterTypeName.endsWith("...")) {
                    varArgs = true;
                    dimension = 1;
                    parameterTypeName = parameterTypeName.substring(0, parameterTypeName.length() - 3).trim();
                }
                boolean isPrimitive = ELProcessor.PRIMITIVES.contains(parameterTypeName);
                if (isPrimitive && dimension > 0) {
                    String str = parameterTypeName;
                    boolean z = true;
                    switch (str.hashCode()) {
                        case -1325958191:
                            if (str.equals("double")) {
                                z = true;
                                break;
                            }
                            break;
                        case 104431:
                            if (str.equals("int")) {
                                z = true;
                                break;
                            }
                            break;
                        case 3039496:
                            if (str.equals("byte")) {
                                z = true;
                                break;
                            }
                            break;
                        case 3052374:
                            if (str.equals("char")) {
                                z = true;
                                break;
                            }
                            break;
                        case 3327612:
                            if (str.equals("long")) {
                                z = true;
                                break;
                            }
                            break;
                        case 64711720:
                            if (str.equals("boolean")) {
                                z = false;
                                break;
                            }
                            break;
                        case 97526364:
                            if (str.equals("float")) {
                                z = true;
                                break;
                            }
                            break;
                        case 109413500:
                            if (str.equals("short")) {
                                z = true;
                                break;
                            }
                            break;
                    }
                    switch (z) {
                        case false:
                            parameterTypeName = "Z";
                            break;
                        case true:
                            parameterTypeName = "B";
                            break;
                        case true:
                            parameterTypeName = "C";
                            break;
                        case true:
                            parameterTypeName = "D";
                            break;
                        case true:
                            parameterTypeName = "F";
                            break;
                        case true:
                            parameterTypeName = "I";
                            break;
                        case true:
                            parameterTypeName = "J";
                            break;
                        case true:
                            parameterTypeName = "S";
                            break;
                    }
                } else if (!isPrimitive && !parameterTypeName.contains(".")) {
                    Class<?> clazz = importHandler.resolveClass(parameterTypeName);
                    if (clazz == null) {
                        throw new NoSuchMethodException(Util.message(context, "elProcessor.defineFunctionInvalidParameterTypeName", this.parameterTypeNames[i2], methodName, className));
                    }
                    parameterTypeName = clazz.getName();
                }
                if (dimension > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < dimension; j++) {
                        sb.append('[');
                    }
                    if (!isPrimitive) {
                        sb.append('L');
                    }
                    sb.append(parameterTypeName);
                    if (!isPrimitive) {
                        sb.append(';');
                    }
                    parameterTypeName = sb.toString();
                }
                if (varArgs) {
                    parameterTypeName = parameterTypeName + "...";
                }
                this.parameterTypeNames[i2] = parameterTypeName;
            }
        }

        public String getName() {
            return this.name;
        }

        public String[] getParamTypeNames() {
            return this.parameterTypeNames;
        }
    }
}