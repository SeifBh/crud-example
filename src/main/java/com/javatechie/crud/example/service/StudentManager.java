public class StudentManager {

    // Mauvaise pratique : utilisation de variables globales non encapsulées
    public static List<String> studentNames = new ArrayList<>();
    public static Map<String, Integer> studentGrades = new HashMap<>();

    // Mauvaise pratique : méthode main trop chargée
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Mauvaise pratique : boucle infinie sans condition d'arrêt claire
        while (true) {
            System.out.println("1. Ajouter un étudiant");
            System.out.println("2. Afficher les étudiants");
            System.out.println("3. Calculer la moyenne");
            System.out.println("4. Quitter");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            if (choice == 1) {
                System.out.println("Nom de l'étudiant :");
                String name = scanner.nextLine();
                studentNames.add(name);

                System.out.println("Note de l'étudiant :");
                int grade = scanner.nextInt();
                studentGrades.put(name, grade);
            } else if (choice == 2) {
                for (String name : studentNames) {
                    System.out.println("Étudiant : " + name + ", Note : " + studentGrades.get(name));
                }
            } else if (choice == 3) {
                int total = 0;
                for (String name : studentGrades.keySet()) {
                    total += studentGrades.get(name);
                }
                System.out.println("Moyenne : " + (total / studentGrades.size())); // Mauvaise gestion des divisions
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Choix invalide.");
            }
        }

        scanner.close();
    }
}
