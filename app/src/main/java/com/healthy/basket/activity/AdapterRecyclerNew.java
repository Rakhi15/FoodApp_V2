package com.healthy.basket.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.healthy.basket.R;
import com.healthy.basket.utils.sqliteHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecyclerNew extends RecyclerView.Adapter<AdapterRecyclerNew.ViewHolder> {

    Context context;
    List<String> titles;
    List<String> images;
    List<String> quantity;
    List<String> mrp;
    List<String> price; //old price
    public static int res_id;
    List<String> itemId;
    List<String> desc;
    List<String> menuId;
    List<String> curQuantity;
    LayoutInflater inflater;
    com.healthy.basket.utils.sqliteHelper sqliteHelper;
    public AdapterRecyclerNew(Context ctx, List<String> titles, List<String> images, List<String> quantity, List<String> mrp, List<String> price,List<String> itemId,List<String> desc,List<String> menuId,List<String> curQuantity){
        this.context=ctx;
        this.titles = titles;
        this.images = images;
        this.quantity=quantity;
        this.mrp=mrp;
        this.price=price;
        this.itemId=itemId;
        this.menuId=menuId;
        this.desc=desc;
        this.curQuantity=curQuantity;
        this.inflater = LayoutInflater.from(ctx);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));

        // holder.gridIcon.setImageResource(images.get(position));
        Picasso.with(context).load("https://healthybaskets.co/uploads/restaurant/"+images.get(position)).into(holder.gridIcon);
        holder.quantityView.setText(quantity.get(position));
        holder.mrpView.setText(mrp.get(position));
        holder.priceView.setText(price.get(position));

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, quantityView, mrpView, priceView;
        ImageView gridIcon;
        Button addcartbtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productName);
            gridIcon = itemView.findViewById(R.id.productImg);
            quantityView=itemView.findViewById(R.id.description);
            mrpView=itemView.findViewById(R.id.oldPrice);
            priceView=itemView.findViewById(R.id.price_new);
            addcartbtn=itemView.findViewById(R.id.addcartBtn);
              TextView minus, plus;

            addcartbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ViewGroup viewGroup =v.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_new, viewGroup, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    final TextView title=(TextView)dialogView.findViewById(R.id.txt_DialogAddToCart);
                    TextView quantityTxt=(TextView)dialogView.findViewById(R.id.txt_DialogQuantity);
                    TextView amountTxt=(TextView)dialogView.findViewById(R.id.txt_DialogAddToCart_amount) ;
                    final TextView selQuantity=(TextView)dialogView.findViewById(R.id.txt_DialogAddToCart_qty) ;
                    ImageView pimage=(ImageView)dialogView.findViewById(R.id.img_DialogAddToCart) ;
                    ImageView closeEd=(ImageView)dialogView.findViewById(R.id.img_DialogAddToCart_close);
                    TextView minux=(TextView)dialogView.findViewById(R.id.txt_DialogAddToCart_qtyMinus);
                    TextView plus=(TextView) dialogView.findViewById(R.id.txt_DialogAddToCart_qtyPlus);
                    CardView finalcart=(CardView)dialogView.findViewById(R.id.card_DialogAddToCart_add);
                    TextView curQauntView=dialogView.findViewById(R.id.txt_DialogAddToCart_qty);
                    curQauntView.setText(curQuantity.get(getAdapterPosition()));

                    title.setText(titles.get(getAdapterPosition()));
                    Picasso.with(context).load("https://healthybaskets.co/uploads/restaurant/"+images.get(getAdapterPosition())).into(pimage);
                    quantityTxt.setText(quantity.get(getAdapterPosition()));

                   closeEd.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           alertDialog.dismiss();
                       }
                   });


                    minux.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int prevQuantity=Integer.parseInt(selQuantity.getText().toString());
                            if(prevQuantity<=1){
                                Toast.makeText(context, "Minimum Order is 1", Toast.LENGTH_SHORT).show();
                            }else{
                                prevQuantity--;
                            }
                            selQuantity.setText(String.valueOf(prevQuantity));
                        }
                    });

                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int prevQuantity=Integer.parseInt(selQuantity.getText().toString());

                                prevQuantity++;

                            selQuantity.setText(String.valueOf(prevQuantity));
                        }
                    });

                    finalcart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sqliteHelper = new sqliteHelper(context);
                            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                            int currentPosition=getAdapterPosition();
                            String productID=itemId.get(currentPosition);
                            Integer quantityToBeAdded=Integer.parseInt(selQuantity.getText().toString());
                            String productName=titles.get(currentPosition);
                            String productDescription=quantity.get(currentPosition);
                            String productMenuId=menuId.get(currentPosition);
                            String originalPrice=price.get(currentPosition);
                            String oldPrice=mrp.get(currentPosition).isEmpty()?price.get(currentPosition):mrp.get(currentPosition);
                            String imageName=images.get(currentPosition);

                                Cursor cur = db1.rawQuery("select * from cart where menuid='"+productID+"';",null);
                            if(cur.getCount()!=0){
                                cur.close();
                                try {
                                        //Cursor updatecur = db1.rawQuery("UPDATE cart SET restcurrency ='" +originalPrice+ "', foodpriceOld = '"+oldPrice+"', foodprice='"+quantityToBeAdded+"' Where menuid ='" + menuId + "';", null);
                                    Cursor updatecur = db1.rawQuery("DELETE from cart Where menuid ='" + productID + "';", null);
                                    Log.e("QUERY ", ""+updatecur.getCount());
                                    updatecur.close();

                                    Toast.makeText(context, "Product Updated in cart", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    Toast.makeText(context, "Cannot Updated in cart, Try Again !", Toast.LENGTH_SHORT).show();

                                    Log.e("Error", e.getMessage());
                                }
                            }
                           // else
                                {


                                ContentValues values = new ContentValues();
                                values.put("menuid", productID); //PRODUCT ID
                                values.put("foodprice", quantityToBeAdded); //QUANTITY
                                values.put("foodname", productName); //PRODUCT NAME
                                values.put("fooddesc", productDescription); //PRODUCT DESCRIPTION
                                values.put("foodid", productMenuId); //MENU ID
                                values.put("resid", "" + res_id); // restaurant ID
                                values.put("restcurrency", originalPrice); //ORG PRICE
                                values.put("foodpriceOld", oldPrice); //OLD PRICE
                                values.put("foodImage", imageName); // FOOD IMAGE
                                values.put("kg", "1"); //THIS IS SHIT
                                db1.insert("cart", null, values);
                                Toast.makeText(context, "Product Added to cart", Toast.LENGTH_SHORT).show();
                                    db1.close();
                                alertDialog.dismiss();
                            }
                        }
                    });

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addcartbtn.callOnClick();
                }
            });
        }
    }


}
