package com.teetov.chat.client.interaction;

import com.teetov.chat.client.control.StageManager;

import javafx.application.Platform;

public class PhaseListenerApp implements PhaseListener{

    private StageManager manager;
    public PhaseListenerApp(StageManager manager) {
        this.manager = manager;
    }

    @Override
    public void started() {
        
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                manager.onActiveateConnection();
            }
        });

    }

    @Override
    public void accessed() {
        
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                manager.onAcceptAccessConnection();
            }
        });

    }

    @Override
    public void terminated() {
        
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                manager.onDeactiveateConnection();
            }
        });
    }
    
}
