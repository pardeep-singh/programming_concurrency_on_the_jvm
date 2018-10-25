package chapter8;

import akka.actor.ActorRef;
import static akka.actor.Actors.remote;

import java.io.File;

public class Client {

    public static void main(String[] args) {
        ActorRef systemMonitor = remote().actorFor(
                "system-monitor", "localhost", 8000
        );

        systemMonitor.sendOneWay("Cores:" + Runtime.getRuntime().availableProcessors());
        systemMonitor.sendOneWay("Total Space:" + new File("/").getTotalSpace());
        systemMonitor.sendOneWay("Free Space:" + new File("/").getFreeSpace());

    }
}
