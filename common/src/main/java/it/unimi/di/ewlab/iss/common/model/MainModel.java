package it.unimi.di.ewlab.iss.common.model;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import it.unimi.di.ewlab.common.R;
import it.unimi.di.ewlab.iss.common.database.AssociationsDb;
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Prototypical;
import it.unimi.di.ewlab.iss.common.storage.JsonManager;
import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction;
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame;
import it.unimi.di.ewlab.iss.common.model.actions.ScreenGestureAction;
import it.unimi.di.ewlab.iss.common.model.actions.ScreenGestureAction.GestureId;
import it.unimi.di.ewlab.iss.common.model.actions.VocalAction;
import it.unimi.di.ewlab.iss.common.photosdatabase.PhotosDatabase;
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager;
import kotlin.Pair;

public class MainModel {

    private static final String TAG = MainModel.class.getName();
    public static final int NEUTRAL_FACIAL_EXPRESSION_ACTION_ID = 1;
    public static final int FACE_MOVEMENT_ACTION_ID = 0;
    private PersistenceManager persistenceManager;

    private static MainModel instance = null;
    private final JsonManager jsonManager;
    private final String neutralFacialExpressionName;
    private HashMap<Integer, Action> actions = new HashMap<>();
    private HashMap<String, Game> games = new HashMap<>();
    private Game currentGame;
    private static PhotosDatabase DB;
    private List<Frame> tempFacialExpressionActionFrames;
    private final MutableLiveData<ButtonAction> tempButtonAction = new MutableLiveData<>(null);
    private AssociationsDb associationsDb;

    private MutableLiveData<String> activePackage = new MutableLiveData<>("");
    private static List<ActionsChangedObserver> observers = new ArrayList<>();

    public static Action FACE_MOVEMENT_ACTION = new Action("Face Movement", "FACIAL_EXPRESSION", true);

    public PhotosDatabase getDB(Context context) {
        if (DB == null) {
            DB = Room.databaseBuilder(context, PhotosDatabase.class, "pose-photos-db")
                    .createFromAsset("pose-photos-db")
                    .build();
        }
        return DB;
    }

    //METODO PER OTTENERE UN ISTANZA DEL JsonManager con anche il context
    private MainModel(Context context) {
        jsonManager = new JsonManager(context);
        neutralFacialExpressionName = context.getString(R.string.feraction_neutral_expression_name);

        associationsDb = Room.databaseBuilder(context, AssociationsDb.class, "associations")
                .fallbackToDestructiveMigration()
                .build();

        persistenceManager = new PersistenceManager(context);
    }

    //METODO PER OTTENERE UN'ISTANZA DEL MainModel
    public static synchronized MainModel getInstance() {

        if (instance == null) {
            throw new IllegalStateException("MainModel not initialized; call getInstance(context) first");
        }

        return instance;
    }

    //METODO PER OTTENERE UN'ISTANZA DEL MainModel CON IL context
    public static synchronized MainModel getInstance(Context context) {
        if (instance == null)
            instance = new MainModel(context);

        return instance;
    }

