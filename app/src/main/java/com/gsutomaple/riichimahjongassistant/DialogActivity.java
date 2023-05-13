package com.gsutomaple.riichimahjongassistant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.riichimahjongassistant.BuildConfig;
import com.example.riichimahjongassistant.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class DialogActivity {

    private final LinearLayout base;
    private final MainActivity context;
    private AlertDialog dialog;
    private final AlertDialog.Builder builder;
    private ArrayList<Integer> extras = new ArrayList<>();
    private int winner;

    public void showSummary(int winner){
        this.winner = winner;
        dialog.setTitle("玩家 " + winner + " 点数结算");
        dialog.show();
    }
    public DialogActivity(@NonNull MainActivity context) {
        this.context = context;
        this.base = (LinearLayout) LinearLayout.inflate(context, R.layout.summary_layout,null);
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("unknown");
        b.setView(base);
        b.setPositiveButton("结算", (dialog, which) -> {
            summary();
                dialog.dismiss();
        });
        b.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder = b;
        ((Spinner)this.base.findViewById(R.id.Fan)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner s = (Spinner)base.findViewById(R.id.Fu);
                if (position >= 4){
                    s.setAlpha(0.5f);
                    s.setEnabled(false);
                }
                else{
                    s.setAlpha(1f);
                    s.setEnabled(true);
                }
                calculatePoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ((Spinner)this.base.findViewById(R.id.Fu)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculatePoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ((Spinner)this.base.findViewById(R.id.Ronsha)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculatePoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ((Spinner)this.base.findViewById(R.id.Type)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner s = (Spinner)base.findViewById(R.id.Ronsha);
                if (position != 0){
                    s.setAlpha(0.5f);
                    s.setEnabled(false);
                }
                else{
                    s.setAlpha(1f);
                    s.setEnabled(true);
                }
                calculatePoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ((Spinner)this.base.findViewById(R.id.Baopai)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculatePoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ((Switch)this.base.findViewById(R.id.IsBaopai)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            Spinner s = (Spinner)base.findViewById(R.id.Baopai);
            if (!isChecked){
                s.setAlpha(0.5f);
                s.setEnabled(false);
            }
            else{
                s.setAlpha(1f);
                s.setEnabled(true);
            }
            calculatePoints();
        });
        dialog = builder.create();
        dialog.setOnShowListener(dialog -> {
            reset();
        });
    }
    private void reset(){
        ((Spinner)this.base.findViewById(R.id.Fan)).setSelection(0);
        ((Spinner)this.base.findViewById(R.id.Fan)).setAlpha(1f);
        ((Spinner)this.base.findViewById(R.id.Fan)).setEnabled(true);
        ((Spinner)this.base.findViewById(R.id.Fu)).setSelection(0);
        ((Spinner)this.base.findViewById(R.id.Ronsha)).setSelection(0);
        ((Spinner)this.base.findViewById(R.id.Ronsha)).setAlpha(1f);
        ((Spinner)this.base.findViewById(R.id.Ronsha)).setEnabled(true);
        ((Spinner)this.base.findViewById(R.id.Baopai)).setSelection(0);
        ((Spinner)this.base.findViewById(R.id.Baopai)).setAlpha(0.5f);
        ((Spinner)this.base.findViewById(R.id.Baopai)).setEnabled(false);
        ((Spinner)this.base.findViewById(R.id.Type)).setSelection(0);
        ((Switch)this.base.findViewById(R.id.IsBaopai)).setChecked(false);
        invalid();
    }
    private ArrayList<Integer> calculatePoints(){
        ArrayList<Integer> p = new ArrayList<>();
        p.add(0);p.add(0);p.add(0);p.add(0);
        extras.clear();
        extras.add(0);extras.add(0);extras.add(0);extras.add(0);
        try {
            int fan = ((Spinner) base.findViewById(R.id.Fan)).getSelectedItemPosition() + 1;
            int fu = Integer.parseInt(((Spinner) base.findViewById(R.id.Fu)).getSelectedItem().toString());
            boolean baopai = ((Switch) base.findViewById(R.id.IsBaopai)).isChecked();
            int type = ((Spinner) base.findViewById(R.id.Type)).getSelectedItemPosition();
            int ronsha = ((Spinner) base.findViewById(R.id.Ronsha)).getSelectedItemPosition() + 1;
            int baosha = ((Spinner) base.findViewById(R.id.Baopai)).getSelectedItemPosition() + 1;
            Log.d("计算", fan + "番" + fu + "符");
            Log.d("家", winner + "和了  " + ronsha + "铳家  胡牌类型" + type);
            if (type == 2) { //罚符
                for (int i = 0; i < (Data.Is_Three ? 3 : 4); i++) {
                    p.set(i, (i + 1 == winner) ? -120 : (Data.Is_Three ? 60 : 40));
                }
                String result = "12000 满贯";
                update(p, result);
            }
            else {
                if ((fan == 1 && (fu == 20 || fu == 25)) || (type == 0  && ronsha == winner || (Data.Is_Three && ronsha == 4))
                        || (baopai && (baosha == winner || (Data.Is_Three && baosha == 4)))){
                    invalid();
                }
                else {
                    double basic = 0;
                    String addition = "";
                    switch (fan) {
                        case 5:
                            basic = 20;
                            addition = "满贯";
                            break;
                        case 6:
                            basic = 30;
                            addition = "跳满";
                            break;
                        case 7:
                            basic = 40;
                            addition = "倍满";
                            break;
                        case 8:
                            basic = 60;
                            addition = "三倍满";
                            break;
                        case 9:
                            basic = 80;
                            addition = "役满";
                            break;
                        default:
                            basic = fu * Math.pow(2, fan + 2) / 100;
                            basic = Math.min(basic, 20);
                            if (basic == 20) {
                                addition = "满贯";
                            }
                            break;
                    }
                    boolean oya = Data.Round == winner;
                    if (type == 0) {//荣和
                        int points = (int) Math.ceil(basic * (oya ? 6 : 4));
                        if (baopai && ronsha != baosha) {
                            int per = (int) Math.ceil(basic * (oya ? 3 : 2));
                            points = 2 * per;
                            p.set(ronsha - 1, -per);
                            p.set(baosha - 1, -per);
                            extras.set(ronsha - 1, Data.Honsha);
                            extras.set(ronsha - 1, Data.Honsha);
                        } else {
                            p.set(ronsha - 1, -points);
                            extras.set(ronsha - 1, -Data.Honsha * (Data.Is_Three ? 2 : 3));
                        }
                        p.set(winner - 1, points);
                        extras.set(winner - 1, Data.Honsha * (Data.Is_Three ? 2 : 3) + Data.Supply * 10);
                        String result = (points * 100) + " " + addition;
                        update(p, result);
                    } else if (type == 1) { //自摸
                        int oyaP = (int) Math.ceil(basic * (Data.Is_Three ? (double) (8 / 3) : 2));
                        int koP = (int) Math.ceil(basic * (Data.Is_Three ? (double) (4 / 3) : 1));
                        if (oya) {
                            oyaP = (int) Math.ceil(basic * (Data.Is_Three ? 3 : 2));
                        }
                        int points = oya ? (Data.Is_Three ? 2 : 3) * oyaP : oyaP + (Data.Is_Three ? 1 : 2) * koP;
                        if (baopai) {
                            p.set(baosha - 1, -points);
                            p.set(winner - 1, points);
                            extras.set(baosha - 1, -Data.Honsha * (Data.Is_Three ? 2 : 3));
                        } else {
                            for (int i = 0; i < (Data.Is_Three ? 3 : 4); i++) {
                                p.set(i, (i + 1 == winner) ? points : (oya || Data.Round == i + 1 ? -oyaP : -koP));
                                extras.set(i, (i + 1 == winner) ? Data.Honsha * (Data.Is_Three ? 2 : 3) + Data.Supply * 10 : -Data.Honsha);
                            }
                        }
                        String result = koP * 100 + ", " + oyaP * 100 + " (" + points * 100 + ") " + addition;
                        if (oya){
                            result = oyaP * 100 + " ALL (" + points * 100 + ") " + addition;
                        }
                        update(p, result);
                    }
                }
            }
        }
        catch(Exception e){
            invalid();
            Log.d("Exception:", e.toString());
        }
        return p;
    }
    private void update(ArrayList<Integer> array, String result){
        TextView player1 =  base.findViewById(R.id.Player1Result);
        TextView player2 =  base.findViewById(R.id.Player2Result);
        TextView player3 =  base.findViewById(R.id.Player3Result);
        TextView player4 =  base.findViewById(R.id.Player4Result);
        TextView res =  base.findViewById(R.id.Result);
        boolean gain = array.get(0) > 0;
        player1.setText((gain ? "+" : "-") + Math.abs(array.get(0) * 100));
        player1.setTextColor(context.getColor(gain ? R.color.colorGain : R.color.colorLose));
        gain = array.get(1) > 0;
        player2.setText((gain ? "+" : "-") + Math.abs(array.get(1) * 100));
        player2.setTextColor(context.getColor(gain ? R.color.colorGain : R.color.colorLose));
        gain = array.get(2) > 0;
        player3.setText((gain ? "+" : "-") + Math.abs(array.get(2) * 100));
        player3.setTextColor(context.getColor(gain ? R.color.colorGain : R.color.colorLose));
        gain = array.get(3) > 0;
        player4.setText((gain ? "+" : "-") + Math.abs(array.get(3) * 100));
        player4.setTextColor(context.getColor(gain ? R.color.colorGain : R.color.colorLose));
        res.setText(result);
    }
    private void invalid(){
        ArrayList<Integer> p = new ArrayList<>();
        p.add(0);p.add(0);p.add(0);p.add(0);
        update(p, "无效");
    }
    private boolean summary(){
        ArrayList<Integer> a = calculatePoints();
        if (a.get(0) == 0 && a.get(1) == 0 && a.get(2) == 0 && a.get(3) == 0){
            Toast.makeText(context, "输入的内容无效，无法结算", Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            for (int i = 0; i < (Data.Is_Three ? 3 : 4); i++){
                Data.Players.get(i).addPointsWithAnimation(a.get(i) + extras.get(i));
            }
            context.resetSupply();
            return true;
        }
    }
}
