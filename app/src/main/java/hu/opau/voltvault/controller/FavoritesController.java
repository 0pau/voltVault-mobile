package hu.opau.voltvault.controller;

public class FavoritesController {

    private static FavoritesController instance;

    private boolean reloadNeeded = true;

    private FavoritesController() {}


    public static FavoritesController getInstance() {

        if (instance == null) {
            instance = new FavoritesController();
        }

        return instance;
    }

    public boolean isReloadNeeded() {
        return reloadNeeded;
    }

    public void setReloadNeeded(boolean reloadNeeded) {
        this.reloadNeeded = reloadNeeded;
    }
}
