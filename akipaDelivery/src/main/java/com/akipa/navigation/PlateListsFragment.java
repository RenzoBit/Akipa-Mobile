package com.akipa.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akipa.akipadelivery.R;
import com.akipa.listview.utils.ItemListPogo;
import com.akipa.listview.utils.ListViewAdapter;
import com.akipa.listview.utils.ReusableClass;

public class PlateListsFragment extends Fragment {

	// List view
	private ListView lv;

	// Listview Adapter
	//ArrayAdapter<String> adapter;

	// Search EditText
	EditText inputSearch;
	Spinner CategorySpinner;
	private ProgressDialog pgLogin;

	HashMap<String,String> spinnerMap = new HashMap<String, String>();
	String[] spinnerArray;

	// ArrayList for Listview
	ArrayList<HashMap<String, String>> productList;
	ArrayList<ItemListPogo> arraylist = new ArrayList<ItemListPogo>();
	ListViewAdapter adapter;
	Context con;

	public PlateListsFragment(DashBoardActivity dashBoardActivity) {
		con = dashBoardActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView   = inflater.inflate(R.layout.fragment_plate, container, false);
		lv          	= (ListView)rootView.findViewById(R.id.list_view);
		inputSearch 	= (EditText) rootView.findViewById(R.id.inputSearch);
		CategorySpinner = (Spinner)rootView.findViewById(R.id.CategorySpinner);

		
		//=======================================================================================
		//Category List
		//=======================================================================================

		pgLogin = new ProgressDialog(getActivity());
		pgLogin.setMessage("Please wait updating your category ...");
		pgLogin.setIndeterminate(true);
		pgLogin.setCancelable(true);
		pgLogin.setCanceledOnTouchOutside(false);

		pgLogin.show();

		new MyAsyncTaskForCategory().execute();

		//=======================================================================================
		//END Category List
		//=======================================================================================

		
		
		
		//=======================================================================================
		//Spinner onChange
		//=======================================================================================

		CategorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Log.i("TAG", "Value Selected: " + spinnerMap.get(spinnerArray[position]));

				if(spinnerMap.get(spinnerArray[position]) != null)
				{
					//Showing progress dialog 
					pgLogin = new ProgressDialog(getActivity());
					pgLogin.setMessage("Please wait updating your plate lists ...");
					pgLogin.setIndeterminate(true);
					pgLogin.setCancelable(true);
					pgLogin.setCanceledOnTouchOutside(false);

					pgLogin.show();

					new MyAsyncTaskForPlateList().execute(spinnerMap.get(spinnerArray[position]));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}
		});
		
		//=======================================================================================
		//END Spinner onChange
		//=======================================================================================


		

		//=======================================================================================
		//Edittext Filter 
		//=======================================================================================

		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text

				String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
				adapter.filter(text);

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub							
			}
		});
		
		//=======================================================================================
		//Edittext Filter 
		//=======================================================================================

		
		
		return rootView;
	}

	//===================================================================================================================================
	//Get Category List
	//===================================================================================================================================

	private class MyAsyncTaskForCategory extends AsyncTask<String, Integer, Double>{

		String responseBody;
		int responseCode;
		@Override
		protected Double doInBackground(String... params) {
			postData();
			return null;
		}

		protected void onPostExecute(Double result)
		{
			//The HTTP status messages in the 200 series reflect that the request was successful. 
			if(responseCode == 200)
			{
				Log.d("Log",responseBody);
				processLoginResponce(responseBody);
			}
			//Not getting proper response
			else
			{
				if (pgLogin.isShowing()) 
				{
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText(getActivity(), "Sorry!! No responce from server.", Toast.LENGTH_LONG).show();
			}

		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData() {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(ReusableClass.base_url + "lista_categorias_con_platos");

			try 
			{
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httpGet);

				responseCode = response.getStatusLine().getStatusCode();
				responseBody = EntityUtils.toString(response.getEntity());
			} 
			catch (Throwable t ) {
				Log.d("Error Time of Login",t+"");
			} 
		}
	}

	//===================================================================================================================================
	//END Category List
	//===================================================================================================================================

	//===================================================================================================================================
	//processing the XML got from server 
	//===================================================================================================================================
	private void processLoginResponce(String responceFromServer) 
	{

		/*[{"idcategoria":"7","nombre":"Aves"},{"idcategoria":"8","nombre":"Carnes"},{"idcategoria":"20","nombre":"Cervezas"},
		 * {"idcategoria":"4","nombre":"Copas Marinas"}, {"idcategoria":"6","nombre":"Del Mar"},
		 * {"idcategoria":"10","nombre":"Ensaladas"},{"idcategoria":"5","nombre":"Los caseros de toda la vida"},
		 * {"idcategoria":"1","nombre":"Men\u00fa del D\u00eda"},{"idcategoria":"3","nombre":"Para Compartir"},
		 * {"idcategoria":"2","nombre":"Para Empezar"},{"idcategoria":"11","nombre":"Postres"},
		 * {"idcategoria":"9","nombre":"Teque\u00f1os ni taca\u00f1os ni peque\u00f1os"}]
		 */

		try 
		{
			JSONArray jsonarray = new JSONArray(responceFromServer);
			spinnerArray = new String[jsonarray.length()];

			for(int i=0; i<jsonarray.length(); i++)
			{
				JSONObject obj = jsonarray.getJSONObject(i);

				String idcategoria = obj.getString("idcategoria");
				String nombre = obj.getString("nombre");

				spinnerMap.put(nombre, idcategoria);
				spinnerArray[i] = nombre;
			}

			ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, spinnerArray);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			CategorySpinner.setAdapter(adapter);

			adapter.notifyDataSetChanged();

			if (pgLogin.isShowing()) 
			{
				pgLogin.cancel();
				pgLogin.dismiss();
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}   

		if (pgLogin.isShowing()) 
		{
			pgLogin.cancel();
			pgLogin.dismiss();
		}

	}
	//===================================================================================================================================
	//processing the XML 
	//===================================================================================================================================


	//===================================================================================================================================
	//Get Category List
	//===================================================================================================================================

	private class MyAsyncTaskForPlateList extends AsyncTask<String, Integer, Double>{

		String responseBody;
		int responseCode;
		@Override
		protected Double doInBackground(String... params) {
			postData(params[0]);
			return null;
		}

		protected void onPostExecute(Double result)
		{
			//The HTTP status messages in the 200 series reflect that the request was successful. 
			if(responseCode == 200)
			{
				Log.d("Log",responseBody);
				processCategoryResponce(responseBody);
			}
			//Not getting proper response
			else
			{
				if (pgLogin.isShowing()) 
				{
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText(getActivity(), "Sorry!! No responce from server.", Toast.LENGTH_LONG).show();
			}

		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData(String categoryId) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(ReusableClass.base_url + "lista_platos_por_idcategoria?idcategoria="+categoryId);

			try 
			{
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httpGet);

				responseCode = response.getStatusLine().getStatusCode();
				responseBody = EntityUtils.toString(response.getEntity());
			} 
			catch (Throwable t ) {
				Log.d("Error Time of Login",t+"");
			} 
		}
	}

	//===================================================================================================================================
	//END Category List
	//===================================================================================================================================

	//===================================================================================================================================
	//processing the XML got from server 
	//===================================================================================================================================
	private void processCategoryResponce(String responceFromServer) 
	{

		/*[{"idplato":"53","nombre":"Risotto de Seco","precio":"35.00","calificacion":"0","relevancia":"0","imagen":"53","categoria":"Carnes"},
		 * {"idplato":"52","nombre":"Spaghetti a la Huanca\u00edna con Lomo Saltado","precio":"37.00","calificacion":"0","relevancia":"0","imagen":"52","categoria":"Carnes"},
		 * {"idplato":"51","nombre":"Lomo marinado en Chimichurri","precio":"37.00","calificacion":"0","relevancia":"0","imagen":"0","categoria":"Carnes"},
		 * {"idplato":"50","nombre":"Lomo Saltado","precio":"33.00","calificacion":"0","relevancia":"0","imagen":"50","categoria":"Carnes"}]
		 */

		try 
		{
			JSONArray jsonarray = new JSONArray(responceFromServer);
			arraylist.clear();

			for(int i=0; i<jsonarray.length(); i++)
			{
				JSONObject obj = jsonarray.getJSONObject(i);

				String idplato 	  = obj.getString("idplato");
				String nombre 	  = obj.getString("nombre");
				String precio 	  = obj.getString("precio");
				String rating 	  = obj.getString("calificacion");
				String relevancia = obj.getString("relevancia");
				String imagen 	  = obj.getString("imagen");
				String categoria  = obj.getString("categoria");


				ItemListPogo wp = new ItemListPogo(nombre, categoria, precio, rating, imagen, idplato);
				arraylist.add(wp);
			}

			adapter = new ListViewAdapter(getActivity(), arraylist);

			lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}   

		if (pgLogin.isShowing()) 
		{
			pgLogin.cancel();
			pgLogin.dismiss();
		}

	}
	//===================================================================================================================================
	//processing the XML 
	//===================================================================================================================================

}