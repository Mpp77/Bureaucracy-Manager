import java.util.*;
import java.util.concurrent.*;

public class BureaucracySystem
{
    Map<String, Document> documents = new HashMap<>();
    Map<String, Office> offices = new HashMap<>();
    ExecutorService executor = Executors.newCachedThreadPool();

    public void addOffice(Office o)
    {
        offices.put(o.name, o);
        executor.submit(o);
    }

    public void addDocument(Document d)
    {
        documents.put(d.getName(), d);
    }

    public Office getOfficeForDoc(String doc)
    {
        for (Office o : offices.values())
        {
            if (o.documentType.equals(doc)) return o;
        }
        return null;
    }

    public void redirectCustomer(Customer c, String doc) throws InterruptedException
    {
        for (Office o : offices.values())
        {
            if (o.documentType.equals(doc) && o.open)
            {
                System.out.println("[" + c.getName() + "] redirected to " + o.name);
                o.addCustomer(c);
                return;
            }
        }
        System.out.println("[" + c.getName() + "] no open office for " + doc + " -> waiting...");
        Thread.sleep(1000);
        redirectCustomer(c, doc);
    }

    public List<String> getDocumentPath(String target)
    {
        List<String> path = new ArrayList<>();
        buildPath(target, path, new HashSet<>());
        return path;
    }

    private void buildPath(String doc, List<String> path, Set<String> visited)
    {
        if (visited.contains(doc)) {
            System.out.println("[System] circular dependency at " + doc);
            return;
        }
        visited.add(doc);
        Document d = documents.get(doc);
        if (d == null) return;
        for (String dep : d.getDependencies())
        {
            buildPath(dep, path, visited);
        }
        if (!path.contains(doc)) path.add(doc);
    }

    public void startRandomEvents()
    {
        new Thread(() ->
        {
            Random random = new Random();
            while (true) {
                try
                {
                    Thread.sleep(4000 + random.nextInt(3000));
                    List<Office> list = new ArrayList<>(offices.values());
                    if (list.isEmpty()) continue;
                    Office o = list.get(random.nextInt(list.size()));

                    int event = random.nextInt(3);
                    switch (event)
                    {
                        case 0 -> { // close/open office
                            if (o.open)
                            {
                                o.closeOffice();
                                Thread.sleep(2000 + random.nextInt(2000));
                                o.openOffice();
                            }
                        }
                        case 1 ->
                        {
                            o.restockPaper();
                        }
                        case 2 ->
                        { // open new office if queue long
                            if (o.queue.size() > 3)
                            {
                                String newName = o.name + "-extra";
                                if (!offices.containsKey(newName))
                                {
                                    Office newOffice = new Office(newName, o.documentType, this);
                                    addOffice(newOffice);
                                    System.out.println("[System] " + newName + " opened to reduce queue size.");
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
        }).start();
    }
}

//Clasa BureaucracySystem coordonează toate ghișeele și clienții.
// Gestionează redirecționările, dependențele dintre documente și evenimentele aleatorii.
