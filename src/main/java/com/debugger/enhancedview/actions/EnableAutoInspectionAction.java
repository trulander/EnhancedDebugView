package com.debugger.enhancedview.actions;

import com.debugger.enhancedview.services.DebuggerListenerService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;


public class EnableAutoInspectionAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        try {
            DebuggerListenerService service = project.getService(DebuggerListenerService.class);
            if (service != null) {
                service.registerTreeListener();
                service.subscribeListenerService();

                Messages.showInfoMessage(
                        project,
                        "Auto-Inspection feature enabled!\nNow when you expand Python objects in Variables view, " +
                                "'methods, sorted private and protected fields, dunder methods' attribute will be automatically added in the current debug session.",
                        "Auto Inspection Objects Enabled"
                );
            } else {
                Messages.showErrorDialog(
                        project,
                        "Failed to get DebuggerListenerService",
                        "Error"
                );
            }
        } catch (Exception ex) {
            Messages.showErrorDialog(
                    project,
                    "Error enabling auto-inspection: " + ex.getMessage(),
                    "Error"
            );
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        boolean hasDebugSession = project != null &&
                com.intellij.xdebugger.XDebuggerManager.getInstance(project).getCurrentSession() != null;

        e.getPresentation().setEnabledAndVisible(hasDebugSession);
    }
}