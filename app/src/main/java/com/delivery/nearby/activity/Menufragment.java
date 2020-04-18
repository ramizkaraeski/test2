package com.delivery.nearby.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delivery.nearby.Adapter.CustomButtonListener;
import com.delivery.nearby.AppClass;
import com.delivery.nearby.Getset.cartgetset;
import com.delivery.nearby.Getset.ordergetset;
import com.delivery.nearby.R;
import com.delivery.nearby.jsdialog.DialogAddItemPopup;
import com.delivery.nearby.utils.ConnectionDetector;
import com.delivery.nearby.utils.sqliteHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.delivery.nearby.activity.MainActivity.checkInternet;
import static com.delivery.nearby.activity.MainActivity.showErrorDialog;


public class Menufragment extends Fragment implements CustomButtonListener {
    private ArrayList<ordergetset> orderlist;
    sqliteHelper sqliteHelper;
    private ProgressDialog progressDialog;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    Context context;
    public static final String MyPREFERENCES = "Fooddelivery";
    private String menuid;
    private int quantity;
    Cursor cur = null;
    ListView rel_near_listview;
    private RecyclerView recyclerView;
    String id;
    private String detail_id;


    public void init(String id, String id1) {
        detail_id = id1;
        menuid = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menufragment, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        orderlist = new ArrayList<>();
        if (checkInternet(getContext()))
            new GetDataAsyncTask().execute();
        else showErrorDialog(getContext());
        return rootView;
    }

    @Override
    public void onButtonClickListener(int position, EditText editText, int value) {
        editText.setText(value + "Kg");
        Log.e("quantity", "" + quantity);
    }

