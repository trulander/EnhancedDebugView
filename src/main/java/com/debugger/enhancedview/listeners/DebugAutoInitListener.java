package com.debugger.enhancedview.listeners;

import com.debugger.enhancedview.services.DebuggerListenerService;
import com.debugger.enhancedview.settings.EnhancedDebugViewSettings;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ExecutionListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


public class DebugAutoInitListener implements ExecutionListener {

    private final Project project;
    private final EnhancedDebugViewSettings settings;

    public DebugAutoInitListener(Project project) {
        this.project = project;
        this.settings = EnhancedDebugViewSettings.getInstance();
    }

    @Override
    public void processStarted(@NotNull String executorId,
                               @NotNull ExecutionEnvironment env,
                               @NotNull ProcessHandler handler) {

        if (this.settings.autoShowMethods && executorId.equals("Debug")) {
            System.out.println("DEBUG: sturtup session");
            DebuggerListenerService service = project.getService(DebuggerListenerService.class);
            if (service != null) {
                service.subscribeListenerService();
            }
        }
    }
}