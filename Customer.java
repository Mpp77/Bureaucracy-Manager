import java.util.*;

public class Customer extends Thread {

    private final BureaucracySystem sys;
    private final Document target;
    private final Set<Document> obtained = new HashSet<>();

    public Customer(String name, BureaucracySystem sys, Document target) {
        super(name);
        this.sys = sys;
        this.target = target;
    }

    public boolean hasDocument(String name) {
    return obtained.stream().anyMatch(d -> d.getName().equals(name));
}


    public synchronized void receive(Document d) {
    obtained.add(d);
    notifyAll();
    System.out.println("📎 " + getName() + " received " + d.getName());

    // B MODE: If FormA or TaxReceipt done, return to InfoDesk to continue passport process
    if (d.getName().equals("FormA") || d.getName().equals("TaxReceipt")) {
        Office info = sys.getOffice("InfoDesk");
        info.enqueue(this, sys.getDocument("Passport"));
    }
}


  
  
    private synchronized void waitFor(Document d) {
        while (!obtained.contains(d)) {
            try { wait(); } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void run() {
        List<Document> plan = sys.getPlan(target);
        System.out.println(getName() + " needs: " + plan);

        for (Document d : plan) {
            Office o = sys.findOffice(d);
            o.enqueue(this, d);
            waitFor(d);
        }

        System.out.println("🎉 " + getName() + " completed all documents!");
    }
}
