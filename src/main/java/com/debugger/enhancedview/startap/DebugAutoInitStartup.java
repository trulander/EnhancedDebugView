package com.debugger.enhancedview.startap;

import com.debugger.enhancedview.listeners.DebugAutoInitListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;


public class DebugAutoInitStartup implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(ExecutionManager.EXECUTION_TOPIC, new DebugAutoInitListener(project));
    }
}