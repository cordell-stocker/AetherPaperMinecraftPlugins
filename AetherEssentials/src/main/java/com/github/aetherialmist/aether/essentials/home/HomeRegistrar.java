package com.github.aetherialmist.aether.essentials.home;

import com.github.aetherialmist.aether.essentials.exception.AlreadyInitialized;
import com.github.aetherialmist.aether.essentials.home.command.*;
import com.github.aetherialmist.aether.essentials.home.persistence.HomeStorage;

import static com.github.aetherialmist.aether.essentials.CommandRegistration.registerCommand;

/**
 * Handles registration of the Home module
 */
public class HomeRegistrar {

    private static HomeRegistrar instance;

    /**
     * Initialize this module and all of its dependencies
     */
    public static void init() {
        if (instance != null) {
            throw new AlreadyInitialized(HomeRegistrar.class);
        }
        HomeStorage.init();
        instance = new HomeRegistrar();
    }

    public static final String HOME = "home";
    public static final String HOMES = "homes";
    public static final String HOME_INFO = "homeinfo";
    public static final String SET_HOME = "sethome";
    public static final String DELETE_HOME = "delhome";

    private HomeRegistrar() {
        registerCommands();
    }

    private void registerCommands() {
        registerCommand(HomeRegistrar.HOME, new Home());
        registerCommand(HomeRegistrar.HOMES, new Homes());
        registerCommand(HomeRegistrar.HOME_INFO, new HomeInfo());
        registerCommand(HomeRegistrar.SET_HOME, new SetHome());
        registerCommand(HomeRegistrar.DELETE_HOME, new DeleteHome());
    }

}