    //METODO PER OTTENERE LA LISTA DELLE AZIONI DAL MainModel
    public List<Action> getActions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return actions.values().stream()
                    .sorted(Comparator.comparing(Action::getName))
                    .collect(Collectors.toList());
        } else {
            List<Action> result = new ArrayList<>(actions.values());
            Collections.sort(result, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            return result;
        }
    }

    public static void observeActions(ActionsChangedObserver observer) {
        observers.add(observer);
    }

    private void notifyActionsChanged(Action removedAction) {
        for (ActionsChangedObserver observer : observers) {
            observer.onActionsChanged(removedAction);
        }
    }

    public int getNextActionId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return actions.keySet().stream().max(Comparator.naturalOrder()).orElse(0) + 1;
        } else {
            int max = 0;
            for (int key : actions.keySet())
                if (key > max)
                    max = key;
            return max + 1;
        }
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    //METODO CHE SETTA I GIOCHI NEL MAINMODEL PRENDENDOLI DAL FILE JSON
    public void initActions() {
        actions = new HashMap<>();
        for (Action a : jsonManager.getActionsFromJson()) {
            actions.put(a.getActionId(), a);
        }
        if (getNeutralFacialExpressionAction() != null)
            actions.put(FACE_MOVEMENT_ACTION_ID, FACE_MOVEMENT_ACTION);
    }

    public List<ButtonAction> getButtonActions() {
        ArrayList<ButtonAction> result = new ArrayList<>();
        for (Action item : actions.values())
            if (item instanceof ButtonAction)
                result.add((ButtonAction) item);
        return result;
    }

    //METODO PER OTTENERE LE AZIONI DI TIPO ScreenGesture PRESENTI NEL MainModel
    public List<ScreenGestureAction> getScreenGestureActions() {
        ArrayList<ScreenGestureAction> result = new ArrayList<>();
        for (Action item : actions.values())
            if (item instanceof ScreenGestureAction)
                result.add((ScreenGestureAction) item);
        return result;
    }

    //METODO PER OTTENERE LE AZIONI DI TIPO Vocal PRESENTI NEL MainModel
    public List<VocalAction> getVocalActions() {
        ArrayList<VocalAction> result = new ArrayList<>();
        for (Action item : actions.values()) {
            if (item instanceof VocalAction) result.add((VocalAction) item);
        }
        return result;
    }

    public List<FacialExpressionAction> getFacialExpressionActions() {
        List<FacialExpressionAction> result = new ArrayList<>();
        for (Action item : actions.values()) {
            if (item instanceof FacialExpressionAction)
                result.add((FacialExpressionAction) item);
        }
        return result;
    }

    @Nullable
    public FacialExpressionAction getNeutralFacialExpressionAction() {
        Action action = getActionById(NEUTRAL_FACIAL_EXPRESSION_ACTION_ID);
        if (action == null)
            return null;
        if (action instanceof FacialExpressionAction)
            return (FacialExpressionAction) action;
        throw new IllegalStateException("Action with id " + NEUTRAL_FACIAL_EXPRESSION_ACTION_ID + " is not a FacialExpressionAction");
    }

    public boolean isValidActionName(@NonNull String name) {
        String trimmedName = name.trim();

        for (Action action : actions.values())
            if (action.getName().equals(trimmedName))
                return false;

        return !trimmedName.isEmpty() && !trimmedName.equals(neutralFacialExpressionName);
    }

    //METODO PER OTTENERE UN Azione CON UN DETERMINATO NOME  DAL MainModel
    public @Nullable Action getActionByName(String name) {
        for (Action action : actions.values())
            if (action.getName().equals(name))
                return action;
        return null;
    }

    public @Nullable Action getActionById(int actionId) {
        return actions.get(actionId);
    }

    public @Nullable ButtonAction getButtonActionByKeyCode(int keyCode) {
        for (Action action : actions.values()) {
            if (action instanceof ButtonAction) {
                ButtonAction buttonAction = (ButtonAction) action;
                if (buttonAction.getKeyId().equals(String.valueOf(keyCode))) {
                    return buttonAction;
                }
            }
        }
        return null;
    }

    public void setNeutralFacialExpressionAction(@NonNull FacialExpressionAction action) {
        if (getActionById(NEUTRAL_FACIAL_EXPRESSION_ACTION_ID) != null)
            throw new IllegalStateException("Action with id " + NEUTRAL_FACIAL_EXPRESSION_ACTION_ID + " already defined");
        if (action.getActionId() != NEUTRAL_FACIAL_EXPRESSION_ACTION_ID)
            throw new IllegalStateException("Action id is not " + NEUTRAL_FACIAL_EXPRESSION_ACTION_ID);
        if (!Objects.equals(action.getName(), neutralFacialExpressionName))
            throw new IllegalStateException("Action name is not " + neutralFacialExpressionName);
        actions.put(NEUTRAL_FACIAL_EXPRESSION_ACTION_ID, action);
        actions.put(FACE_MOVEMENT_ACTION_ID, FACE_MOVEMENT_ACTION);
    }


    /**
     * Removes the Action with the indicated name. In case that action is used in a configuration,
     * that configuration action is set to null. If the Action removed is a ActionVocal this method deletes all the files related to it
     * and all the SVMmodel that have at least one file in common.
     *
     * @param actionId the id of the action to delete
     * @return if the action was used in a configuration
     */
    public boolean removeAction(int actionId) {
        boolean linksRemoved = false;

        Action action = getActionById(actionId);
        if (action == null)
            return false;

        if (action instanceof VocalAction) {
            ((VocalAction) action).deleteAllSounds();
        }

        for (Game game : games.values()) {
            for (Configuration conf : game.getConfigurations()) {
                for (Link link : conf.getLinks()) {
                    if (link.getActionId() == actionId) {
                        link.setAction(null);
                        linksRemoved = true;
                        break;
                    }
                }
            }
        }

        actions.remove(actionId);
        notifyActionsChanged(action);

        return linksRemoved;
    }

    public String findVocalActionFromFile(String fileName) {
        String label = "";
        List<VocalAction> allVocalActions = getVocalActions();

        for (VocalAction aiv : allVocalActions) {
            if (aiv.getFiles().contains(fileName)) {
                label = aiv.getName();
            }
        }

        return label;
    }


    public boolean removeFileVocalAction(String actionName, String fileName) {

        boolean deleted = false;

        VocalAction thisVocalAction = (VocalAction) getActionByName(actionName);
        assert thisVocalAction != null;
        Set<String> files = thisVocalAction.getFiles();

        for (String file : files)
            if (file.equals(fileName))
                deleted = thisVocalAction.deleteFile(fileName);

        if (thisVocalAction.getFiles().size() == 0)
            this.removeAction(thisVocalAction.getActionId());

        return deleted;
    }

    public LiveData<ButtonAction> getTempButtonAction() {
        return tempButtonAction;
    }

    public void setTempButtonAction(ButtonAction action) {
        tempButtonAction.setValue(action);
    }

    public List<Frame> getTempFacialExpressionActionFrames() {
        List<Frame> temp = tempFacialExpressionActionFrames;
        tempFacialExpressionActionFrames = null;
        return temp;
    }

    public void setTempFacialExpressionActionFrames(@NonNull List<Frame> tempActionFrames) {
        if (tempActionFrames.size() != FacialExpressionAction.FRAMES_X_EXPRESSION) {
            Log.e(TAG, "Missing frames " + tempActionFrames.size() + "/" + FacialExpressionAction.FRAMES_X_EXPRESSION);
            throw new IllegalArgumentException(
                    "Missing frames " + tempActionFrames.size() + "/" + FacialExpressionAction.FRAMES_X_EXPRESSION
            );
        }
        tempFacialExpressionActionFrames = tempActionFrames;
    }

    public List<Pair<Integer, Integer>> getFacialFeaturesDistances(@NonNull Context context) {
        return jsonManager.getFacialFeaturesDistancesFromJson(context);
    }

