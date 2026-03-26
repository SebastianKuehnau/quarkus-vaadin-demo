package dev.example.quarkai.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;

import java.util.Optional;

@CssImport("./styles/main-layout.css")
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final H1 title;

    public MainLayout() {
        title = new H1("Quarkus AI");
        title.addClassName("main-layout-title");

        addToNavbar(new DrawerToggle(), title);
        addToDrawer(createSideNav());
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        RouteConfiguration.forSessionScope().getAvailableRoutes().forEach(route -> {
            var path = route.getTemplate();
            var label = Optional.ofNullable(route.getNavigationTarget().getAnnotation(PageTitle.class))
                    .map(PageTitle::value)
                    .orElse(path);
            nav.addItem(new SideNavItem(label, path));
        });
        return nav;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        var viewTitle = getCurrentPageTitle(event);
        title.setText(viewTitle.orElse("Quarkus AI"));
    }

    private Optional<String> getCurrentPageTitle(AfterNavigationEvent event) {
        return event.getActiveChain().stream()
                .findFirst()
                .map(component -> component.getClass().getAnnotation(PageTitle.class))
                .map(PageTitle::value);
    }
}
