import java.io.File;
import java.io.IOException;

// TODO : supprimer le contenu du dossier ref après chaque exécution
// TODO : utiliser File.separator au lieu des "/" dans le main
// TODO : faire de vrais tests au lieu des sout

/**
 * Bibliothèques nécessaires au projet :
 *  - org.apache : commons io (fonctions de manipulation de dossier et fichiers)
 *  - org.apache : commons lang3 (fonctions de manipulation de chaînes de caractères)
 */
public class Main {

    final static String USER_DIR = System.getProperty("user.dir");
    public static void main(String[] args) throws IOException {
        boolean testProperties = false;
        boolean testRelativePath = false;
        boolean testAbsolutePath = false;
        boolean testAncestors = false;
        boolean testGetChildren = false;
        // Attention à bien créer la référence au moins une fois avant de lancer la synchronisation
        // Ou créer les dossiers à la main, voir doc de generateReference()
        boolean createRef = false;
        boolean testComputeDirty = true;
        boolean testReconcile = true;

        String folderName = "synHome";
        String directoryRoot = USER_DIR + File.separator + folderName;

        FileSystem fileSystem = new LocalFileSystem(directoryRoot);
        FileSystem refSystem = fileSystem.getReference();

        String directoryRootB = USER_DIR + File.separator + "systemeB" + File.separator + folderName;
        FileSystem fileSystem2 = new LocalFileSystem(directoryRootB);
        FileSystem refSystem2 = fileSystem2.getReference();


        Synchronizer sync = new Synchronizer();

        /*
            Test des get et propriétés simples
         */
        if (testProperties){
            System.out.println("Root A : " + fileSystem.getRoot());
            System.out.println("SyncRoot A : " + fileSystem.getSyncRoot());
            System.out.println("Référence A : " + refSystem.getRoot() + File.separator + refSystem.getSyncRoot());
            System.out.println("Root B : " +fileSystem2.getRoot());
            System.out.println("SyncRoot B : " + fileSystem2.getSyncRoot());
            System.out.println("Référence B : " + refSystem2.getRoot() + File.separator + refSystem2.getSyncRoot());
        }

        /*
            Test fonctions AbsolutePath, RelativePath
         */
        if (testRelativePath) {
            System.out.println("Relative path :");
            System.out.println(fileSystem.getRelativePath(directoryRoot + "/subdir1/z.txt"));
            System.out.println(fileSystem.getRelativePath("/Users/diana/IdeaProjects/replication/synHome/x.txt"));
        }

        if (testAbsolutePath){
            System.out.println("Absolute path :");
            System.out.println(fileSystem.getAbsolutePath("/subdir1/z.txt"));
        }

        /*
            Test ancestors
         */
        if (testAncestors){
            System.out.println("Liste Ancestors : ");
            System.out.println(fileSystem.getAncestors(directoryRoot+"/x.txt"));
            System.out.println(fileSystem.getAncestors(directoryRoot+ "/subdir1/z.txt"));
        }

        /*
            Test des enfants
         */
        if (testGetChildren){
            String folder = "synHome";
            System.out.println("(Main) getChildren(" + folder + ") : " + fileSystem.getChildren(folder));
        }

        /*
            Create ref
         */
        if (createRef){
            fileSystem.generateReference();
            fileSystem2.generateReference();
        }

        /*
            Test Synchronizer
         */
        if (testComputeDirty){
            System.out.println("(Main) Dirties de A : " + sync.computeDirty(fileSystem, refSystem, ""));
            System.out.println("(Main) Dirties de B : " + sync.computeDirty(fileSystem2, refSystem2, ""));
        }

        /*
            Test fonction finale
         */
        if (testReconcile){
            sync.synchronize(fileSystem, fileSystem2);
        }

    }


}