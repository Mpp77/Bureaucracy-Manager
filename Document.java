import java.util.ArrayList;
import java.util.List;

public class Document {
    private final String name;
    private final List<Document> deps = new ArrayList<>();

    public Document(String name) { this.name = name; }

    public String getName() { return name; }

    public void addRequirement(Document d) { deps.add(d); }

    public List<Document> getRequiredDocuments() { return deps; }

    @Override
    public String toString() { return name; }
}
