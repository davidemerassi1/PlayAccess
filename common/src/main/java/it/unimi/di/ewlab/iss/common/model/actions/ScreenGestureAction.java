package it.unimi.di.ewlab.iss.common.model.actions;

public class ScreenGestureAction extends Action {

    private static final ActionType TYPE = ActionType.SCREEN_GESTURE;

    private GestureId s_g_a_BtnId;

    public ScreenGestureAction(int actionId, String name, GestureId s_g_a_BtnId) {
        super(actionId, name, TYPE);
        this.s_g_a_BtnId = s_g_a_BtnId;
    }

    ScreenGestureAction() {
        super(TYPE);
    }

    public GestureId getS_g_a_BtnId() {
        return s_g_a_BtnId;
    }

    public Layout getLayout() {
        return s_g_a_BtnId.layout;
    }

    public enum Layout {
        HORIZ_SCREEN,
        FULL_SCREEN,
        VERT_SCREEN,
        X_SCREEN,
        SWIPE_SCREEN,
    }

    public enum GestureId {
        HR_UP (Layout.HORIZ_SCREEN),
        HR_DOWN (Layout.HORIZ_SCREEN),
        VR_LEFT (Layout.VERT_SCREEN),
        VR_RIGHT (Layout.VERT_SCREEN),
        FULL_SCREEN (Layout.FULL_SCREEN),
        X_UP (Layout.X_SCREEN),
        X_DOWN (Layout.X_SCREEN),
        X_LEFT (Layout.X_SCREEN),
        X_RIGHT (Layout.X_SCREEN),
        SWIPE_UP (Layout.SWIPE_SCREEN),
        SWIPE_DOWN (Layout.SWIPE_SCREEN),
        SWIPE_LEFT (Layout.SWIPE_SCREEN),
        SWIPE_RIGHT (Layout.SWIPE_SCREEN);


        public final Layout layout;
        GestureId(Layout layout) {
            this.layout = layout;
        }
    }
}
