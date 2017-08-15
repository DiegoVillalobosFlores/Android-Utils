import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Date;

import installred.installred.installred.installred.System_Classes.Settings;

/**
 * Created by deimi on 04/01/2017.
 */

public class DateTimePickersFragments extends DialogFragment{

    public interface OnDateTimeSet{
        void onDateSelected(int year, int month, int day);
        void onTimeSelected(int hour, int minute);
    }

    public void setOnDateTimeSet(OnDateTimeSet onDateTimeSet) {
        this.onDateTimeSet = onDateTimeSet;
    }

    OnDateTimeSet onDateTimeSet;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_date_time_pickers,container,false);

        TextView title = (TextView) v.findViewById(R.id.fragment_date_time_pickers_title);
        final TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        final DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        final TextView submit = (TextView) v.findViewById(R.id.fragment_date_time_pickers_button_ok);
        TextView cancel = (TextView) v.findViewById(R.id.fragment_date_time_pickers_button_cancel);

        final Boolean date = getArguments().getBoolean("date");
        String message = getArguments().getString("title");
        timePicker.setIs24HourView(false);
        Settings settings = (Settings) getArguments().get("settings");

        title.setText(message);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                if(date){
                    onDateTimeSet.onDateSelected(year,month,day);
                }else {
                    onDateTimeSet.onTimeSelected(hour,minute);
                }
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        if(date){
            datePicker.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.GONE);
            Date date1 = new Date();

            datePicker.setMinDate(date1.getTime() + 86400000);
            if(settings != null){
                if(settings.getKeys() != null){
                    long maxDays = Long.parseLong(settings.getKeys().getMax_days());
                    long minDays = Long.parseLong(settings.getKeys().getMin_days());
                    datePicker.setMaxDate(date1.getTime() + getDays(maxDays));
                    datePicker.setMinDate(date1.getTime() + getDays(minDays));
                }
            }
        }else{
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);
        }
        return v;
    }

    public long getDays(long days){
        return days * 86400000;
    }
}
