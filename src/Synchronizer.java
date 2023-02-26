import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;

public class Synchronizer {

    public void synchronize(FileSystem fs1, FileSystem fs2) throws IOException {
        FileSystem refCopy1 = fs1.getReference();
        FileSystem refCopy2 = fs2.getReference();

        List<String> dirtyPaths1 = computeDirty(refCopy1, fs1, "");
        List<String> dirtyPaths2 = computeDirty(refCopy2, fs2, "");
        reconcile(fs1, dirtyPaths1, fs2, dirtyPaths2, "");
    }

    public void reconcile(FileSystem fs1,
                          List<String> dirtyPaths1,
                          FileSystem fs2,
                          List<String> dirtyPaths2,
                          String currentRelativePath){
        System.out.println("Current relative path : " + currentRelativePath);
            File file1 = new File(fs1.getAbsolutePath(currentRelativePath));
            File file2 = new File(fs2.getAbsolutePath(currentRelativePath));

            if (currentRelativePath.equals("") ){
                currentRelativePath = File.separator + fs1.getSyncRoot();
            }

            /* Cas n°1 : si le path indiqué n'a eu aucune modification */
            if (!dirtyPaths1.contains(currentRelativePath) && !dirtyPaths2.contains(currentRelativePath))
                return;

            /* Cas n°2 : si file est un dossier et dans A, et dans B */
            if (file1.isDirectory() && file2.isDirectory()){

                List<String> childrenA = fs1.getChildren(file1.getPath());
                List<String> childrenB = fs2.getChildren(file2.getPath());

                List<String> plist = getPlist(childrenA, childrenB);

                for (String path : plist)
                    reconcile(fs1,
                        dirtyPaths1,
                        fs2,
                        dirtyPaths2,
                        path);

                /* Cas n°3 : not dirty to A, dirty to B */
            }else if (dirtyPaths2.contains(currentRelativePath)) {
                applyChanges(fs1, fs2, currentRelativePath);

                /* Cas n°4 : not dirty to B, dirty to A */
            }else if (dirtyPaths1.contains(currentRelativePath)) {
                applyChanges(fs2, fs1, currentRelativePath);

                /* Cas n°5 : conflits */
            }else{
                System.out.println(currentRelativePath);
                manageConflict(fs1, fs2, currentRelativePath);
            }
        }


    /**
     * Retourne la liste fusionnée et sans double des enfants de A et de B
     */
    private List<String> getPlist(List<String> childrenA, List<String> childrenB) {
        for (String child : childrenB){
            if (!childrenA.contains(child)) childrenA.add(child);
        }
        Collections.sort(childrenA);
        return childrenA;
    }


    /**
     * @param currentRelativePath : ""
     * (je ne sais pas si ça fonctionne avec autre chose...)
     * (à noter qu'on transforme directement le param avec la racine du dossier synchronisé )
     *
     * Renvoie la liste des fichiers, dossiers modifiés.
     * Exemple :
     * [/synHome, /synHome/subdir1, /synHome/subdir1/z.txt, /synHome/subdir2,
     *  /synHome/subdir2/subsubdir1, /synHome/subdir2/subsubdir1/alpha.txt  ]
     *
     *  Note :Ce serait une bonne idée de la revoir un jour vu la tête qu'elle a
     */
    public List<String> computeDirty(FileSystem fs,
                                     FileSystem lastSync,
                                     String currentRelativePath) throws IOException {

        String pathCurrent = fs.getAbsolutePath(currentRelativePath);

        List<String> abspaths = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        List<String> dirtyFiles = new ArrayList<>();
        // On choisit un set pour éviter les doublons et avoir un tri à l'insertion
        Set<String> dirties = new TreeSet<>();

        // On obtient tous les chemins absolus des sous-parties du dossier
        Files.walk(Paths.get(pathCurrent)).forEach(path ->
        abspaths.add(path.toFile().getAbsolutePath()));

        // On obtient leurs paths relatifs pour pouvoir les recoller
        // sur le fs actuel et sur le fs de référence
        for (String path : abspaths){
            paths.add(fs.getRelativePath(path));
        }

        for (String path : paths){
            File file = new File(fs.getRoot() + path);
            File lastSyncedFile = new File(lastSync.getRoot() + path);

            if (file.isFile()){
                if (! FileUtils.contentEquals(file, lastSyncedFile)) {
                    String dirtyFile = fs.getRelativePath(fs.getRoot() + path);
                    dirtyFiles.add(dirtyFile);
                    dirties.add(dirtyFile);
                    dirties.add(File.separator + fs.getSyncRoot());
                }
            }

        }

        // Enfin, on crée la chaine comme on veut pour l'utiliser dans l'algo
        // i.e E, E/subdir/, E/subdir/z.txt
        for (String dirty : dirtyFiles){
            String todo = dirty;
            String toast = File.separator + fs.getSyncRoot();

            while ( ! (todo = StringUtils.substringBeforeLast(todo, File.separator)).equals(toast)){
                dirties.add(todo);
            }
        }

        return new ArrayList<>(dirties);
    }


    private void manageConflict(FileSystem fs1, FileSystem fs2, String currentRelativePath) {
        System.out.println("Un conflit est géré ici : " + currentRelativePath);
    }

    private void applyChanges(FileSystem fs2, FileSystem fs1, String currentRelativePath) {
        System.out.println("Des changements sont appliqués ici : " + currentRelativePath);

    }

}
