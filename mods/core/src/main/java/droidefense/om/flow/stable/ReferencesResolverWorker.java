package droidefense.om.flow.stable;

import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IDroidefenseField;
import droidefense.om.machine.base.struct.generic.IDroidefenseFrame;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.om.machine.base.struct.model.AndroidRField;
import droidefense.sdk.helpers.Util;
import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
import droidefense.vfs.model.impl.VirtualFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final strictfp class ReferencesResolverWorker extends AbstractDVMThread {

    private static final String ANDROID_R_ID_REGEX = "@android\\:(0x){0,1}([0-9a-zA-Z]{8})";
    private static final String ANDROID_ID_REGEX = "android\\:([a-zA-Z0-9]+(_[a-zA-Z0-9]+)*)=\"(\\w|\\s|\\d|[-,\\.\\$])*\"";
    private transient ArrayList<AndroidRField> references;

    public ReferencesResolverWorker(DroidefenseProject currentProject) {
        super(currentProject);
        references = new ArrayList<>();
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: ReferencesResolverWorker");
        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
    }

    @Override
    public void run() {
        try {
            execute(false);
        } catch (Throwable throwable) {
            Log.write(LoggerType.ERROR, throwable.getLocalizedMessage());
        }
    }

    @Override
    public void finish() {
        Log.write(LoggerType.DEBUG, "Android R references resolved!");
        Log.write(LoggerType.DEBUG, "Number of references resolved: "+this.references.size());
        Log.write(LoggerType.DEBUG, "Remapping old files...");
        this.remap();
    }

    private void remap() {
        this.currentProject.setAndroidReferences(this.references);
        ArrayList<VirtualFile> xmls = this.currentProject.getXmlFiles();
        for (VirtualFile vf : xmls) {
            Log.write(LoggerType.DEBUG, "Updating content of "+vf.getPath());
            singleRemap(vf);
        }
        //todo reparse AndroidManifest.xml
        Log.write(LoggerType.DEBUG, "Remapping [OK]");
    }

    private void singleRemap(VirtualFile xmlFile) {
        String data = new String(xmlFile.getContent());
        for(AndroidRField field : references) {
            String idOriginal = field.getAssembledID();
            String idReversed = field.reverseName();
            data = data.replaceAll(idOriginal, idReversed);
        }
        HashMap<String, String> keymap = new HashMap<>();

        //https://developer.android.com/reference/android/R.html

         //R.anim [DONE]
        keymap.put("@android:010a0004", "@anim/accelerate_interpolator");
        keymap.put("@android:010a0005", "@anim/anticipate_interpolator");
        keymap.put("@android:010a0007", "@anim/anticipate_overshoot_interpolator");
        keymap.put("@android:010a0009", "@anim/bounce_interpolator");
        keymap.put("@android:010a000a", "@anim/cycle_interpolator");
        keymap.put("@android:010a000c", "@anim/decelerate_interpolator");
        keymap.put("@android:010a0006", "@anim/fade_in");
        keymap.put("@android:010a0000", "@anim/fade_out");
        keymap.put("@android:010a0001", "@anim/linear_interpolator");
        keymap.put("@android:010a000b", "@anim/overshoot_interpolator");
        keymap.put("@android:010a0008", "@anim/slide_in_left");
        keymap.put("@android:010a0002", "@anim/slide_out_right");

        //R.animator [DONE]
        keymap.put("@android:010b0000", "@animator/fade_in");
        keymap.put("@android:010b0001", "@animator/fade_out");

        //R.array [DONE]
        keymap.put("@android:01070000", "@array/emailAddressTypes");
        keymap.put("@android:01070001", "@array/imProtocols");
        keymap.put("@android:01070002", "@array/organizationTypes");
        keymap.put("@android:01070003", "@array/phoneTypes");
        keymap.put("@android:01070004", "@array/postalAddressTypes");

        //R.attr

        //R.bool [DONE]

        //R.color [DONE]
        keymap.put("@android:0106000e", "@color/background_dark");
        keymap.put("@android:0106000f", "@color/background_light");
        keymap.put("@android:0106000c", "@color/black");
        keymap.put("@android:01060000", "@color/darker_gray");
        keymap.put("@android:0106001b", "@color/holo_blue_bright");
        keymap.put("@android:01060013", "@color/holo_blue_dark");
        keymap.put("@android:01060012", "@color/holo_blue_light");
        keymap.put("@android:01060015", "@color/holo_green_dark");
        keymap.put("@android:01060014", "@color/holo_green_light");
        keymap.put("@android:01060019", "@color/holo_orange_dark");
        keymap.put("@android:01060018", "@color/holo_orange_light");
        keymap.put("@android:0106001a", "@color/holo_purple");
        keymap.put("@android:01060017", "@color/holo_red_dark");
        keymap.put("@android:01060016", "@color/holo_red_light");
        keymap.put("@android:01060001", "@color/primary_text_dark");
        keymap.put("@android:01060002", "@color/primary_text_dark_nodisable");
        keymap.put("@android:01060003", "@color/primary_text_light");
        keymap.put("@android:01060004", "@color/primary_text_light_nodisable");
        keymap.put("@android:01060005", "@color/secondary_text_dark");
        keymap.put("@android:01060006", "@color/secondary_text_dark_nodisable");
        keymap.put("@android:01060007", "@color/secondary_text_light");
        keymap.put("@android:01060008", "@color/secondary_text_light_nodisable");
        keymap.put("@android:01060009", "@color/tab_indicator_text");
        keymap.put("@android:01060010", "@color/tertiary_text_dark");
        keymap.put("@android:01060011", "@color/tertiary_text_light");
        keymap.put("@android:0106000d", "@color/transparent");
        keymap.put("@android:0106000b", "@color/white");
        keymap.put("@android:0106000a", "@color/widget_edittext_dark");

        //R.dimen [DONE]
        keymap.put("@android:01050000", "@dimen/app_icon_size");
        keymap.put("@android:01050003", "@dimen/dialog_min_width_major");
        keymap.put("@android:01050004", "@dimen/dialog_min_width_minor");
        keymap.put("@android:01050006", "@dimen/notification_large_icon_height");
        keymap.put("@android:01050005", "@dimen/notification_large_icon_width");
        keymap.put("@android:01050001", "@dimen/thumbnail_height");
        keymap.put("@android:01050002", "@dimen/thumbnail_width");

        //R.drawable [DONE]
        keymap.put("@android:01080000", "@drawable/alert_dark_frame");
        keymap.put("@android:01080001", "@drawable/alert_light_frame");
        keymap.put("@android:01080002", "@drawable/arrow_down_float");
        keymap.put("@android:01080003", "@drawable/arrow_up_float");
        keymap.put("@android:0108009a", "@drawable/bottom_bar");
        keymap.put("@android:01080004", "@drawable/btn_default");
        keymap.put("@android:01080005", "@drawable/btn_default_small");
        keymap.put("@android:01080017", "@drawable/btn_dialog");
        keymap.put("@android:01080006", "@drawable/btn_dropdown");
        keymap.put("@android:01080007", "@drawable/btn_minus");
        keymap.put("@android:01080008", "@drawable/btn_plus");
        keymap.put("@android:01080009", "@drawable/btn_radio");
        keymap.put("@android:0108000a", "@drawable/btn_star");
        keymap.put("@android:0108000b", "@drawable/btn_star_big_off");
        keymap.put("@android:0108000c", "@drawable/btn_star_big_on");
        keymap.put("@android:0108000e", "@drawable/button_onoff_indicator_off");
        keymap.put("@android:0108000d", "@drawable/button_onoff_indicator_on");
        keymap.put("@android:0108000f", "@drawable/checkbox_off_background");
        keymap.put("@android:01080010", "@drawable/checkbox_on_background");
        keymap.put("@android:010800a5", "@drawable/dark_header");
        keymap.put("@android:01080011", "@drawable/dialog_frame");
        keymap.put("@android:010800b2", "@drawable/dialog_holo_dark_frame");
        keymap.put("@android:010800b3", "@drawable/dialog_holo_light_frame");
        keymap.put("@android:01080012", "@drawable/divider_horizontal_bright");
        keymap.put("@android:01080014", "@drawable/divider_horizontal_dark");
        keymap.put("@android:01080015", "@drawable/divider_horizontal_dim_dark");
        keymap.put("@android:01080013", "@drawable/divider_horizontal_textfield");
        keymap.put("@android:01080016", "@drawable/edit_text");
        keymap.put("@android:01080018", "@drawable/editbox_background");
        keymap.put("@android:01080019", "@drawable/editbox_background_normal");
        keymap.put("@android:0108001a", "@drawable/editbox_dropdown_dark_frame");
        keymap.put("@android:0108001b", "@drawable/editbox_dropdown_light_frame");
        keymap.put("@android:0108001c", "@drawable/gallery_thumb");
        keymap.put("@android:010800a4", "@drawable/ic_btn_speak_now");
        keymap.put("@android:0108001d", "@drawable/ic_delete");
        keymap.put("@android:01080027", "@drawable/ic_dialog_alert");
        keymap.put("@android:01080028", "@drawable/ic_dialog_dialer");
        keymap.put("@android:01080029", "@drawable/ic_dialog_email");
        keymap.put("@android:0108009b", "@drawable/ic_dialog_info");
        keymap.put("@android:0108002a", "@drawable/ic_dialog_map");
        keymap.put("@android:0108002b", "@drawable/ic_input_add");
        keymap.put("@android:0108002c", "@drawable/ic_input_delete");
        keymap.put("@android:0108002d", "@drawable/ic_input_get");
        keymap.put("@android:0108002e", "@drawable/ic_lock_idle_alarm");
        keymap.put("@android:0108001e", "@drawable/ic_lock_idle_charging");
        keymap.put("@android:0108001f", "@drawable/ic_lock_idle_lock");
        keymap.put("@android:01080020", "@drawable/ic_lock_idle_low_battery");
        keymap.put("@android:0108002f", "@drawable/ic_lock_lock");
        keymap.put("@android:01080030", "@drawable/ic_lock_power_off");
        keymap.put("@android:01080031", "@drawable/ic_lock_silent_mode");
        keymap.put("@android:01080032", "@drawable/ic_lock_silent_mode_off");
        keymap.put("@android:01080021", "@drawable/ic_media_ff");
        keymap.put("@android:01080022", "@drawable/ic_media_next");
        keymap.put("@android:01080023", "@drawable/ic_media_pause");
        keymap.put("@android:01080024", "@drawable/ic_media_play");
        keymap.put("@android:01080025", "@drawable/ic_media_previous");
        keymap.put("@android:01080026", "@drawable/ic_media_rew");
        keymap.put("@android:01080033", "@drawable/ic_menu_add");
        keymap.put("@android:01080034", "@drawable/ic_menu_agenda");
        keymap.put("@android:01080035", "@drawable/ic_menu_always_landscape_portrait");
        keymap.put("@android:01080036", "@drawable/ic_menu_call");
        keymap.put("@android:01080037", "@drawable/ic_menu_camera");
        keymap.put("@android:01080038", "@drawable/ic_menu_close_clear_cancel");
        keymap.put("@android:01080039", "@drawable/ic_menu_compass");
        keymap.put("@android:0108003a", "@drawable/ic_menu_crop");
        keymap.put("@android:0108003b", "@drawable/ic_menu_day");
        keymap.put("@android:0108003c", "@drawable/ic_menu_delete");
        keymap.put("@android:0108003d", "@drawable/ic_menu_directions");
        keymap.put("@android:0108003e", "@drawable/ic_menu_edit");
        keymap.put("@android:0108003f", "@drawable/ic_menu_gallery");
        keymap.put("@android:01080040", "@drawable/ic_menu_help");
        keymap.put("@android:01080041", "@drawable/ic_menu_info_details");
        keymap.put("@android:01080042", "@drawable/ic_menu_manage");
        keymap.put("@android:01080043", "@drawable/ic_menu_mapmode");
        keymap.put("@android:01080044", "@drawable/ic_menu_month");
        keymap.put("@android:01080045", "@drawable/ic_menu_more");
        keymap.put("@android:01080046", "@drawable/ic_menu_my_calendar");
        keymap.put("@android:01080047", "@drawable/ic_menu_mylocation");
        keymap.put("@android:01080048", "@drawable/ic_menu_myplaces");
        keymap.put("@android:01080049", "@drawable/ic_menu_preferences");
        keymap.put("@android:0108004a", "@drawable/ic_menu_recent_history");
        keymap.put("@android:0108004b", "@drawable/ic_menu_report_image");
        keymap.put("@android:0108004c", "@drawable/ic_menu_revert");
        keymap.put("@android:0108004d", "@drawable/ic_menu_rotate");
        keymap.put("@android:0108004e", "@drawable/ic_menu_save");
        keymap.put("@android:0108004f", "@drawable/ic_menu_search");
        keymap.put("@android:01080050", "@drawable/ic_menu_send");
        keymap.put("@android:01080051", "@drawable/ic_menu_set_as");
        keymap.put("@android:01080052", "@drawable/ic_menu_share");
        keymap.put("@android:01080053", "@drawable/ic_menu_slideshow");
        keymap.put("@android:0108009c", "@drawable/ic_menu_sort_alphabetically");
        keymap.put("@android:0108009d", "@drawable/ic_menu_sort_by_size");
        keymap.put("@android:01080054", "@drawable/ic_menu_today");
        keymap.put("@android:01080055", "@drawable/ic_menu_upload");
        keymap.put("@android:01080056", "@drawable/ic_menu_upload_you_tube");
        keymap.put("@android:01080057", "@drawable/ic_menu_view");
        keymap.put("@android:01080058", "@drawable/ic_menu_week");
        keymap.put("@android:01080059", "@drawable/ic_menu_zoom");
        keymap.put("@android:0108005a", "@drawable/ic_notification_clear_all");
        keymap.put("@android:0108005b", "@drawable/ic_notification_overlay");
        keymap.put("@android:0108005c", "@drawable/ic_partial_secure");
        keymap.put("@android:0108005d", "@drawable/ic_popup_disk_full");
        keymap.put("@android:0108005e", "@drawable/ic_popup_reminder");
        keymap.put("@android:0108005f", "@drawable/ic_popup_sync");
        keymap.put("@android:01080060", "@drawable/ic_search_category_default");
        keymap.put("@android:01080061", "@drawable/ic_secure");
        keymap.put("@android:01080062", "@drawable/list_selector_background");
        keymap.put("@android:01080063", "@drawable/menu_frame");
        keymap.put("@android:01080064", "@drawable/menu_full_frame");
        keymap.put("@android:01080065", "@drawable/menuitem_background");
        keymap.put("@android:01080066", "@drawable/picture_frame");
        keymap.put("@android:010800af", "@drawable/presence_audio_away");
        keymap.put("@android:010800b0", "@drawable/presence_audio_busy");
        keymap.put("@android:010800b1", "@drawable/presence_audio_online");
        keymap.put("@android:01080067", "@drawable/presence_away");
        keymap.put("@android:01080068", "@drawable/presence_busy");
        keymap.put("@android:01080069", "@drawable/presence_invisible");
        keymap.put("@android:0108006a", "@drawable/presence_offline");
        keymap.put("@android:0108006b", "@drawable/presence_online");
        keymap.put("@android:010800ac", "@drawable/presence_video_away");
        keymap.put("@android:010800ad", "@drawable/presence_video_busy");
        keymap.put("@android:010800ae", "@drawable/presence_video_online");
        keymap.put("@android:0108006c", "@drawable/progress_horizontal");
        keymap.put("@android:0108006d", "@drawable/progress_indeterminate_horizontal");
        keymap.put("@android:0108006e", "@drawable/radiobutton_off_background");
        keymap.put("@android:0108006f", "@drawable/radiobutton_on_background");
        keymap.put("@android:01080098", "@drawable/screen_background_dark");
        keymap.put("@android:010800a9", "@drawable/screen_background_dark_transparent");
        keymap.put("@android:01080099", "@drawable/screen_background_light");
        keymap.put("@android:010800aa", "@drawable/screen_background_light_transparent");
        keymap.put("@android:01080070", "@drawable/spinner_background");
        keymap.put("@android:01080071", "@drawable/spinner_dropdown_background");
        keymap.put("@android:01080073", "@drawable/star_big_off");
        keymap.put("@android:01080072", "@drawable/star_big_on");
        keymap.put("@android:01080075", "@drawable/star_off");
        keymap.put("@android:01080074", "@drawable/star_on");
        keymap.put("@android:01080076", "@drawable/stat_notify_call_mute");
        keymap.put("@android:01080077", "@drawable/stat_notify_chat");
        keymap.put("@android:01080078", "@drawable/stat_notify_error");
        keymap.put("@android:0108007f", "@drawable/stat_notify_missed_call");
        keymap.put("@android:01080079", "@drawable/stat_notify_more");
        keymap.put("@android:0108007a", "@drawable/stat_notify_sdcard");
        keymap.put("@android:010800ab", "@drawable/stat_notify_sdcard_prepare");
        keymap.put("@android:0108007b", "@drawable/stat_notify_sdcard_usb");
        keymap.put("@android:0108007c", "@drawable/stat_notify_sync");
        keymap.put("@android:0108007d", "@drawable/stat_notify_sync_noanim");
        keymap.put("@android:0108007e", "@drawable/stat_notify_voicemail");
        keymap.put("@android:01080080", "@drawable/stat_sys_data_bluetooth");
        keymap.put("@android:01080081", "@drawable/stat_sys_download");
        keymap.put("@android:01080082", "@drawable/stat_sys_download_done");
        keymap.put("@android:01080083", "@drawable/stat_sys_headset");
        keymap.put("@android:01080083", "@drawable/stat_sys_phone_call");
        keymap.put("@android:01080084", "@drawable/stat_sys_phone_call_forward");
        keymap.put("@android:01080085", "@drawable/stat_sys_phone_call_on_hold");
        keymap.put("@android:01080086", "@drawable/stat_sys_speakerphone");
        keymap.put("@android:01080088", "@drawable/stat_sys_upload");
        keymap.put("@android:01080089", "@drawable/stat_sys_upload_done");
        keymap.put("@android:010800a7", "@drawable/stat_sys_vp_phone_call");
        keymap.put("@android:010800a8", "@drawable/stat_sys_vp_phone_call_on_hold");
        keymap.put("@android:0108008a", "@drawable/stat_sys_warning");
        keymap.put("@android:0108008b", "@drawable/status_bar_item_app_background");
        keymap.put("@android:0108008c", "@drawable/status_bar_item_background");
        keymap.put("@android:0108008d", "@drawable/sym_action_call");
        keymap.put("@android:0108008e", "@drawable/sym_action_chat");
        keymap.put("@android:0108008f", "@drawable/sym_action_email");
        keymap.put("@android:01080090", "@drawable/sym_call_incoming");
        keymap.put("@android:01080091", "@drawable/sym_call_missed");
        keymap.put("@android:01080092", "@drawable/sym_call_outgoing");
        keymap.put("@android:01080094", "@drawable/sym_contact_card");
        keymap.put("@android:01080093", "@drawable/sym_def_app_icon");
        keymap.put("@android:01080095", "@drawable/title_bar");
        keymap.put("@android:010800a6", "@drawable/title_bar_tall");
        keymap.put("@android:01080096", "@drawable/toast_frame");
        keymap.put("@android:01080097", "@drawable/zoom_plate");

        //R.fraction [DONE]

        //R.id [DONE]
        keymap.put("@android:0102003c", "@id/accessibilityActionContextClick");
        keymap.put("@android:01020042", "@id/accessibilityActionMoveWindow");
        keymap.put("@android:0102003a", "@id/accessibilityActionScrollDown");
        keymap.put("@android:01020039", "@id/accessibilityActionScrollLeft");
        keymap.put("@android:0102003b", "@id/accessibilityActionScrollRight");
        keymap.put("@android:01020037", "@id/accessibilityActionScrollToPosition");
        keymap.put("@android:01020038", "@id/accessibilityActionScrollUp");
        keymap.put("@android:0102003d", "@id/accessibilityActionSetProgress");
        keymap.put("@android:01020036", "@id/accessibilityActionShowOnScreen");
        keymap.put("@android:0102002a", "@id/addToDictionary");
        keymap.put("@android:01020043", "@id/autofill");
        keymap.put("@android:01020000", "@id/background");
        keymap.put("@android:01020019", "@id/button1");
        keymap.put("@android:0102001a", "@id/button2");
        keymap.put("@android:0102001b", "@id/button3");
        keymap.put("@android:0102001d", "@id/candidatesArea");
        keymap.put("@android:01020001", "@id/checkbox");
        keymap.put("@android:01020027", "@id/closeButton");
        keymap.put("@android:01020002", "@id/content");
        keymap.put("@android:01020021", "@id/copy");
        keymap.put("@android:01020023", "@id/copyUrl");
        keymap.put("@android:0102002b", "@id/custom");
        keymap.put("@android:01020020", "@id/cut");
        keymap.put("@android:01020003", "@id/edit");
        keymap.put("@android:01020004", "@id/empty");
        keymap.put("@android:0102001c", "@id/extractArea");
        keymap.put("@android:01020005", "@id/hint");
        keymap.put("@android:0102002c", "@id/home");
        keymap.put("@android:01020006", "@id/icon");
        keymap.put("@android:01020007", "@id/icon1");
        keymap.put("@android:01020008", "@id/icon2");
        keymap.put("@android:0102003e", "@id/icon_frame");
        keymap.put("@android:01020009", "@id/input");
        keymap.put("@android:0102001e", "@id/inputArea");
        keymap.put("@android:01020025", "@id/inputExtractEditText");
        keymap.put("@android:01020026", "@id/keyboardView");
        keymap.put("@android:0102000a", "@id/list");
        keymap.put("@android:0102003f", "@id/list_container");
        keymap.put("@android:0102002e", "@id/mask");
        keymap.put("@android:0102000b", "@id/message");
        keymap.put("@android:01020030", "@id/navigationBarBackground");
        keymap.put("@android:01020022", "@id/paste");
        keymap.put("@android:01020031", "@id/pasteAsPlainText");
        keymap.put("@android:0102000c", "@id/primary");
        keymap.put("@android:0102000d", "@id/progress");
        keymap.put("@android:01020033", "@id/redo");
        keymap.put("@android:01020034", "@id/replaceText");
        keymap.put("@android:0102000f", "@id/secondaryProgress");
        keymap.put("@android:0102001f", "@id/selectAll");
        keymap.put("@android:0102002d", "@id/selectTextMode");
        keymap.put("@android:0102000e", "@id/selectedIcon");
        keymap.put("@android:01020035", "@id/shareText");
        keymap.put("@android:01020028", "@id/startSelectingText");
        keymap.put("@android:0102002f", "@id/statusBarBackground");
        keymap.put("@android:01020029", "@id/stopSelectingText");
        keymap.put("@android:01020010", "@id/summary");
        keymap.put("@android:01020024", "@id/switchInputMethod");
        keymap.put("@android:01020040", "@id/switch_widget");
        keymap.put("@android:01020011", "@id/tabcontent");
        keymap.put("@android:01020012", "@id/tabhost");
        keymap.put("@android:01020013", "@id/tabs");
        keymap.put("@android:01020014", "@id/text1");
        keymap.put("@android:01020015", "@id/text2");
        keymap.put("@android:01020041", "@id/textAssist");
        keymap.put("@android:01020016", "@id/title");
        keymap.put("@android:01020017", "@id/toggle");
        keymap.put("@android:01020032", "@id/undo");
        keymap.put("@android:01020018", "@id/widget_frame");

        //R.integer [DONE]
        keymap.put("@android:010e0002", "@integer/config_longAnimTime");
        keymap.put("@android:010e0001", "@integer/config_mediumAnimTime");
        keymap.put("@android:010e0000", "@integer/config_shortAnimTime");
        keymap.put("@android:010e0003", "@integer/status_bar_notification_info_maxnum");

        //R.interpolar [DONE]
        keymap.put("@android:010c0002", "@interpolator/accelerate_cubic");
        keymap.put("@android:010c0006", "@interpolator/accelerate_decelerate");
        keymap.put("@android:010c0000", "@interpolator/accelerate_quad");
        keymap.put("@android:010c0004", "@interpolator/accelerate_quint");
        keymap.put("@android:010c0007", "@interpolator/anticipate");
        keymap.put("@android:010c0009", "@interpolator/anticipate_overshoot");
        keymap.put("@android:010c000a", "@interpolator/bounce");
        keymap.put("@android:010c000c", "@interpolator/cycle");
        keymap.put("@android:010c0003", "@interpolator/decelerate_cubic");
        keymap.put("@android:010c0001", "@interpolator/decelerate_quad");
        keymap.put("@android:010c0005", "@interpolator/decelerate_quint");
        keymap.put("@android:010c000f", "@interpolator/fast_out_linear_in");
        keymap.put("@android:010c000d", "@interpolator/fast_out_slow_in");
        keymap.put("@android:010c000b", "@interpolator/linear");
        keymap.put("@android:010c000e", "@interpolator/linear_out_slow_in");
        keymap.put("@android:010c0008", "@interpolator/overshoot");

        //R.layout [DONE]
        keymap.put("@android:01090000", "@layout/activity_list_item");
        keymap.put("@android:0109000e", "@layout/browser_link_context_header");
        keymap.put("@android:01090001", "@layout/expandable_list_content");
        keymap.put("@android:01090014", "@layout/list_content");
        keymap.put("@android:01090002", "@layout/preference_category");
        keymap.put("@android:01090011", "@layout/select_dialog_item");
        keymap.put("@android:01090013", "@layout/select_dialog_multichoice");
        keymap.put("@android:01090012", "@layout/select_dialog_singlechoice");
        keymap.put("@android:0109000a", "@layout/simple_dropdown_item_1line");
        keymap.put("@android:01090006", "@layout/simple_expandable_list_item_1");
        keymap.put("@android:01090007", "@layout/simple_expandable_list_item_2");
        keymap.put("@android:0109000b", "@layout/simple_gallery_item");
        keymap.put("@android:01090003", "@layout/simple_list_item_1");
        keymap.put("@android:01090004", "@layout/simple_list_item_2");
        keymap.put("@android:01090016", "@layout/simple_list_item_activated_1");
        keymap.put("@android:01090017", "@layout/simple_list_item_activated_2");
        keymap.put("@android:01090005", "@layout/simple_list_item_checked");
        keymap.put("@android:01090010", "@layout/simple_list_item_multiple_choice");
        keymap.put("@android:0109000f", "@layout/simple_list_item_single_choice");
        keymap.put("@android:01090015", "@layout/simple_selectable_list_item");
        keymap.put("@android:01090009", "@layout/simple_spinner_dropdown_item");
        keymap.put("@android:01090008", "@layout/simple_spinner_item");
        keymap.put("@android:0109000c", "@layout/test_list_item");
        keymap.put("@android:0109000d", "@layout/two_line_list_item");

        //R.menu [DONE]

        //R.mipmap
        keymap.put("@android:010d0000", "@layout/sym_def_app_icon");

        //R.plurals [DONE]

        //R.raw [DONE]

        //R.string [DONE]
        keymap.put("@android:01040010", "@string/VideoView_error_button");
        keymap.put("@android:01040015", "@string/VideoView_error_text_invalid_progressive_playback");
        keymap.put("@android:01040011", "@string/VideoView_error_text_unknown");
        keymap.put("@android:01040012", "@string/VideoView_error_title");
        keymap.put("@android:01040000", "@string/cancel");
        keymap.put("@android:01040001", "@string/copy");
        keymap.put("@android:01040002", "@string/copyUrl");
        keymap.put("@android:01040003", "@string/cut");
        keymap.put("@android:01040005", "@string/defaultMsisdnAlphaTag");
        keymap.put("@android:01040004", "@string/defaultVoiceMailAlphaTag");
        keymap.put("@android:01040014", "@string/dialog_alert_title");
        keymap.put("@android:01040006", "@string/emptyPhoneNumber");
        keymap.put("@android:01040018", "@string/fingerprint_icon_content_description");
        keymap.put("@android:01040007", "@string/httpErrorBadUrl");
        keymap.put("@android:01040008", "@string/httpErrorUnsupportedScheme");
        keymap.put("@android:01040009", "@string/no");
        keymap.put("@android:0104000a", "@string/ok");
        keymap.put("@android:0104000b", "@string/paste");
        keymap.put("@android:01040019", "@string/paste_as_plain_text");
        keymap.put("@android:0104000c", "@string/search_go");
        keymap.put("@android:0104000d", "@string/selectAll");
        keymap.put("@android:01040016", "@string/selectTextMode");
        keymap.put("@android:01040017", "@string/status_bar_notification_info_overflow");
        keymap.put("@android:0104000e", "@string/unknownName");
        keymap.put("@android:0104000f", "@string/untitled");
        keymap.put("@android:01040013", "@string/yes");

        //R.style

        //R.styleable

        //R.transition [DONE]
        keymap.put("@android:010f0003", "@transation/explode");
        keymap.put("@android:010f0002", "@transation/fade");
        keymap.put("@android:010f0001", "@transation/move");
        keymap.put("@android:010f0000", "@transation/no_transition");
        keymap.put("@android:010f0004", "@transation/slide_bottom");
        keymap.put("@android:010f0007", "@transation/slide_left");
        keymap.put("@android:010f0006", "@transation/slide_right");
        keymap.put("@android:010f0005", "@transation/slide_top");

        //R.xml [DONE]

        data = reverseAndroidRefs(ANDROID_R_ID_REGEX, data, keymap);

        HashMap<String, String> secondaryKeymap = new HashMap<>();

        //res/configuration
        secondaryKeymap.put("android:orientation=\"0\"", "android:orientation=\"undefined\"");
        secondaryKeymap.put("android:orientation=\"3\"", "android:orientation=\"square\"");
        secondaryKeymap.put("android:orientation=\"1\"", "android:orientation=\"portrait\"");
        secondaryKeymap.put("android:orientation=\"2\"", "android:orientation=\"landscape\"");

        secondaryKeymap.put("android:layout_height=\"-2\"", "android:layout_height=\"wrap_content\"");
        secondaryKeymap.put("android:showAsAction=\"0x00000002\"", "android:showAsAction=\"always\"");
        secondaryKeymap.put("android:showAsAction=\"0x00000008\"", "android:showAsAction=\"collapse_action_view\"");
        secondaryKeymap.put("android:showAsAction=\"0x00000001\"", "android:showAsAction=\"if_room\"");
        secondaryKeymap.put("android:showAsAction=\"0x00000000\"", "android:showAsAction=\"never\"");
        secondaryKeymap.put("android:showAsAction=\"0x00000004\"", "android:showAsAction=\"with_text\"");
        secondaryKeymap.put("android:configChanges=\"0x000000A0\"", "android:configChanges=\"0x000000A0\"");

        data = reverseAndroidRefs(ANDROID_ID_REGEX, data, secondaryKeymap);

        data = Util.prettyFormatXML(data);
        Log.write(LoggerType.TRACE, data);
    }

    private String reverseAndroidRefs(String key, String data, HashMap<String, String> db) {

        String hash;

        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile(key)
                .matcher(data);
        while (m.find()) {
            allMatches.add(m.group());
        }
        //replace found data
        for (String match : allMatches){
            hash = match.toLowerCase();
            String decoded = (db.get(hash) !=null) ? db.get(hash) : match;
            data = data.replaceAll(match, decoded);
        }
        return data;
    }

    @Override
    public int getInitialArgumentCount(IDroidefenseClass cls, IDroidefenseMethod m) {
        return 0; //do not use arguments
    }

    @Override
    public Object getInitialArguments(IDroidefenseClass cls, IDroidefenseMethod m) {
        return null; //do not use arguments
    }

    @Override
    public IDroidefenseClass[] getInitialDVMClass() {
        //only return developer class and skip known java jdk and android sdk classes

        IDroidefenseClass[] alllist = currentProject.getInternalInfo().getAllClasses();
        ArrayList<IDroidefenseClass> developerClasses = new ArrayList<>();
        for (IDroidefenseClass cls : alllist) {
            if ( cls.isAndroidRclass() )
                developerClasses.add(cls);
        }
        IDroidefenseClass[] list = developerClasses.toArray(new IDroidefenseClass[developerClasses.size()]);
        Log.write(LoggerType.TRACE, "Estimated node count: ");
        int nodes = 0;
        for (IDroidefenseClass cls : list) {
            nodes += cls.getAllMethods().length;
        }
        Log.write(LoggerType.TRACE, nodes + " R nodes");
        return list;
    }

    @Override
    public IDroidefenseMethod[] getInitialMethodToRun(IDroidefenseClass dexClass) {
        return dexClass.getAllMethods();
    }

    @Override
    public strictfp void execute(boolean keepScanning) throws Throwable {

        Log.write(LoggerType.DEBUG, "Reading Android R references...");
        IDroidefenseFrame frame = getCurrentFrame();
        IDroidefenseMethod method = frame.getMethod();
        IDroidefenseClass methodOwnerClass = method.getOwnerClass();

        Log.write(LoggerType.DEBUG, "Class name detected as: "+methodOwnerClass.getName());
        decodeFieldMap(methodOwnerClass.getStaticFieldMap());
    }

    private void decodeFieldMap(Hashtable staticFieldMap) {
        Iterator it = staticFieldMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, IDroidefenseField> entry = (Map.Entry<String, IDroidefenseField>) it.next();

            if(entry!=null){
                IDroidefenseField field = entry.getValue();
                String name = field.getName();
                int value = field.getIntValue();
                String owner = field.getOwnerClass().getName();
                //clean owner from com/packagename/R$id to id
                owner = owner.split("\\$")[1];
                references.add(new AndroidRField(owner, name, value));
            }
        }
    }

    @Override
    public AbstractDVMThread cleanThreadContext() {
        //cleanThreadContext 'thread' status
        this.setStatus(STATUS_NOT_STARTED);
        this.removeFrames();
        this.timestamp = new ExecutionTimer();
        return this;
    }
}