//----------------------------------------------------------------------------------------------------------------------------------

    /**
     * METODI PER GESTIRE I GIOCHI
     */

    //METODO CHE SETTA I GIOCHI NEL MAINMODEL PRENDENDOLI DAL FILE JSON
    /*public void initGames() {
        games = new HashMap<>();

        for (Game g : jsonManager.getGamesFromJson())
            games.put(g.getBundleId(), g);
    }*/

    //METODO PER OTTENERE I GIOCHI PRESENTI NEL MainModel
    public ArrayList<Game> getGames() {
        return new ArrayList<>(games.values());
    }

    //METODO PER OTTENERE UN GIOCO CON UN DETERMINATO BundleId
    public @Nullable Game getGameByBundleId(String bundleId) {
        return games.get(bundleId);
    }

    //METODO PER OTTENERE UN GIOCO CON UN DETERMINATO NOME
    public @Nullable Game getGameByTitle(String title) {
        for (Game game : games.values())
            if (game.getTitle().equals(title))
                return game;
        return null;
    }

    //METODO PER AGGIUNGERE UN NUOVO GIOCO AL MainModel
    public Boolean addNewGame(@NonNull Game newGame) {
        if (games.containsKey(newGame.getBundleId()))
            return false;

        games.put(newGame.getBundleId(), newGame);

        return true;
    }


    /**
     * Removes the Game with the indicated title and all the configuratios that refer to it
     *
     * @param bundleId title of the game to be removed
     * @return the removed gameItem, if any.
     */
    //METODO PER RIMUOVERE UN GIOCO //
    public Game removeGame(String bundleId) {
        return games.remove(bundleId);
    }


