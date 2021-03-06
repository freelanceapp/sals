package com.creativeshare.sals.activities_fragments.secdule.fragments.fragment_shipping_detials;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.creativeshare.sals.activities_fragments.secdule.activity.Scedule_Activity;
import com.creativeshare.sals.adapter.Spinner_City_Adapter;
import com.creativeshare.sals.R;
import com.creativeshare.sals.adapter.Spinner_Country_Adapter;
import com.creativeshare.sals.models.Country_Model;
import com.creativeshare.sals.models.Move_Data_Model;
import com.creativeshare.sals.share.Common;
import com.creativeshare.sals.models.CityModel;
import com.creativeshare.sals.models.Computrized_Model;
import com.creativeshare.sals.models.Dementions_Model;
import com.creativeshare.sals.models.Quote_Array_Model;
import com.creativeshare.sals.models.Quote_Model;
import com.creativeshare.sals.models.UserModel;
import com.creativeshare.sals.preferences.Preferences;
import com.creativeshare.sals.remote.Api;
import com.creativeshare.sals.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Shipping_Detials extends Fragment {
    private Scedule_Activity activity;
    private Preferences preferences;
    private UserModel userModel;
    private String current_lang;
    private Button next, bt_Shipping_dimensions;
    private ImageButton bt_incremental, bt_decremantal;
    private FrameLayout fr_document, fr_parcel;
    private ImageView im_document, im_parcel, im_map;
    private TextView tv_document, tv_parcel, tv_Quantity;
    private EditText edt_desc, edt_weight, edt_name, edt_phone, edt_address, edt_email;
    private List<CityModel.postal_codes> cityModelList;
    private Spinner spinner_city_from, spinner_country_from, spinner_city_to, spinner_country_to;
    private Spinner_City_Adapter city_adapter;
    private int quantity = 1;
    private List<String> wegights, widths, hights, lengths, volumeweights;
    private String is_dutiable = "0", ready_time_gmt_offset = "+00:00", dimension_unit = "CM", weight_unit = "KG", payment_country_code, cityf, to_city, to_country_code, from_country, to_country, from_country_code;


    private String parcel = "0";
    private List<CityModel.postal_codes> citiesList;
    private Spinner_City_Adapter city_adapter2;
    private Spinner_Country_Adapter country_adapter;
    private List<Country_Model.Countries> countriesList;

    public static Fragment_Shipping_Detials newInstance() {
        return new Fragment_Shipping_Detials();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shipping_details, container, false);
        initView(view);
        setto();
        setfr();
       // getCountry();
        return view;
    }

    private void setto() {
        to_country_code = "SA";
        to_country = "Saudi Arabia";
        if (current_lang.equals("en")) {
            Move_Data_Model.setCountryt("Saudi Arabia");

        } else {
            Move_Data_Model.setCountryt("المملكة العربية السعوديه");

        }
        Move_Data_Model.setTocountrycode("SA");
        Move_Data_Model.settophonecode("966");
        getCities(to_country_code, 2);
    }

    private void setfr() {
        payment_country_code ="SA";
        from_country = "Saudi Arabia";
        from_country_code = "SA";
        if (current_lang.equals("en")) {
            Move_Data_Model.setcountryf("Saudi Arabia");
            ;
        } else {
            Move_Data_Model.setcountryf("المملكة العربية السعوديه");

        }
        Move_Data_Model.setcode("SA");

        getCities(from_country_code, 1);
    }
    private void initView(View view) {
        countriesList = new ArrayList<>();
        cityModelList = new ArrayList<>();
        citiesList=new ArrayList<>();
        activity = (Scedule_Activity) getActivity();
        wegights = new ArrayList<>();
        lengths = new ArrayList<>();
        widths = new ArrayList<>();
        hights = new ArrayList<>();
        volumeweights = new ArrayList<>();
        Paper.init(activity);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        bt_incremental = view.findViewById(R.id.increment);
        bt_decremantal = view.findViewById(R.id.decrement);
        tv_Quantity = view.findViewById(R.id.tv_quantity);
        fr_document = view.findViewById(R.id.fr_document);
        fr_parcel = view.findViewById(R.id.fr_parcel);
        im_map = view.findViewById(R.id.im_map);
        im_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.DisplayFragmentMap();
            }
        });
        im_document = view.findViewById(R.id.im_document);
        im_parcel = view.findViewById(R.id.im_parcel);
        tv_document = view.findViewById(R.id.tv_document);
        tv_parcel = view.findViewById(R.id.tv_parcel);
        edt_desc = view.findViewById(R.id.edt_desc);
        edt_name = view.findViewById(R.id.edt_name);
        edt_phone = view.findViewById(R.id.edt_phone);
        edt_address = view.findViewById(R.id.edt_address);
        edt_weight = view.findViewById(R.id.edt_weight);
        edt_email = view.findViewById(R.id.edt_email);
        bt_Shipping_dimensions = view.findViewById(R.id.bt_shipping_dimensions);
        next = view.findViewById(R.id.bt_next);
        spinner_city_to = view.findViewById(R.id.sp_cityto);
        spinner_city_from = view.findViewById(R.id.sp_cityfrom);
        spinner_country_from = view.findViewById(R.id.sp_countryfrom);
        spinner_country_to = view.findViewById(R.id.sp_countryto);
        city_adapter = new Spinner_City_Adapter(activity, cityModelList);
        city_adapter2 = new Spinner_City_Adapter(activity, citiesList);
        country_adapter = new Spinner_Country_Adapter(activity, countriesList);
        spinner_country_from.setAdapter(country_adapter);
        spinner_country_to.setAdapter(country_adapter);
        spinner_city_to.setAdapter(city_adapter2);
        spinner_city_from.setAdapter(city_adapter);
        spinner_country_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    from_country = "";
                    from_country_code = "";
                } else {
                    payment_country_code = countriesList.get(position).getIso_two();
                    from_country = countriesList.get(position).getEn_name();
                    from_country_code = countriesList.get(position).getIso_two();
                    if (current_lang.equals("en")) {
                        Move_Data_Model.setcountryf(countriesList.get(position).getEn_name());
                        ;
                    } else {
                        Move_Data_Model.setcountryf(countriesList.get(position).getAr_name());

                    }
                    Move_Data_Model.setcode(countriesList.get(position).getIso_two());

                    getCities(countriesList.get(position).getIso_two(), 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_country_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    to_country = "";
                    to_country_code = "";

                } else {
                    to_country_code = countriesList.get(position).getIso_two();
                    to_country = countriesList.get(position).getEn_name();
                    if (current_lang.equals("en")) {
                        Move_Data_Model.setCountryt(countriesList.get(position).getEn_name());

                    } else {
                        Move_Data_Model.setCountryt(countriesList.get(position).getAr_name());

                    }
                    Move_Data_Model.setTocountrycode(countriesList.get(position).getIso_two());
                    Move_Data_Model.settophonecode(countriesList.get(position).getPhone_code());
                    getCities(countriesList.get(position).getIso_two(), 2);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    cityf = "";
                } else {
                    cityf = cityModelList.get(position).getCity();
                    // Move_Data_Model.setcityt(to_city);

                    Move_Data_Model.setpostalf(cityModelList.get(position).getPostal_code());
                    if (cityModelList.get(position).getPostal_code() == null || cityModelList.get(position).getPostal_code().isEmpty()) {
                        Move_Data_Model.setpostalf("21589");
                    }
                    if (current_lang.equals("en")) {
                        Move_Data_Model.setCityf(cityModelList.get(position).getCity());

                    } else {
                        Move_Data_Model.setCityf(cityModelList.get(position).getCity());

                    }
                    Move_Data_Model.setcityfe(cityModelList.get(position).getCity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    to_city = "";
                } else {
                    to_city = citiesList.get(position).getCity();
                    // Move_Data_Model.setcityt(to_city);
                    Move_Data_Model.setpostalt(citiesList.get(position).getPostal_code());
                    if (citiesList.get(position).getPostal_code() == null || citiesList.get(position).getPostal_code().isEmpty()) {
                        Move_Data_Model.setpostalt("11543");
                    }
                    if (current_lang.equals("en")) {
                        Move_Data_Model.setcityt(citiesList.get(position).getCity());
                    } else {
                        Move_Data_Model.setcityt(citiesList.get(position).getCity());

                    }
                    Move_Data_Model.setCityte(citiesList.get(position).getCity());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bt_incremental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity += 1;
                tv_Quantity.setText(quantity + "");
            }
        });
        bt_decremantal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 1) {
                    quantity -= 1;
                    tv_Quantity.setText(quantity + "");
                }
            }
        });
        fr_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parcel = "0";
                fr_document.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                fr_parcel.setBackgroundColor(getResources().getColor(R.color.white));
                im_document.setColorFilter(getResources().getColor(R.color.white));
                im_parcel.setColorFilter(getResources().getColor(R.color.colorPrimary));
                tv_document.setTextColor(getResources().getColor(R.color.white));
                tv_parcel.setTextColor(getResources().getColor(R.color.colorPrimary));
                bt_Shipping_dimensions.setVisibility(View.GONE);
                edt_desc.setVisibility(View.GONE);
            }
        });
        fr_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parcel = "1";

                fr_document.setBackgroundColor(getResources().getColor(R.color.white));
                fr_parcel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                im_document.setColorFilter(getResources().getColor(R.color.colorPrimary));
                im_parcel.setColorFilter(getResources().getColor(R.color.white));
                tv_document.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_parcel.setTextColor(getResources().getColor(R.color.white));
                bt_Shipping_dimensions.setVisibility(View.VISIBLE);
                edt_desc.setVisibility(View.GONE);
            }
        });
        bt_Shipping_dimensions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.DisplayFragmentShippingDimentions();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkdata();
                // activity.DisplayFragmentdelivrychooser();
            }
        });

    }

  /*  private void getCities() {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .getCity("Bearer" + " " + userModel.getToken(), current_lang)
                .enqueue(new Callback<CityModel>() {
                    @Override
                    public void onResponse(Call<CityModel> call, Response<CityModel> response) {
                        dialog.dismiss();

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                cityModelList.clear();
                                cityModelList.add(new CityModel.Cities("إختر المدينه", "Choose city"));
                                cityModelList.addAll(response.body().getCities());
                                city_adapter.notifyDataSetChanged();

                            }
                        } else {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CityModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });

    }*/

    private void checkdata() {
        Common.CloseKeyBoard(activity, edt_weight);

        if (widths.size() != 0 || parcel.equals("0")) {
            String weight = edt_weight.getText().toString();
            String name = edt_name.getText().toString();
            String phone = edt_phone.getText().toString();
            String address = edt_address.getText().toString();
            String email = edt_email.getText().toString();
            if (TextUtils.isEmpty(to_country) || TextUtils.isEmpty(from_country) || TextUtils.isEmpty(to_city) || TextUtils.isEmpty(cityf) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) || TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                if (TextUtils.isEmpty(weight)) {
                    edt_weight.setError(getResources().getString(R.string.field_req));
                }
                if (TextUtils.isEmpty(to_city) || TextUtils.isEmpty(cityf)) {
                    Common.CreateSignAlertDialog(activity, getResources().getString(R.string.add_city));
                }
                if (TextUtils.isEmpty(to_country) || TextUtils.isEmpty(from_country)) {
                    Common.CreateSignAlertDialog(activity, getResources().getString(R.string.Add_Country));

                }
                if (TextUtils.isEmpty(name)) {
                    edt_name.setError(getResources().getString(R.string.field_req));
                }
                if (TextUtils.isEmpty(phone)) {
                    edt_phone.setError(getResources().getString(R.string.field_req));
                }
                if (TextUtils.isEmpty(address)) {
                    edt_address.setError(getResources().getString(R.string.field_req));
                }
                if (TextUtils.isEmpty(email)) {
                    edt_email.setError(getResources().getString(R.string.field_req));
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edt_email.setError(getResources().getString(R.string.error_Email));
                }

            } else {
                List<String> wegights = new ArrayList<>();
                edt_weight.setError(null);
                //tv_date.setError(null);
                //tv_time.setError(null);
                int loop;
                if (parcel.equals("1")) {
                    if (quantity > widths.size()) {
                        loop = quantity;
                    } else {
                        loop = widths.size();
                    }
                } else {
                    loop = quantity;
                }
                if (parcel.equals("0")) {
                    wegights.clear();
                    widths.clear();
                    hights.clear();
                    lengths.clear();
                    volumeweights.clear();
                }
                for (int i = 0; i < loop; i++) {
                    wegights.add(weight);

                    if (parcel.equals("0")) {

                        widths.add("0");
                        hights.add("0");
                        lengths.add("0");
                        volumeweights.add("0");
                    }
                }
                Log.e("width", widths.size() + "");
                //  Computrized_Model.setQuantity(quantity+"");
                //  Computrized_Model.setWeight(weight);
                Move_Data_Model.setWegights(wegights);
                if (parcel.equals("1")) {
                    if (quantity > widths.size()) {
                        int ind = widths.size() - 1;
                        for (int i = widths.size(); i < quantity; i++) {

                            widths.add(widths.get(ind) + "");
                            hights.add(hights.get(ind) + "");
                            lengths.add(lengths.get(ind) + "");
                            volumeweights.add(volumeweights.get(ind) + "");

                        }
                    }


                }
                Move_Data_Model.setWidths(widths);
                Move_Data_Model.setHights(hights);
                Move_Data_Model.setLengths(lengths);
                Move_Data_Model.setVolumeweights(volumeweights);
                Move_Data_Model.setParcel(parcel);
                Move_Data_Model.setemailt(email);
                Move_Data_Model.setName(name);
                // Move_Data_Model.setcityt(to_city);
                Move_Data_Model.setPhone(phone);
                address = address.replaceAll("،", "");
                address = address.replaceAll("\\s(\\d)", "");
                address = address.replaceAll("(\\d)\\s", "");
                address = address.replaceAll(",", "");
                if (address.length() > 23) {
                    address = address.substring(0, 22);
                }
                Move_Data_Model.setAdddresst(address);
                getQoute(wegights);

            }
        } else {
            Common.CreateSignAlertDialog(activity, getResources().getString(R.string.demintion_for_eacj_piece));
        }
    }

    private void getQoute(final List<String> wegights) {
        //  Log.e("data", Move_Data_Model.getDate()+wegights+is_dutiable+Move_Data_Model.getTime()+ready_time_gmt_offset+dimension_unit+weight_unit+payment_country_code+Move_Data_Model.getFromcountrycode()+Move_Data_Model.getCityf()+to_city+to_country_code);
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService().get_quote("Bearer" + " " + userModel.getToken(), Move_Data_Model.getDate(), wegights, is_dutiable, Move_Data_Model.getTime(), ready_time_gmt_offset, dimension_unit, weight_unit, payment_country_code, Move_Data_Model.getFromcountrycode(), cityf, to_city, to_country_code).enqueue(new Callback<Quote_Model>() {
            @Override
            public void onResponse(Call<Quote_Model> call, Response<Quote_Model> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    //     assert response.body() != null;
                    //  Log.e("price",response.body().getData().getGetQuoteResponse().getBkgDetails().getQtdShp().getWeightCharge());
                    //  activity.DisplayFragmentComputrizedprice();
                  /*  if (response.body() != null) {
                        Log.e("ss",response.body().toString()+response.raw()+response.headers()+response.body().getData().getGetQuoteResponse().getBkgDetails());
                    }*/
                    if (response.body().getData().getGetQuoteResponse().getBkgDetails() == null) {
//Log.e("res",response.body().getData().getResponse().getStatus().getCondition().getConditionData());
                    } else {
                        adddata(response.body());
                    }
                } else {
                    try {
                        Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                        Log.e("Error_code", response.code() + "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Quote_Model> call, Throwable t) {
                try {
                    dialog.dismiss();
                    getQoute2(wegights);
                    Log.e("Error", t.getMessage());
                } catch (Exception e) {

                }
            }
        });
    }

    private void getQoute2(List<String> wegights) {
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService().get_quote2("Bearer" + " " + userModel.getToken(), Move_Data_Model.getDate(), wegights, is_dutiable, Move_Data_Model.getTime(), ready_time_gmt_offset, dimension_unit, weight_unit, payment_country_code, Move_Data_Model.getFromcountrycode(), cityf, to_city, to_country_code).enqueue(new Callback<Quote_Array_Model>() {
            @Override
            public void onResponse(Call<Quote_Array_Model> call, Response<Quote_Array_Model> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    //     assert response.body() != null;
                    //  Log.e("price",response.body().getData().getGetQuoteResponse().getBkgDetails().getQtdShp().getWeightCharge());
                    //  activity.DisplayFragmentComputrizedprice();
                    if (response.body().getData().getGetQuoteResponse().getBkgDetails() == null) {
//Log.e("res",response.body().getData().getResponse().getStatus().getCondition().getConditionData());
                    } else {
                        adddata2(response.body());
                    }

                } else {
                    try {
                        Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                        Log.e("Error_code", response.code() + "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Quote_Array_Model> call, Throwable t) {
                try {
                    dialog.dismiss();

                    //Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error", t.getMessage());
                } catch (Exception e) {

                }
            }
        });
    }

    public void adddeminssion(Dementions_Model dementions_model) {
        // Log.e("de",dementions_model.getGrossweight()+"");
        for (int i = 0; i < quantity; i++) {

            widths.add(dementions_model.getWidth() + "");
            hights.add(dementions_model.getHight() + "");
            lengths.add(dementions_model.getLength() + "");
            volumeweights.add(dementions_model.getVoulumeweight() + "");
        }


    }

    private void adddata(Quote_Model body) {
        Move_Data_Model.setPrice(body.getData().getGetQuoteResponse().getBkgDetails().getQtdShp().getWeightCharge());
        Move_Data_Model.setDay_number(body.getData().getGetQuoteResponse().getBkgDetails().getQtdShp().getTotalTransitDays());

        Computrized_Model.setTime(body.getData().getGetQuoteResponse().getBkgDetails().getQtdShp().getDeliveryTime());
        activity.DisplayFragmentdelivrychooser();

    }

    private void adddata2(Quote_Array_Model body) {
        double total = 0;
        int totalday = 0;
        for (int i = 0; i < body.getData().getGetQuoteResponse().getBkgDetails().getQtdShp().size(); i++) {
            total += Double.parseDouble(body.getData().getGetQuoteResponse().getBkgDetails().getQtdShp().get(i).getWeightCharge());
            totalday += Integer.parseInt(body.getData().getGetQuoteResponse().getBkgDetails().getQtdShp().get(i).getTotalTransitDays());
        }
        Move_Data_Model.setPrice(total + "");
        Move_Data_Model.setDay_number(totalday + "");

        Computrized_Model.setTime(body.getData().getGetQuoteResponse().getBkgDetails().getQtdShp().get(0).getDeliveryTime());
        activity.DisplayFragmentdelivrychooser();

    }

    public void setaddressto(String address) {
        edt_address.setText(address);
    }

    private void getCities(String iso_two, final int type) {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .getCity("Bearer" + " " + userModel.getToken(), iso_two)
                .enqueue(new Callback<CityModel>() {
                    @Override
                    public void onResponse(Call<CityModel> call, Response<CityModel> response) {
                        dialog.dismiss();

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (type == 1) {
                                    cityModelList.clear();
                                    cityModelList.add(new CityModel.postal_codes("Choose city"));
                                    cityModelList.addAll(response.body().getPostal_codes());
                                    city_adapter.notifyDataSetChanged();
                                } else {
                                    citiesList.clear();
                                    citiesList.add(new CityModel.postal_codes("Choose city"));
                                    citiesList.addAll(response.body().getPostal_codes());
                                    city_adapter2.notifyDataSetChanged();
                                }

                            }
                        } else {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CityModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void getCountry() {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .getCoutry("Bearer" + " " + userModel.getToken(), current_lang)
                .enqueue(new Callback<Country_Model>() {
                    @Override
                    public void onResponse(Call<Country_Model> call, Response<Country_Model> response) {
                        dialog.dismiss();

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                countriesList.clear();
                                countriesList.add(new Country_Model.Countries("Choose", "إختر"));
                                countriesList.addAll(response.body().getCountries());
                                country_adapter.notifyDataSetChanged();

                            }
                        } else {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Country_Model> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });

    }
}
