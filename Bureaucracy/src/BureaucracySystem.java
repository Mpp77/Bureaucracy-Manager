import java.util.*;
import java.util.concurrent.*;

public class BureaucracySystem {
    private Map<String, Document> documents = new HashMap<>();
    private Map<String, Office> offices = new HashMap<>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Random random = new Random();

    // add new document type
    public synchronized void addDocument(Document d) {
        documents.put(d.getName(), d);
    }

    // add new office and start its thread
    public synchronized void addOffice(Office o) {
        offices.put(o.getName(), o);
        executor.submit(o); // start office thread
        System.out.println("[System] started " + o.getName() + " for " + o.getDocumentType());
    }

    // find office that can issue given document
    public synchronized Office getOfficeForDoc(String docName) {
        for (Office o : offices.values()) {
            if (o.getDocumentType().equals(docName)) {
                return o;
            }
        }
        return null;
    }

    // build dependency plan for requested document
    public List<String> getAcquisitionPlan(String target) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        buildPlanDFS(target, ordered, new HashSet<>());
        return new ArrayList<>(ordered);
    }

    // DFS to compute dependencies in order
    private void buildPlanDFS(String docName, LinkedHashSet<String> ordered, Set<String> visiting) {
        Document d = documents.get(docName);
        if (d == null) {
            ordered.add(docName);
            return;
        }

        if (visiting.contains(docName)) return; // avoid loops
        visiting.add(docName);

        for (String dep : d.getDependencies()) {
            buildPlanDFS(dep, ordered, visiting);
        }

        ordered.add(docName);
    }

    // run random background events
    public void startRandomEvents() {
        Thread controller = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);

                    synchronized (BureaucracySystem.this) {
                        for (Office o : offices.values()) {
                            // random break
                            if (random.nextDouble() < 0.05) {
                                long breakMs = 500 + random.nextInt(1000);
                                o.takeBreak(breakMs);
                            }

                            // random paper refill
                            if (random.nextDouble() < 0.1) {
                                o.restockPaper(2 + random.nextInt(3));
                            }

                            // open extra counter if queue too long
                            if (o.getQueueSize() > 5 && random.nextDouble() < 0.2) {
                                String newName = o.getName() + "-extra-" + (100 + random.nextInt(900));
                                if (!offices.containsKey(newName)) {
                                    Office clone = new Office(newName, o.getDocumentType(), this);
                                    addOffice(clone);
                                    System.out.println("[System] opened " + newName + " for " + o.getDocumentType());
                                }
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ignored) {}
            }
        });

        controller.setDaemon(true); // stops with main program
        controller.start();
    }
}
