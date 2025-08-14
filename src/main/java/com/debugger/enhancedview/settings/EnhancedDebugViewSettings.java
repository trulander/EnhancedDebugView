package com.debugger.enhancedview.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "EnhancedDebugViewSettings",
    storages = {@Storage("enhancedDebugViewSettings.xml")}
)
public class EnhancedDebugViewSettings implements PersistentStateComponent<EnhancedDebugViewSettings> {

    public boolean autoShowMethods = false;
    public boolean showMethods = true;
    public boolean showMagicMethods = false;
    public boolean showProtectedFields = false;
    public boolean showPrivateFields = false;

    public static EnhancedDebugViewSettings getInstance() {
        return ApplicationManager.getApplication().getService(EnhancedDebugViewSettings.class);
    }

    @Override
    public @Nullable EnhancedDebugViewSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull EnhancedDebugViewSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}