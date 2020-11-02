package com.healthy.basket.activity;

import android.app.AlertDialog;
import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecyclerNew extends RecyclerView.Adapter<AdapterRecyclerNew.ViewHolder> {

    Context context;
    List<String> titles;
    List<String> images;
    List<String> quantity;
    List<String> mrp;
    List<String> price;

    LayoutInflater inflater;

    public AdapterRecyclerNew(Context ctx, List<String> titles, List<String> images, List<String> quantity, List<String> mrp, List<String> price){
        this.context=ctx;
        this.titles = titles;
        this.images = images;
        this.quantity=quantity;
        this.mrp=mrp;
        this.price=price;

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
        holder.quantity.setText(quantity.get(position));
        holder.mrp.setText(mrp.get(position));
        holder.price.setText(price.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, quantity, mrp, price;
        ImageView gridIcon;
        Button addcartbtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productName);
            gridIcon = itemView.findViewById(R.id.productImg);
            quantity=itemView.findViewById(R.id.qnt_txt);
            mrp=itemView.findViewById(R.id.mrp_text);
            price=itemView.findViewById(R.id.price_new);
            addcartbtn=itemView.findViewById(R.id.addcartBtn);

            TextView minux, plus;

            addcartbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ViewGroup viewGroup =v.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_new, viewGroup, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    TextView title=(TextView)dialogView.findViewById(R.id.txt_DialogAddToCart);
                    TextView quantity=(TextView)dialogView.findViewById(R.id.txt_DialogQuantity);
                    TextView amount=(TextView)dialogView.findViewById(R.id.txt_DialogAddToCart_amount) ;
                    ImageView pimage=(ImageView)dialogView.findViewById(R.id.img_DialogAddToCart) ;
                    ImageView closeEd=(ImageView)dialogView.findViewById(R.id.img_DialogAddToCart_close);
                    TextView minux=(TextView)dialogView.findViewById(R.id.txt_DialogAddToCart_qtyMinus);
                    TextView plus=(TextView) dialogView.findViewById(R.id.txt_DialogAddToCart_qtyPlus);
                    CardView finalcart=(CardView)dialogView.findViewById(R.id.card_DialogAddToCart_add);


                    title.setText(titles.get(getAdapterPosition()));
                    Picasso.with(context).load("https://healthybaskets.co/uploads/restaurant/"+images.get(getAdapterPosition())).into(pimage);


                   closeEd.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           alertDialog.dismiss();
                       }
                   });


                    minux.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                        }
                    });

                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                        }
                    });

                    finalcart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //
                        }
                    });

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Clicked -> " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
