import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LocalFileSystem implements FileSystem{
    String root;            // "/../../../ (puis synHome) "
    String ref;             // "/../../reference/synHome "
    File rootFile;
    File refFile;
    String syncRoot;        // "synHome"
    File syncFile;

    public LocalFileSystem(String path) throws IOException {
        this.syncFile = new File(path);
        this.syncRoot = syncFile.getName();

        this.root = StringUtils.substringBeforeLast(path, File.separator);
        this.rootFile = new File(root);

        createReference();

    }

    /**
     * Renvoie ce qui est derrière la racine du dossier synchronisé
     * Exemple : pour un dossier synchronisé /../../../directory/synHome,
     *                              renvoie  /../../../directory
     */
    @Override
    public String getRoot() {
        return root;
    }

    /**
     * Renvoie le nom du dossier de synchronisation.
     * Exemple : pour un dossier synchronisé /../../../directory/synHome,
     *                              renvoie  synHome
     */
    public String getSyncRoot() { return syncRoot; }


    /**
     * Cette fonction renvoie le nom du parent au-dessus,
     * i.e ce qui est juste derrière le dernier "/".
     */
    @Override
    public String getParent(String path) {
        String parent = new File(path).getParent();
        File parentFile = new File(parent);
        return parentFile.getName();
    }

    /**
     * Renvoie le chemin relatif d'un enfant
     * On dirait que ça utilise le working directory, parce que fonctionne
     * lorsqu'on lui donne "synHome" en paramètre par ex
     */
    @Override
    public List<String> getChildren(String path) throws IllegalArgumentException {
        File file = new File(path);

        if (file.isDirectory()){
            List<String> children = new ArrayList<>();
            File[] files = file.listFiles();
            for (File child : files){
                children.add( getRelativePath(file.getAbsolutePath() + File.separator + child.getName()));
            }

            return children;
        }else{
            List<String> children = new ArrayList<>();
            children.add(path);
            return children;
        }
    }

    /**
     * Ancêtres jusque la racine du dossier synchronisé
     * Utilise la fonction getRelativePath
     *
     * Actuellement : donner en paramètre un chemin absolu
     */
    @Override
    public List<String> getAncestors(String path) {
        path = getRelativePath(path) ;
        List<String> ancestors = Pattern.compile(File.separator)
                .splitAsStream(path)
                .collect(Collectors.toList());
        ancestors.remove(0);

        return ancestors;
    }

    /**
     * Ne fait que rajouter la racine du dossier synchronisé au relative path donné.
     */
    @Override
    public String getAbsolutePath(String relativePath) {
        return root + File.separator + syncRoot + relativePath;
    }

    public String getAbsolutePathNoSyncHome(String relativePath) { return root + File.separator + relativePath ; }

    /**
     * Renvoie le path dans le dossier synchronisé, commençant par
     * le nom de la racine du dossier synchronisé.
     */
    @Override
    public String getRelativePath(String absolutePath) {
        return StringUtils.substringAfter(absolutePath, this.root);
    }

    @Override
    public FileSystem getReference() throws IOException {
        return new LocalFileSystem(ref);
    }


    /**
     * Initialise les variables :
     *  - ref
     *  - refFile
     *
     * Crée le path de référence tel que pour root :
     *        /../../root/
     *    ->  /../../reference/root/
     *
     * Ainsi que le dossier copie.
     *
     * Écrase l'ancien dossier copie.
     */
    public void createReference(){
        this.ref =
                this.root
                + File.separator
                + "reference"
                + File.separator
                + this.syncRoot;

        refFile = new File(ref);
    }

    /**
     * Crée le dossier au path de référence donné,
     * supprimer l'ancien
     * @throws IOException
     */
    public void generateReference() throws IOException{
        FileUtils.deleteDirectory(refFile);
        FileUtils.copyDirectory(syncFile, refFile);
    }

    @Override
    public void copyFile(File input, File output) throws Exception {
        FileUtils.copyFile(input, output);
    }


    @Override
    public File copyDirectory(File input, File output) throws IOException {
        FileUtils.copyDirectory(input, output);
        return null;
    }

    public void remove(File target) throws IOException {
        //System.out.println("[REMOVE THIS] deleting "+target);
        FileUtils.forceDelete(target);
    }

    @Override
    public void replace(String absolutePathTargetFS, FileSystem fsSource, String absolutePathSourceFS) {

    }



}
