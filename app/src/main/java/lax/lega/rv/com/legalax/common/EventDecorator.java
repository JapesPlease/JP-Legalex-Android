package lax.lega.rv.com.legalax.common;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import lax.lega.rv.com.legalax.R;

public class EventDecorator implements DayViewDecorator {


    private final List<CalendarDay> dates;
    Activity activity;
    Drawable drawable;

    public EventDecorator(Drawable drawable,List<CalendarDay> dates, Activity activity) {

        this.dates = dates;
        this.activity=activity;
        this.drawable=drawable;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,android.R.color.white)));
        view.setBackgroundDrawable(drawable);
    }
}