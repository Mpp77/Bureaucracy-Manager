import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Office implements Runnable {
    private String name;
    private String documentType;
    private boolean open = true; // office is open or closed
    private boolean serving = true; // if true, office is serving customers
    private int paperSupply = 4; // how many documents can be printed
    private BlockingQueue<Customer> queue = new LinkedBlockingQueue<>();
    private BureaucracySystem system;
    private Random random = new Random();

    public Office(String name, String documentType, BureaucracySystem system) {
        this.name = name;
        this.documentType = documentType;
        this.system = system;
    }

    public String getName() {
        return name;
    }

    public String getDocumentType() {
        return documentType;
    }

    public int getQueueSize() {
        return queue.size();
    }

    // add a customer to the waiting queue
    public void addCustomer(Customer c) {
        try {
            queue.put(c);
            System.out.println("[" + name + "] " + c.getName() + " joined queue for " + documentType + " (queue=" + queue.size() + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // pause serving (coffee break)
    public void takeBreak(long ms) {
        if (!open) return;
        serving = false;
        System.out.println("[" + name + "] coffee break (" + ms + " ms)");
        new Thread(() -> {
            try {
                Thread.sleep(ms); // wait during break
            } catch (InterruptedException ignored) {}
            serving = true; // back to work
            System.out.println("[" + name + "] back from break");
        }).start();
    }

    // refill paper for printing
    public synchronized void restockPaper(int amount) {
        paperSupply += amount;
        System.out.println("[" + name + "] restocked paper by " + amount + " (now " + paperSupply + ")");
    }

    // close this office
    public void closeOffice() {
        open = false;
        serving = false;
        System.out.println("[" + name + "] closed");
    }

    @Override
    public void run() {
        try {
            while (open) {
                if (!serving) { // office is on break
                    Thread.sleep(200);
                    continue;
                }

                synchronized (this) {
                    if (paperSupply <= 0) {
                        System.out.println("[" + name + "] out of paper");
                    }
                    // wait until paper is restocked
                    while (paperSupply <= 0 && open) {
                        Thread.sleep(300);
                    }
                }

                // get next customer from queue
                Customer c = queue.poll();
                if (c != null) {
                    System.out.println("[" + name + "] processing " + c.getName() + " for " + documentType);
                    Thread.sleep(800 + random.nextInt(800)); // simulate time to process document

                    synchronized (this) {
                        paperSupply--; // use one sheet
                    }

                    // give the document to customer
                    c.receiveDocument(documentType);
                } else {
                    // no customers waiting
                    Thread.sleep(200);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + name + "] stopped working");
    }
}
