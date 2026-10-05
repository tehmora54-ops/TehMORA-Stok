package com.tehmora.stok;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    final String PREF="teh_mora_stock";
    LinearLayout list;
    SharedPreferences sp;
    final String[] cats={"VARIAN","CUP","BELANJAAN"};

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        sp=getSharedPreferences(PREF,MODE_PRIVATE);
        build();
    }

    TextView tv(String s,int size){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size);
        t.setTextColor(Color.rgb(23,53,31)); t.setPadding(0,8,0,8); return t;
    }

    void build(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(245,247,242));

        TextView head=tv("Teh MORA — STOK",22); head.setTextColor(Color.WHITE);
        head.setTypeface(null,Typeface.BOLD); head.setPadding(20,22,20,22);
        head.setBackgroundColor(Color.rgb(29,91,53)); root.addView(head);

        LinearLayout add=new LinearLayout(this); add.setOrientation(LinearLayout.VERTICAL);
        add.setPadding(16,12,16,8);
        EditText name=new EditText(this); name.setHint("Nama barang / varian");
        EditText qty=new EditText(this); qty.setHint("Stok awal"); qty.setInputType(2);
        Spinner cat=new Spinner(this);
        cat.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,cats));
        Button save=new Button(this); save.setText("SIMPAN BARANG");
        add.addView(name); add.addView(qty); add.addView(cat); add.addView(save);
        root.addView(add);

        ScrollView sv=new ScrollView(this);
        list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); list.setPadding(16,4,16,20);
        sv.addView(list); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        save.setOnClickListener(v->{
            String n=name.getText().toString().trim();
            if(n.isEmpty()){Toast.makeText(this,"Nama barang belum diisi",Toast.LENGTH_SHORT).show();return;}
            int q=0; try{q=Integer.parseInt(qty.getText().toString());}catch(Exception e){}
            String c=cats[cat.getSelectedItemPosition()];
            String key=key(c,n);
            sp.edit().putInt(key,sp.getInt(key,0)+Math.max(0,q)).apply();
            name.setText(""); qty.setText(""); refresh();
        });
        setContentView(root); refresh();
    }

    String key(String c,String n){ return c+"|"+n; }

    void refresh(){
        list.removeAllViews();
        for(String c:cats){
            TextView title=tv(c,18); title.setTypeface(null,Typeface.BOLD);
            list.addView(title);
            boolean any=false;
            Map<String,?> all=sp.getAll();
            for(String k:all.keySet()){
                if(!k.startsWith(c+"|")) continue;
                any=true; String n=k.substring(c.length()+1); int val=sp.getInt(k,0);
                LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout info=new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL);
                TextView a=tv(n,16); a.setTypeface(null,Typeface.BOLD);
                TextView b=tv("Stok: "+val,13); info.addView(a); info.addView(b);
                row.addView(info,new LinearLayout.LayoutParams(0,-2,1));
                Button minus=new Button(this); minus.setText("−");
                Button plus=new Button(this); plus.setText("+");
                Button del=new Button(this); del.setText("×");
                row.addView(minus); row.addView(plus); row.addView(del);
                minus.setOnClickListener(v->change(k,-1));
                plus.setOnClickListener(v->change(k,1));
                del.setOnClickListener(v->{sp.edit().remove(k).apply();refresh();});
                list.addView(row);
            }
            if(!any){TextView e=tv("Belum ada data",13); e.setTextColor(Color.GRAY); list.addView(e);}
        }
    }

    void change(String k,int d){
        int v=Math.max(0,sp.getInt(k,0)+d);
        sp.edit().putInt(k,v).apply(); refresh();
    }
}