//---------------------------------------------------------------------------------------------------------------------------------

    //METODO PER OTTENERE UNA CONFIGURAZIONE (DI UN DETERMINATO GIOCO, CON UN DETERMINATO NOME) DAL MainModel
    public @Nullable Configuration getConfiguration(String bundleId, String confName) {
        Game game = getGameByBundleId(bundleId);
        if (game == null) return null;

        for (Configuration conf : game.getConfigurations()) {
            if (conf.getConfName().equals(confName)) {
                return conf;
            }
        }
        return null;
    }

    //METODO PER OTTENERE LE CONFIGURAZIONI DI UN DETERMINATO GIOCO
    public List<Configuration> getConfigurationsFromGame(String bundleId) {
        Game game = getGameByBundleId(bundleId);

        if (game == null)
            return new ArrayList<>();
        else
            return game.getConfigurations();
    }

    //METODO PER OTTENERE LA CONFIGURAZIONE ATTIVA PER UN DETERMINATO GIOCO
    public @Nullable Configuration getActiveConfigurationFromGame(String bundleId) {
        Game game = getGameByBundleId(bundleId);
        if (game == null)
            return null;
        else return game.getSelectedConfiguration();
    }

//---------------------------------------------------------------------------------------------------------------------------------

    /**
     * METODI PER SCRIVERE I DATI DEL MAINMODEL ALL'INTERNO DEI FILE JSON CORRETTI
     */

    public void initFolders(@NonNull Context context) {
        File dir = context.getExternalFilesDir("PlayAccess");
        assert dir != null;
        File[] contents = dir.listFiles();

        if (contents == null) {
            Log.d(TAG, "initFolders: NOT REALLY A DIRECTORY");
        } else {
            initActions();
            //initGames();

            Log.d(TAG, "initFolders loaded actions: " + getActions().toString());
        }
    }

    public void writeActionsJson() {
        List<Action> toBeWritten = new ArrayList<>(actions.values());
        toBeWritten.remove(FACE_MOVEMENT_ACTION);
        JsonManager.writeActions(toBeWritten);
    }

    /**
     * This method receives an action and verifies if it is suitable
     * to be inserted in the list of actions.
     *
     * @param actionToAdd the action that we want to add
     * @return true if the action was added
     */
    public boolean addAction(Action actionToAdd) {
        if (
                actions.containsKey(actionToAdd.getActionId()) ||
                        !isValidActionName(actionToAdd.getName())
        ) return false;

        if (actionToAdd instanceof ButtonAction) {
            ButtonAction buttonActionToAdd = (ButtonAction) actionToAdd;

            if (
                    hasButtonAction(buttonActionToAdd) ||
                            buttonActionToAdd.getSourceId() == null ||
                            buttonActionToAdd.getKeyId() == null
            )
                return false;

            if (
                    tempButtonAction.getValue() != null &&
                            buttonActionToAdd.getActionId() == tempButtonAction.getValue().getActionId()
            )
                tempButtonAction.setValue(null);

        }

        actions.put(actionToAdd.getActionId(), actionToAdd);
        notifyActionsChanged(null);

        return true;
    }

    public boolean hasButtonAction(@NotNull ButtonAction action) {
        if (actions.containsKey(action.getActionId()))
            return true;

        for (ButtonAction b : getButtonActions()) {
            if (
                    b.getName().equals(action.getName()) || (
                            b.getSourceId().equals(action.getSourceId()) &&
                                    b.getKeyId().equals(action.getKeyId())
                    )
            )
                return true;
        }

        return false;
    }

    public AssociationsDb getAssociationsDb() {
        return associationsDb;
    }

    public MutableLiveData<String> getActivePackage() {
        return activePackage;
    }

    public void setPrecision(float precision) {
        persistenceManager.setValue("precision", precision);
        for (ActionsChangedObserver observer : observers) {
            observer.onPrecisionChanged(precision);
        }
    }

    public float getPrecision() {
        return (float) persistenceManager.getValue("precision", Prototypical.DEFAULT_RADIUS);
    }
}


