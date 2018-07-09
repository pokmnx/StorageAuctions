package com.storageauctions.dev;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.storageauctions.dev.ServiceManager.AuctionRequest;
import com.storageauctions.dev.ServiceManager.Facility;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class CreateAuctionFragment extends Fragment {

    ArrayList<Facility> facilityArr;
    ArrayList<String> sqFeetArr = new ArrayList<String>(Arrays.asList("5 × 5", "6 × 5", "5 × 10", "6 × 9", "6 × 12", "10 × 10", "5 × 12", "8 × 10", "10 × 12", "10 × 14",
    "12 × 12", "10 × 15", "10 × 16", "10 × 20", "10 × 24", "12 × 24", "20 × 20", "20 × 25", "10 × 30", "30 × 30"));
    ArrayList<String> unitSizeArr = new ArrayList<String>(Arrays.asList("25", "30", "50", "54", "72", "100", "60", "80", "120", "140",
    "144", "150", "160", "200", "240", "288", "400", "500", "300", "900"));
    ArrayList<String> timeZoneArr = new ArrayList<String>(Arrays.asList("US/Eastern", "US/Central", "US/Mountain", "America/Phoenix", "US/Pacific", "America/Anchorage", "Pacific/Honolulu"));
    ArrayList<String> cleanOutArr = new ArrayList<String>(Arrays.asList("24 hours", "48 hours", "72 hours", "Other (Specify)"));
    ArrayList<String> cleanNumArr = new ArrayList<String>(Arrays.asList("24", "48", "72", "0"));
    ArrayList<String> accessArr = new ArrayList<>(Arrays.asList("Office Hours", "Gate Hours", "24 Hour Access"));

    public String additionalInfo = "";
    public String termsCond = "";
    public Facility selectedFacility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_auction, container, false);

        final Spinner facilitySpinner = (Spinner) view.findViewById(R.id.facility_spinner);
        ServiceManager.sharedManager().getAllFacilities(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray facDicArr = response.getJSONArray("facilities");
                    facilityArr = new ArrayList<Facility>();
                    for (int i = 0; i < facDicArr.length(); i++) {
                        JSONObject facDic = facDicArr.getJSONObject(i);

                        Facility facility = new Facility();
                        try {
                            facility.id = facDic.getInt("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try { facility.user_id = facDic.getInt("user_id"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.active = facDic.getInt("active"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.name = facDic.getString("name"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.address = facDic.getString("address"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.city = facDic.getString("city"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.acc = facDic.getInt("acc"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.state_code = facDic.getString("state_code"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.postal_code = facDic.getString("postal_code"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.country = facDic.getString("country"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.lat = facDic.getDouble("lat"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.lon = facDic.getDouble("lon"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.phone = facDic.getString("phone"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.website = facDic.getString("website"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.email = facDic.getString("email"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.time_created = getDateFrom(facDic.getString("time_created")); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.time_updated = getDateFrom(facDic.getString("time_updated")); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.source = facDic.getString("source"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.source_id = facDic.getInt("source_id"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.terms = facDic.getString("terms"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.taxrate = facDic.getDouble("taxrate"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.remote_id = facDic.getInt("remote_id"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.commission_rate = facDic.getDouble("commission_rate"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.url = facDic.getString("url"); } catch (JSONException e) { e.printStackTrace(); }

                        facilityArr.add(facility);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressView.sharedView().dismiss();
                            final List<String> facilityNameList = new ArrayList<>();
                            facilityNameList.add("Select Facility");
                            for (int index = 0; index < facilityArr.size(); index++) {
                                Facility facility = facilityArr.get(index);
                                facilityNameList.add(facility.name);
                            }
                            final ArrayAdapter<String> facSpinnerArrAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, facilityNameList) {
                                @Override
                                public boolean isEnabled(int position) {
                                    if (position == 0) return false;
                                    else return true;
                                }

                                @Override
                                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView tv = (TextView) view;
                                    if (position == 0) tv.setTextColor(Color.GRAY);
                                    else tv.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };
                            facSpinnerArrAdapter.setDropDownViewResource(R.layout.spinner_item);
                            facilitySpinner.setAdapter(facSpinnerArrAdapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ProgressView.sharedView().showToast(getContext(), "Loading Failed");
            }
        });

        final Spinner sqFeetSpinner = (Spinner) view.findViewById(R.id.unit_sq_feet_spinner);
        ArrayAdapter<String> sqFeetAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sqFeetArr);
        sqFeetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sqFeetSpinner.setAdapter(sqFeetAdapter);

        final Spinner timeZoneSpinner = (Spinner) view.findViewById(R.id.timezone_spinner);
        ArrayAdapter<String> timeZoneAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, timeZoneArr);
        timeZoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeZoneSpinner.setAdapter(timeZoneAdapter);

        final Spinner cleanOutSpinner = (Spinner) view.findViewById(R.id.cleanout_spinner);
        ArrayAdapter<String> cleanOutAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, cleanOutArr);
        cleanOutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cleanOutSpinner.setAdapter(cleanOutAdapter);

        final Spinner accessSpinner = (Spinner) view.findViewById(R.id.access_spinner);
        ArrayAdapter<String> accessAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, accessArr);
        accessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accessSpinner.setAdapter(accessAdapter);

        final EditText startDateText = (EditText) view.findViewById(R.id.start_date_spinner);
        final EditText startTimeText = (EditText) view.findViewById(R.id.start_time_spinner);
        final EditText endDateText = (EditText) view.findViewById(R.id.end_date_spinner);
        final EditText endTimeText = (EditText) view.findViewById(R.id.end_time_spinner);

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                startDateText.setText(sdf.format(calendar.getTime()));
            }
        };

        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Calendar endCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, month);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                endDateText.setText(sdf.format(endCalendar.getTime()));
            }
        };

        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), endDateSetListener, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour > 12)
                            startTimeText.setText( selectedHour % 12 + ":" + selectedMinute + "PM");
                        else
                            startTimeText.setText( selectedHour + ":" + selectedMinute + "AM");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour > 12)
                            endTimeText.setText( selectedHour % 12 + ":" + selectedMinute + "PM");
                        else
                            endTimeText.setText( selectedHour + ":" + selectedMinute + "AM");
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        final Switch cashSwitch = (Switch) view.findViewById(R.id.cash_switch);
        final Switch creditSwitch = (Switch) view.findViewById(R.id.credit_switch);
        final Switch moneyOrderSwitch = (Switch) view.findViewById(R.id.money_order_switch);
        final Switch checkSwitch = (Switch) view.findViewById(R.id.check_switch);
        final Switch otherSwitch = (Switch) view.findViewById(R.id.other_switch);
        final EditText otherPayment = (EditText) view.findViewById(R.id.other_payment);

        final EditText auctionTitle = (EditText) view.findViewById(R.id.auction_title);
        final EditText realUnit = (EditText) view.findViewById(R.id.real_unit_count);
        final EditText altUnit = (EditText) view.findViewById(R.id.alt_unit_count);
        final EditText cleaningDeposit = (EditText) view.findViewById(R.id.cleaning_deposit);
        final EditText lockTagID = (EditText) view.findViewById(R.id.lock_tag_id);
        final EditText tenantName = (EditText) view.findViewById(R.id.tenant_name);
        final EditText otherCleanout = (EditText) view.findViewById(R.id.other_cleanout);
        otherCleanout.setEnabled(false);
        cleanOutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == cleanOutArr.size() - 1) {
                    otherCleanout.setEnabled(true);
                } else {
                    otherCleanout.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button additionalInformationBtn = (Button) view.findViewById(R.id.additional_information_btn);
        final Button termsConditionBtn = (Button) view.findViewById(R.id.terms_condition_btn);
        final Button bidScheduleBtn = (Button) view.findViewById(R.id.bid_schedule_btn);
        final Button cancelAuctionBtn = (Button) view.findViewById(R.id.cancel_auction_btn);
        final Button saveBtn = (Button) view.findViewById(R.id.save_btn);

        additionalInformationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Additional Information");
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        additionalInfo = input.getText().toString();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        termsConditionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Terms & Conditions");
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        termsCond = input.getText().toString();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        cashSwitch.setChecked(true);
        creditSwitch.setChecked(true);
        moneyOrderSwitch.setChecked(true);
        checkSwitch.setChecked(true);
        otherSwitch.setChecked(false);
        otherPayment.setEnabled(false);

        otherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                otherPayment.setEnabled(isChecked);
            }
        });

        final Fragment self = (Fragment) this;
        cancelAuctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(self).commit();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressView.sharedView().show(getContext());

                AuctionRequest request = new AuctionRequest();
                request.amount_owed = "40";
                request.alt_unit_number = altUnit.getText().toString();
                request.reserve = "0";
                request.time_end = endDateText.getText().toString() + " " + endTimeText.getText().toString().replace(" ", "").toLowerCase();
                request.terms = termsCond;
                request.prebid_close = "0";
                request.lock_tag = lockTagID.getText().toString();
                request.fees = "40";
                request.batch_email = "1";
                request.cleanout_other = cleanOutSpinner.getSelectedItemPosition() == cleanOutArr.size() - 1 ? otherCleanout.getText().toString() : "";
                request.payment = "15";
                request.time_st_zone = timeZoneArr.get(timeZoneSpinner.getSelectedItemPosition());
                request.cleanout = cleanNumArr.get(cleanOutSpinner.getSelectedItemPosition());
                // request.access = _access.text;
                request.access = "";
                request.auction_type = "0";
                request.time_start = startDateText.getText().toString() + " " + startTimeText.getText().toString().replace(" ", "").toLowerCase();

                request.facility_id = facilitySpinner.getSelectedItemPosition() > 0 ? Integer.toString(facilityArr.get(facilitySpinner.getSelectedItemPosition() - 1).id) : "";
                request.unit_size = unitSizeArr.get(sqFeetSpinner.getSelectedItemPosition());
                request.descr = auctionTitle.getText().toString();
                request.unit_number = realUnit.getText().toString();
                request.time_end_zone = timeZoneArr.get(timeZoneSpinner.getSelectedItemPosition());
                request.save_terms = "0";
                request.payment_other = otherSwitch.isChecked() ? otherPayment.getText().toString() : "";
                request.tenant_name = tenantName.getText().toString();

                ServiceManager.sharedManager().createAuction(request, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressView.sharedView().dismiss();
                                AuctionListFragment fragment = new AuctionListFragment();

                                if (fragment != null) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, fragment);
                                    ft.commit();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        try {
                            JSONObject error = errorResponse.getJSONObject("errors");
                            Iterator<String> keys = error.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                String value = error.getString(key);
                                if (value != null && value.length() > 0) {
                                    ProgressView.sharedView().showToast(value);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        return view;
    }

    public Date getDateFrom(String str) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
