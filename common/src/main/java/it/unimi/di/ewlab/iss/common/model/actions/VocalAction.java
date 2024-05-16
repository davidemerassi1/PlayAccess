package it.unimi.di.ewlab.iss.common.model.actions;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class VocalAction extends Action {

    private Set<String> fileNames = new HashSet<>();

    public ArrayList<VocalFile> vocalFiles = new ArrayList<>();

    private static final ActionType TYPE = ActionType.VOCAL;

    public VocalAction(int actionId, String name, ArrayList<VocalFile> vocalFiles) {
        super(actionId,name, TYPE);

        this.vocalFiles = vocalFiles;
    }

    public VocalAction(int actionId, String name) {
        super(actionId,name, TYPE);

    }

    public VocalAction(int actionId, String name, Set<String> files) {
        super(actionId,name, TYPE);
        this.fileNames = files;
    }

    //METODO CHE RITORNA LA LISTA DI FILE VOCALI
    public ArrayList<VocalFile> getVocalFiles() {
        return vocalFiles;
    }

    // Ritorna il relativo VocalFile dal nome del file
    public VocalFile getVocalFile(String fileName) {

        VocalFile temp = null;

        for(VocalFile vocalFile : vocalFiles) {

            if(vocalFile.getFileName().equals(fileName))
                temp = vocalFile;
        }

        return temp;
    }

    // Aggiunge vocalFile alla lista di vocalFiles
    public void addVocalFile(VocalFile vocalFile){
        vocalFiles.add(vocalFile);
    }

    // Aggiunge una lista di VocalFile alla lista di vocalFiles
    public void addVocalFiles(ArrayList<VocalFile> newVocalFiles) {
        vocalFiles.addAll(newVocalFiles);
    }

    // Elimina il VocalFile passato come argomento dalla lista di vocalFiles
    public void deleteVocalFile(VocalFile vocalFile) {
        vocalFiles.remove(vocalFile);
    }

    public Set<String> getFiles() {
        return fileNames;
    }

    public ArrayList<VocalFile> getFiles2() { return vocalFiles; }

    public void setFiles(Set<String> files) { this.fileNames = files; }

    /**
     * This method returns true if the deletion of the specified file was successful. This method eliminates both the file reference
     * within the action and the file itself in the Download / A-Cube / Sounds folder
     * @param fileName of the file to delete
     * @return true if deleted
     */
    public boolean deleteFile(String fileName){
        boolean deleted = false;

        for (Iterator<String> iterator = fileNames.iterator(); iterator.hasNext();) {

            String s =  iterator.next();
            if (s.equals(fileName)) {
                iterator.remove();

                //String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/A-Cube/Sounds/"+fileName;
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/Play_Access/Sounds/"+fileName;
                File fdelete = new File(filePath);

                deleted = fdelete.delete();
            }
        }

        return deleted;
    }

    public void deleteFile2(String fileName) {

        //String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/A-Cube/Sounds/" + fileName;
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/Play_Access/Sounds/" + fileName;
        File recordFile = new File(filePath);
        if (recordFile.exists())
            recordFile.delete();

    }

    public void addFile(String fileName){
        this.fileNames.add(fileName);
    }

    public void addFiles(Set<String> newFiles){
        fileNames.addAll(newFiles);
    }

    public boolean deleteAllSounds(){
        boolean deleted = false;

        for(String file : fileNames){
            //String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/A-Cube/Sounds/"+file;
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/Play_Access/Sounds/"+file;
            File fdelete = new File(filePath);

            deleted = fdelete.delete();

        }
        //ELIMINO LE REGISTRAZIONI
        for(VocalFile vocalFile : vocalFiles) {
            Log.d("deleteAllSounds","dentro for, vocalFile: "+vocalFile.toString());
            this.deleteFile2(vocalFile.getFileName() + ".wav");
        }

        fileNames.clear();
        vocalFiles.clear();

        return deleted;
    }

}
