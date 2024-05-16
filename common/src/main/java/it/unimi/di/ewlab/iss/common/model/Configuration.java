package it.unimi.di.ewlab.iss.common.model;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction;
import it.unimi.di.ewlab.iss.common.model.actions.ScreenGestureAction;
import it.unimi.di.ewlab.iss.common.model.actions.VocalAction;
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Prototypical;
import it.unimi.di.ewlab.iss.common.utils.Utils;

public class Configuration implements Serializable {

    private static final String TAG = "Configuration";

    private String confName = "";
    private int confId = 0;
    private String screenImage;
    private List<Link> links;

    private final Settings settings = new Settings();

    public Configuration(int confId, String confName, List<Link> links, String screenImage) {
        this.confId = confId;
        this.confName = confName.trim();
        this.screenImage = screenImage;
        this.links = links;
    }

    public Configuration(int confId, String confName, String screenImage) {
        this(confId, confName, new ArrayList<>(), screenImage);
    }

    public Configuration(int confId, String confName, int numEvent) {
        this(confId, confName, null);
        this.numEvent = numEvent;
    }

    public Bitmap getScreenImageBitmap() {
        return Utils.INSTANCE.decodeBase64ToBitmap(screenImage);
    }

    public String getScreenImage() {
        return screenImage;
    }

    public void setScreenImage(String screenImage) {
        edited = true;
        this.screenImage = screenImage;
    }

    public int getConfId() {
        return confId;
    }

    public String getConfName() {
        return confName;
    }

    public void setConfName(String name) {
        confName = name;
    }

    public boolean isLandscapeMode() {
        Bitmap bitmap = getScreenImageBitmap();
        if (bitmap == null)
            return false;
        return bitmap.getWidth() > bitmap.getHeight();
    }

    public boolean isPortraitMode() {
        return !isLandscapeMode();
    }

    public List<Link> getLinks() {
        return links.stream().sorted(Comparator.comparing(l -> l.getEvent().getName())).collect(Collectors.toList());
    }

    public void setLink(int pos, Link newLink) {
        links.set(pos, newLink);
    }

    public boolean isFullyDefined() {
        if (links.isEmpty())
            return false;
        for (Link link : links) {
            if (!link.isFullyDefined())
                return false;
        }
        return true;
    }

    public Link getLink(String event) {

        Link link = new Link();

        for (Link l : links) {

            if (l.getEvent().getName().equals(event))
                link = l;

        }

        return link;
    }


    public Link getLinkFromAction(String action) {

        for(Link l : links) {

            if(l.getAction() != null && l.getAction().getName().equals(action))
                return l;

        }

        return null;
    }


    public ArrayList<Action> getActions() {

        ArrayList<Action> actions = new ArrayList<>();
        for(Link l : links) {
            if(l.getAction() != null) {
                actions.add(l.getAction());
                if (l.getDxAction() != null)
                    actions.add(l.getDxAction());
                if (l.getSxAction() != null)
                    actions.add(l.getSxAction());
            }
        }


        return actions;
    }

    public ArrayList<ButtonAction> getButtonActions() {
        ArrayList<ButtonAction> result = new ArrayList<>();

        for(Action action : this.getActions()){
            if(action instanceof ButtonAction){
                result.add((ButtonAction) action);
            }
        }

        return result;
    }

    public ArrayList<ScreenGestureAction> getScreenGestureActions(){
        ArrayList<ScreenGestureAction> result =  new ArrayList<>();

        for (Action action : this.getActions()){
            if (action instanceof ScreenGestureAction){
                result.add((ScreenGestureAction) action);
            }
        }

        return result;
    }

    public List<VocalAction> getVocalActions(){

        ArrayList<VocalAction> result = new ArrayList<>();

        for(Action action : this.getActions()){
            if(action instanceof VocalAction){
                result.add((VocalAction) action);
            }
        }

        return result;
    }

    public List<FacialExpressionAction> getFacialExpressionActions() {
        List<FacialExpressionAction> result = new ArrayList<>();
        for (Action action : this.getActions()) {
            if (action instanceof FacialExpressionAction) {
                result.add((FacialExpressionAction) action);
            }
        }
        return result;
    }

    public Settings getSettings() {
        return settings;
    }

