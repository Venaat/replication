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
        boolean testAncestors = false;
        boolean testRelativePath = false;
        boolean testAbsolutePath = false;
        boolean testSynchronize = false;
        boolean createRef = false;

        String folderName = "synHome";
        String directoryRoot = USER_DIR + "/" + folderName;

        FileSystem fileSystem = new LocalFileSystem(directoryRoot);
        fileSystem.createReference();
        FileSystem refSystem = fileSystem.getReference();

        /*
            Test des get et propriétés simples
         */
        if (testProperties){
            System.out.println(fileSystem.getRoot());
            System.out.println(fileSystem.getSyncRoot());
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
            Create ref
         */
        if (createRef){
            fileSystem.generateReference();
        }

        /*
            Test Synchronizer
         */
        if (testSynchronize){
            Synchronizer sync = new Synchronizer();
            System.out.println(sync.computeDirty(fileSystem, refSystem, ""));
        }



    }


}