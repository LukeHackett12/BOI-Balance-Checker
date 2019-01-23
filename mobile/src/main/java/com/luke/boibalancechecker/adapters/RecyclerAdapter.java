package com.luke.boibalancechecker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luke.boibalancechecker.R;
import com.luke.boibalancechecker.setup.SetupLoginActivity;
import com.luke.boibalancechecker.helpers.KeyStoreHelper;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static com.luke.boibalancechecker.views.MainActivity.BOI_ALIAS;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private String[] options;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;

        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public RecyclerAdapter(String[] options) {
        this.options = options;
    }

    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setting_list_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TextView title = holder.view.findViewById(R.id.settingTitle);
        title.setText(options[position]);

        switch (options[position]) {
            case "Security":
                addSecurityButtons(holder);
                break;
            case "Account Details":
                try {
                    addAccountButtons(holder);
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                }
                break;
            case "Danger Zone":
                addRemoveButton(holder);
                break;
        }
    }

    private void addSecurityButtons(MyViewHolder holder) {
        LinearLayout layout = holder.view.findViewById(R.id.setting_list_view);

        Button remove = new Button(holder.view.getContext());//holder.view.findViewById(R.id.recyclerButton);
        remove.setText(R.string.add_pin);
        remove.setId(R.id.pinButton);
        remove.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        remove.setOnClickListener(v -> {
            Intent intent = new Intent(holder.view.getContext(), SetupLoginActivity.class);
            holder.view.getContext().startActivity(intent);
        });

        layout.addView(remove);
    }

    private void addAccountButtons(MyViewHolder holder) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableKeyException {
        LinearLayout layout = holder.view.findViewById(R.id.setting_list_view);

        SharedPreferences accountDetails = holder.view.getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(BOI_ALIAS, null);
        PublicKey publicKey = keyStore.getCertificate(BOI_ALIAS).getPublicKey();

        String accountID = "accountID - " + KeyStoreHelper.decrypt(privateKey, accountDetails.getString("accountID", null));
        String dobDate = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("dobDate", null));
        String dobMonth = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("dobMonth", null));
        String dobYear = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("dobYear", null));

        String dob = "Date of Birth - " + dobDate + "/" + dobMonth + "/" + dobYear;

        String phoneNum = "phoneNum - " + KeyStoreHelper.decrypt(privateKey, accountDetails.getString("phoneNum", null));
        String sixDigitCode = "sixDigitCode - " + KeyStoreHelper.decrypt(privateKey, accountDetails.getString("sixDigitCode", null));

        TextView accountText = new TextView(holder.view.getContext());
        TextView dobText = new TextView(holder.view.getContext());
        TextView phoneNumText = new TextView(holder.view.getContext());
        TextView sixDigitCodeText = new TextView(holder.view.getContext());

        accountText.setText(accountID);
        dobText.setText(dob);
        phoneNumText.setText(phoneNum);
        sixDigitCodeText.setText(sixDigitCode);

        TextView[] textViews = {accountText, dobText, phoneNumText, sixDigitCodeText};

        for (TextView view : textViews) {
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setPadding(20, 10, 0, 10);
            view.setTextSize(18);
            view.setOnClickListener(v -> {
                View popup = LayoutInflater.from(holder.view.getContext())
                        .inflate(R.layout.edit_popup, (ViewGroup) holder.view.getParent(), false);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.view.getContext());

                alertDialogBuilder.setView(popup);
                final EditText userInput = popup.findViewById(R.id.editTextDialogUserInput);

                alertDialogBuilder
                        .setMessage("New " + String.valueOf(view.getText()).split(" - ")[0] + ":")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            String input = String.valueOf(userInput.getText());
                            String field = String.valueOf(view.getText()).split(" - ")[0];

                            if (field.equals("Date of Birth")) {
                                String[] date = input.split("/");
                                String encryptedDay = KeyStoreHelper.encrypt(publicKey, date[0]);
                                String encryptedMonth = KeyStoreHelper.encrypt(publicKey, date[1]);
                                String encryptedYear = KeyStoreHelper.encrypt(publicKey, date[2]);

                                accountDetails.edit().putString("dobDate", encryptedDay).apply();
                                accountDetails.edit().putString("dobMonth", encryptedMonth).apply();
                                accountDetails.edit().putString("dobYear", encryptedYear).apply();
                            } else {
                                String encrypted = KeyStoreHelper.encrypt(publicKey, input);
                                accountDetails.edit().putString(field, encrypted).apply();
                            }

                            String newText = field + " - " + input;
                            view.setText(newText);
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            });
            layout.addView(view);
        }
    }

    private void addRemoveButton(MyViewHolder holder) {
        LinearLayout layout = holder.view.findViewById(R.id.setting_list_view);

        Button remove = new Button(holder.view.getContext());//holder.view.findViewById(R.id.recyclerButton);
        remove.setText(R.string.delete_account);
        remove.setId(R.id.removeButton);
        remove.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        remove.setOnClickListener(v -> {
            SharedPreferences accountDetails = holder.view.getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = accountDetails.edit();

            PublicKey publicKey = null;
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                publicKey = keyStore.getCertificate(BOI_ALIAS).getPublicKey();
            } catch (Exception ignore){

            }
            editor.putString("stage", KeyStoreHelper.encrypt(publicKey, "0"));
            editor.apply();

            Toast.makeText(holder.view.getContext(), "Deleted Information", Toast.LENGTH_SHORT).show();
        });

        layout.addView(remove);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return options.length;
    }


}
