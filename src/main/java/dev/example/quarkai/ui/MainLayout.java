package dev.example.quarkai.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouteConfiguration;

@CssImport("./styles/main-layout.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        var title = new H1("Quarkus AI");
        title.addClassName("main-layout-title");

        addToNavbar(new DrawerToggle(), title);
        addToDrawer(createSideNav());
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        RouteConfiguration.forSessionScope().getAvailableRoutes().forEach(route -> {
            var path = route.getTemplate();
            var label = path.isEmpty() ? "Chat" : formatLabel(path);
            nav.addItem(new SideNavItem(label, path));
        });
        return nav;
    }

    private String formatLabel(String path) {
        return path.substring(0, 1).toUpperCase()
                + path.substring(1).replace("-", " ");
    }
}
