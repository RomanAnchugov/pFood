package com.example.pfood.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pfood.R;
import com.example.pfood.model.UserItem;
import com.example.pfood.utils.FirebaseUtils;
import com.example.pfood.utils.KeyboardUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    public static final String KEY_IS_OPEN_FIREBASE = "key_is_open_firebase";

    private static final int LOGIN_REQUEST_CODE = 256;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().getBoolean(KEY_IS_OPEN_FIREBASE)) {
            goToLoginScreen();
        }
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.profile_title);

        Button loginButton = view.findViewById(R.id.profile_login_button);
        final EditText profileNameEt = view.findViewById(R.id.profile_name_et);
        final EditText profileAddress = view.findViewById(R.id.profile_addres_et);
        final TextView ratingText = view.findViewById(R.id.profile_rating_position_tv);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        if (FirebaseUtils.isUserLoggedIn()) {
            loginButton.setText(R.string.save);
            profileAddress.setEnabled(true);
            profileNameEt.setEnabled(true);

            //set data from database to edit text
            database
                    .child("users")
                    .child(FirebaseUtils.getUserId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<UserItem> generic
                                    = new GenericTypeIndicator<UserItem>() {
                            };

                            UserItem value = dataSnapshot.getValue(generic);

                            Log.d(TAG, "onDataChange: Hallo? " + value);

                            if (value != null) {
                                profileAddress.setText(value.address);
                                profileNameEt.setText(value.name);

                                ratingText.setVisibility(View.VISIBLE);
                                ratingText.setText(getContext().getString(
                                        R.string.profile_rating_placeholder,
                                        value.ratingPosition.toString(),
                                        value.monthRatingPostion.toString(),
                                        value.rating.toString()
                                        )
                                );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            ratingText.setVisibility(View.GONE);
            loginButton.setText(R.string.login);
            profileAddress.setEnabled(false);
            profileNameEt.setEnabled(false);
        }

        //set on click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!FirebaseUtils.isUserLoggedIn()) {
                    goToLoginScreen();
                } else {
                    KeyboardUtils.hideKeyboardFrom(getContext(), view);

                    database.child("users").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<HashMap<String, UserItem>> generic
                                            = new GenericTypeIndicator<HashMap<String, UserItem>>() {
                                    };

                                    //map of users
                                    HashMap<String, UserItem> value = dataSnapshot.getValue(generic);

                                    //new User
                                    if (value.get(FirebaseAuth.getInstance().getUid()) == null) {
                                        UserItem userItem = new UserItem(
                                                profileNameEt.getText().toString(),
                                                profileAddress.getText().toString(),
                                                //TODO: потенциально инвайт код может быть у двух пользователей одинаковым - придумать как этого избежать
                                                Long.toString(Calendar.getInstance().getTimeInMillis()),
                                                0L,
                                                0L,
                                                (long) value.keySet().size() + 1,
                                                0L,
                                                (long) value.keySet().size() + 1
                                        );

                                        database
                                                .child("users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .setValue(userItem)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Snackbar.make(view, R.string.default_error, Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {//old user
                                        Map<String, Object> updates = new HashMap<>();

                                        updates.put("name", profileNameEt.getText().toString());
                                        updates.put("address", profileAddress.getText().toString());

                                        database
                                                .child("users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .updateChildren(updates)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Snackbar.make(view, R.string.default_error, Snackbar.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Snackbar.make(view, R.string.edit_profile_success, Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(getView(), getContext().getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                    return;
                }

                Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void goToLoginScreen() {
        List<String> whitelistedCountries = new ArrayList<>();
        whitelistedCountries.add("ru");

        AuthUI.IdpConfig phoneLoginConfig = new AuthUI.IdpConfig.PhoneBuilder()
                .setWhitelistedCountries(whitelistedCountries)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                phoneLoginConfig))
                        .build(),
                LOGIN_REQUEST_CODE);
    }
}
