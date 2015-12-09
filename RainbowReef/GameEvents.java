package RainbowReef;

import java.awt.event.KeyEvent;
import java.util.Observable;

      public class GameEvents extends Observable {

        int type;
        Object event;

        public void setValue(KeyEvent e) {
            type = 1; // let's assume this mean key input. Should use CONSTANT value for this
            event = e;
            setChanged();
            // trigger notification
            notifyObservers(this);
        }

        public void setValue(String msg) {
            type = 2; 
            event = msg;
            setChanged();
            notifyObservers(this);
        }
    }