    class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            orderlist.clear();
            URL hp = null;
            String error;
            try {
                orderlist.clear();
                hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "restaurant_submenu.php?menucategory_id=" + menuid);
                Log.e("menucategory_id", "" + hp);
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                Log.d("input", "" + input);
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x = "";
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                Log.d("URL", "" + total);
                JSONArray jObject = new JSONArray(total.toString());
                Log.d("URL12", "" + jObject);
                JSONObject Obj;
                Obj = jObject.getJSONObject(0);
                Log.e("Obj", Obj.toString());
                ordergetset temp1 = new ordergetset();
                temp1.setStatus(Obj.getString("status"));
                JSONArray jarr = Obj.getJSONArray("Menu_List");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject Obj1;
                    Obj1 = jarr.getJSONObject(i);
                    Log.e("Obj1", Obj1.toString());
                    ordergetset temp = new ordergetset();
                    temp.setId(Obj1.getString("id"));
                    temp.setImage(Obj1.getString("photo"));
                    temp.setName(Obj1.getString("name"));
                    temp.setPrice(Obj1.getString("price"));
                    temp.setDesc(Obj1.getString("desc"));
                    temp.setCreated_at(Obj1.getString("created_at"));
                    String value = "0";
                    temp.setCounting(value);
                    orderlist.add(temp);
                    Log.e("orderlist", orderlist.get(0).getId());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                error = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                error = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            orderadapter1 adapter = new orderadapter1(orderlist, menuid, detail_id, quantity);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            adapter.setCustomButtonListener(Menufragment.this);
            adapter.notifyDataSetChanged();

        }
    }

    public class orderadapter1 extends RecyclerView.Adapter<orderadapter1.MyViewHolder> {
        String id;
        final String menuid1;
        Cursor cur = null;
        String resid, foodid, foodname, fooddesc, foodkg;
        final int va = 0;
        final String detail_id1;
        String menuid321, foodprice321, restcurrency321, kg;


        String de;
        boolean b;
        CustomButtonListener customButtonListener;
        private final ArrayList<ordergetset> data1;
        private ArrayList<cartgetset> cartlist;

        private LayoutInflater inflater = null;
        final ArrayList<Integer> quantity = new ArrayList<>();
        final int quen;
        int desk;
        String sa;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            final TextView txt_name;
            final TextView txt_desc;
            final TextView txt_price;
            public ImageView imageview;
            final ImageButton btn_minus;
            final ImageButton btn_plus;
            final EditText edTextQuantity;
            final ImageView img_categoryImage;
            final EditText txt_kg;
            final CardView card_CategoryAdd;
            final RelativeLayout rel_CategoryAdd;

            MyViewHolder(View view) {
                super(view);
                txt_name = view.findViewById(R.id.txt_name);
                txt_desc = view.findViewById(R.id.txt_desc);
                txt_price = view.findViewById(R.id.txt_price);
                btn_minus = view.findViewById(R.id.btn_minus);
                btn_plus = view.findViewById(R.id.btn_plus);
                edTextQuantity = view.findViewById(R.id.edTextQuantity);
                img_categoryImage = view.findViewById(R.id.img_categoryImage);
                txt_kg = view.findViewById(R.id.txt_kg);
                rel_CategoryAdd = view.findViewById(R.id.rel_CategoryAdd);
                card_CategoryAdd = view.findViewById(R.id.card_CategoryAdd);

            }
        }

        void setCustomButtonListener(CustomButtonListener customButtonListner) {
            this.customButtonListener = customButtonListner;
        }

        orderadapter1(ArrayList<ordergetset> moviesList, String menu_id, String detail_id, int quantity) {

            data1 = moviesList;
            menuid1 = menu_id;
            detail_id1 = detail_id;
            quen = quantity;

            for (int i = quen; i < data1.size(); i++) {
                this.quantity.add(i);
            }
        }

        @Override
        public orderadapter1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_category, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final int pos = position;
            final MyViewHolder holder1 = holder;

            holder.txt_name.setText(data1.get(position).getName());
            holder.txt_desc.setText(data1.get(position).getDesc());
            try {
                holder.txt_price.setText(AppClass.preferences.getCurrency() + Double.parseDouble(data1.get(position).getPrice()) + "");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.txt_price.setText(AppClass.preferences.getCurrency() + data1.get(position).getPrice() + "");
            }
            getlist(position);
            if (data1.get(position).getId().equals(menuid321)) {
                de = foodprice321;
                int var = Integer.parseInt(de);
                Log.e("stringde123", "" + var);
                holder.edTextQuantity.setText("" + de);
                b = true;
                Log.e("hello", "" + b);
            } else {
                holder.edTextQuantity.setText("" + va);
                b = false;
                Log.e("hello123", "" + b);
            }

            Picasso.with(getContext()).load(getContext().getResources().getString(R.string.link) + getContext().getString(R.string.imagepath) + data1.get(position).getImage()).into(holder.img_categoryImage);

            holder.rel_CategoryAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogAddItemPopup(getContext(),
                            data1.get(position).getName(),
                            data1.get(position).getImage(),
                            data1.get(position).getPrice(),
                            data1.get(position).getId(),
                            new DialogAddItemPopup.OnDataAddClickLisstener() {
                                @Override
                                public void onDataAdd(String price, String mQty, String mSize) {
                                    if (customButtonListener != null) {
                                        desk = Integer.parseInt(mQty);
                                        Log.e("dexk", "" + desk);
                                        try {

                                            if (quantity.size() > 0) {

                                                quantity.set(pos, desk);
                                                getlist(pos);

                                                if (data1.get(pos).getId().equals(menuid321)) {

                                                    sqliteHelper = new sqliteHelper(getActivity());
                                                    SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                                                    try {
                                                        cur = db1.rawQuery("UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + mQty + "',restcurrency='" + data1.get(pos).getPrice() + "',kg='" + mSize + "' Where menuid ='" + data1.get(pos).getId() + "';", null);
                                                        Log.e("updatequeryalready", "" + "UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';");
                                                        if (cur.getCount() != 0) {
                                                            if (cur.moveToFirst()) {
                                                                do {
                                                                    cartgetset obj = new cartgetset();
                                                                    resid = cur.getString(cur.getColumnIndex("resid"));
                                                                    menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                                                                    foodid = cur.getString(cur.getColumnIndex("foodid"));
                                                                    foodname = cur.getString(cur.getColumnIndex("foodname"));
                                                                    foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                                                                    fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                                                                    restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
                                                                    kg = cur.getString(cur.getColumnIndex("kg"));
                                                                    obj.setResid(resid);
                                                                    obj.setFoodid(foodid);
                                                                    obj.setMenuid(menuid321);
                                                                    obj.setFoodname(foodname);
                                                                    obj.setFoodprice(foodprice321);
                                                                    obj.setFooddesc(fooddesc);
                                                                    obj.setRestcurrency(restcurrency321);
                                                                    obj.setKg(kg);
                                                                    Log.e("menuid321updated", "" + menuid321);
                                                                    Log.e("foodp321updated", "" + foodprice321);
                                                                    cartlist.add(obj);
                                                                } while (cur.moveToNext());
                                                            }
                                                        }
                                                        cur.close();
                                                        db1.close();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    if (data1.get(pos).getId().equals(menuid321)
                                                    ) {
                                                    } else {
                                                        getlist(pos);
                                                        sqliteHelper = new sqliteHelper(getActivity());
                                                        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                                                        ContentValues values = new ContentValues();
                                                        values.put("menuid", "" + data1.get(pos).getId());
                                                        values.put("foodprice", desk);
                                                        values.put("foodname", "" + data1.get(pos).getName());
                                                        values.put("fooddesc", "" + data1.get(pos).getDesc());
                                                        values.put("foodid", menuid1);
                                                        values.put("resid", detail_id1);
                                                        values.put("restcurrency", data1.get(pos).getPrice());
                                                        values.put("kg", mSize);
                                                        db1.insert("cart", null, values);
                                                        Log.e("inserted values", values.toString());
                                                    }
                                                }
                                            }
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).show();
                }
            });

            holder.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customButtonListener != null) {
                        customButtonListener.onButtonClickListener(pos, holder1.edTextQuantity, 1);
                        desk = Integer.parseInt(holder1.edTextQuantity.getText().toString());
                        Log.e("dexk", "" + desk);
                        try {

                            if (quantity.size() > 0) {

                                quantity.set(pos, desk);
                                getlist(pos);

                                if (data1.get(pos).getId().equals(menuid321)) {

                                    sqliteHelper = new sqliteHelper(getActivity());
                                    SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                                    try {
                                        cur = db1.rawQuery("UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';", null);
                                        Log.e("updatequeryalready", "" + "UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';");
                                        if (cur.getCount() != 0) {
                                            if (cur.moveToFirst()) {
                                                do {
                                                    cartgetset obj = new cartgetset();
                                                    resid = cur.getString(cur.getColumnIndex("resid"));
                                                    menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                                                    foodid = cur.getString(cur.getColumnIndex("foodid"));
                                                    foodname = cur.getString(cur.getColumnIndex("foodname"));
                                                    foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                                                    fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                                                    restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
                                                    obj.setResid(resid);
                                                    obj.setFoodid(foodid);
                                                    obj.setMenuid(menuid321);
                                                    obj.setFoodname(foodname);
                                                    obj.setFoodprice(foodprice321);
                                                    obj.setFooddesc(fooddesc);
                                                    obj.setRestcurrency(restcurrency321);
                                                    Log.e("menuid321updated", "" + menuid321);
                                                    Log.e("foodp321updated", "" + foodprice321);
                                                    cartlist.add(obj);
                                                } while (cur.moveToNext());
                                            }
                                        }
                                        cur.close();
                                        db1.close();
//                                        myDbHelper.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    if (data1.get(pos).getId().equals(menuid321)
                                    ) {
                                    } else {
                                        getlist(pos);
                                        sqliteHelper = new sqliteHelper(getActivity());
                                        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put("menuid", "" + data1.get(pos).getId());
                                        values.put("foodprice", desk);
                                        values.put("foodname", "" + data1.get(pos).getName());
                                        values.put("fooddesc", "" + data1.get(pos).getDesc());
                                        values.put("foodid", menuid1);
                                        values.put("resid", detail_id1);
                                        values.put("restcurrency", data1.get(pos).getPrice());
                                        db1.insert("cart", null, values);
                                        Log.e("inserted values", values.toString());
                                    }
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            holder.btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (customButtonListener != null) {
                        customButtonListener.onButtonClickListener(pos, holder1.edTextQuantity, -1);
                        try {

                            if (quantity.size() > 0) {
                                if (quantity.get(pos) > 0)
                                    sa = String.valueOf(quantity.set(pos, desk));
                                desk = Integer.parseInt(holder1.edTextQuantity.getText().toString());
                                Log.e("dexk", "" + desk);
                                quantity.set(pos, desk);
                                Log.e("valuesaminus", "" + desk);
                                sqliteHelper = new sqliteHelper(getActivity());
                                SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();


                                try {
                                    cur = db1.rawQuery("UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';", null);
                                    Log.e("updatequeryminus", "" + "UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';");
                                    Log.e("SIZWA", "" + cur.getCount());
                                    if (cur.getCount() != 0) {
                                        if (cur.moveToFirst()) {
                                            do {
                                                cartgetset obj = new cartgetset();
                                                resid = cur.getString(cur.getColumnIndex("resid"));
                                                menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                                                foodid = cur.getString(cur.getColumnIndex("foodid"));
                                                foodname = cur.getString(cur.getColumnIndex("foodname"));
                                                foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                                                fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                                                restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
                                                obj.setResid(resid);
                                                obj.setFoodid(foodid);
                                                obj.setMenuid(menuid321);
                                                obj.setFoodname(foodname);
                                                obj.setFoodprice(foodprice321);
                                                obj.setFooddesc(fooddesc);
                                                obj.setRestcurrency(restcurrency321);
                                                Log.e("menuid321updated", "" + menuid321);
                                                Log.e("foodp321updated", "" + foodprice321);
                                                cartlist.add(obj);
                                            } while (cur.moveToNext());
                                        }
                                    }
                                    cur.close();
                                    db1.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            holder.txt_kg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getActivity(), v);
                    menu.getMenu().add(Menu.NONE, 1, 1, "1 Kg");
                    menu.getMenu().add(Menu.NONE, 2, 2, "2 Kg");
                    menu.getMenu().add(Menu.NONE, 3, 3, "3 Kg");
                    menu.getMenu().add(Menu.NONE, 4, 4, "4 Kg");
                    menu.getMenu().add(Menu.NONE, 5, 5, "5 Kg");
                    menu.show();

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int kg = item.getOrder();

                            if (customButtonListener != null) {
                                customButtonListener.onButtonClickListener(pos, holder1.txt_kg, kg);
                                desk = kg;//Integer.parseInt(holder1.edTextQuantity.getText().toString());
                                Log.e("dexk", "" + desk);
                                try {

                                    if (quantity.size() > 0) {

                                        quantity.set(pos, desk);
                                        getlist(pos);

                                        if (data1.get(pos).getId().equals(menuid321)) {

                                            sqliteHelper = new sqliteHelper(getActivity());
                                            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                                            try {
                                                cur = db1.rawQuery("UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';", null);
                                                Log.e("updatequeryalready", "" + "UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';");
                                                if (cur.getCount() != 0) {
                                                    if (cur.moveToFirst()) {
                                                        do {
                                                            cartgetset obj = new cartgetset();
                                                            resid = cur.getString(cur.getColumnIndex("resid"));
                                                            menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                                                            foodid = cur.getString(cur.getColumnIndex("foodid"));
                                                            foodname = cur.getString(cur.getColumnIndex("foodname"));
                                                            foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                                                            fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                                                            restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
                                                            obj.setResid(resid);
                                                            obj.setFoodid(foodid);
                                                            obj.setMenuid(menuid321);
                                                            obj.setFoodname(foodname);
                                                            obj.setFoodprice(foodprice321);
                                                            obj.setFooddesc(fooddesc);
                                                            obj.setRestcurrency(restcurrency321);
                                                            Log.e("menuid321updated", "" + menuid321);
                                                            Log.e("foodp321updated", "" + foodprice321);
                                                            cartlist.add(obj);
                                                        } while (cur.moveToNext());
                                                    }
                                                }
                                                cur.close();
                                                db1.close();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            if (data1.get(pos).getId().equals(menuid321)
                                            ) {
                                            } else {
                                                getlist(pos);
                                                sqliteHelper = new sqliteHelper(getActivity());
                                                SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();

                                                ContentValues values = new ContentValues();
                                                values.put("menuid", "" + data1.get(pos).getId());
                                                values.put("foodprice", desk);
                                                values.put("foodname", "" + data1.get(pos).getName());
                                                values.put("fooddesc", "" + data1.get(pos).getDesc());
                                                values.put("foodid", menuid1);
                                                values.put("resid", detail_id1);
                                                values.put("restcurrency", data1.get(pos).getPrice());
                                                db1.insert("cart", null, values);
                                                Log.e("inserted values", values.toString());
                                            }
                                        }
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                            return false;
                        }
                    });
                }
            });
        }

        private void getlist(int position) {

            sqliteHelper = new sqliteHelper(getActivity());
            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
            try {
                cur = db1.rawQuery("select * from cart where menuid=" + data1.get(position).getId() + ";", null);
                Log.e("selectmenuid", "select * from cart where menuid=" + data1.get(position).getId() + ";");
                if (cur.getCount() != 0) {

                    if (cur.moveToFirst()) {
                        do {
                            cartgetset obj = new cartgetset();
                            resid = cur.getString(cur.getColumnIndex("resid"));
                            menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                            foodid = cur.getString(cur.getColumnIndex("foodid"));
                            foodname = cur.getString(cur.getColumnIndex("foodname"));
                            foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                            fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                            foodkg = cur.getString(cur.getColumnIndex("kg"));
                            obj.setResid(resid);
                            obj.setFoodid(foodid);
                            obj.setMenuid(menuid321);
                            obj.setFoodname(foodname);
                            obj.setFooddesc(fooddesc);
                            obj.setKg(foodkg);
                            Log.e("menuid321", menuid321);
                            Log.e("foodp321", "" + foodprice321);
                            cartlist.add(obj);
                        } while (cur.moveToNext());
                    }
                }

                cur.close();
                db1.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }


        @Override
        public int getItemCount() {
            Log.e("sizedata", "" + data1.size());
            return data1.size();
        }
    }

}