package it.unimi.di.ewlab.iss.common.model.actionsmodels;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.actions.VocalAction;

public class VocalActionsModel extends ActionsModel{


    private ArrayList<VocalAction> vocalActions = null;
    public static final String NOISE_NAME = "Noise";

    public VocalActionsModel(String name){
        modelName = name;
        vocalActions = new ArrayList<>();
    }

    public VocalActionsModel(String model_name, ArrayList<VocalAction> vocalActions) {
        modelName = model_name;
        this.vocalActions = new ArrayList<>();
        this.vocalActions = vocalActions;

    }

    public void addVocalAction(VocalAction action){
        vocalActions.add(action);
    }

    public void removeVocalAction(VocalAction action){
        vocalActions.remove(action);
    }
    public String getName() { return modelName; }

    public ArrayList<VocalAction> getVocalActions(){ return vocalActions;}

    public void setName(String model_name){
        this.modelName = model_name;
    }

    public boolean containsVocalAction(String actionName){

        for(VocalAction vocal : this.getVocalActions()){
            if(vocal.getName().equals(actionName)){
                return true;
            }
        }

        return false;
    }

    /**
     * This method returns true if the model includes all and only the vocal actions given as a parameter
     * @param noiseIncluded a Boolean value that defines whether the actionItemVocal list contains the noise value or not
     * @param vocalActions the list of ActionVocal to check, excluded the "noise" sound
     * @return true if this SVMmodel contains all (and only) the sounds passed as argument
     */
    boolean containsTheVocalActions(boolean noiseIncluded, List<VocalAction> vocalActions) {

        if(noiseIncluded){

            if (vocalActions.size() != vocalActions.size())
                return false;

        }
        else {

            if (vocalActions.size() != vocalActions.size()+1) return false;

        }

        for (VocalAction item : vocalActions) {
            if (!vocalActions.contains(item)) return false;
        }

        return true;
    }

    /**
     * This method deletes the svmModel file associated with this svmModel
     * @return true if the deletion was successful
     */
    boolean prepareForDelete(){
        boolean deleted = false;
        //String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/A-Cube/Models/"+ modelName;
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/Play_Access/Models/"+ modelName;
        File fdelete = new File(filePath);

        deleted = fdelete.delete();

        return deleted;
    }

    //METODO PER ELIMINARE IL FILE DEL MODELSVM DALLA CARTELLA Models
    public void deleteModelFile(String fileName) {

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/A-Cube/Models/" + fileName;
        File recordFile = new File(filePath);
        if (recordFile.exists())
            recordFile.delete();

    }


    public boolean equals(Object other){
        if(!(other instanceof VocalActionsModel)){
            return false;
        }

        VocalActionsModel otherVocalActionsModel = (VocalActionsModel) other;
        return this.getName().equals(otherVocalActionsModel.getName());
    }

    @Override
    public String toString() {
        return "SVMmodel{" +
                "modelName='" + modelName + '\'' +
                ", sounds=" + vocalActions +
                '}';
    }
}