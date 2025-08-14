package com.debugger.enhancedview.services;

import com.debugger.enhancedview.listeners.DebuggerTreeExpansionListener;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.xdebugger.*;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


@Service
public final class DebuggerListenerService{

    private final Project project;
    private final Set<XDebuggerTree> registeredTrees = new HashSet<>();
    private final DebuggerTreeExpansionListener expansionListener;
    private MessageBusConnection connection;

    public DebuggerListenerService(Project project) {
        this.project = project;
        this.expansionListener = new DebuggerTreeExpansionListener(project);
    }

    public void subscribeListenerService() {
        if (connection != null) {
            connection.disconnect();
        }

        connection = project.getMessageBus().connect();
        connection.subscribe(XDebuggerManager.TOPIC, new XDebuggerManagerListener() {
            @Override
            public void processStarted(@NotNull XDebugProcess debugProcess) {
                attachToSession(debugProcess);
            }

            @Override
            public void processStopped(@NotNull XDebugProcess debugProcess) {
                clean();
            }
        });

        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(project);
        for (XDebugSession session : debuggerManager.getDebugSessions()) {
            XDebugProcess process = session.getDebugProcess();
            attachToSession(process);
        }
    }

    private void attachToSession(XDebugProcess process) {
        XDebugSession session = process.getSession();
        session.addSessionListener(new XDebugSessionListener() {
            private boolean listenerAttached = false;
            @Override
            public void sessionPaused() {
                if (listenerAttached) return;
                listenerAttached = registerTreeListener();
            }
            @Override
            public void stackFrameChanged() {
                if (listenerAttached) return;
                listenerAttached = registerTreeListener();
            }
        });
    }

    public boolean registerTreeListener() {
        XDebuggerTree tree = findAndRegisterDebuggerTrees();
        boolean result = false;
        if (tree != null) {
            if (!registeredTrees.contains(tree)) {
                System.out.println("DEBUG: Found XDebuggerTree, registering expansion listener");
                tree.addTreeWillExpandListener(expansionListener);
                result = true;
                registeredTrees.add(tree);
            }
        }
        return result;
    }

    private XDebuggerTree findAndRegisterDebuggerTrees() {
        Window[] windows = Window.getWindows();
        XDebuggerTree tree = null;
        for (Window window : windows) {
            tree = findDebuggerTreesInContainer(window);
            if (tree != null) {
                break;
            }
        }
        return tree;
    }

    private XDebuggerTree findDebuggerTreesInContainer(Container container) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof XDebuggerTree tree) {
                return tree;
            } else if (component instanceof Container innerContainer) {
                XDebuggerTree result = findDebuggerTreesInContainer(innerContainer);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public void clean() {
        registeredTrees.clear();
        expansionListener.clearCache();
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }
}