import java.util.*;

public class Customer extends Thread
{
    BureaucracySystem system;
    String targetDoc;
    Set<String> obtained = new HashSet<>();
    Random random = new Random();

    public Customer(String name, BureaucracySystem system, String targetDoc)
    {
        super(name);
        this.system = system;
        this.targetDoc = targetDoc;
    }

    public void receiveDocument(String doc)
    {
        obtained.add(doc);
        System.out.println("[" + getName() + "] received " + doc);
    }

    @Override
    public void run()
    {
        try {
            System.out.println("[" + getName() + "] wants to get " + targetDoc);
            List<String> path = system.getDocumentPath(targetDoc);
            for (String d : path)
            {
                Office o = system.getOfficeForDoc(d);
                if (o != null)
                {
                    o.addCustomer(this);
                    Thread.sleep(1500 + random.nextInt(1000));
                }
            }
            System.out.println("[" + getName() + "] completed: " + obtained);
        } catch (Exception e) {
            System.out.println("[" + getName() + "] gave up (error).");
        }
    }
}

//Clasa Customer simulează un client (thread).
//Merge la ghișee în ordinea documentelor de care are nevoie și interacționează cu sistemul.