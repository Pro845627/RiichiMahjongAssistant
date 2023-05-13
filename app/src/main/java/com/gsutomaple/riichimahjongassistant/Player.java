package com.gsutomaple.riichimahjongassistant;

import android.animation.*;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.riichimahjongassistant.R;

public class Player {
    public Player(int id, int cardId, int cardSubId, int pointId,int reachId, int changeId, MainActivity context, int points, boolean reached){
        this.id = id;
        this.cardId = cardId;
        this.cardSubId = cardSubId;
        this.pointId = pointId;
        this.reachId = reachId;
        this.changeId = changeId;
        this.points = points;
        this.reached = reached;
        this.context = context;
        this.cardColorId = Data.Round == id ? R.color.colorOya : R.color.colorKo;
        getCardView().setOnClickListener(v -> {
            context.switchVisibility(getSubView(), id == 4 && Data.Is_Three);
        });
        getCardView().setOnLongClickListener(v-> {
            if (!Data.Is_Three ||  id != 4){
                this.context.activity.showSummary(id);
            }
            return true;
        });
    }
    private final int id;
    private final int cardId;
    private final int cardSubId;
    private final int pointId;
    private final int reachId;
    private final int changeId;
    private final MainActivity context;
    private int points;
    private int cardColorId;
    private boolean reached;

    public int getId(){
        return id;
    }
    public int getCardId(){
        return cardId;
    }
    public int getCardSubId(){
        return cardSubId;
    }
    public int getPoints(){
        return points;
    }
    public boolean getReached(){return reached;}
    public void setPoints(int points){
        this.points = points;
        getPointsView().setText(String.valueOf(points));
    }
    public void addPoints(int cound){
        this.points += cound;
        getPointsView().setText(String.valueOf(points));
    }
    public void addPointsWithAnimation(int count){
        if (count == 0){
            return;
        }
        boolean gain = count > 0;
        getChangeView().setText((gain ? "+" : "-") + Math.abs(count) * 100);
        getChangeView().setTextColor(context.getColor(gain ? R.color.colorGain : R.color.colorLose));
        context.fadeInAndOut(getChangeView(), 5000);
        ValueAnimator va = ValueAnimator.ofInt(this.points,  this.points + count);
        va.setDuration(3000);
        va.setInterpolator(new AccelerateDecelerateInterpolator());
        va.start();
        va.addUpdateListener(v -> {
            setPoints((int)v.getAnimatedValue());
        });
    }
    public void refreshCardColor(){
        if (id == 4){
            getCardView().setAlpha(Data.Is_Three ? 0.5f : 1f);
        }
        int colorId;
        if (Data.Round == id)
            colorId = reached ? R.color.colorOyaReach : R.color.colorOya;
        else
            colorId = reached ? R.color.colorReach : R.color.colorKo;
        Log.d(String.valueOf(this.cardId), context.getColor(cardColorId) + " -> " + context.getColor(colorId));
        ObjectAnimator om = ObjectAnimator.ofObject(getCardView(), "cardBackgroundColor",new ArgbEvaluator(), context.getColor(cardColorId), context.getColor(colorId));
        this.cardColorId = colorId;
        om.setInterpolator(new AccelerateDecelerateInterpolator());
        om.setDuration(1000);
        om.start();
    }
    public void refresh(){
        getPointsView().setText(String.valueOf(points)); //点数刷新
        refreshCardColor(); //卡片颜色刷新
        getReachView().setVisibility(this.reached ? View.VISIBLE : View.GONE);
    }
    public void reach(){
        if (this.reached){
            return;
        }
        this.reached = true;
        getReachView().setVisibility(View.VISIBLE);
        refreshCardColor();
        context.nextSupply();
        this.setPoints(this.points - 10);
    }
    public void unreach(){
        this.reached = false;
        refreshCardColor();
        getReachView().setVisibility(View.GONE);
    }
    public TextView getPointsView(){
        return context.findViewById(pointId);
    }
    public TextView getReachView(){
        return context.findViewById(reachId);
    }
    public TextView getChangeView(){
        return context.findViewById(changeId);
    }
    public CardView getCardView(){
        return context.findViewById(cardId);
    }
    public RelativeLayout getSubView(){
        return context.findViewById(cardSubId);
    }
}
