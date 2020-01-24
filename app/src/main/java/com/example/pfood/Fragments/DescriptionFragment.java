package com.example.pfood.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pfood.Classes.AppSettings;
import com.example.pfood.Classes.Food;
import com.example.pfood.Classes.FoodCollectable;
import com.example.pfood.R;

public class DescriptionFragment extends Fragment {

    private View rootView;
    private ImageView foodImage;
    private TextView foodName, foodPrice, foodDescription;
    private Button addButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_description, container, false);

        getActivity().setTitle("Подробнее");

        foodImage = rootView.findViewById(R.id.description_image);
        foodName = rootView.findViewById(R.id.foodName);
        foodPrice = rootView.findViewById(R.id.foodPrice);
        foodDescription = rootView.findViewById(R.id.foodDescription);
        addButton = rootView.findViewById(R.id.addToCart);
        Glide.with(foodImage).load(AppSettings.getInstance().clickedFood.getImageUrl()).into(foodImage);
        foodName.setText(AppSettings.getInstance().clickedFood.getName());

        foodPrice.setText(AppSettings.getInstance().clickedFood.getPrice() + "\u20BD");

        foodDescription.setText(AppSettings.getInstance().clickedFood.getDescription());

        final LinearLayout countControlContainer = rootView.findViewById(R.id.count_control_ll);
        final TextView tvCount = rootView.findViewById(R.id.cartFoodCount);
        ImageButton bAdd = rootView.findViewById(R.id.button_add);
        ImageButton bRemove = rootView.findViewById(R.id.button_remove);

        if (AppSettings.getInstance().findFood(AppSettings.getInstance().clickedFood.getId()) != null
                && AppSettings.getInstance().findFood(AppSettings.getInstance().clickedFood.getId()).getFoodCount() >= 1) {
            addButton.setVisibility(View.GONE);
            countControlContainer.setVisibility(View.VISIBLE);
            tvCount.setText(AppSettings.getInstance().findFood(AppSettings.getInstance().clickedFood.getId()).getFoodCount().toString());
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food clickedFood = AppSettings.getInstance().clickedFood;
                Integer clickedFood_id = clickedFood.getId();

                AppSettings.getInstance().foodCache.add(clickedFood);
                AppSettings.getInstance().foodCount++;

                if (AppSettings.getInstance().findFood(clickedFood_id) == null) {
                    AppSettings.getInstance().foodCart.add(
                            new FoodCollectable(clickedFood, 1));
                } else {
                    AppSettings.getInstance().findFood(clickedFood_id)
                            .setFoodCount(AppSettings.getInstance().findFood(clickedFood_id).getFoodCount() + 1);
                }

                AppSettings.getInstance().tvNum.setVisibility(View.VISIBLE);
                AppSettings.getInstance().ivCircle.setVisibility(View.VISIBLE);

                AppSettings.getInstance().fullNumPrice += clickedFood.getPrice();
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                AppSettings.getInstance().tvNum.startAnimation(anim);
                AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");

                Log.i("DB FOOD CHECK", clickedFood.getName() + " " + AppSettings.getInstance().findFood(clickedFood_id).getFoodCount());
                addButton.setVisibility(View.GONE);
                countControlContainer.setVisibility(View.VISIBLE);
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food clickedFood = AppSettings.getInstance().clickedFood;
                Integer clickedFood_id = clickedFood.getId();

                AppSettings.getInstance().foodCache.add(clickedFood);
                AppSettings.getInstance().foodCount++;

                if (AppSettings.getInstance().findFood(clickedFood_id) == null) {
                    AppSettings.getInstance().foodCart.add(
                            new FoodCollectable(clickedFood, 1));
                } else {
                    AppSettings.getInstance().findFood(clickedFood_id)
                            .setFoodCount(AppSettings.getInstance().findFood(clickedFood_id).getFoodCount() + 1);
                    tvCount.setText(AppSettings.getInstance().findFood(clickedFood_id).getFoodCount().toString());
                }

                AppSettings.getInstance().tvNum.setVisibility(View.VISIBLE);
                AppSettings.getInstance().ivCircle.setVisibility(View.VISIBLE);

                AppSettings.getInstance().fullNumPrice += clickedFood.getPrice();
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                AppSettings.getInstance().tvNum.startAnimation(anim);
                AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");

                Log.i("DB FOOD CHECK", clickedFood.getName() + " " + AppSettings.getInstance().findFood(clickedFood_id).getFoodCount());
            }
        });

        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food clickedFood = AppSettings.getInstance().clickedFood;
                Integer clickedFood_id = clickedFood.getId();

                if (AppSettings.getInstance().findFood(clickedFood_id) != null) {
                    if (AppSettings.getInstance().findFood(clickedFood_id).getFoodCount() > 0) {
                        AppSettings.getInstance().findFood(clickedFood_id)
                                .setFoodCount(AppSettings.getInstance().findFood(clickedFood_id).getFoodCount() - 1);

                        AppSettings.getInstance().foodCache.remove(clickedFood);
                        AppSettings.getInstance().foodCount--;

                        AppSettings.getInstance().tvNum.setVisibility(View.VISIBLE);
                        AppSettings.getInstance().ivCircle.setVisibility(View.VISIBLE);

                        AppSettings.getInstance().fullNumPrice -= clickedFood.getPrice();
                        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                        AppSettings.getInstance().tvNum.startAnimation(anim);
                        AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");
                        tvCount.setText(AppSettings.getInstance().findFood(clickedFood_id).getFoodCount().toString());
                    }
                }

                Log.i("DB FOOD CHECK", clickedFood.getName() + " " + AppSettings.getInstance().findFood(clickedFood_id).getFoodCount());
            }
        });

        return rootView;
    }
}
