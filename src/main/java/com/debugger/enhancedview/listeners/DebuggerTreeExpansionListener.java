package com.debugger.enhancedview.listeners;

import com.debugger.enhancedview.components.BuilderObjectPath;
import com.debugger.enhancedview.services.ObjectInspectionService;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.jetbrains.python.debugger.PyDebugValue;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class DebuggerTreeExpansionListener implements TreeWillExpandListener {

    private final Project project;
    private final ObjectInspectionService methodService;

    private final Set<String> processedObjects = ConcurrentHashMap.newKeySet();
    private final BuilderObjectPath builderObjectPath;

    public DebuggerTreeExpansionListener(Project project) {
        this.project = project;
        this.methodService = new ObjectInspectionService(project);
        this.builderObjectPath = new BuilderObjectPath();
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        TreePath path = event.getPath();
        Object lastComponent = path.getLastPathComponent();

        if (lastComponent instanceof XValueNodeImpl valueNode) {

            if (valueNode.getValueContainer() instanceof PyDebugValue pyValue) {
                String variableName = pyValue.getName();

                if (
                    variableName.isBlank() ||
                    variableName.equals("methods") ||
                    variableName.equals("protected_fields") ||
                    variableName.equals("private_fields") ||
                    variableName.equals("dunder_methods")
                ) {
                    return;
                }

                System.out.println("DEBUG: Expanding Python object: " + variableName);

                String fullPath = this.builderObjectPath.buildFullPath(path);
                System.out.println("DEBUG: Expanding Python object: " + fullPath);

                String objectKey = createObjectKey(fullPath, pyValue);

                if (!hasMethodsAttribute(objectKey, pyValue)) {
                    System.out.println("DEBUG: Adding methods attribute to " + variableName);

                    methodService.addMethodsAttributeToValue(valueNode, fullPath);

                    processedObjects.add(objectKey);

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    System.out.println("DEBUG: Object " + variableName + " already has methods attribute (cached)");
                }
            }
        }
    }


    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

    }

    private String createObjectKey(String fullPath, PyDebugValue pyValue) {
        String objectType = pyValue.getType();

        XDebugSession session = XDebuggerManager.getInstance(project).getCurrentSession();
        String sessionId = session != null ? session.getSessionName() : "unknown";

        return sessionId + ":" + fullPath + ":" + objectType;
    }

    private boolean hasMethodsAttribute(String objectKey, PyDebugValue pyValue) {
        return processedObjects.contains(objectKey);
    }

    public void clearCache() {
        processedObjects.clear();
        System.out.println("DEBUG: Processed objects cache cleared");
    }
}