package com.dmiranda.revert.shared;

import com.badlogic.gdx.Gdx;
import com.dmiranda.revert.Revert;

import java.util.HashMap;

public class StateMachine {

    private HashMap<String, State> states = new HashMap<String, State>();
    private State currentState;
    private Revert game;

    public StateMachine(Revert game){
        this.game = game;
    }

    public void render(){
        if(currentState != null) currentState.render();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String name) {
        if(!states.containsKey(name)){
            Gdx.app.error("StateMachine", "State " + name + " does not exist in state list");
            return;
        }

        Gdx.app.log("StateMachine", "State change to " + name);

        if(currentState != null) currentState.end();
        currentState = states.get(name);
        currentState.begin();
    }

    public void addState(String name, State state){
        states.put(name, state);
    }

    public Revert getGame() {
        return game;
    }

    public interface State {
        void begin();
        void render();
        void end();
    }
}
