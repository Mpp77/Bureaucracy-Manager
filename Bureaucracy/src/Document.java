import java.util.*;

public class Document
{
    String name;
    List<String> dependencies;

    public Document(String name, List<String> dependencies)
    {
        this.name = name;
        this.dependencies = dependencies;
    }

    public String getName()
    {
        return name;
    }

    public List<String> getDependencies()
    {
        return dependencies;
    }
}

//Clasa Document reține numele unui act și lista actelor de care depinde
//pta simula relațiile dintre documente (dependințe)