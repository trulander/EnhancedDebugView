package com.debugger.enhancedview.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;


public class EnhancedDebugViewConfigurable implements Configurable {

    private JCheckBox autoShowMethodsCheckbox;
    private JCheckBox showMethodsCheckbox;
    private JCheckBox showMagicMethodsCheckbox;
    private JCheckBox showPrivateFieldsCheckbox;
    private JCheckBox showProtectedFieldsCheckbox;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Enhanced Debug View";
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Auto show methods
        autoShowMethodsCheckbox = new JCheckBox("Automatically apply for all objects in the variable panel");
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(autoShowMethodsCheckbox, gbc);

        // Show methods
        showMethodsCheckbox = new JCheckBox("Show methods");
        gbc.gridy = 1;
        mainPanel.add(showMethodsCheckbox, gbc);

        // Show inherited methods
        showPrivateFieldsCheckbox = new JCheckBox("Show private fields (starting with __)");
        gbc.gridy = 2;
        mainPanel.add(showPrivateFieldsCheckbox, gbc);

        // Show private methods
        showProtectedFieldsCheckbox = new JCheckBox("Show protected fields (starting with _)");
        gbc.gridy = 3;
        mainPanel.add(showProtectedFieldsCheckbox, gbc);

        // Show magic methods
        showMagicMethodsCheckbox = new JCheckBox("Show magic(dunder) methods (__init__, __str__, etc.)");
        gbc.gridy = 4;
        mainPanel.add(showMagicMethodsCheckbox, gbc);

        // Add some spacing at the bottom
        gbc.gridy = 5; gbc.weighty = 1.0;
        mainPanel.add(new JPanel(), gbc);

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        EnhancedDebugViewSettings settings = EnhancedDebugViewSettings.getInstance();
        return autoShowMethodsCheckbox.isSelected() != settings.autoShowMethods ||
                showMethodsCheckbox.isSelected() != settings.showMethods ||
                showMagicMethodsCheckbox.isSelected() != settings.showMagicMethods ||
                showProtectedFieldsCheckbox.isSelected() != settings.showProtectedFields ||
                showPrivateFieldsCheckbox.isSelected() != settings.showPrivateFields;
    }

    @Override
    public void apply() throws ConfigurationException {
        EnhancedDebugViewSettings settings = EnhancedDebugViewSettings.getInstance();
        settings.autoShowMethods = autoShowMethodsCheckbox.isSelected();
        settings.showMethods = showMethodsCheckbox.isSelected();
        settings.showMagicMethods = showMagicMethodsCheckbox.isSelected();
        settings.showProtectedFields = showProtectedFieldsCheckbox.isSelected();
        settings.showPrivateFields = showPrivateFieldsCheckbox.isSelected();
    }

    @Override
    public void reset() {
        EnhancedDebugViewSettings settings = EnhancedDebugViewSettings.getInstance();
        autoShowMethodsCheckbox.setSelected(settings.autoShowMethods);
        showMethodsCheckbox.setSelected(settings.showMethods);
        showMagicMethodsCheckbox.setSelected(settings.showMagicMethods);
        showProtectedFieldsCheckbox.setSelected(settings.showProtectedFields);
        showPrivateFieldsCheckbox.setSelected(settings.showPrivateFields);
    }
}