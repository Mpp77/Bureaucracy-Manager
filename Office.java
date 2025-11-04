import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Office implements Runnable {

    private final String name;
    private final String type; // office category for cloning
    private final BureaucracySystem system;

    private final BlockingQueue<Request> queue = new LinkedBlockingQueue<>();
    private final AtomicInteger paper = new AtomicInteger(5);

    private volatile boolean open = true;
    private long lastRestock = System.currentTimeMillis();

    private static class Request {
        Customer customer;
        Document document;
        Request(Customer c, Document d) { this.customer = c; this.document = d; }
    }

    public Office(String name, String type, BureaucracySystem sys) {
        this.name = name;
        this.type = type;
        this.system = sys;
    }

    public String getName() { return name; }
    public String getType() { return type; }

    public boolean issues(Document d) {
        return system.officeHasDocument(name, d.getName());
    }

    public void enqueue(Customer c, Document d) {
        queue.add(new Request(c, d));
        System.out.println(c.getName() + " joined queue at " + name + " for " + d.getName());
    }

    private void maybeBreak() throws InterruptedException {
        if (Math.random() < 0.04) {
            System.out.println(name + " is on coffee break...");
            open = false;
            Thread.sleep(3000);
            open = true;
            System.out.println(name + " reopened");
        }
    }

    private void maybeRestock() {
        long now = System.currentTimeMillis();
        if (paper.get() <= 1 || now - lastRestock > 10000 || Math.random() < 0.04) {
            paper.addAndGet(5);
            lastRestock = now;
            System.out.println("Paper restocked at " + name);
        }
    }

    private void maybeExpand() {
        if (queue.size() > 5) {
            System.out.println("Queue at " + name + " = " + queue.size() + " -> requesting new desk");
            system.expandOffice(this);
        }
    }

    private void serve(Request r) throws InterruptedException {

        // Passport workflow logic (ask to go do extra tasks)
        if (r.document.getName().equals("Passport")) {

            if (!r.customer.hasDocument("FormA")) {
                System.out.println(name + ": You need FormA first. Redirecting to FormsDesk...");
                Office forms = system.getOffice("FormsDesk");
                Document f = system.getDocument("FormA");
                if (forms != null && f != null) {
                    forms.enqueue(r.customer, f);
                    return;
                } else {
                    System.out.println(name + " ERROR: FormsDesk or FormA missing");
                }
            }

            if (!r.customer.hasDocument("TaxReceipt")) {
                System.out.println(name + ": You must pay a $3 fee first. Redirecting to PaymentsDesk...");
                Office pay = system.getOffice("PaymentsDesk");
                Document t = system.getDocument("TaxReceipt");
                if (pay != null && t != null) {
                    pay.enqueue(r.customer, t);
                    return;
                } else {
                    System.out.println(name + " ERROR: PaymentsDesk or TaxReceipt missing");
                }
            }
        }

        // Handle paper shortage
        while (paper.get() <= 0) {
            System.out.println(name + " ran out of paper — " + r.customer.getName() + " waiting");
            Thread.sleep(1500);
        }

        paper.decrementAndGet();
        Thread.sleep(1200);

        System.out.println(name + " processed " + r.document.getName() + " for " + r.customer.getName());
        r.customer.receive(r.document);
    }

    @Override
    public void run() {
        System.out.println("Office started: " + name + " [" + type + "]");

        while (true) {
            try {
                maybeBreak();
                maybeRestock();
                maybeExpand();

                Request r = queue.take();

                if (!open) {
                    System.out.println("Warning: " + name + " closed — redirecting " + r.customer.getName());
                    Office alt = system.findOffice(r.document);
                    if (alt != null && alt != this) {
                        alt.enqueue(r.customer, r.document);
                        continue;
                    }
                }

                serve(r);

            } catch (Exception ignored) {}
        }
    }
}