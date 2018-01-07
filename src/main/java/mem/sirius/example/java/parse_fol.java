package mem.sirius.example.java;

import java.io.File;
import java.util.ArrayList;

public class parse_fol {
    private ArrayList<String> files = new ArrayList<String>();

    public void processFilesFromFolder(File folder) {
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries) {
            if (entry.isDirectory()) {
                processFilesFromFolder(entry);
                continue;
            }
            files.add(entry.getName());
        }
    }

    public String getLinksAccepted(String a, Integer b, String serverpath) {
        String ans = null;
        for (String path : files) {
            if (b == 0){
                break;
            }
            if (ans != null) {
                ans = ans + ',';
            }
            if (a == null || path.compareTo(a) < 0){
                if (ans == null){
                    ans = "";
                }
                ans = ans + serverpath + path;
                b--;
            }
        }
        return ans;
    }
}
