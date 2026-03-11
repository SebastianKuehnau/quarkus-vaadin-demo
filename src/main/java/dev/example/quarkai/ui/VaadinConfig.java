package dev.example.quarkai.ui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.aura.Aura;

/**
 * Application-wide Vaadin configuration.
 *
 * {@link StyleSheet @StyleSheet} loads the Aura theme
 * {@link Push @Push} enables server push
 */
@StyleSheet(Aura.STYLESHEET)
@Push
public class VaadinConfig implements AppShellConfigurator {
}
