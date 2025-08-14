package com.debugger.enhancedview.services;

import com.debugger.enhancedview.settings.EnhancedDebugViewSettings;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.jetbrains.python.debugger.PyDebugValue;
import org.jetbrains.annotations.NotNull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class ObjectInspectionService {

    private final Project project;
    private final EnhancedDebugViewSettings settings;

    public ObjectInspectionService(Project project) {
        this.project = project;
        this.settings = EnhancedDebugViewSettings.getInstance();
    }

    public void addMethodsAttributeToValue(XValueNodeImpl valueNode, String fullPath) {
        try {
            XDebugSession debugSession = XDebuggerManager.getInstance(project).getCurrentSession();
            if (debugSession == null) return;

            if (!(valueNode.getValueContainer() instanceof PyDebugValue pyValue)) return;

            String variableName = pyValue.getName();
            if (variableName.isEmpty()) return;

            debugSession.getDebugProcess();
            XDebuggerEvaluator evaluator = debugSession.getDebugProcess().getEvaluator();

            if (evaluator == null) return;

            String importCode = "import inspect; import sys; 'imports done'";
            evaluator.evaluate(importCode, new XDebuggerEvaluator.XEvaluationCallback() {
                @Override
                public void evaluated(@NotNull XValue result) {
                    String mainCode = null;
                    try {
                        mainCode = buildMethodRetrievalCode(fullPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    evaluator.evaluate(mainCode, new XDebuggerEvaluator.XEvaluationCallback() {
                        @Override
                        public void evaluated(@NotNull XValue result2) {
                            if (result2 instanceof PyDebugValue) {
                                String resultValue = ((PyDebugValue) result2).getValue();
                                System.out.println("DEBUG: Success: " + resultValue);
                            }
                        }

                        @Override
                        public void errorOccurred(@NotNull String errorMessage) {
                            System.out.println("ERROR in main code: " + errorMessage);
                        }
                    }, null);
                }

                @Override
                public void errorOccurred(@NotNull String errorMessage) {
                    System.out.println("ERROR in import: " + errorMessage);
                    String mainCode = null;
                    try {
                        mainCode = buildMethodRetrievalCode(fullPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    evaluator.evaluate(mainCode, new XDebuggerEvaluator.XEvaluationCallback() {
                        @Override
                        public void evaluated(@NotNull XValue result) {
                            System.out.println("DEBUG: Executed without import");
                        }
                        @Override
                        public void errorOccurred(@NotNull String errorMessage) {
                            System.out.println("ERROR: " + errorMessage);
                        }
                    }, null);
                }
            }, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String buildMethodRetrievalCode(String variableName) throws IOException {
        String script;
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("python/debugger.py")) {

            if (inputStream == null) {
                throw new FileNotFoundException("Debugger script not found in resources");
            }

            script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        script = script.replace("{variable_name}", variableName);
        script = script.replace("{show_magic_methods}", settings.showMagicMethods ? "True" : "False");
        script = script.replace("{show_methods}", settings.showMethods ? "True" : "False");
        script = script.replace("{show_protected_fields}", settings.showProtectedFields ? "True" : "False");
        script = script.replace("{show_private_fields}", settings.showPrivateFields ? "True" : "False");

        return script;
    }
}
