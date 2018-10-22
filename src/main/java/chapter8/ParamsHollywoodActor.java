package chapter8;

import akka.actor.UntypedActor;

public class ParamsHollywoodActor extends UntypedActor {

    private final String name;

    public ParamsHollywoodActor(final String name) {
        this.name = name;
    }

    public void onReceive(final Object role) {
        if (role instanceof String)
            System.out.println(String.format("%s playing %s", name, role));
        else
            System.out.println(String.format("%s plays no %s", name, role));
    }

}
