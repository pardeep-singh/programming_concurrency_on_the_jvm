package chapter8;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;

public class SizeCollector extends UntypedActor {
    private final List<String> toProcessFileNames = new ArrayList<String>();
    private final List<ActorRef> idleFileProcessors =
            new ArrayList<ActorRef>();
    private long pendingNumberOfFilesToVisit = 0L;
    private long totolSize = 0;
    private long start = System.nanoTime();

    public void sendAFileToProcess() {
        System.out.println(toProcessFileNames.size() + ":" + idleFileProcessors.size());
        if (!toProcessFileNames.isEmpty() && !idleFileProcessors.isEmpty())
            idleFileProcessors.remove(0).sendOneWay(
                    new FileToProcess(toProcessFileNames.remove(0))
            );
    }

    public void onReceive(final Object message) {
        if (message instanceof RequestAFile) {
            idleFileProcessors.add(getContext().getSender().get());
            sendAFileToProcess();
        } else if (message instanceof FileToProcess) {
            toProcessFileNames.add(((FileToProcess) (message)).fileName);
            pendingNumberOfFilesToVisit += 1;
            sendAFileToProcess();
        } else if (message instanceof FileSize) {
            totolSize += ((FileSize)(message)).size;
            pendingNumberOfFilesToVisit -= 1;

            if (pendingNumberOfFilesToVisit == 0) {
                long end = System.nanoTime();
                System.out.println("Total Size is " + totolSize);
                System.out.println("Time taken is " + (end - start) / 1.0e9);
                Actors.registry().shutdownAll();
            }
        }
    }
}
