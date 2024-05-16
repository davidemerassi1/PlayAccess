/*
JSONMANAGER: allows to manage the json files about player, games anc links.
 */
package it.unimi.di.ewlab.iss.common.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unimi.di.ewlab.iss.common.utils.Utils;
import it.unimi.di.ewlab.iss.common.model.Game;
import it.unimi.di.ewlab.iss.common.model.actions.Action;
import kotlin.Pair;

public class JsonManager {

    private static final String TAG = JsonManager.class.getName();
    private static String JSON_PATH = "";
    private static final String FILE_GAMES = "games.json";
    private static final String FILE_ACTIONS = "actions.json";
    private static final String DISTANCES_PAIRS_FILE = "distances_pairs.json";
    private static Gson typedSerializer;
    private static Gson serializer;

    public JsonManager(Context context) {

        RuntimeTypeAdapterFactory<Action> adapter = RuntimeTypeAdapterFactory.of(Action.class, "actionType");

        for (Action.ActionType type : Action.ActionType.values())
            adapter.registerSubtype(type.actionClass, type.name());

        typedSerializer = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Bitmap.class, new BitmapTypeAdapter()).registerTypeAdapterFactory(adapter).create();
        serializer = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Bitmap.class, new BitmapTypeAdapter()).create();

        //Log.e(TAG, "CREO JSON MANAGER _ Context:" + context.toString());
        if(context != null)
            JSON_PATH = context.getExternalFilesDir("PlayAccess").getPath();
    }


    /** METODI PER LEGGERE E SCRIVERE AZIONI DA E PER FILE actions.json */

    //METODO PER OTTENERE LE AZIONI PRESENTI NEL FILE actions.json
    public List<Action> getActionsFromJson() {

        List<Action> actions = new ArrayList<>();
        String json = this.readMemoryJsonFile(FILE_ACTIONS);
        if(json != null && !json.equals("")) {
            Type listType = new TypeToken<ArrayList<Action>>() {
            }.getType();
            actions = typedSerializer.fromJson(json, listType);
        }
        return actions;
    }

    public static void writeActions(Collection<Action> values) {

        String json = typedSerializer.toJson(values);
        JsonManager.writeJson("actions", json);

    }

    //----------------------------------------------------------------------------------------------
    /** METODI PER LEGGERE E SCRIVERE GIOCHI DA E PER FILE games.json */

    public List<Game> getGamesFromJson() {

        ArrayList<Game> games = new ArrayList<>();
        String json = this.readMemoryJsonFile(FILE_GAMES);

        if(json != null && !json.equals("")) {
            Type listType = new TypeToken<ArrayList<Game>>() {
            }.getType();
            games = typedSerializer.fromJson(json, listType);

        }
        return games;

    }

    public static void writeGames(Collection<Game> values) {

        String json = serializer.toJson(values);
        JsonManager.writeJson("games", json);

    }

    public List<Pair<Integer, Integer>> getFacialFeaturesDistancesFromJson(@NonNull Context context) {
        String json = readAssetsJsonFile(context, DISTANCES_PAIRS_FILE);
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();

        if (json != null && !json.equals("")) {
            Type pairListType = new TypeToken<List<Pair<Integer, Integer>>>() {}.getType();
            pairs.addAll(serializer.fromJson(json, pairListType));
        }

        return pairs;
    }

    //______________________________________________________________________________________________________________________________
    /** METODI GENERALI PER GESTIRE I JSON*/


    private String readAssetsJsonFile(@NonNull Context context, String jsonFile) {
        try {
            return readInputStream(context.getAssets().open(jsonFile));
        } catch (IOException e) {
            Log.e(TAG, "Unable to read file " + jsonFile + ": " + e.getMessage());
        }

        return "";
    }

    private String readMemoryJsonFile(String jsonFile) {
        File file = new File(JSON_PATH, jsonFile);
        try {
            return readInputStream(new FileInputStream(file));
        } catch (IOException e) {
            Log.e(TAG, "Unable to read file " + jsonFile + ": " + e.getMessage());
        }

        return "";
    }

    private String readInputStream(InputStream inputStream) {
        String content = "";

        try {
            StringBuilder stringBuilder = new StringBuilder();

            if (inputStream != null) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                //Use a while loop to append the lines from the Buffered reader
                while ((receiveString = bufferedReader.readLine()) != null){
                    stringBuilder.append(receiveString);
                }
                //Close your InputStream and save stringBuilder as a String
                inputStream.close();
                content = stringBuilder.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    //METODO CHE RICEVE COME PARAMETRO IL JSONOBJECT DA SALVARE E IL FILE IN CUI SALVARE IL JSONOBJECT
    private static void writeJson(String fileName, String json) {

        String file = fileName+".json";

        File jsonFile = new File(JSON_PATH, file);

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
            bw.write(json);
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class BitmapTypeAdapter extends TypeAdapter<Bitmap> {

        @Override
        public void write(JsonWriter out, Bitmap value) throws IOException {
            String base64 = Utils.INSTANCE.encodeToBase64(value);
            out.value(base64);
        }

        @Override
        public Bitmap read(JsonReader in) throws IOException {
            return Utils.INSTANCE.decodeBase64ToBitmap(in.nextString());
        }
    }
}
