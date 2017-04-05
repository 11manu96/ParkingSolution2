package com.android.parking.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.parking.Activity.ParkingDetailActivity;
import com.android.parking.R;
import com.google.android.gms.vision.text.Text;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by manumaheshwari on 7/18/16.
 */
public class ParkingListCustomAdapter extends BaseAdapter {

    String [] parkingNameList;
    String [] parkingImageList;
    String[] parkingVicinityList;
    String[] parkingDistanceList;
    Context context;
    private static LayoutInflater inflater=null;


    public ParkingListCustomAdapter(Context context, String[] parkingLotName, String[] parkingLotPicture, String[] parkingLotVicinity, String[] parkingLotDistance) {
        // TODO Auto-generated constructor stub

        this.parkingNameList = parkingLotName;
        this.parkingDistanceList = parkingLotDistance;
        this.parkingVicinityList = parkingLotVicinity;
        this.parkingImageList = parkingLotPicture;
        this.context = context;

        Log.d("yoo", String.valueOf(parkingLotName.length));

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class Holder
    {
        TextView nameTextView;
        TextView vicinityTextView;
        TextView distanceTextView;
        ImageView Parkingicon;
    }

    @Override
    public int getCount() {
        return parkingNameList.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.parking_list_item_layout, null);
        holder.Parkingicon=(ImageView) rowView.findViewById(R.id.place_picture);
        holder.nameTextView=(TextView) rowView.findViewById(R.id.place_name_textview);
        holder.vicinityTextView = (TextView) rowView.findViewById(R.id.vicinity);
        holder.distanceTextView = (TextView) rowView.findViewById(R.id.distance_textview);

        new LoadImage(holder.Parkingicon).execute(parkingImageList[position]);
        holder.distanceTextView.setText(parkingDistanceList[position] + " Km" );
        holder.vicinityTextView.setText(parkingVicinityList[position]);
        holder.nameTextView.setText(parkingNameList[position]);


        /*rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ParkingDetailActivity.class);
                intent.putExtra("image", parkingImageList[position]);
                intent.putExtra("name", parkingNameList[position]);
                intent.putExtra("vicinity", parkingVicinityList[position]);
                context.startActivity(intent);
            }
        });
*/
        return rowView;
    }


    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;
        ImageView img;

        public LoadImage(ImageView imageView){
            this.img = imageView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                img.setImageBitmap(image);

            }else{

                Toast.makeText(context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
