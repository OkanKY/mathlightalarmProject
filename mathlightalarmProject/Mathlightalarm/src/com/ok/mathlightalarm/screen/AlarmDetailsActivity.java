package com.ok.mathlightalarm.screen;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.ok.mathlightalarm.CustomSwitch;
import com.ok.mathlightalarm.R;
import com.ok.mathlightalarm.database.AlarmDBHelper;
import com.ok.mathlightalarm.database.AlarmModel;
import com.ok.mathlightalarm.service.AlarmManagerHelper;


public class AlarmDetailsActivity extends Activity  {
	
	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	
	private AlarmModel alarmDetails;
	
	private TimePicker timePicker;
	private EditText edtName;
	private CustomSwitch chkWeekly;
	private CustomSwitch chkSunday;
	private CustomSwitch chkMonday;
	private CustomSwitch chkTuesday;
	private CustomSwitch chkWednesday;
	private CustomSwitch chkThursday;
	private CustomSwitch chkFriday;
	private CustomSwitch chkSaturday;
	private TextView txtToneSelection;
	private ToggleButton btnToggle;
	private ToggleButton btnToggleFlash;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private RadioGroup rg;
	private boolean selected =false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_details);

		getActionBar().setTitle("Create New Alarm");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		rg=(RadioGroup) findViewById(R.id.radioGroup1);
		rb1=(RadioButton) findViewById(R.id.radio0);
		rb2=(RadioButton) findViewById(R.id.radio1);
		rb3=(RadioButton) findViewById(R.id.radio2);
		btnToggle =(ToggleButton) findViewById(R.id.toggleButton1);
		btnToggleFlash =(ToggleButton) findViewById(R.id.toggleButton2);
		timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
		edtName = (EditText) findViewById(R.id.alarm_details_name);
		chkWeekly = (CustomSwitch) findViewById(R.id.alarm_details_repeat_weekly);
		chkSunday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_sunday);
		chkMonday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_monday);
		chkTuesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_tuesday);
		chkWednesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_wednesday);
		chkThursday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_thursday);
		chkFriday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_friday);
		chkSaturday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_saturday);
		txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
		btnToggle.setOnClickListener(new OnClickListener()  {
			@Override
             public void onClick(View arg0)
			{
            if(btnToggle.isChecked())
            {
              selected = true;
              for(int i = 0; i < rg.getChildCount(); i++){
                  ((RadioButton)rg.getChildAt(i)).setEnabled(true);
              }
            }
            else
            {
            	selected = false;
            	for(int i = 0; i < rg.getChildCount(); i++){
                    ((RadioButton)rg.getChildAt(i)).setEnabled(false);
                }
            }
                            
            }
		});
		long id = getIntent().getExtras().getLong("id");
		
		if (id == -1) {
			alarmDetails = new AlarmModel();
		} else {
			alarmDetails = dbHelper.getAlarm(id);
			
			timePicker.setCurrentMinute(alarmDetails.timeMinute);
			timePicker.setCurrentHour(alarmDetails.timeHour);
			if(alarmDetails.isMathEnabled==0)
			{	
				btnToggle.setChecked(false);
				for(int i = 0; i < rg.getChildCount(); i++){
                    ((RadioButton)rg.getChildAt(i)).setEnabled(false);
                }
			}
			else
			{  btnToggle.setChecked(true);
				for(int i = 0; i < rg.getChildCount(); i++){
                    ((RadioButton)rg.getChildAt(i)).setEnabled(true);
                }
		    if(alarmDetails.isMathEnabled==1)
				rb1.setChecked(true);
			else if(alarmDetails.isMathEnabled==2)
				rb2.setChecked(true);
			else if(alarmDetails.isMathEnabled==3)
				rb3.setChecked(true);
			}	
			btnToggleFlash.setChecked(alarmDetails.isFlashEnabled);
			edtName.setText(alarmDetails.name);
			chkWeekly.setChecked(alarmDetails.repeatWeekly);
			chkSunday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
			chkMonday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
			chkTuesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
			chkWednesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
			chkThursday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
			chkFriday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRDIAY));
			chkSaturday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));
			txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
		}

		final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
		ringToneContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				startActivityForResult(intent , 1);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
	        switch (requestCode) {
		        case 1: {
		        	alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		        	txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
		            break;
		        }
		        default: {
		            break;
		        }
	        }
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_details, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home: {
				finish();
				break;
			}
			case R.id.action_save_alarm_details: {
				updateModelFromLayout();
				
				AlarmManagerHelper.cancelAlarms(this);
				
				if (alarmDetails.id < 0) {
					dbHelper.createAlarm(alarmDetails);
				} else {
					dbHelper.updateAlarm(alarmDetails);
				}
				
				AlarmManagerHelper.setAlarms(this);
				
				setResult(RESULT_OK);
				finish();
			}
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void updateModelFromLayout() {		
		alarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
		alarmDetails.timeHour = timePicker.getCurrentHour().intValue();
		alarmDetails.name = edtName.getText().toString();
		alarmDetails.repeatWeekly = chkWeekly.isChecked();	
		alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, chkSunday.isChecked());	
		alarmDetails.setRepeatingDay(AlarmModel.MONDAY, chkMonday.isChecked());	
		alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, chkTuesday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, chkWednesday.isChecked());	
		alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, chkThursday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.FRDIAY, chkFriday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, chkSaturday.isChecked());
		alarmDetails.isFlashEnabled=btnToggleFlash.isChecked();
		if(!btnToggle.isChecked())
			alarmDetails.isMathEnabled=0;
		else
		{ 
		if(rb1.isChecked())
			alarmDetails.isMathEnabled=1;
		else if(rb2.isChecked())
			alarmDetails.isMathEnabled=2;
		else if(rb3.isChecked())
			alarmDetails.isMathEnabled=3;
		}
		alarmDetails.isEnabled = true;
	}

	
	
}