    /**
     * This method checks if a new link can be added in this configuration and, if the conditions are positive, adds it.
     *
     * @param newLink the link we want to add in the configuration
     * @return true if the link is added
     */
    public boolean addLink(Link newLink) {
        if (links == null || links.size() == 0) {
            Log.d(TAG, "saveLink: Lista link vuota, aggiungo direttamente link");
            links = new ArrayList<>();
            return links.add(newLink);
        }

        //check that in the configuration there are no overlapping uses concerning the action
        for(Link l : links){
            if(l.getAction()!=null && newLink.getAction()!=null && l.getAction().equals(newLink.getAction())){
                return false;
            }
        }

        return links.add(newLink);
    }

    public boolean removeLink(Link linkToRemove){
        edited = true;
        return links.remove(linkToRemove);
    }

    private boolean isSvmModelNeeded(){
        for(Action action : this.getActions()){
            if(action instanceof VocalAction){
                return true;
            }
        }

        return false;
    }


    /**
     * This method returns the number of defined links, a link is considered defined if it has an event action pair
     * or, in the case of Long tap - On / Off links, if it also has an action Stop
     * @return int
     */

    public int definedLinks() {

        int i = 0;

        for(Link l : links) {

            if(l.isFullyDefined())
                i++;

        }

        return i;
    }

    public int undefinedLinks() {
        int i = 0;

        for(Link l : links) {

            if(!l.isFullyDefined())
                i++;

        }

        return i;
    }

    public List<Event> getAllEvents() {
        List<Event> eventsToAdd = new ArrayList<>();
        for (Link link : getLinks()) {
            eventsToAdd.add(link.getEvent());
        }
        return eventsToAdd;
    }


    //--------------------------------NEW VARIABLES AND METHODS-------------------------------------

    private boolean edited;
    private DeviceInfo deviceInfo = DeviceInfo.Companion.getLocal();
    private int numEvent;

    public boolean isEdited() {
        return edited;
    }

