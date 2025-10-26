import java.util.List;

public class Document {
    private final String name;                // document name
    private final List<String> dependencies;  // required docs

    public Document(String name, List<String> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public List<String> getDependencies() {
        return dependencies;
    }
}
