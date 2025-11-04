import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Document {
    private final String name;
    private final List<Document> deps = new ArrayList<>();

    public Document(String name) { this.name = name; }

    public String getName() { return name; }

    public void addRequirement(Document d) { deps.add(d); }

    public List<Document> getRequiredDocuments() { return deps; }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document other = (Document) o;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}