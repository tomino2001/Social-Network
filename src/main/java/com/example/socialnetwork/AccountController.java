package com.example.socialnetwork;

import javafx.scene.control.Alert;
import socialnetwork.domain.Utilizator;

public class AccountController {
    private Utilizator utilizator;

    public AccountController() {
    }

    public void onBtnSimpleClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, utilizator.toString());
        alert.show();
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }
}