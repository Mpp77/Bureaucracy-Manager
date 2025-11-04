import java.util.*;
import java.util.concurrent.*;

public class BureaucracySystem {

    private final Map<String, Document> docs = new ConcurrentHashMap<>();
    private final Map<String, Office> offices = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> officeDocs = new ConcurrentHashMap<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public void addDocument(Document d) { docs.put(d.getName(), d); }

    public void addOffice(String name, String type) {
        Office o = new Office(name, type, this);
        offices.put(name, o);
        officeDocs.put(name, ConcurrentHashMap.newKeySet());
        pool.submit(o);
        System.out.println("New office opened: " + name + " (" + type + ")");
    }

    public Office getOffice(String name) {
        return offices.get(name);
    }

    public Document getDocument(String name) {
        return docs.get(name);
    }

    public synchronized void expandOffice(Office office) {
        String base = office.getType();
        long count = offices.keySet().stream()
                .filter(n -> n.startsWith(base))
                .count();

        String newOfficeName = base + (count + 1);

        if (offices.containsKey(newOfficeName)) return;

        addOffice(newOfficeName, base);

        Set<String> src = officeDocs.get(office.getName());
        if (src != null) {
            for (String doc : src) {
                officeDocs.get(newOfficeName).add(doc);
            }
        }

        System.out.println("Backup desk created: " + newOfficeName + " for " + base);
    }

    public void assign(String office, String doc) {
        officeDocs.computeIfAbsent(office, k -> ConcurrentHashMap.newKeySet()).add(doc);
    }

    public boolean officeHasDocument(String office, String doc) {
        Set<String> s = officeDocs.get(office);
        return s != null && s.contains(doc);
    }

    public Office findOffice(Document d) {
        return offices.values().stream()
                .filter(o -> officeHasDocument(o.getName(), d.getName()))
                .findAny()
                .orElse(null);
    }

    public List<Document> getPlan(Document t) {
        List<Document> result = new ArrayList<>();
        Set<Document> visited = new HashSet<>();
        dfs(t, visited, result);
        Collections.reverse(result);
        return result;
    }

    private void dfs(Document d, Set<Document> vis, List<Document> out) {
        if (!vis.add(d)) return;
        for (Document req : d.getRequiredDocuments())
            dfs(req, vis, out);
        out.add(d);
    }
}