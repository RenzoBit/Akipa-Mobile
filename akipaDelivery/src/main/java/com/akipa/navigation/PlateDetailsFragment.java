package com.akipa.navigation;

import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akipa.akipadelivery.R;
import com.akipa.listview.utils.ReusableClass;
import com.akipa.navigation.utils.NoSSLv3Factory;

public class PlateDetailsFragment extends Fragment {

	Activity con;
	int plateId = 0;
	private ProgressDialog pgLogin;
	TextView price;
	TextView desc;
	ImageView fullImage;
	LinearLayout commentsMain;


	public PlateDetailsFragment(Activity mContext, int Id) {
		con = mContext;
		plateId = Id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_details_plate, container, false);
		price = (TextView) rootView.findViewById(R.id.price_details);
		desc = (TextView) rootView.findViewById(R.id.desc);
		fullImage = (ImageView) rootView.findViewById(R.id.iv_plate);
		commentsMain = (LinearLayout) rootView.findViewById(R.id.comments);

		pgLogin = new ProgressDialog(getActivity());
		pgLogin.setMessage("Please wait loading plate details ...");
		pgLogin.setIndeterminate(true);
		pgLogin.setCancelable(true);
		pgLogin.setCanceledOnTouchOutside(false);

		pgLogin.show();

		new MyAsyncTaskForPlateDetails().execute(plateId + "");

		return rootView;
	}

	//===================================================================================================================================
	//Get Plate Details
	//===================================================================================================================================

	private class MyAsyncTaskForPlateDetails extends AsyncTask<String, Integer, Double> {

		String responseBody;
		int responseCode;

		@Override
		protected Double doInBackground(String... params) {
			postData(params[0]);
			return null;
		}

		protected void onPostExecute(Double result) {
			//The HTTP status messages in the 200 series reflect that the request was successful. 
			if (responseCode == 200) {
				Log.d("Log", responseBody);
				processCategoryResponce(responseBody);
			}
			//Not getting proper response
			else {
				if (pgLogin.isShowing()) {
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText(getActivity(), "Sorry!! No responce from server.", Toast.LENGTH_LONG).show();
			}

		}

		protected void onProgressUpdate(Integer... progress) {

		}

		public void postData(String plateId) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(ReusableClass.base_url + "obtener_plato?idplato=" + plateId);

			try {
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httpGet);

				responseCode = response.getStatusLine().getStatusCode();
				responseBody = EntityUtils.toString(response.getEntity());
			} catch (Throwable t) {
				Log.d("Error Time of Login", t + "");
			}
		}
	}

	//===================================================================================================================================
	//END Plate Details
	//===================================================================================================================================

	//===================================================================================================================================
	//processing the XML got from server 
	//===================================================================================================================================
	private void processCategoryResponce(String responceFromServer) {

		/*{"idplato":"7","nombre":"Tiradito a la Crema de Rocoto","descripcion":"L\u00e1minas de pesca del d\u00eda en jugo de cebiche, salsa de rocoto con camote y choclo",
		 * "precio":"27.00","idcategoria":"2","delivery":"1","relevancia":"0","imagen":"0","categoria":"Para Empezar"}		 
		 */

		try {
			JSONObject obj = new JSONObject(responceFromServer);

			String idplato = obj.getString("idplato");
			String nombre = obj.getString("nombre");
			String descripcion = obj.getString("descripcion");
			String precio = obj.getString("precio");
			String idcategoria = obj.getString("idcategoria");
			String delivery = obj.getString("delivery");
			String relevancia = obj.getString("relevancia");
			String imagen = obj.getString("imagen");
			String categoria = obj.getString("categoria");

			price.setText("Precio: S/. " + precio);
			desc.setText(descripcion);
			if (Integer.parseInt(imagen) != 0)
				new DownloadImageTask(fullImage).execute(ReusableClass.asset_url + imagen + ".jpg");

			JSONArray comentarios = obj.getJSONArray("comentarios");
			for (int i = 0; i < comentarios.length(); i++) {
				JSONObject obj1 = comentarios.getJSONObject(i);

				String calificacion = obj1.getString("calificacion");
				String comentario = obj1.getString("comentario");
				String image_url = obj1.getString("image");

				Log.v("TAG", "comment : " + comentario);
				Log.v("TAG", "rating : " + calificacion);
				Log.v("TAG", "image url : " + image_url);

				RelativeLayout comment = new RelativeLayout(con);
				;
				RelativeLayout.LayoutParams parm = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				comment.setLayoutParams(parm);

				ImageView userImage = new ImageView(con);
				userImage.setPadding(0, 0, 5, 0);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
				userImage.setLayoutParams(params);
				userImage.setId(100 + i);
				comment.addView(userImage);

				TextView valueTV = new TextView(con);
				valueTV.setTextColor(Color.WHITE);
				valueTV.setId(200 + i);
				valueTV.setText(comentario);
				RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				par.addRule(RelativeLayout.RIGHT_OF, 100 + i);
				valueTV.setLayoutParams(par);
				comment.addView(valueTV);

				RatingBar rb = new RatingBar(con, null, android.R.attr.ratingBarStyleSmall);
				RelativeLayout.LayoutParams pars = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				pars.addRule(RelativeLayout.RIGHT_OF, 100 + i);
				pars.addRule(RelativeLayout.BELOW, 200 + i);
				rb.setLayoutParams(pars);
				rb.setMax(5);
				rb.setPadding(0, 0, 0, 20);
				rb.setRating(Float.parseFloat(calificacion));
				comment.addView(rb);

				if (image_url.length() >= 0)
					new DownloadImageTask(userImage).execute(image_url);
				else
					userImage.setImageResource(R.drawable.default_profile_pic);
				commentsMain.addView(comment);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (pgLogin.isShowing()) {
			pgLogin.cancel();
			pgLogin.dismiss();
		}
	}
	//===================================================================================================================================
	//processing the XML 
	//===================================================================================================================================


	//=========================================================================================================
	// Image download online
	//=========================================================================================================

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3Factory());
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
				Log.v("TAG", "set image");
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}


	//=========================================================================================================
	// Image download online
	//=========================================================================================================
}