package org.springframework.web.servlet.view.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.scripting.support.StandardScriptEvalException;
import org.springframework.scripting.support.StandardScriptUtils;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/script/ScriptTemplateView.class */
public class ScriptTemplateView extends AbstractUrlBasedView {
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_RESOURCE_LOADER_PATH = "classpath:";
    @Nullable
    private ScriptEngine engine;
    @Nullable
    private String engineName;
    @Nullable
    private Boolean sharedEngine;
    @Nullable
    private String[] scripts;
    @Nullable
    private String renderObject;
    @Nullable
    private String renderFunction;
    @Nullable
    private Charset charset;
    @Nullable
    private String[] resourceLoaderPaths;
    @Nullable
    private volatile ScriptEngineManager scriptEngineManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ThreadLocal<Map<Object, ScriptEngine>> enginesHolder = new NamedThreadLocal("ScriptTemplateView engines");

    public ScriptTemplateView() {
        setContentType(null);
    }

    public ScriptTemplateView(String url) {
        super(url);
        setContentType(null);
    }

    public void setEngine(ScriptEngine engine) {
        this.engine = engine;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public void setSharedEngine(Boolean sharedEngine) {
        this.sharedEngine = sharedEngine;
    }

    public void setScripts(String... scripts) {
        this.scripts = scripts;
    }

    public void setRenderObject(String renderObject) {
        this.renderObject = renderObject;
    }

    public void setRenderFunction(String functionName) {
        this.renderFunction = functionName;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setResourceLoaderPath(String resourceLoaderPath) {
        String[] paths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);
        this.resourceLoaderPaths = new String[paths.length + 1];
        this.resourceLoaderPaths[0] = "";
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (!path.endsWith("/") && !path.endsWith(":")) {
                path = path + "/";
            }
            this.resourceLoaderPaths[i + 1] = path;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.WebApplicationObjectSupport, org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        ScriptTemplateConfig viewConfig = autodetectViewConfig();
        if (this.engine == null && viewConfig.getEngine() != null) {
            setEngine(viewConfig.getEngine());
        }
        if (this.engineName == null && viewConfig.getEngineName() != null) {
            this.engineName = viewConfig.getEngineName();
        }
        if (this.scripts == null && viewConfig.getScripts() != null) {
            this.scripts = viewConfig.getScripts();
        }
        if (this.renderObject == null && viewConfig.getRenderObject() != null) {
            this.renderObject = viewConfig.getRenderObject();
        }
        if (this.renderFunction == null && viewConfig.getRenderFunction() != null) {
            this.renderFunction = viewConfig.getRenderFunction();
        }
        if (getContentType() == null) {
            setContentType(viewConfig.getContentType() != null ? viewConfig.getContentType() : "text/html");
        }
        if (this.charset == null) {
            this.charset = viewConfig.getCharset() != null ? viewConfig.getCharset() : DEFAULT_CHARSET;
        }
        if (this.resourceLoaderPaths == null) {
            String resourceLoaderPath = viewConfig.getResourceLoaderPath();
            setResourceLoaderPath(resourceLoaderPath != null ? resourceLoaderPath : "classpath:");
        }
        if (this.sharedEngine == null && viewConfig.isSharedEngine() != null) {
            this.sharedEngine = viewConfig.isSharedEngine();
        }
        Assert.isTrue(this.engine == null || this.engineName == null, "You should define either 'engine' or 'engineName', not both.");
        Assert.isTrue((this.engine == null && this.engineName == null) ? false : true, "No script engine found, please specify either 'engine' or 'engineName'.");
        if (Boolean.FALSE.equals(this.sharedEngine)) {
            Assert.isTrue(this.engineName != null, "When 'sharedEngine' is set to false, you should specify the script engine using the 'engineName' property, not the 'engine' one.");
        } else if (this.engine != null) {
            loadScripts(this.engine);
        } else {
            setEngine(createEngineFromName(this.engineName));
        }
        if (this.renderFunction != null && this.engine != null) {
            Assert.isInstanceOf(Invocable.class, this.engine, "ScriptEngine must implement Invocable when 'renderFunction' is specified");
        }
    }

    protected ScriptEngine getEngine() {
        if (Boolean.FALSE.equals(this.sharedEngine)) {
            Map<Object, ScriptEngine> engines = enginesHolder.get();
            if (engines == null) {
                engines = new HashMap<>(4);
                enginesHolder.set(engines);
            }
            Assert.state(this.engineName != null, "No engine name specified");
            Object engineKey = !ObjectUtils.isEmpty((Object[]) this.scripts) ? new EngineKey(this.engineName, this.scripts) : this.engineName;
            ScriptEngine engine = engines.get(engineKey);
            if (engine == null) {
                engine = createEngineFromName(this.engineName);
                engines.put(engineKey, engine);
            }
            return engine;
        }
        Assert.state(this.engine != null, "No shared engine available");
        return this.engine;
    }

    protected ScriptEngine createEngineFromName(String engineName) {
        ScriptEngineManager scriptEngineManager = this.scriptEngineManager;
        if (scriptEngineManager == null) {
            scriptEngineManager = new ScriptEngineManager(obtainApplicationContext().getClassLoader());
            this.scriptEngineManager = scriptEngineManager;
        }
        ScriptEngine engine = StandardScriptUtils.retrieveEngineByName(scriptEngineManager, engineName);
        loadScripts(engine);
        return engine;
    }

    protected void loadScripts(ScriptEngine engine) {
        String[] strArr;
        if (!ObjectUtils.isEmpty((Object[]) this.scripts)) {
            for (String script : this.scripts) {
                Resource resource = getResource(script);
                if (resource == null) {
                    throw new IllegalStateException("Script resource [" + script + "] not found");
                }
                try {
                    engine.eval(new InputStreamReader(resource.getInputStream()));
                } catch (Throwable ex) {
                    throw new IllegalStateException("Failed to evaluate script [" + script + "]", ex);
                }
            }
        }
    }

    @Nullable
    protected Resource getResource(String location) {
        String[] strArr;
        if (this.resourceLoaderPaths != null) {
            for (String path : this.resourceLoaderPaths) {
                Resource resource = obtainApplicationContext().getResource(path + location);
                if (resource.exists()) {
                    return resource;
                }
            }
            return null;
        }
        return null;
    }

    protected ScriptTemplateConfig autodetectViewConfig() throws BeansException {
        try {
            return (ScriptTemplateConfig) BeanFactoryUtils.beanOfTypeIncludingAncestors(obtainApplicationContext(), ScriptTemplateConfig.class, true, false);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Expected a single ScriptTemplateConfig bean in the current Servlet web application context or the parent root context: ScriptTemplateConfigurer is the usual implementation. This bean may have any name.", ex);
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractUrlBasedView
    public boolean checkResource(Locale locale) throws Exception {
        String url = getUrl();
        Assert.state(url != null, "'url' not set");
        return getResource(url) != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.AbstractView
    public void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        super.prepareResponse(request, response);
        setResponseContentType(request, response);
        if (this.charset != null) {
            response.setCharacterEncoding(this.charset.name());
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object html;
        try {
            Invocable engine = getEngine();
            String url = getUrl();
            Assert.state(url != null, "'url' not set");
            String template = getTemplate(url);
            Function<String, String> templateLoader = path -> {
                try {
                    return getTemplate(path);
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            };
            Locale locale = RequestContextUtils.getLocale(request);
            RenderingContext context = new RenderingContext(obtainApplicationContext(), locale, templateLoader, url);
            if (this.renderFunction == null) {
                SimpleBindings bindings = new SimpleBindings();
                bindings.putAll(model);
                model.put("renderingContext", context);
                html = engine.eval(template, bindings);
            } else if (this.renderObject != null) {
                Object thiz = engine.eval(this.renderObject);
                html = engine.invokeMethod(thiz, this.renderFunction, new Object[]{template, model, context});
            } else {
                html = engine.invokeFunction(this.renderFunction, new Object[]{template, model, context});
            }
            response.getWriter().write(String.valueOf(html));
        } catch (ScriptException ex) {
            throw new ServletException("Failed to render script template", new StandardScriptEvalException(ex));
        }
    }

    protected String getTemplate(String path) throws IOException {
        InputStreamReader inputStreamReader;
        Resource resource = getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Template resource [" + path + "] not found");
        }
        if (this.charset != null) {
            inputStreamReader = new InputStreamReader(resource.getInputStream(), this.charset);
        } else {
            inputStreamReader = new InputStreamReader(resource.getInputStream());
        }
        InputStreamReader reader = inputStreamReader;
        return FileCopyUtils.copyToString(reader);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/script/ScriptTemplateView$EngineKey.class */
    public static class EngineKey {
        private final String engineName;
        private final String[] scripts;

        public EngineKey(String engineName, String[] scripts) {
            this.engineName = engineName;
            this.scripts = scripts;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof EngineKey)) {
                return false;
            }
            EngineKey otherKey = (EngineKey) other;
            return this.engineName.equals(otherKey.engineName) && Arrays.equals(this.scripts, otherKey.scripts);
        }

        public int hashCode() {
            return (this.engineName.hashCode() * 29) + Arrays.hashCode(this.scripts);
        }
    }
}