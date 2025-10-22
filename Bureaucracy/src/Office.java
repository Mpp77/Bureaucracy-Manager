import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Office implements Runnable
{
    String name;
    String documentType;
    boolean open = true;
    int paperSupply = 4;
    BlockingQueue<Customer> queue = new LinkedBlockingQueue<>();
    BureaucracySystem system;
    Random random = new Random();

    public Office(String name, String documentType, BureaucracySystem system)
    {
        this.name = name;
        this.documentType = documentType;
        this.system = system;
    }

    public void addCustomer(Customer c) throws InterruptedException
    {
        if (!open)
        {
            System.out.println("[" + c.getName() + "] tries to join " + name + " but it's closed -> redirected.");
            system.redirectCustomer(c, documentType);
            return;
        }
        queue.put(c);
        System.out.println("[" + c.getName() + "] joins the queue at " + name);
    }

    public void closeOffice()
    {
        open = false;
        System.out.println("[" + name + "] is now closed (coffee break).");
    }

    public void openOffice()
    {
        open = true;
        System.out.println("[" + name + "] is now open again!");
    }

    public void restockPaper()
    {
        paperSupply += 3;
        System.out.println("[" + name + "] paper restocked (total " + paperSupply + ")");
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                if (!open)
                {
                    Thread.sleep(500);
                    continue;
                }

                Customer c = queue.poll();
                if (c != null)
                {
                    if (paperSupply <= 0)
                    {
                        System.out.println("[" + name + "] out of paper! Waiting for restock...");
                        Thread.sleep(2000);
                        restockPaper();
                    }
                    System.out.println("[" + name + "] processing " + c.getName());
                    Thread.sleep(1000 + random.nextInt(1000));
                    paperSupply--;
                    c.receiveDocument(documentType);
                } else {
                    Thread.sleep(300);
                }
            }
        } catch (InterruptedException e)
        {
            System.out.println("[" + name + "] stopped working.");
        }
    }
}

//Clasa Office reprezintă un ghișeu. Poate procesa clienți, se poate închide,
// cand rămâne fără hârtie și este reumplută automat. Rulează într-un thread separat.
