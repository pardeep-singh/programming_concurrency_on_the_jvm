package chapter8;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActorFactory;

public class ConcurrentFileSizeWAkka {
    public static void main(String[] args) {
        final ActorRef sizeCollector =
                Actors.actorOf(SizeCollector.class).start();

        sizeCollector.sendOneWay(new FileToProcess("/Users/pardeep/Documents/work"));

        for (int i = 0; i < 100; i++)
            Actors.actorOf(new UntypedActorFactory() {
                @Override
                public Actor create() {
                    return new FileProcessor(sizeCollector);
                }
            }).start();
    }
}
