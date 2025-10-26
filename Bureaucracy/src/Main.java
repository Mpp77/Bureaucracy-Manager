import java.util.List;

public class Main {
    public static void main(String[] args) {
        BureaucracySystem system = new BureaucracySystem();

        Office officeA = new Office("CounterA", "DocA", system);
        Office officeB = new Office("CounterB", "DocB", system);
        Office officeC = new Office("CounterC", "DocC", system);

        system.addOffice(officeA);
        system.addOffice(officeB);
        system.addOffice(officeC);

        // Document dependency graph:
        // DocA -> no deps
        // DocB -> needs DocA
        // DocC -> needs DocA and DocB
        // DocD -> needs DocC
        // DocE -> needs DocA and DocD
        system.addDocument(new Document("DocA", List.of()));
        system.addDocument(new Document("DocB", List.of("DocA")));
        system.addDocument(new Document("DocC", List.of("DocA", "DocB")));
        system.addDocument(new Document("DocD", List.of("DocC")));
        system.addDocument(new Document("DocE", List.of("DocA", "DocD")));

        system.startRandomEvents();

        new Customer("Customer-1", system, "DocB").start();
        new Customer("Customer-2", system, "DocE").start();
        new Customer("Customer-3", system, "DocA").start();
        new Customer("Customer-4", system, "DocD").start();
        new Customer("Customer-5", system, "DocC").start();
    }
}
