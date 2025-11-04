public class Main {
    public static void main(String[] args) {

        BureaucracySystem sys = new BureaucracySystem();

        // Documents
        Document ID = new Document("ID");
        Document FormA = new Document("FormA");
        Document Passport = new Document("Passport");
        Document TaxReceipt = new Document("TaxReceipt");

        FormA.addRequirement(ID);
        Passport.addRequirement(FormA);
        Passport.addRequirement(TaxReceipt);

        sys.addDocument(ID);
        sys.addDocument(FormA);
        sys.addDocument(Passport);
        sys.addDocument(TaxReceipt);

        // Initial offices
        sys.addOffice("InfoDesk", "InfoDesk");
        sys.addOffice("FormsDesk", "FormsDesk");
        sys.addOffice("PaymentsDesk", "PaymentsDesk");


        // Assign which desk handles what
        sys.assign("InfoDesk", "ID");
        sys.assign("FormsDesk", "FormA");
        sys.assign("PaymentsDesk", "TaxReceipt");
        sys.assign("InfoDesk", "Passport");

        // First wave of 4 clients
        for (int i = 1; i <= 4; i++) {
            new Customer("Client" + i, sys, Passport).start();
        }

        // Wait then spawn 2 more
        try { Thread.sleep(6000); } catch (Exception ignored) {}
        for (int i = 5; i <= 6; i++) {
            new Customer("Client" + i, sys, Passport).start();
        }

        // Continue normal arrivals
        int n = 7;
        while (true) {
            new Customer("Client" + n++, sys, Passport).start();
            try { Thread.sleep(5000); } catch (Exception ignored) {}
        }
    }
}