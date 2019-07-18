package com.adhiwie.mymoodjournal.sensor.push;

import java.util.Calendar;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;

import com.adhiwie.mymoodjournal.debug.CustomExceptionHandler;
import com.adhiwie.mymoodjournal.file.FileMgr;
import com.adhiwie.mymoodjournal.sensor.data.AccessibilityEventData;
import com.adhiwie.mymoodjournal.system.APILevel;
import com.adhiwie.mymoodjournal.user.permission.Permission;
import com.adhiwie.mymoodjournal.utils.Log;

public class AccessibilitySensor extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log log = new Log();
        try {
            log.v("New Accessibility Event");
            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
                Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));

            AccessibilityEventData aed = new AccessibilityEventData(event.getEventType(),
                    event.getPackageName().toString(), Calendar.getInstance().getTimeInMillis());
            FileMgr fm = new FileMgr(getApplicationContext());
            fm.addData(aed);
            log.d("Accessibility Result: " + aed.toJSONString());
        } catch (Exception e) {
            log.e(e.toString());
        }
    }

    @Override
    protected void onServiceConnected() {
        new Log().e("Service Connected");

        if (!new APILevel().isSuitableForAccessibilityMetaTag()) {
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
                    | AccessibilityEvent.TYPE_VIEW_CLICKED
                    | AccessibilityEvent.TYPE_VIEW_FOCUSED
                    | AccessibilityEvent.TYPE_VIEW_LONG_CLICKED
                    | AccessibilityEvent.TYPE_VIEW_SELECTED
                    | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;

            /**** these events are not available for API below 14. ****/
            //				| AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START
            //				| AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END
            //				| AccessibilityEvent.TYPE_VIEW_HOVER_ENTER
            //				| AccessibilityEvent.TYPE_VIEW_HOVER_EXIT
            //				| AccessibilityEvent.TYPE_VIEW_SCROLLED
            //				| AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED

            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
            info.notificationTimeout = 100;
            setServiceInfo(info);
        }
    }

    @Override
    public void onInterrupt() {
        new Log().e("Accessibility Service Interruption");
    }

    @Override
    public void onDestroy() {
        new Log().e("Accessibility Service Destroyed");
        new Permission(getApplicationContext()).notifyUserIfAccessibilityPermissionRevoked();
    }


    //	private ArrayList<String> getInstalledAccessiblityServicesIds(Context context)
    //	{
    //		AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    //		ArrayList<String> ids = new ArrayList<String>();
    //	    List<AccessibilityServiceInfo> runningServices = am.getInstalledAccessibilityServiceList();
    //	    for (AccessibilityServiceInfo service : runningServices)
    //	        ids.add(service.getId());
    //	    return ids;
    //	}

}

