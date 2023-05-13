package com.gsutomaple.riichimahjongassistant;

import android.animation.*;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import com.example.riichimahjongassistant.R;

import java.util.HashSet;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    public DialogActivity activity;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putInt(getString(R.string.player1_points_key), Data.Players.get(0).getPoints());
        e.putInt(getString(R.string.player2_points_key), Data.Players.get(1).getPoints());
        e.putInt(getString(R.string.player3_points_key), Data.Players.get(2).getPoints());
        e.putInt(getString(R.string.player4_points_key), Data.Players.get(3).getPoints());
        e.putBoolean(getString(R.string.player1_reached_key), Data.Players.get(0).getReached());
        e.putBoolean(getString(R.string.player2_reached_key), Data.Players.get(1).getReached());
        e.putBoolean(getString(R.string.player3_reached_key), Data.Players.get(2).getReached());
        e.putBoolean(getString(R.string.player4_reached_key), Data.Players.get(3).getReached());
        e.putInt(getString(R.string.default_points_four_key), Data.Default_Points_Four);
        e.putInt(getString(R.string.default_points_three_key), Data.Default_Points_Three);
        e.putInt(getString(R.string.honsha_key), Data.Honsha);
        e.putInt(getString(R.string.supply_key), Data.Supply);
        e.putInt(getString(R.string.round_key), Data.Round);
        e.putInt(getString(R.string.field_key), Data.Field.getNumber());
        e.putBoolean(getString(R.string.three_player_mode_key), Data.Is_Three);
        e.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        Data.Players = new LinkedList<>();
        Data.Default_Points_Three = sp.getInt(getString(R.string.default_points_three_key), 350);
        Data.Default_Points_Four = sp.getInt(getString(R.string.default_points_four_key), 250);
        Data.Honsha = sp.getInt(getString(R.string.honsha_key), 0);
        Data.Round = sp.getInt(getString(R.string.round_key), 1);
        Data.Supply = sp.getInt(getString(R.string.supply_key), 0);
        Data.Field = Field.toField(sp.getInt(getString(R.string.field_key), 1));
        Data.Is_Three = sp.getBoolean(getString(R.string.three_player_mode_key), false);
        int def = Data.Is_Three ? Data.Default_Points_Three : Data.Default_Points_Four;
        Data.Players.add(new Player(1, R.id.Player1, R.id.PlayerSub1, R.id.player1Points, R.id.player1Reach, R.id.player1PointChange,this,
                sp.getInt(getString(R.string.player1_points_key), def), sp.getBoolean(getString(R.string.player1_reached_key), false)));
        Data.Players.add(new Player(2, R.id.Player2, R.id.PlayerSub2, R.id.player2Points, R.id.player2Reach, R.id.player2PointChange,this,
                sp.getInt(getString(R.string.player2_points_key), def), sp.getBoolean(getString(R.string.player2_reached_key), false)));
        Data.Players.add(new Player(3, R.id.Player3, R.id.PlayerSub3, R.id.player3Points, R.id.player3Reach, R.id.player3PointChange,this,
                sp.getInt(getString(R.string.player3_points_key), def), sp.getBoolean(getString(R.string.player3_reached_key), false)));
        Data.Players.add(new Player(4, R.id.Player4, R.id.PlayerSub4, R.id.player4Points, R.id.player4Reach, R.id.player4PointChange,this,
                sp.getInt(getString(R.string.player4_points_key), def), sp.getBoolean(getString(R.string.player4_reached_key), false)));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById(R.id.Field).setOnClickListener(x -> nextField());
        findViewById(R.id.Field).setOnLongClickListener(v -> {resetField();return true;});
        findViewById(R.id.Honsha).setOnClickListener(v -> nextHonsha());
        findViewById(R.id.Honsha).setOnLongClickListener(v -> {resetHonsha();return true;});
        findViewById(R.id.Supply).setOnClickListener(v -> nextSupply());
        findViewById(R.id.Supply).setOnLongClickListener(v -> {resetSupply();return true;});
        findViewById(R.id.Round).setOnClickListener(v -> nextRound());
        findViewById(R.id.Round).setOnLongClickListener(v -> {resetRound();return true;});
        findViewById(R.id.restore).setOnClickListener(v -> resetAll());
        findViewById(R.id.refresh).setOnClickListener(v -> refresh());
        Switch s = findViewById(R.id.is_three);
        s.setChecked(Data.Is_Three);
        s.setOnCheckedChangeListener((v,b) -> {
           Data.Is_Three = b;
           refreshCardStatus();
        });
        activity = new DialogActivity(this);
        refresh();
    }
    public void refreshCardStatus(){
        for (Player p : Data.Players){
            p.refresh();
        }
    }

    public void nextField() {
        Data.Field = Data.Field.Next();
        refreshField();
        resetRound();
    }
    public void resetField(){
        Data.Field = Field.East;
        refreshField();
        resetRound();
    }
    public void refreshField(){
        ((CardView)findViewById(R.id.Field)).setCardBackgroundColor(getColorStateList(Data.Field.colorId()));
        ((TextView)findViewById(R.id.FieldWord)).setText(getString(Data.Field.wordId()));
    }

    public void nextRound(){
        int round = Data.Round;
        if (round == 3 && Data.Is_Three){
            nextField();
        }
        else if ((round == 4 && !Data.Is_Three) || round > 4){
            nextField();
        }
        else {
            Data.Round++;
            refreshRound();
            unreachAll();
        }
    }
    public void unreachAll(){
        for (Player player : Data.Players){
            player.unreach();
        }
    }
    public void reach(View v){
        for (Player player : Data.Players){
            if (player.getSubView() == v.getParent()){
                player.reach();
            }
        }
    }
    public void resetRound(){
        Data.Round = 1;
        refreshRound();
        unreachAll();
    }
    public void refreshRound(){((TextView)findViewById(R.id.RoundWord)).setText(Data.Round + " 局");}
    public void nextHonsha(){
        Data.Honsha++;
        refreshHonsha();
        unreachAll();
    }
    public void resetHonsha(){
        Data.Honsha = 0;
        refreshHonsha();
        unreachAll();
    }
    public void refreshHonsha(){((TextView)findViewById(R.id.HonshaWord)).setText(String.valueOf(Data.Honsha));}
    public void nextSupply(){
        Data.Supply++;
        refreshSupply();
    }
    public void resetSupply(){
        Data.Supply = 0;
        refreshSupply();
    }
    public void refreshSupply(){((TextView)findViewById(R.id.SupplyWord)).setText(String.valueOf(Data.Supply));}
    public void resetAll(){
        int point = Data.Is_Three ? Data.Default_Points_Three : Data.Default_Points_Four;
        for (Player p : Data.Players){
            p.setPoints(point);
            p.unreach();
        }
        resetField();
        resetSupply();
        resetHonsha();
        refresh();
    }
    public void refresh(){
        refreshField();
        refreshRound();
        refreshHonsha();
        refreshSupply();
        refreshCardStatus();
    }
    public void PointChangeInternal(View v, int count)
    {
        for (Player player : Data.Players){
            if (player.getSubView() == v.getParent()){
                player.addPoints(count);
            }
        }
    }
    public void fadeInAndOut(View v, long duation) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        oa.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float x) {
                if (x <= 0.25)
                    return (float)Math.sin(2* x * Math.PI);
                else
                    return 1;
            }
        });
        oa.setDuration(duation /  2);
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
        oa1.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float x) {
                if (x < 0.75)
                    return 0;
                else
                    return 4 * x * x - 3 * x;
            }
        });
        oa1.setDuration(duation / 2);
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                oa1.start();
            }
        });
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
            }
        });
        oa.start();
        v.setVisibility(View.VISIBLE);
    }
    public void switchVisibility(View v, boolean forceNo){
        int change = v.getVisibility() == View.VISIBLE || forceNo ? View.GONE : View.VISIBLE;
        int cur = v.getVisibility();
        if (cur == change){
            return;
        }
        if (change == View.GONE && cur == View.VISIBLE){ //消失
            ObjectAnimator oa = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
            oa.setDuration(200);
            oa.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    v.setVisibility(change);
                }
            });
            oa.start();
        }
        else if (change == View.VISIBLE && cur == View.GONE){ //出现
            ObjectAnimator oa = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
            oa.setDuration(200);
            oa.start();
            v.setVisibility(change);
        }
    }
    public void pointLoseK(View v){
        PointChangeInternal(v, -10);
    }
    public void pointLoseH(View v) {
        PointChangeInternal(v, -1);
    }
    public void pointGainK(View v){
        PointChangeInternal(v, 10);
    }
    public void pointGainH(View v) {
        PointChangeInternal(v, 1);
    }
}
