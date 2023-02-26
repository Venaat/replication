import java.io.File;
import java.io.IOException;

// TODO : utiliser File.separator au lieu des "/" dans le main
// TODO : faire de vrais tests au lieu des sout

/**
 * Bibliothèques nécessaires au projet :
 *  - org.apache : commons io (fonctions de manipulation de dossier et fichiers)
 *  - org.apache : commons lang3 (fonctions de manipulation de chaînes de caractères)
 */
public class Main {

    final static String USER_DIR = System.getProperty("user.dir");
    public static void main(String[] args) throws Exception {
        boolean sout_debug = true;
        boolean testProperties = true;
        boolean testRelativePath = false;
        boolean testAbsolutePath = false;
        boolean testAncestors = false;
        boolean testGetChildren = false;
        // Attention à bien créer la référence au moins une fois avant de lancer la synchronisation
        // Ou créer les dossiers à la main, voir doc de generateReference()
        boolean createRef = false ;
        boolean testComputeDirty = true;
        boolean testReconcile = true;

        String folderName = "synHome";
        String directoryRoot = USER_DIR + File.separator + folderName;
        String directoryRootB = USER_DIR + File.separator + "systemeB" + File.separator + folderName;

        // Système A
        FileSystem fileSystem = new LocalFileSystem(directoryRoot);
        FileSystem refSystem = fileSystem.getReference();

        // Système B
        FileSystem fileSystem2 = new LocalFileSystem(directoryRootB);
        FileSystem refSystem2 = fileSystem2.getReference();

        Synchronizer sync = new Synchronizer();

        /*
            Test des get et propriétés simples
         */
        if (testProperties){

            // assert(

            if (sout_debug) {
                System.out.println("(Main) Test propriétés : ");
                System.out.println("--------- SystA se trouve ici : " + directoryRoot + "---------");
                System.out.println("Root A : " + fileSystem.getRoot());
                System.out.println("SyncRoot A : " + fileSystem.getSyncRoot());
                System.out.println("Référence A : " + refSystem.getRoot() + File.separator + refSystem.getSyncRoot());
                System.out.println();

                System.out.println("--------- SystB se trouve ici : " + directoryRootB + "---------");
                System.out.println("Root B : " + fileSystem2.getRoot());
                System.out.println("SyncRoot B : " + fileSystem2.getSyncRoot());
                System.out.println("Référence B : " + refSystem2.getRoot() + File.separator + refSystem2.getSyncRoot());
                System.out.println();
            }
        }

        /*
            Test fonctions AbsolutePath, RelativePath
         */
        if (testRelativePath) {
            String absolutePathA_1 = directoryRoot + File.separator + "x.txt" ;
            String absolutePathA_2 = directoryRoot + File.separator + "subdir1" + File.separator + "z.txt";
            String absolutePathB_1 = directoryRootB + File.separator + "x.txt";
            String absolutePathB_2 = directoryRootB + File.separator + "subdir1" + File.separator + "z.txt";

            String toRelativeA_1 = fileSystem.getRelativePath(absolutePathA_1);
            String toRelativeA_2 = fileSystem.getRelativePath(absolutePathA_2);
            String toRelativeB_1 = fileSystem2.getRelativePath(absolutePathB_1);
            String toRelativeB_2 = fileSystem2.getRelativePath(absolutePathB_2);

            assert(toRelativeA_1.equals(toRelativeB_1));
            assert(toRelativeA_2.equals(toRelativeB_2));

            if (sout_debug){
                System.out.println("--------- RelativePath pour A ---------");
                System.out.println("Chemin absolu : " + absolutePathA_1) ;
                System.out.println("-> Chemin relatif : " + toRelativeA_1);
                System.out.println();
                System.out.println("Chemin absolu : " + absolutePathA_2) ;
                System.out.println("-> Chemin relatif : " + toRelativeA_2);
                System.out.println();


                System.out.println("--------- RelativePath pour B ---------");
                System.out.println("Chemin absolu : " + absolutePathB_1) ;
                System.out.println("-> Chemin relatif : " + toRelativeB_1);
                System.out.println();
                System.out.println("Chemin absolu : " + absolutePathB_2) ;
                System.out.println("-> Chemin relatif : " + toRelativeB_2);
                System.out.println();
            }


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
            System.out.println("--------- Dirties ---------");
            System.out.println("(Main) Dirties de A : " + sync.computeDirty(fileSystem, refSystem, ""));
            System.out.println("(Main) Dirties de B : " + sync.computeDirty(fileSystem2, refSystem2, ""));
            System.out.println();
        }

        /*
            Test fonction finale
         */
        if (testReconcile){
            sync.synchronize(fileSystem, fileSystem2);
        }

    }


}