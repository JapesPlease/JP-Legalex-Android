package lax.lega.rv.com.legalax.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.fragment.MyLogic;


public class AppointmentActivity extends Activity {
    CalendarView calendarView;
    List<MyLogic.Datum>list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_activity);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        calendarView.setSelectionType(SelectionType.MULTIPLE);
        list=new ArrayList<>();
    }
}
