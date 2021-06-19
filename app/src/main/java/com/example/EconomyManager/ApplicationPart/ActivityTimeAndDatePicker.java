package com.example.EconomyManager.ApplicationPart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.EconomyManager.Months;
import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityTimeAndDatePicker extends AppCompatActivity
{
    private Button saveChanges;
    private ImageView goBack;
    private TextView title;
    private Bundle extras;
    private EditText day, year, hour, minute, second;
    private Spinner month;
    private FirebaseAuth fbAuth=FirebaseAuth.getInstance();
    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_and_date_picker);
        setVariables();
        setTheme();
        setTitle();
        setMonthSpinner();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables()
    {
        saveChanges=findViewById(R.id.timeAndDatePickerSave);
        goBack=findViewById(R.id.timeAndDatePickerBack);
        extras=getIntent().getExtras();
        day=findViewById(R.id.timeAndDatePickerEditDay);
        year=findViewById(R.id.timeAndDatePickerEditYear);
        hour=findViewById(R.id.timeAndDatePickerEditHour);
        minute=findViewById(R.id.timeAndDatePickerEditMinute);
        second=findViewById(R.id.timeAndDatePickerEditSecond);
        month=findViewById(R.id.timeAndDatePickerEditMonth);
        title=findViewById(R.id.timeAndDatePickerTitle);
    }

    private void setOnClickListeners()
    {
        goBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean dayModified=false, monthModified=false, yearModified=false, hourModified=false, minuteModified=false, secondModified=false;
                String initialDate=String.valueOf(extras.getString("initialDate")), modifiedDate;
                String[] initialDateSplit=initialDate.split("\\W");
                String initialDay=initialDateSplit[1], initialMonth=initialDateSplit[0], initialYear=initialDateSplit[3], initialHour=initialDateSplit[4], initialMinute=initialDateSplit[5], initialSecond=initialDateSplit[6];
                String modifiedDay=null, modifiedMonth=null, modifiedYear=null, modifiedHour=null, modifiedMinute=null, modifiedSecond=null;
                boolean daysError=false;

                if(!String.valueOf(day.getText()).trim().equals("") && !String.valueOf(day.getText()).trim().equals(initialDay))
                {
                    modifiedDay=String.valueOf(day.getText());
                    if(Integer.parseInt(modifiedDay)>=1 && Integer.parseInt(modifiedDay)<=31)
                    {
                        if(modifiedDay.charAt(0)=='+' || (modifiedDay.charAt(0)=='-')) // daca numarul incepe cu + sau cu -
                            modifiedDay=modifiedDay.substring(1);
                        if(modifiedDay.charAt(0)=='0') // daca numarul are la inceput cel putin un 0
                        {
                            int numberOfZeros=1;
                            for(int i=0; i<modifiedDay.length(); i++)
                            {
                                for(int j=i+1; j<=i+1; j++)
                                    if(modifiedDay.charAt(i)=='0' && modifiedDay.charAt(j)=='0')
                                        numberOfZeros++;
                            }
                            modifiedDay=modifiedDay.substring(numberOfZeros);
                        }

                        if(Integer.parseInt(modifiedDay)>=1 && Integer.parseInt(modifiedDay)<=9) // daca ziua este intre 1 si 9, adaugam un 0 la inceput
                            modifiedDay="0"+modifiedDay;

                        if(!year.getText().toString().isEmpty()) // daca am modificat anul
                        {
                            if(!String.valueOf(month.getSelectedItem()).trim().equals("") && !String.valueOf(month.getSelectedItem()).equals(initialMonth)) // daca am modificat luna
                            {
                                if(String.valueOf(month.getSelectedItem()).equals("February")) // daca luna modificata este februarie
                                {
                                    if(yearIsLeap(Integer.parseInt(String.valueOf(year.getText())))) // daca anul este bisect
                                    {
                                        if(Integer.parseInt(modifiedDay)>29)
                                        {
                                            Toast.makeText(ActivityTimeAndDatePicker.this, month.getSelectedItem()+" "+year.getText()+getResources().getString(R.string.time_date_month_29_days), Toast.LENGTH_SHORT).show();
                                            daysError=true;
                                        }
                                    }
                                    else if(Integer.parseInt(modifiedDay)>28)
                                    {
                                        Toast.makeText(ActivityTimeAndDatePicker.this, month.getSelectedItem()+" "+year.getText()+getResources().getString(R.string.time_date_month_28_days), Toast.LENGTH_SHORT).show();
                                        daysError=true;
                                    }
                                }
                                else if(String.valueOf(month.getSelectedItem()).equals("April") || String.valueOf(month.getSelectedItem()).equals("June") || String.valueOf(month.getSelectedItem()).equals("September") || String.valueOf(month.getSelectedItem()).equals("November"))
                                {
                                    if(Integer.parseInt(modifiedDay)>30)
                                    {
                                        daysError=true;
                                        Toast.makeText(ActivityTimeAndDatePicker.this, month.getSelectedItem()+" "+year.getText()+getResources().getString(R.string.time_date_month_30_days), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else // daca nu am modificat luna
                            {
                                if(initialMonth.equals("February")) // daca luna este februarie
                                {
                                    if(yearIsLeap(Integer.parseInt(String.valueOf(year.getText())))) // daca anul este bisect
                                    {
                                        if(Integer.parseInt(modifiedDay)>29)
                                        {
                                            Toast.makeText(ActivityTimeAndDatePicker.this, initialMonth+" "+year.getText()+getResources().getString(R.string.time_date_month_29_days), Toast.LENGTH_SHORT).show();
                                            daysError=true;
                                        }
                                    }
                                    else if(Integer.parseInt(modifiedDay)>28)
                                    {
                                        Toast.makeText(ActivityTimeAndDatePicker.this, initialMonth+" "+year.getText()+getResources().getString(R.string.time_date_month_28_days), Toast.LENGTH_SHORT).show();
                                        daysError=true;
                                    }
                                }
                            }
                        }
                        else // daca nu am modificat anul
                        {
                            if(!String.valueOf(month.getSelectedItem()).equals("Select month") && !String.valueOf(month.getSelectedItem()).equals(initialMonth)) // daca am modificat luna
                            {
                                if(String.valueOf(month.getSelectedItem()).equals("February")) // daca luna modificata este februarie
                                {
                                    if(yearIsLeap(Integer.parseInt(initialYear))) // daca anul este bisect
                                    {
                                        if(Integer.parseInt(modifiedDay)>29)
                                        {
                                            Toast.makeText(ActivityTimeAndDatePicker.this, month.getSelectedItem()+" "+initialYear+getResources().getString(R.string.time_date_month_29_days), Toast.LENGTH_SHORT).show();
                                            daysError=true;
                                        }
                                    }
                                    else if(Integer.parseInt(modifiedDay)>28)
                                    {
                                        Toast.makeText(ActivityTimeAndDatePicker.this, month.getSelectedItem()+" "+initialYear+getResources().getString(R.string.time_date_month_28_days), Toast.LENGTH_SHORT).show();
                                        daysError=true;
                                    }
                                }
                                else if(String.valueOf(month.getSelectedItem()).equals("April") || String.valueOf(month.getSelectedItem()).equals("June") || String.valueOf(month.getSelectedItem()).equals("September") || String.valueOf(month.getSelectedItem()).equals("November"))
                                {
                                    if(Integer.parseInt(modifiedDay)>30)
                                    {
                                        daysError=true;
                                        Toast.makeText(ActivityTimeAndDatePicker.this, month.getSelectedItem()+" "+initialYear+getResources().getString(R.string.time_date_month_30_days), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else // daca nu am modificat luna
                            {
                                if(initialMonth.equals("February")) // daca luna este februarie
                                {
                                    if(yearIsLeap(Integer.parseInt(initialYear))) // daca anul este bisect
                                    {
                                        if(Integer.parseInt(modifiedDay)>29)
                                        {
                                            Toast.makeText(ActivityTimeAndDatePicker.this, initialMonth+" "+initialYear+getResources().getString(R.string.time_date_month_29_days), Toast.LENGTH_SHORT).show();
                                            daysError=true;
                                        }
                                    }
                                    else if(Integer.parseInt(modifiedDay)>28)
                                    {
                                        Toast.makeText(ActivityTimeAndDatePicker.this, initialMonth+" "+initialYear+getResources().getString(R.string.time_date_month_28_days), Toast.LENGTH_SHORT).show();
                                        daysError=true;
                                    }
                                }
                            }
                        }

                        if(initialMonth.equals("April") || initialMonth.equals("June") || initialMonth.equals("September") || initialMonth.equals("November"))
                            if(Integer.parseInt(modifiedDay)>30)
                            {
                                daysError=true;
                                Toast.makeText(ActivityTimeAndDatePicker.this, initialMonth+" "+initialYear+getResources().getString(R.string.time_date_month_30_days), Toast.LENGTH_SHORT).show();
                            }

                        if(!daysError)
                            dayModified=true;
                    }
                    else
                    {
                        daysError=true;
                        Toast.makeText(ActivityTimeAndDatePicker.this, getResources().getString(R.string.time_date_month_31_days), Toast.LENGTH_SHORT).show();
                    }
                }

                if(!String.valueOf(month.getSelectedItem()).trim().equals("") && !Months.getMonthInEnglish(ActivityTimeAndDatePicker.this, String.valueOf(month.getSelectedItem()).trim()).trim().equals(initialMonth))
                {
                    modifiedMonth=Months.getMonthInEnglish(ActivityTimeAndDatePicker.this, String.valueOf(month.getSelectedItem()).trim()).trim();
                    monthModified=true;
                }

                if(!String.valueOf(year.getText()).trim().equals("") && !String.valueOf(year.getText()).trim().equals(initialYear))
                {
                    modifiedYear=String.valueOf(year.getText());
                    Calendar currentTime=Calendar.getInstance();
                    if(Integer.parseInt(modifiedYear)>=currentTime.get(Calendar.YEAR)-50 && Integer.parseInt(modifiedYear)<=currentTime.get(Calendar.YEAR)+50)
                    {
                        if(modifiedYear.charAt(0)=='+' || (modifiedYear.charAt(0)=='-')) // daca numarul incepe cu + sau cu -
                            modifiedYear=modifiedYear.substring(1);
                        if(modifiedYear.charAt(0)=='0') // daca numarul are la inceput cel putin un 0
                        {
                            int numberOfZeros=1;
                            for(int i=0; i<modifiedYear.length(); i++)
                            {
                                for(int j=i+1; j<=i+1; j++)
                                    if(modifiedYear.charAt(i)=='0' && modifiedYear.charAt(j)=='0')
                                        numberOfZeros++;
                            }
                            modifiedYear=modifiedYear.substring(numberOfZeros);
                        }
                        yearModified=true;
                    }
                    else Toast.makeText(ActivityTimeAndDatePicker.this, getResources().getString(R.string.time_date_year)+(currentTime.get(Calendar.YEAR)-50)+getResources().getString(R.string.time_date_and)+(currentTime.get(Calendar.YEAR)+50), Toast.LENGTH_SHORT).show();
                }

                if(!String.valueOf(hour.getText()).trim().equals("") && !String.valueOf(hour.getText()).trim().equals(initialHour))
                {
                    modifiedHour=String.valueOf(hour.getText());
                    if(Integer.parseInt(modifiedHour)>=0 && Integer.parseInt(modifiedHour)<=23)
                    {
                        if(modifiedHour.charAt(0)=='+' || (modifiedHour.charAt(0)=='-')) // daca numarul incepe cu + sau cu -
                            modifiedHour=modifiedHour.substring(1);
                        if(modifiedHour.charAt(0)=='0') // daca numarul are la inceput cel putin un 0
                        {
                            int numberOfZeros=1;
                            for(int i=0; i<modifiedHour.length(); i++)
                            {
                                for(int j=i+1; j<=i+1; j++)
                                    if(modifiedHour.charAt(i)=='0' && modifiedHour.charAt(j)=='0')
                                        numberOfZeros++;
                            }
                            modifiedHour=modifiedHour.substring(numberOfZeros);
                        }

                        if(Integer.parseInt(modifiedHour)>=0 && Integer.parseInt(modifiedHour)<=9) // daca ora este intre 0 si 9, adaugam un 0 la inceput
                            modifiedHour="0"+modifiedHour;
                        hourModified=true;
                    }
                    else Toast.makeText(ActivityTimeAndDatePicker.this, getResources().getString(R.string.time_date_hour), Toast.LENGTH_SHORT).show();
                }

                if(!String.valueOf(minute.getText()).trim().equals("") && !String.valueOf(hour.getText()).trim().equals(initialMinute))
                {
                    modifiedMinute=String.valueOf(minute.getText());
                    if(Integer.parseInt(modifiedMinute)>=0 && Integer.parseInt(modifiedMinute)<=59)
                    {
                        if(modifiedMinute.charAt(0)=='+' || (modifiedMinute.charAt(0)=='-')) // daca numarul incepe cu + sau cu -
                            modifiedMinute=modifiedMinute.substring(1);
                        if(modifiedMinute.charAt(0)=='0') // daca numarul are la inceput cel putin un 0
                        {
                            int numberOfZeros=1;
                            for(int i=0; i<modifiedMinute.length(); i++)
                            {
                                for(int j=i+1; j<=i+1; j++)
                                    if(modifiedMinute.charAt(i)=='0' && modifiedMinute.charAt(j)=='0')
                                        numberOfZeros++;
                            }
                            modifiedMinute=modifiedMinute.substring(numberOfZeros);
                        }

                        if(Integer.parseInt(modifiedMinute)>=0 && Integer.parseInt(modifiedMinute)<=9) // daca ora este intre 0 si 9, adaugam un 0 la inceput
                            modifiedMinute="0"+modifiedMinute;
                        minuteModified=true;
                    }
                    else Toast.makeText(ActivityTimeAndDatePicker.this, getResources().getString(R.string.time_date_minute), Toast.LENGTH_SHORT).show();
                }

                if(!String.valueOf(second.getText()).trim().equals("") && !String.valueOf(hour.getText()).trim().equals(initialSecond))
                {
                    modifiedSecond=String.valueOf(second.getText());
                    if(Integer.parseInt(modifiedSecond)>=0 && Integer.parseInt(modifiedSecond)<=59)
                    {
                        if(modifiedSecond.charAt(0)=='+' || (modifiedSecond.charAt(0)=='-')) // daca numarul incepe cu + sau cu -
                            modifiedSecond=modifiedSecond.substring(1);
                        if(modifiedSecond.charAt(0)=='0') // daca numarul are la inceput cel putin un 0
                        {
                            int numberOfZeros=1;
                            for(int i=0; i<modifiedSecond.length(); i++)
                            {
                                for(int j=i+1; j<=i+1; j++)
                                    if(modifiedSecond.charAt(i)=='0' && modifiedSecond.charAt(j)=='0')
                                        numberOfZeros++;
                            }
                            modifiedSecond=modifiedSecond.substring(numberOfZeros);
                        }

                        if(Integer.parseInt(modifiedSecond)>=0 && Integer.parseInt(modifiedSecond)<=9) // daca ora este intre 0 si 9, adaugam un 0 la inceput
                            modifiedSecond="0"+modifiedSecond;
                        secondModified=true;
                    }
                    else Toast.makeText(ActivityTimeAndDatePicker.this, getResources().getString(R.string.time_date_second), Toast.LENGTH_SHORT).show();
                }

                if(monthModified)
                    modifiedDate=modifiedMonth;
                else modifiedDate=initialMonth;

                if(dayModified)
                {
                    if(!initialDay.equals(modifiedDay)) // daca ziua initiala si ziua modificata NU coincid
                        modifiedDate+=" "+modifiedDay;
                }
                else modifiedDate+=" "+initialDay;

                if(yearModified)
                {
                    if(!initialYear.equals(modifiedYear))
                        modifiedDate+=", "+modifiedYear;
                }
                else modifiedDate+=", "+initialYear;

                if(hourModified)
                {
                    if(!initialHour.equals(modifiedHour))
                        modifiedDate+=" "+modifiedHour;
                }
                else modifiedDate+=" "+initialHour;

                if(minuteModified)
                {
                    if(!initialMinute.equals(modifiedMinute))
                        modifiedDate+=":"+modifiedMinute;
                }
                else modifiedDate+=":"+initialMinute;

                if(secondModified)
                {
                    if(!initialSecond.equals(modifiedSecond))
                        modifiedDate+=":"+modifiedSecond;
                }
                else modifiedDate+=":"+initialSecond;

                if(!daysError)
                {
                    /*if(!dayModified && !monthModified && !yearModified && !hourModified && !minuteModified && !secondModified)
                        Toast.makeText(ActivityTimeAndDatePicker.this, "No changes made", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(ActivityTimeAndDatePicker.this, "Changes saved", Toast.LENGTH_SHORT).show();*/
                    Intent returnIntent=getIntent();
                    returnIntent.putExtra("modifiedDate", modifiedDate);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });
    }

    private boolean yearIsLeap(int givenYear)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR, givenYear);
        return calendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365;
    }

    private void setTitle()
    {
        title.setTextColor(Color.WHITE);
        title.setText(getResources().getString(R.string.edit_transaction_time_date).trim());
        title.setTextSize(18);
    }

    private void setTheme()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("darkTheme"))
                    {
                        boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                        int backgroundTheme, color, spinnerItemBackground;

                        if(!darkThemeEnabled)
                        {
                            backgroundTheme=R.drawable.ic_white_gradient_tobacco_ad;
                            color=Color.parseColor("#195190");
                            spinnerItemBackground=R.drawable.ic_blue_gradient_unloved_teen;
                        }
                        else
                        {
                            backgroundTheme=R.drawable.ic_black_gradient_night_shift;
                            color=Color.WHITE;
                            spinnerItemBackground=R.drawable.ic_white_gradient_tobacco_ad;
                        }

                        getWindow().setBackgroundDrawableResource(backgroundTheme);
                        month.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // setam culoarea sagetii
                        month.setPopupBackgroundResource(spinnerItemBackground);
                        setColor(day, color);
                        setColor(year, color);
                        setColor(hour, color);
                        setColor(minute, color);
                        setColor(second, color);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void setMonthSpinner()
    {
        String initialDate=String.valueOf(extras.getString("initialDate"));
        ArrayList<String> monthsList=new ArrayList<>();
        String[] initialDateSplit=initialDate.split("\\W");
        String initialMonth=Months.getTranslatedMonth(ActivityTimeAndDatePicker.this, initialDateSplit[0].trim());

        ArrayAdapter<String> monthAdapter=new ArrayAdapter<String>(this, R.layout.custom_spinner_item, monthsList)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                final View v=super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER);

                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.hasChild("darkTheme"))
                            {
                                boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int itemsColor;

                                if(!darkThemeEnabled)
                                    itemsColor=Color.WHITE;
                                else itemsColor=Color.BLACK;

                                ((TextView)v).setTextColor(itemsColor); // setam culoarea textului elementelor in functie de tema selectata
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });

                return v;
            }
        };

        AdapterView.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id)
            {
                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.hasChild("darkTheme"))
                            {
                                boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int textColor;

                                if(!darkThemeEnabled)
                                    textColor=Color.parseColor("#195190");
                                else textColor=Color.WHITE;

                                ((TextView)parent.getChildAt(0)).setTextColor(textColor);
                                ((TextView)parent.getChildAt(0)).setGravity(Gravity.START);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        };

        monthsList.add(getResources().getString(R.string.edit_account_gender_select));
        monthsList.add(getResources().getString(R.string.month_january));
        monthsList.add(getResources().getString(R.string.month_february));
        monthsList.add(getResources().getString(R.string.month_march));
        monthsList.add(getResources().getString(R.string.month_april));
        monthsList.add(getResources().getString(R.string.month_may));
        monthsList.add(getResources().getString(R.string.month_june));
        monthsList.add(getResources().getString(R.string.month_july));
        monthsList.add(getResources().getString(R.string.month_august));
        monthsList.add(getResources().getString(R.string.month_september));
        monthsList.add(getResources().getString(R.string.month_october));
        monthsList.add(getResources().getString(R.string.month_november));
        monthsList.add(getResources().getString(R.string.month_december));

        month.setAdapter(monthAdapter);
        month.setSelection(getMonthPositionInMonthsList(initialMonth));
        month.setOnItemSelectedListener(listener);
    }

    private void setColor(EditText editText, int color)
    {
        editText.setTextColor(color);
        editText.setHintTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

//    private String getTranslatedMonth(String monthNameInEnglish)
//    {
//        switch(monthNameInEnglish)
//        {
//            case "January":
//                return getResources().getString(R.string.month_january);
//            case "February":
//                return getResources().getString(R.string.month_february);
//            case "March":
//                return getResources().getString(R.string.month_march);
//            case "April":
//                return getResources().getString(R.string.month_april);
//            case "May":
//                return getResources().getString(R.string.month_may);
//            case "June":
//                return getResources().getString(R.string.month_june);
//            case "July":
//                return getResources().getString(R.string.month_july);
//            case "August":
//                return getResources().getString(R.string.month_august);
//            case "September":
//                return getResources().getString(R.string.month_september);
//            case "October":
//                return getResources().getString(R.string.month_october);
//            case "November":
//                return getResources().getString(R.string.month_november);
//            case "December":
//                return getResources().getString(R.string.month_december);
//            default:
//                return "";
//        }
//    }

    private int getMonthPositionInMonthsList(String monthName)
    {
        if(monthName.trim().equals(getResources().getString(R.string.month_january)))
            return 1;
        else if(monthName.trim().equals(getResources().getString(R.string.month_february)))
            return 2;
        else if(monthName.trim().equals(getResources().getString(R.string.month_march)))
            return 3;
        else if(monthName.trim().equals(getResources().getString(R.string.month_april)))
            return 4;
        else if(monthName.trim().equals(getResources().getString(R.string.month_may)))
            return 5;
        else if(monthName.trim().equals(getResources().getString(R.string.month_june)))
            return 6;
        else if(monthName.trim().equals(getResources().getString(R.string.month_july)))
            return 7;
        else if(monthName.trim().equals(getResources().getString(R.string.month_august)))
            return 8;
        else if(monthName.trim().equals(getResources().getString(R.string.month_september)))
            return 9;
        else if(monthName.trim().equals(getResources().getString(R.string.month_october)))
            return 10;
        else if(monthName.trim().equals(getResources().getString(R.string.month_november)))
            return 11;
        else if(monthName.trim().equals(getResources().getString(R.string.month_december)))
            return 12;
        else return 0;
    }

//    private String getMonthInEnglish(String monthName)
//    {
//        if(monthName.trim().equals(getResources().getString(R.string.month_january)))
//            return "January";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_february)))
//            return "February";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_march)))
//            return "March";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_april)))
//            return "April";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_may)))
//            return "May";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_june)))
//            return "June";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_july)))
//            return "July";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_august)))
//            return "August";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_september)))
//            return "September";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_october)))
//            return "October";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_november)))
//            return "November";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_december)))
//            return "December";
//        else return "";
//    }
}