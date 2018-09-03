package com.storageauctions.dev;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
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
import com.storageauctions.dev.ServiceManager.Auction;
import com.storageauctions.dev.ServiceManager.AuctionMedia;
import com.storageauctions.dev.ServiceManager.AuctionMeta;
import com.storageauctions.dev.ServiceManager.AuctionRequest;
import com.storageauctions.dev.ServiceManager.Facility;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    public Auction mAuction = null;
    public Boolean bEdit = false;
    public View mContainer = null;

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((EditText) mContainer.findViewById(R.id.auction_title)).getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_auction, container, false);
        mContainer = view;

        final Spinner facilitySpinner = (Spinner) view.findViewById(R.id.facility_spinner);
        ProgressView.sharedView().show(getContext());
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
                            int selectedIndex = -1;
                            for (int index = 0; index < facilityArr.size(); index++) {
                                Facility facility = facilityArr.get(index);
                                facilityNameList.add(facility.name);
                                if (mAuction != null && bEdit == true && facility.id == mAuction.facility_id) {
                                    selectedIndex = index;
                                }
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

                            if (selectedIndex != -1) {
                                facilitySpinner.setSelection(selectedIndex + 1);
                            }
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
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                startDateText.setText(sdf.format(calendar.getTime()));
            }
        };

        startDateText.setText(getNextDate(new Date()));
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
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                endDateText.setText(sdf.format(endCalendar.getTime()));
            }
        };

        endDateText.setText(getDateAfterWeek(new Date()));
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), endDateSetListener, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startTimeText.setText("12:00 PM");
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
                            startTimeText.setText(String.format("%d:%02d PM", selectedHour % 12, selectedMinute));
                        else
                            startTimeText.setText(String.format("%d:%02d AM", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endTimeText.setText("12:00 PM");
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
                            endTimeText.setText(String.format("%d:%02d PM", selectedHour % 12, selectedMinute));
                        else
                            endTimeText.setText(String.format("%d:%02d AM", selectedHour, selectedMinute));
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
        final EditText startingPrice = (EditText) view.findViewById(R.id.starting_price);
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
        creditSwitch.setChecked(false);
        moneyOrderSwitch.setChecked(false);
        checkSwitch.setChecked(false);
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
                if (mAuction != null && bEdit == true) {
                    AuctionRequest request = new AuctionRequest();
                    request.amount_owed = mAuction.amount_owed;
                    request.alt_unit_number = mAuction.alt_unit_number;
                    request.reserve = ServiceManager.fmt(mAuction.reserve);
                    request.terms = mAuction.terms;
                    request.lock_tag = mAuction.lock_tag;
                    request.fees = ServiceManager.fmt(mAuction.fees);
                    request.batch_email = String.valueOf(mAuction.batch_email);
                    request.time_st_zone = mAuction.time_st_zone;
                    request.time_end_zone = mAuction.time_end_zone;
                    request.facility_id = String.valueOf(mAuction.facility_id);
                    request.unit_size = String.format("%d", mAuction.unit_size);
                    request.descr = mAuction.descr;
                    request.unit_number = mAuction.unit_number;
                    request.tenant_name = mAuction.tenant_name;
                    request.auction_type = mAuction.type;
                    request.status = "6";
                    request.auct_id = String.format("%d", mAuction.auct_id);

                    if (mAuction.meta == null) {
                        request.cleanout = String.format("%d", mAuction.meta.cleanout);
                        request.cleanout_other = mAuction.meta.cleanout_other;
                        request.payment_other = mAuction.meta.payment_other;
                        request.payment = String.format("%d", mAuction.meta.payment);
                    }

                    String myFormat = "yyyy-M-d hh:mma"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                    request.time_start = sdf.format(mAuction.time_start).replace("AM", "am").replace("PM", "pm");
                    request.time_end = sdf.format(mAuction.time_end).replace("AM", "am").replace("PM", "pm");

                    ProgressView.sharedView().show();
                    ServiceManager.sharedManager().setAuction(request, new JsonHttpResponseHandler() {
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressView.sharedView().showToast("Cancel Auction Failed");
                                }
                            });
                        }
                    });
                }
                else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        if (bEdit != false && mAuction != null) {
            auctionTitle.setText(mAuction.title);
            realUnit.setText(mAuction.unit_number);
            altUnit.setText(mAuction.alt_unit_number);
            cleaningDeposit.setText(new DecimalFormat("##.##").format(mAuction.fees));
            startingPrice.setText(new DecimalFormat("##.##").format(mAuction.reserve));
            if (mAuction.status == 6 || mAuction.status == 7 || mAuction.status == 8) {
                cancelAuctionBtn.setEnabled(false);
            }

            startDateText.setText(getDateStr(mAuction.time_start, mAuction.time_st_zone));
            endDateText.setText(getDateStr(mAuction.time_end, mAuction.time_end_zone));
            startTimeText.setText(getTimeStr(mAuction.time_start, mAuction.time_st_zone));
            endTimeText.setText(getTimeStr(mAuction.time_end, mAuction.time_end_zone));

            for (int index = 0; index < timeZoneArr.size(); index++) {
                String timezone = timeZoneArr.get(index);
                if (mAuction.time_st_zone.contains(timezone)) {
                    timeZoneSpinner.setSelection(index);
                    break;
                }
            }

            if (mAuction.meta != null) {
                switch (mAuction.meta.cleanout) {
                    case 0:
                        cleanOutSpinner.setSelection(3);
                        otherCleanout.setEnabled(true);
                        break;
                    case 24:
                        cleanOutSpinner.setSelection(0);
                        otherCleanout.setEnabled(false);
                        break;
                    case 48:
                        cleanOutSpinner.setSelection(1);
                        otherCleanout.setEnabled(false);
                        break;
                    case 72:
                        cleanOutSpinner.setSelection(2);
                        otherCleanout.setEnabled(false);
                        break;
                    default:
                        break;
                }

                int payment = mAuction.meta.payment;
                if (payment % 2 == 1) { cashSwitch.setChecked(true); } payment = payment / 2;
                if (payment % 2 == 1) { creditSwitch.setChecked(true); } payment = payment / 2;
                if (payment % 2 == 1) { moneyOrderSwitch.setChecked(true); } payment = payment / 2;
                if (payment % 2 == 1) { checkSwitch.setChecked(true); } payment = payment / 2;

                if (mAuction.meta.payment_other != null && mAuction.meta.payment_other.compareTo("<null>") != 0) {
                    otherSwitch.setChecked(true);
                    otherPayment.setText(mAuction.meta.payment_other);
                }
            }

            tenantName.setText(mAuction.tenant_name);
            termsCond = mAuction.terms;

            if (mAuction.lock_tag != null && mAuction.lock_tag.compareTo("<null>") != 0)
                lockTagID.setText(mAuction.lock_tag);

            for (int index = 0; index < unitSizeArr.size(); index++) {
                String sqFeet = unitSizeArr.get(index);
                if (Integer.parseInt(sqFeet) == mAuction.unit_size) {
                    sqFeetSpinner.setSelection(index);
                    break;
                }
            }

            if (mAuction.meta != null) {
                accessSpinner.setSelection(mAuction.meta.access);
                for (int index = 0; index < cleanNumArr.size(); index++) {
                    int cleanNum = Integer.parseInt(cleanNumArr.get(index));
                    if (cleanNum == mAuction.meta.cleanout) {
                        cleanOutSpinner.setSelection(index);
                    }
                }
                if (mAuction.meta.cleanout_other != null && mAuction.meta.cleanout_other.compareTo("<null>") != 0) {
                    otherCleanout.setText(mAuction.meta.cleanout_other);
                }
            }
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressView.sharedView().show(getContext());

                AuctionRequest request = new AuctionRequest();
                request.amount_owed = "40";
                request.alt_unit_number = altUnit.getText().toString();
                request.terms = termsCond;
                request.prebid_close = "0";
                request.lock_tag = lockTagID.getText().toString();
                request.reserve = startingPrice.getText().toString();
                request.fees = cleaningDeposit.getText().toString();
                request.batch_email = "1";
                request.cleanout_other = cleanOutSpinner.getSelectedItemPosition() == cleanOutArr.size() - 1 ? otherCleanout.getText().toString() : "";
                request.payment = "15";
                request.time_st_zone = timeZoneArr.get(timeZoneSpinner.getSelectedItemPosition());
                request.time_end_zone = timeZoneArr.get(timeZoneSpinner.getSelectedItemPosition());
                request.cleanout = cleanNumArr.get(cleanOutSpinner.getSelectedItemPosition());
                // request.access = _access.text;
                request.access = Integer.toString(accessSpinner.getSelectedItemPosition());
                request.auction_type = "0";

                String startTimeStr = convertTimezone(startDateText.getText().toString() + " " + startTimeText.getText().toString(), request.time_st_zone);
                request.time_start = startTimeStr.toLowerCase();

                String endTimeStr = convertTimezone(endDateText.getText().toString() + " " + endTimeText.getText().toString(), request.time_st_zone);
                request.time_end = endTimeStr.toLowerCase();

                request.facility_id = facilitySpinner.getSelectedItemPosition() > 0 ? Integer.toString(facilityArr.get(facilitySpinner.getSelectedItemPosition() - 1).id) : "";
                request.unit_size = unitSizeArr.get(sqFeetSpinner.getSelectedItemPosition());
                request.descr = auctionTitle.getText().toString();
                request.unit_number = realUnit.getText().toString();

                request.save_terms = "0";
                request.payment_other = otherSwitch.isChecked() ? otherPayment.getText().toString() : "";
                request.tenant_name = tenantName.getText().toString();

                if (mAuction != null && bEdit == true) {
                    ServiceManager.sharedManager().setAuction(request, new JsonHttpResponseHandler() {
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
                } else {
                    ServiceManager.sharedManager().createAuction(request, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressView.sharedView().dismiss();
                                    AuctionDetailFragment fragment = new AuctionDetailFragment();

                                    if (fragment != null) {
                                        fragment.mAuction = getAuctionFromDic(response);
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
            }
        });
        setCustomFont(view);

        return view;
    }

    public void setCustomFont(View view) {
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView13), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView16), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView17), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView18), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView19), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView19_), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView20), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView21), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView22), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView23), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView24), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView26), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView27), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView28), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView29), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView30), R.font.raleway_regular);

        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView14), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.textView15), R.font.montserrat_medium);

        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.end_time_spinner), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.end_date_spinner), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.start_time_spinner), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.start_date_spinner), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.other_cleanout), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.tenant_name), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.lock_tag_id), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.starting_price), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.cleaning_deposit), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.alt_unit_count), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.real_unit_count), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (EditText) view.findViewById(R.id.auction_title), R.font.montserrat_medium);

        ServiceManager.sharedManager().setTypeFace(getContext(), (Switch) view.findViewById(R.id.cash_switch), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Switch) view.findViewById(R.id.credit_switch), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Switch) view.findViewById(R.id.money_order_switch), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Switch) view.findViewById(R.id.check_switch), R.font.raleway_regular);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Switch) view.findViewById(R.id.other_switch), R.font.raleway_regular);

        ServiceManager.sharedManager().setTypeFace(getContext(), (Button) view.findViewById(R.id.additional_information_btn), R.font.montserrat_semibold);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Button) view.findViewById(R.id.terms_condition_btn), R.font.montserrat_semibold);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Button) view.findViewById(R.id.bid_schedule_btn), R.font.montserrat_semibold);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Button) view.findViewById(R.id.cancel_auction_btn), R.font.montserrat_semibold);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Button) view.findViewById(R.id.save_btn), R.font.montserrat_semibold);
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

    public String getNextDate(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return format.format(calendar.getTime());
    }

    public String getDateAfterWeek(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 8);
        return format.format(calendar.getTime());
    }

    public String getDateStr(Date date, String timeZone) {
        final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        format.setTimeZone(tz);
        return format.format(date);
    }

    public String getTimeStr(Date date, String timeZone) {
        final SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        format.setTimeZone(tz);
        return format.format(date);
    }

    public String convertTimezone(String dateStr, String timeZone) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        oldFormat.setTimeZone(tz);
        try {
            Date date = oldFormat.parse(dateStr);
            SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            TimeZone utctz = TimeZone.getTimeZone("UTC");
            newFormat.setTimeZone(utctz);
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Auction getAuctionFromDic(JSONObject dic) {
        Auction auction = new Auction();
        try { auction.user_id = dic.getInt("user_id"); }catch (JSONException e) { e.printStackTrace(); }

        try { auction.status = dic.getInt("status"); }catch (JSONException e) { e.printStackTrace(); }


        try { auction.auct_id = dic.getInt("id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.facility_id = dic.getInt("facility_id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.auction_online_bid_id = dic.getInt("auction_online_bid_id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.failed_reason = dic.getString("failed_reason"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.descr = dic.getString("descr"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.time_start = getDateFrom(dic.getString("time_start")); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.time_st_zone = dic.getString("time_st_zone"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.time_end = getDateFrom(dic.getString("time_end")); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.time_end_zone = dic.getString("time_end_zone"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.amount_owed = dic.getString("amount_owed"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.fees = dic.getDouble("fees"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.tenant_name = dic.getString("tenant_name"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.unit_number = dic.getString("unit_number"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.unit_size = dic.getInt("unit_size"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.lock_tag = dic.getString("lock_tag"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.time_created = getDateFrom(dic.getString("time_created")); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.time_updated = getDateFrom(dic.getString("time_updated")); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.bid_count = dic.getInt("bid_count"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.winner_code = dic.getInt("winner_code"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.seller_fee = dic.getDouble("seller_fee"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.buyer_fee = dic.getDouble("buyer_fee"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.emailed = dic.getInt("emailed"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.terms = dic.getString("terms"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.queue_flag = dic.getInt("queue_flag"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.auction_id = dic.getString("auction_id"); }catch (JSONException e) { e.printStackTrace(); }
        auction.meta = new AuctionMeta();
        try {
            JSONObject metaDic = dic.getJSONObject("meta");
            if (metaDic.length() > 0) {
                try { auction.meta.cleanout = metaDic.getInt("cleanout"); }catch (JSONException e) { e.printStackTrace(); }
                try { auction.meta.cleanout_other = metaDic.getString("cleanout_other"); }catch (JSONException e) { e.printStackTrace(); }
                try { auction.meta.payment = metaDic.getInt("payment"); }catch (JSONException e) { e.printStackTrace(); }
                try { auction.meta.payment_other = metaDic.getString("payment_other"); }catch (JSONException e) { e.printStackTrace(); }
                try { auction.meta.access = metaDic.getInt("access"); }catch (JSONException e) { e.printStackTrace(); }
                try { auction.meta.currentBidPrice = metaDic.getDouble("ibid_current_price"); }catch (JSONException e) { e.printStackTrace(); }
                try {
                    JSONArray imageArr = metaDic.getJSONArray("ibid_images");
                    if (imageArr.length() > 0) {
                        auction.meta.images = new ArrayList<String>();
                        for (int j = 0; j < imageArr.length(); j++) {
                            try {
                                JSONObject imageInfo = imageArr.getJSONObject(j);
                                Iterator<String> keys = imageInfo.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    auction.meta.images.add(key);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }catch (JSONException e) { e.printStackTrace(); }

            } else {
                auction.meta = null;
            }
        }
        catch (JSONException e) { e.printStackTrace(); }

        try { auction.tax = dic.getDouble("tax"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.remote_id = dic.getString("remote_id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.source_id = dic.getString("source_id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.reserve = dic.getDouble("reserve"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.bid_schedule_id = dic.getInt("bid_schedule_id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.alt_unit_number = dic.getString("alt_unit_number"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.batch_email = dic.getInt("batch_email"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.title = dic.getString("title"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.facility_name = dic.getString("facility_name"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.address = dic.getString("address"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.city = dic.getString("city"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.state_code = dic.getString("state_code"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.amount = dic.getDouble("amount"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.bid_user_id = dic.getInt("bid_user_id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.media_auction_id = dic.getInt("media_auction_id"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.photo_path_size0 = dic.getString("photo_path_size0"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.photo_path_size1 = dic.getString("photo_path_size1"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.photo_path_size2 = dic.getString("photo_path_size2"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.photo_path_size3 = dic.getString("photo_path_size3"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.view_count = dic.getInt("view_count"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.os_date = dic.getString("os_date"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.os_time = dic.getString("os_time"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.url = dic.getString("url"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.group = dic.getString("group"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.type = dic.getString("type"); }catch (JSONException e) { e.printStackTrace(); }
        try { auction.category = dic.getString("category"); }catch (JSONException e) { e.printStackTrace(); }
        try {
            JSONArray mediaData = dic.getJSONArray("all_media");
            auction.mediaArray = new ArrayList<AuctionMedia>();
            for (int ind = 0; ind < mediaData.length(); ind++) {
                JSONObject data = mediaData.getJSONObject(ind);
                AuctionMedia media = new AuctionMedia();
                media.mediaID = data.getInt("id");
                media.type = data.getString("type");
                media.height0 = data.getString("height_0");
                media.height1 = data.getString("height_1");
                media.height2 = data.getString("height_2");
                media.height3 = data.getString("height_3");
                media.url0 = data.getString("photo_url_size0");
                media.url1 = data.getString("photo_url_size1");
                media.url2 = data.getString("photo_url_size2");
                media.url3 = data.getString("photo_url_size3");
                media.width0 = data.getString("width_0");
                media.width1 = data.getString("width_1");
                media.width2 = data.getString("width_2");
                media.width3 = data.getString("width_3");
                auction.mediaArray.add(media);
            }
        }catch (JSONException e) { e.printStackTrace(); }

        return auction;
    }
}
