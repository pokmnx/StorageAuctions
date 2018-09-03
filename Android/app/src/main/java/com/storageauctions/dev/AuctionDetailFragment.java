package com.storageauctions.dev;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.storageauctions.dev.ServiceManager.Auction;
import com.storageauctions.dev.ServiceManager.AuctionMedia;
import com.storageauctions.dev.ServiceManager.AuctionRequest;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AuctionDetailFragment extends Fragment {

    public Auction mAuction;
    public ArrayList imageIDArray = new ArrayList();
    public ArrayList imageArray = new ArrayList();
    public View mContainer;
    public ImageAdapter listAdapter;

    private int REQUEST_GALLERY = 1801, REQUEST_CAMERA = 1802;


    @Override
    public void onPause() {
        super.onPause();
        AuctionRequest request = new AuctionRequest();
        request.amount_owed = mAuction.amount_owed;
        request.alt_unit_number = mAuction.alt_unit_number;
        request.reserve = ServiceManager.fmt(mAuction.reserve);
        request.terms = mAuction.terms;
        request.lock_tag = mAuction.lock_tag;
        request.fees = ServiceManager.fmt(mAuction.fees);
        request.batch_email = String.format("%d", mAuction.batch_email);
        request.time_st_zone = mAuction.time_st_zone;
        request.time_end_zone = mAuction.time_end_zone;
        request.facility_id = String.format("%d", mAuction.facility_id);
        request.unit_size = String.format("%d", mAuction.unit_size);
        request.descr = mAuction.descr;
        request.unit_number = mAuction.unit_number;
        request.tenant_name = mAuction.tenant_name;
        request.auct_id = String.format("%d", mAuction.auct_id);

        if (mAuction.meta == null) {
            request.cleanout = String.format("%d", mAuction.meta.cleanout);
            request.cleanout_other = mAuction.meta.cleanout_other;
            request.payment_other = mAuction.meta.payment_other;
        }

        String myFormat = "yyyy-M-d hh:mma"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        TimeZone tz = TimeZone.getTimeZone(mAuction.time_st_zone);
        sdf.setTimeZone(tz);
        request.time_start = sdf.format(mAuction.time_start).replace("AM", "am").replace("PM", "pm");
        request.time_end = sdf.format(mAuction.time_end).replace("AM", "am").replace("PM", "pm");
        ServiceManager.sharedManager().setAuction(request, new JsonHttpResponseHandler() {

        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auction_detail, container, false);
        mContainer = view;
        setCustomFont(view);

        TextView auctionTitle = (TextView) view.findViewById(R.id.auction_title_text);
        TextView status = (TextView) view.findViewById(R.id.status);
        TextView startDate = (TextView) view.findViewById(R.id.start_date);
        TextView currentBid = (TextView) view.findViewById(R.id.current_bid);
        TextView endDate = (TextView) view.findViewById(R.id.end_date);

        auctionTitle.setText(mAuction.title);
        switch (mAuction.status) {
            case 0:
                status.setText("INACTIVE");
                break;
            case 1:
                status.setText("ACTIVE");
                break;
            case 2:
                status.setText("CLOSED");
                break;
            case 3:
                status.setText("PAID");
                break;
            case 4:
                status.setText("UNSOLD");
                break;
            case 5:
                status.setText("FAILED PAYMENT");
                break;
            case 6:
                status.setText("CANCELED");
                break;
            case 7:
                status.setText("CANCELED");
                break;
            case 8:
                status.setText("CANCELED");
                break;
            case 9:
                status.setText("FAILED FINAL");
                break;
            case 10:
                status.setText("PENDING");
                break;
            case 11:
                status.setText("ONSITE");
                break;
            case 88:
                status.setText("UPCOMING");
                break;
            default:
                status.setText("PROCESSING");
                break;
        }

        if (mAuction.meta != null && mAuction.meta.currentBidPrice != 0) {
            currentBid.setText("$" + String.format("%1$,.2f", mAuction.meta.currentBidPrice));
        } else {
            currentBid.setText("$0");
        }

        String myFormat = "M/d/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        startDate.setText(sdf.format(mAuction.time_start));
        endDate.setText(sdf.format(mAuction.time_end));

        Button editButton = (Button) view.findViewById(R.id.edit_detail_button);
        Button uploadButton = (Button) view.findViewById(R.id.upload_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAuctionFragment fragment = new CreateAuctionFragment();
                if (fragment != null) {
                    fragment.mAuction = mAuction;
                    fragment.bEdit = true;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        GridView imageGridView = (GridView) view.findViewById(R.id.image_grid_view);
        initImageArr();
        listAdapter = new ImageAdapter(getContext(), mAuction);
        imageGridView.setAdapter(listAdapter);

        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= imageArray.size()) {
                    showPictureDialog();
                }
            }
        });

        return view;
    }

    public void initImageArr() {
        if (mAuction.mediaArray == null) {
            setFirstImage();
            return;
        }

        for (int index = 0; index < mAuction.mediaArray.size(); index++) {
            AuctionMedia media = mAuction.mediaArray.get(index);
            if (media.url0 != null && media.url0.length() > 0)
                imageArray.add(media.url0);
            else if (media.url1 != null && media.url1.length() > 0)
                imageArray.add(media.url1);
            else if (media.url2 != null && media.url2.length() > 0)
                imageArray.add(media.url2);
            else if (media.url3 != null && media.url3.length() > 0)
                imageArray.add(media.url3);

            imageIDArray.add(String.format("%d", media.mediaID));
        }
        setFirstImage();
    }

    public void setFirstImage() {
        ImageView thumbView = (ImageView) mContainer.findViewById(R.id.default_image_view);
        if (imageArray.size() > 0) {
            String path = (String) imageArray.get(0);
            Picasso.get().load(path).into(thumbView);
        } else {
            Picasso.get().load(R.drawable.auction_background).into(thumbView);
        }
    }

    public void setCustomFont(View view) {
        ServiceManager.sharedManager().setTypeFace(getContext(), (Button) view.findViewById(R.id.upload_button), R.font.montserrat_semibold);
        ServiceManager.sharedManager().setTypeFace(getContext(), (Button) view.findViewById(R.id.edit_detail_button), R.font.montserrat_semibold);

        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.auction_title_text), R.font.raleway_medium);

        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.status_title), R.font.montserrat_extralight);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.start_date_title), R.font.montserrat_extralight);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.current_bid_title), R.font.montserrat_extralight);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.end_date_title), R.font.montserrat_extralight);

        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.status), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.start_date), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.current_bid), R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) view.findViewById(R.id.end_date), R.font.montserrat_medium);
    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private Auction mAuction;
        private LayoutInflater inflater;

        public ImageAdapter(Context context, Auction auction) {
            super();
            mContext = context;
            mAuction = auction;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (imageArray.size() > 6)
                return imageArray.size();
            else
                return 6;
        }

        @Override
        public Object getItem(int position) {
            if (position < imageArray.size())
                return imageArray.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_auction_image, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.auction_image);
                viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.delete_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position < imageArray.size()) {
                String path = (String) imageArray.get(position);
                Picasso.get().load(path).resize(200, 200).into(viewHolder.imageView);
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProgressView.sharedView().show();
                        ServiceManager.sharedManager().deleteImage(mAuction.auct_id, String.valueOf(imageIDArray.get(position)), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressView.sharedView().dismiss();
                                        imageArray.remove(position);
                                        imageIDArray.remove(position);
                                        listAdapter.notifyDataSetChanged();
                                        setFirstImage();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressView.sharedView().showToast("Delete Image Failed");
                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                viewHolder.deleteButton.setVisibility(View.INVISIBLE);
                if (imageArray.size() == 0 && position == 0)
                    Picasso.get().load(R.drawable.auction_background).resize(200, 200).into(viewHolder.imageView);
                else
                    Picasso.get().load(R.drawable.default_placeholder_2).resize(200, 200).into(viewHolder.imageView);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            ImageButton deleteButton;
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    public void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Upload Photos");
        String[] pictureDialogItems = {
                "Choose a Photo from Gallery",
                "Take a Photo from Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                // Do something with imagePath

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // imageview.setImageBitmap(photo);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri selectedImage = getImageUri(getActivity(), photo);
                String realPath=getRealPathFromURI(selectedImage);
                addNewImage(realPath, photo);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                cursor.close();
                addNewImage(imagePath, bitmap);
            }
        }
    }

    public void addNewImage(final String filePath, Bitmap bitmap) {
        ProgressView.sharedView().show();
        ServiceManager.sharedManager().setImage(getContext(), mAuction.auct_id, filePath, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressView.sharedView().showToast("Uploading Image Failed");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONObject data = new JSONObject(jsonData);
                    if (response.isSuccessful()) {
                        if (mAuction.mediaArray == null)
                            mAuction.mediaArray = new ArrayList<AuctionMedia>();

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

                        if (media.url0 != null && media.url0.length() > 0)
                            imageArray.add(media.url0);
                        else if (media.url1 != null && media.url1.length() > 0)
                            imageArray.add(media.url1);
                        else if (media.url2 != null && media.url2.length() > 0)
                            imageArray.add(media.url2);
                        else if (media.url3 != null && media.url3.length() > 0)
                            imageArray.add(media.url3);

                        mAuction.mediaArray.add(media);
                        imageIDArray.add(media.mediaID);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressView.sharedView().dismiss();
                                setFirstImage();
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        JSONArray errArr = data.getJSONObject("errors").getJSONArray("media");
                        String err = "";
                        for (int index = 0; index < errArr.length(); index++) {
                            String msg = errArr.getString(index);
                            err += msg + " ";
                        }
                        final String errMsg = err;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressView.sharedView().showToast(errMsg);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressView.sharedView().showToast("Uploading Image Failed");
                        }
                    });
                }
            }
        });
    }

    public Bitmap getImageFromUri(Uri contentUri) {
        String filePath = getRealPathFromURI(contentUri);
        String[] filePathArr = new String[1];
        filePathArr[0] = filePath;
        Cursor cursor = getActivity().getContentResolver().query(contentUri, filePathArr, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        cursor.close();
        return bitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