    public void setEdited() {
        this.edited = true;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public int getNumEvent(){
        return numEvent;
    }

    public Configuration(JSONObject jsonObject) throws JSONException {
        this.confId = jsonObject.getInt("confId");

        this.edited = false;
        JSONObject deviceInfoJson = jsonObject.getJSONObject("deviceInfo");
        this.deviceInfo = new DeviceInfo(
                deviceInfoJson.getInt("screenHeightDp"),
                deviceInfoJson.getInt("screenWidthDp"),
                deviceInfoJson.getInt("densityDpi"),
                deviceInfoJson.getInt("heightPixels"),
                deviceInfoJson.getInt("widthPixels")
        );

        this.confName = jsonObject.getString("confName");
        this.screenImage = jsonObject.getString("screenImage");
        this.links = new ArrayList<>();
        JSONArray linkArray = jsonObject.getJSONArray("links");
        for (int i = 0; i < linkArray.length(); i++) {
            JSONObject linkJson = linkArray.getJSONObject(i);
            JSONObject eventJson = linkArray.getJSONObject(i).getJSONObject("eventObject");
            EventType eventType = null;
            for(EventType et : EventType.values()){
                if(et.toString().compareTo(eventJson.getString("type"))==0){
                    eventType = et;
                }
            }
            Link link = new Link(new Event(eventJson.getString("name"),
                    eventType,
                    eventJson.getDouble("x"),
                    eventJson.getDouble("y"),
                    eventJson.getBoolean("portrait")),
                    linkJson.getInt("markerColor"),
                    linkJson.getInt("markerSize"));
            this.links.add(link);
        }
    }

    public Configuration(JSONObject jsonObject, int confId){
        try {
            this.confId = confId;

            this.edited = false;

            JSONObject configuration = jsonObject.getJSONObject("configuration");

            try{
                this.deviceInfo = new DeviceInfo(
                        configuration.getInt("screenheightdp"),
                        configuration.getInt("screenwidthdp"),
                        configuration.getInt("densitydpi"),
                        configuration.getInt("heightpixel"),
                        configuration.getInt("widthpixel")
                );
            }catch (Exception e){
                this.deviceInfo = DeviceInfo.Companion.getLocal();
            }

            this.confName = configuration.getString("name");
            this.screenImage = configuration.getString("screenshot");
            JSONArray pointArray = jsonObject.getJSONArray("point");
            this.links = new ArrayList<>();
            for (int i = 0; i < pointArray.length(); i++) {
                JSONObject pointJson = pointArray.getJSONObject(i);
                EventType eventType = null;
                for(EventType et : EventType.values()){
                    if(et.toString().compareTo(pointJson.getString("event"))==0){
                        eventType = et;
                    }
                }
                Link link = new Link(new Event(pointJson.getString("name"),
                        eventType,
                        pointJson.getDouble("cordx"),
                        pointJson.getDouble("cordy"),
                        true),
                        pointJson.getInt("color"),
                        pointJson.getInt("size"));
                this.links.add(link);
            }
            this.numEvent = pointArray.length();
        } catch (JSONException e) {
            Log.e(TAG, "Errore di deserializzazione da json: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void setPortraitToEvents(boolean portrait){
        for (Link link : links) {
            if (link.getEvent() != null)
                link.getEvent().setPortrait(portrait);
        }
    }


    public static class Settings implements Serializable {

        public enum FacialExpressionPrecision {
            LOW (1),
            MEDIUM (3),
            HIGH (5);

            public final int bufferSize;

            FacialExpressionPrecision(int bufferSize) {
                this.bufferSize = bufferSize;
            }
        }

        public static final boolean SHOW_FEEDBACK_SCREEN_DEFAULT = true;
        public static final boolean SHOW_RECOGNIZED_FACIAL_EXPRESSIONS_DEFAULT = true;
        public static final boolean SHOW_EVENTS_ON_SCREEN_DEFAULT = true;
        public static final boolean SHOW_EXPRESSION_LANDMARKS_DEFAULT = false;
        public static final FacialExpressionPrecision FE_PRECISION_DEFAULT = FacialExpressionPrecision.MEDIUM;
        public static final boolean ENABLE_AUDIO_DEFAULT = true;

        private boolean showFeedbackScreen = SHOW_FEEDBACK_SCREEN_DEFAULT;
        private boolean showRecognizedFacialExpressions = SHOW_RECOGNIZED_FACIAL_EXPRESSIONS_DEFAULT;
        private boolean showEventsOnScreen = SHOW_EVENTS_ON_SCREEN_DEFAULT;
        private boolean showExpressionLandmarks = SHOW_EXPRESSION_LANDMARKS_DEFAULT;
        private FacialExpressionPrecision fePrecision = FE_PRECISION_DEFAULT;
        private boolean enableAudio = ENABLE_AUDIO_DEFAULT;

        private float feClassifierRadius = Prototypical.DEFAULT_RADIUS;

        public float getFacialExpressionsClassifierRadius() {
            return feClassifierRadius;
        }

        public void setFacialExpressionsClassifierRadius(float precision) {
            if (precision <= 0)
                throw new IllegalArgumentException("The precision must be a positive number");
            this.feClassifierRadius = precision;
        }

        public boolean getShowFeedbackScreen() {
            return showFeedbackScreen;
        }

        public void setShowFeedbackScreen(boolean showFeedbackScreen) {
            this.showFeedbackScreen = showFeedbackScreen;
        }

        public boolean getShowRecognizedFacialExpressions() {
            return showRecognizedFacialExpressions;
        }

        public void setShowRecognizedFacialExpressions(boolean showRecognizedFacialExpressions) {
            this.showRecognizedFacialExpressions = showRecognizedFacialExpressions;
        }

        public boolean getShowEventsOnScreen() {
            return showEventsOnScreen;
        }

        public void setShowEventsOnScreen(boolean showEventsOnScreen) {
            this.showEventsOnScreen = showEventsOnScreen;
        }

        public boolean getShowExpressionLandmarks() {
            return showExpressionLandmarks;
        }

        public void setShowExpressionLandmarks(boolean showExpressionLandmarks) {
            this.showExpressionLandmarks = showExpressionLandmarks;
        }

        public FacialExpressionPrecision getFacialExpressionsPrecision() {
            return fePrecision;
        }

        public void setFacialExpressionsPrecision(FacialExpressionPrecision fePrecision) {
            this.fePrecision = fePrecision;
        }

        public boolean getEnableAudio() {
            return enableAudio;
        }

        public void setEnableAudio(boolean audioOn) {
            this.enableAudio = audioOn;
        }
    }
}
