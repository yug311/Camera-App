package com.example.photogallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Images>
{
    List<Images> list;
    Context context;
    int xmlResource;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Images> objects){
        super(context, resource, objects);
        xmlResource = resource;
        list = objects;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        //return super.getView(position, convertView, parents);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        SquareImages image = adapterLayout.findViewById(R.id.imageView);
        // image.setImageResource(list.get(position).getImage());
        image.setImageBitmap(list.get(position).getBitmap());
        return adapterLayout;

    }

}
