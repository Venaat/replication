import java.io.File;
import java.io.IOException;
import java.util.List;
public interface FileSystem {

    public String getRoot();

    public String getSyncRoot();

    public String getParent(String path);

    public List<String> getChildren(String path);
    public List<String> getAncestors(String path);

    public String getAbsolutePath(String relativePath);
    public String getAbsolutePathNoSyncHome(String relativePath);

    public String getRelativePath(String absolutePath);


    public FileSystem getReference() throws IOException;


    public void createReference() ;


    public void generateReference() throws IOException;

    public File copyDirectory(File input, File output) throws IOException;
    public void copyFile(File input, File output) throws Exception;
    public void replace(String absolutePathTargetFS,
                        FileSystem fsSource,
                        String absolutePathSourceFS);
}
