package com.example.test.activities;

import static com.klaytn.caver.kct.kip7.KIP7ConstantData.ABI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.test.MainActivity;
import com.example.test.databinding.ActivityCaverJavaTestBinding;
import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;

import java.math.BigInteger;
import java.util.Arrays;

public class CaverJavaTestActivity extends AppCompatActivity {

    private static final String TAG = CaverJavaTestActivity.class.getSimpleName();

    ActivityCaverJavaTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCaverJavaTestBinding.inflate(getLayoutInflater());

        binding.button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Caver caver = new Caver(Caver.BAOBAB_URL);
                                String contractAddress = "";

                                SingleKeyring executor = KeyringFactory.createFromPrivateKey("");
                                caver.wallet.add(executor);
                                Contract contract = new Contract(caver, ABI, contractAddress);

                                contract.getMethods().forEach((methodName, contractMethod) -> {
                                    Log.d(TAG, "methodName : " + methodName + ", ContractMethod : " + contractMethod);
                                });
                                Log.d(TAG, "ContractAddress : " + contract.getContractAddress());

                                SendOptions sendOptions = new SendOptions();
                                sendOptions.setFrom(executor.getAddress());
                                sendOptions.setGas(BigInteger.valueOf(400000));
                                //sendOptions.setValue(BigInteger.valueOf(10));

                                Object[] array = {"", 100};
                                TransactionReceipt.TransactionReceiptData receipt = contract.getMethod("transfer").send(Arrays.asList(array), sendOptions);
                                Log.d(TAG, receipt.toString());
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
                            }

                        }
                    }).start();



//                    contract.getMethods().forEach((methodName, contractMethod) -> {
//                        Log.d(TAG, "methodName : " + methodName + ", ContractMethod : " + contractMethod);
//                    });
//                    Log.d(TAG, "ContractAddress : " + contract.getContractAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
        });

        setContentView(binding.getRoot());
    }
}