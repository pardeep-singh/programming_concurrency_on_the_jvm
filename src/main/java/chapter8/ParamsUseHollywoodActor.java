package chapter8;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActorFactory;

public class ParamsUseHollywoodActor {

    public static void main(String[] args)
            throws InterruptedException {
        final ActorRef tomHanks = Actors.actorOf(new UntypedActorFactory() {
            @Override
            public Actor create() {
                return new ParamsHollywoodActor("Hanks");
            }
        }).start();

        tomHanks.sendOneWay("James Lovell");
        tomHanks.sendOneWay(new StringBuilder("Politics"));
        tomHanks.sendOneWay("Forrest Gump");
        Thread.sleep(100);
        tomHanks.stop();
    }
}
