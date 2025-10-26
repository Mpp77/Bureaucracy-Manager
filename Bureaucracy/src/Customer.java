import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Customer extends Thread {
    private BureaucracySystem system;
    private String targetDoc;
    private Set<String> obtained = new HashSet<>(); // documents already received
    private Random random = new Random();

    public Customer(String name, BureaucracySystem system, String targetDoc) {
        super(name);
        this.system = system;
        this.targetDoc = targetDoc;
    }

    // called when an office gives the document to this customer
    public synchronized void receiveDocument(String docName) {
        obtained.add(docName);
        System.out.println("[" + getName() + "] received " + docName + " (has: " + obtained + ")");
        notifyAll(); // wake thread if waiting for this document
    }

    // wait until document is received
    private void waitUntilObtained(String docName) throws InterruptedException {
        synchronized (this) {
            while (!obtained.contains(docName)) {
                wait(200); // check periodically
            }
        }
    }

    @Override
    public void run() {
        try {
            // list of documents to get (with dependencies)
            List<String> plan = system.getAcquisitionPlan(targetDoc);
            System.out.println("[" + getName() + "] wants " + targetDoc + " -> plan " + plan);

            for (String needed : plan) {
                // skip if already has document
                if (obtained.contains(needed)) continue;

                // find office that issues this document
                Office o = system.getOfficeForDoc(needed);
                if (o == null) {
                    System.out.println("[" + getName() + "] no office for " + needed);
                    return;
                }

                // go to that office and join queue
                System.out.println("[" + getName() + "] goes to " + o.getName() + " for " + needed);
                o.addCustomer(this);

                // wait for document to be issued
                waitUntilObtained(needed);

                // small random delay before next document
                Thread.sleep(500 + random.nextInt(500));
            }

            System.out.println("[" + getName() + "] finished, obtained: " + obtained);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("[" + getName() + "] error: " + e.getMessage());
        }
    }
}
