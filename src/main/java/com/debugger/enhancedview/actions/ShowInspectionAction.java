package com.debugger.enhancedview.actions;

import com.debugger.enhancedview.components.BuilderObjectPath;
import com.debugger.enhancedview.services.ObjectInspectionService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;
import javax.swing.tree.TreePath;


public class ShowInspectionAction extends AnAction {

    private final BuilderObjectPath builderObjectPath;

    public ShowInspectionAction() {
        this.builderObjectPath = new BuilderObjectPath();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        XDebuggerTree tree = getDebuggerTree(e);
        if (tree == null) return;

        XValueNodeImpl selectedNode = getSelectedValueNode(tree);
        if (selectedNode == null) return;

        ObjectInspectionService methodService = new ObjectInspectionService(project);

        TreePath path = selectedNode.getPath();
        String fullPath = this.builderObjectPath.buildFullPath(path);
        System.out.println("DEBUG: Expanding Python object: " + fullPath);

        methodService.addMethodsAttributeToValue(selectedNode, fullPath);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        XDebuggerTree tree = getDebuggerTree(e);
        XValueNodeImpl selectedNode = tree != null ? getSelectedValueNode(tree) : null;
        e.getPresentation().setEnabledAndVisible(selectedNode != null);
    }

    private XDebuggerTree getDebuggerTree(AnActionEvent e) {
        return (XDebuggerTree) e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
    }

    private XValueNodeImpl getSelectedValueNode(XDebuggerTree tree) {
        if (tree.getSelectionPath() == null) return null;
        Object lastComponent = tree.getSelectionPath().getLastPathComponent();
        return lastComponent instanceof XValueNodeImpl ? (XValueNodeImpl) lastComponent : null;
    }
}
