package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.*;

public final class SessionNavigation {
    private SessionNavigation() {
    }

    public static void logoutAndShowLogin(Component parent, Controller controller) {
        int choice = JOptionPane.showConfirmDialog(parent, "Tornare alla pagina di login?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Window currentWindow = SwingUtilities.getWindowAncestor(parent);
        if (currentWindow != null) {
            currentWindow.dispose();
        }

        login.showLogin(controller);
    }
}
