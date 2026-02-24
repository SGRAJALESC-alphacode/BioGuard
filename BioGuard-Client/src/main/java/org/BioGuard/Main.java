package org.BioGuard;

import org.BioGuard.ui.MenuCliente;

/**
 * Punto de entrada del cliente BioGuard.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        MenuCliente menu = new MenuCliente();
        menu.iniciar();
    }
}