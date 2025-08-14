package com.debugger.enhancedview.components;

import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.jetbrains.python.debugger.PyDebugValue;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;


public class BuilderObjectPath {

    public String buildFullPath(TreePath path) {
        List<String> pathComponents = new ArrayList<>();

        Object[] pathArray = path.getPath();

        for (Object component : pathArray) {
            if (component instanceof XValueNodeImpl node) {

                if (node.getValueContainer() instanceof PyDebugValue value) {
                    String name = value.getName();

                    if (!name.isEmpty()) {
                        String processedName = processVariableName(name);
                        pathComponents.add(processedName);
                    }
                }
            }
        }

        pathComponents.removeIf(String::isEmpty);

        if (pathComponents.isEmpty()) {
            return "";
        }

        StringBuilder fullPath = new StringBuilder();
        for (int i = 0; i < pathComponents.size(); i++) {
            if (i == 0) {
                fullPath.append(pathComponents.get(i));
            } else {
                String component = pathComponents.get(i);
                if (needsSpecialAccess(component)) {
                    fullPath.append(".").append(component);
                } else {
                    fullPath.append(".").append(component);
                }
            }
        }

        return fullPath.toString();
    }

    private String processVariableName(String name) {
        if (name.startsWith("'") && name.endsWith("'")) {
            return name.substring(1, name.length() - 1);
        }

        if (name.startsWith("[") && name.endsWith("]")) {
            return name;
        }

        return name;
    }

    private boolean needsSpecialAccess(String component) {
        return component.contains(" ") ||
                component.contains("-") ||
                component.startsWith("[") ||
                !component.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }
}
