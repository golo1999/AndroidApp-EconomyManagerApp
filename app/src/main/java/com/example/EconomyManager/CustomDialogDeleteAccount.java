package com.example.EconomyManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomDialogDeleteAccount extends AppCompatDialogFragment
{
    private EditText password;
    private CustomDialogListener listener;
    private int choice;

    public CustomDialogDeleteAccount(int choice)
    {
        this.choice=choice;
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        if(getActivity()!=null)
        {
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.linearlayout_custom_dialog, null);

            if(choice==0 || choice==1)
            {
                builder.setView(view).setPositiveButton(getResources().getString(R.string.proceed), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String typedPassword=String.valueOf(password.getText());
                        if(choice==0)
                            listener.getTypedPasswordAndDeleteTheAccount(typedPassword);
                        else if(choice==1)
                            listener.getTypedPasswordAndResetTheDatabase(typedPassword);
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
            }
            else if(choice==2)
                listener.deleteAccount();
            else if(choice==3)
                listener.resetDatabase();

            password=view.findViewById(R.id.deleteAccountPassword);
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try
        {
            listener=(CustomDialogListener)context;
        }
        catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString()+" must implement the listener");
        }
    }

    public interface CustomDialogListener
    {
        void getTypedPasswordAndDeleteTheAccount(String pass);
        void deleteAccount();
        void getTypedPasswordAndResetTheDatabase(String pass);
        void resetDatabase();
    }
}
