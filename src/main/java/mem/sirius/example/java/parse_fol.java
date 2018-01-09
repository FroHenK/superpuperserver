package mem.sirius.example.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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

    public ArrayList<String> getLinksAccepted(String a, Integer b, String serverpath) {
        ArrayList<String> ans = new ArrayList<String>();
        Collections.sort(files);
        for (String path : files) {
            System.out.println(path);
            if (b == 0){
                break;
            }
            if (a.length() == 0 || path.compareTo(a) > 0) {
                ans.add(serverpath + path);
                b--;
            }
        }
        return ans;
    }
}
