public class Main
{
    public static void main(String[] args)
    {
        BureaucracySystem system = new BureaucracySystem();

        Office a = new Office("Counter A", "DocA", system);
        Office b = new Office("Counter B", "DocB", system);
        Office c = new Office("Counter C", "DocC", system);

        system.addOffice(a);
        system.addOffice(b);
        system.addOffice(c);

        system.addDocument(new Document("DocA", java.util.List.of()));
        system.addDocument(new Document("DocB", java.util.List.of("DocA")));
        system.addDocument(new Document("DocC", java.util.List.of("DocA", "DocB")));
        system.addDocument(new Document("DocD", java.util.List.of("DocC"))); // nested dependency
        system.addDocument(new Document("DocE", java.util.List.of("DocA", "DocD"))); // complex chain

        system.startRandomEvents();

        new Customer("Customer-1", system, "DocB").start();
        new Customer("Customer-2", system, "DocE").start();
        new Customer("Customer-3", system, "DocA").start();
        new Customer("Customer-4", system, "DocD").start();
        new Customer("Customer-5", system, "DocC").start();
    }
}

/*
Ce am făcut:
- e creat un sistem birocratic concurent în Java.
- Fiecare Customer și Office rulează pe thread-uri separate.
- Sistemul simulează probleme reale:
    • dependențe între documente
    • ghișee care se închid și se redeschid (pauze)
    • lipsă de hârtie și reaprovizionare
    • cozi lungi → se deschide ghișeu suplimentar
    • redirecționare automată a clienților

Ce s-ar mai putea îmbunătăți:
- Să salvăm logurile într-un fișier text (nu doar consolă).
- Să adăugăm o interfață grafică (JavaFX sau Swing).
- Să controlăm mai bine durata simulării (ex: oprire automată după 1 minut).
- Să folosim un fișier JSON pentru configurare (office-uri, documente, dependențe).

*/
