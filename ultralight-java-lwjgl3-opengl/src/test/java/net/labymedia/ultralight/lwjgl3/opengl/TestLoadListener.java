package net.labymedia.ultralight.lwjgl3.opengl;

import net.labymedia.ultralight.Databind;
import net.labymedia.ultralight.DatabindConfiguration;
import net.labymedia.ultralight.DatabindJavascriptClass;
import net.labymedia.ultralight.UltralightView;
import net.labymedia.ultralight.api.JavaAPI;
import net.labymedia.ultralight.javascript.*;
import net.labymedia.ultralight.plugin.loading.UltralightLoadListener;

/**
 * Test load listener for the main view.
 */
public class TestLoadListener implements UltralightLoadListener {
    private final UltralightView view;

    public TestLoadListener(UltralightView view) {
        this.view = view;
    }

    private String frameName(long frameId, boolean isMainFrame, String url) {
        return "[" + (isMainFrame ? "MainFrame" : "Frame") + " " + frameId + " (" + url + ")]: ";
    }

    @Override
    public void onBeginLoading(long frameId, boolean isMainFrame, String url) {
        System.out.println(frameName(frameId, isMainFrame, url) + "The view is about to load");
    }

    @Override
    public void onFinishLoading(long frameId, boolean isMainFrame, String url) {
        System.out.println(frameName(frameId, isMainFrame, url) + "The view finished loading");
    }

    @Override
    public void onFailLoading(
            long frameId, boolean isMainFrame, String url, String description, String errorDomain, int errorCode) {
        System.err.println(frameName(frameId, isMainFrame, url) +
                "Failed to load " + errorDomain + ", " + errorCode + "(" + description + ")");
    }

    @Override
    public void onUpdateHistory() {
        System.out.println("The view has updated the history");
    }

    @Override
    public void onWindowObjectReady(long frameId, boolean isMainFrame, String url) {
        try (JavascriptContextLock lock = view.lockJavascriptContext()) {
            JavascriptContext context = lock.getContext();
            JavascriptGlobalContext globalContext = context.getGlobalContext();

            JavascriptObject globalObject = globalContext.getGlobalObject();

            Databind databind = new Databind(DatabindConfiguration.builder().build());
            JavascriptObject javaApi = context.makeObject(databind.toJavascript(JavaAPI.class), DatabindJavascriptClass.Data.builder().instance(new JavaAPI(databind)).build());
            globalObject.setProperty("java", javaApi, 0);
            globalObject.setProperty("int", context.makeObject(new JavascriptClassDefinition().name("int").bake(), DatabindJavascriptClass.Data.builder().javaClass(int.class).build()), 0);
            globalObject.setProperty("float", context.makeObject(new JavascriptClassDefinition().name("float").bake(), DatabindJavascriptClass.Data.builder().javaClass(float.class).build()), 0);
            globalObject.setProperty("double", context.makeObject(new JavascriptClassDefinition().name("double").bake(), DatabindJavascriptClass.Data.builder().javaClass(double.class).build()), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDOMReady(long frameId, boolean isMainFrame, String url) {

    }
}
