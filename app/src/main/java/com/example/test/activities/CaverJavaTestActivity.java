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
                                /**
                                 * Klaytn Baobab 네트워크 설정
                                 */
                                Caver caver = new Caver(Caver.BAOBAB_URL);

                                /**
                                 * STC Token Contract 주소 (KIP-7)
                                 */
                                String contractAddress = "0x42769193c97b72cbf9620fce567d175b80d50621";

                                /**
                                 * Contract 를 실행 시키는 Wallet Private Key
                                 */
                                SingleKeyring executor = KeyringFactory.createFromPrivateKey("0xf19e8c98358901e1ff5968881179ee0bc7d920e2c8eb8c1f7c29e6e86e6dc4f2");
                                caver.wallet.add(executor);

                                /**
                                 * Contract 생성 ( KIP-7 ABI 로 설정 )
                                 */
                                Contract contract = new Contract(caver, ABI, contractAddress);

                                /**
                                 * Contract 에서 호출 할 수 있는 Method 리스트 가져오기
                                 */
                                contract.getMethods().forEach((methodName, contractMethod) -> {
                                    Log.d(TAG, "methodName : " + methodName + ", ContractMethod : " + contractMethod);
                                });
                                Log.d(TAG, "ContractAddress : " + contract.getContractAddress());

                                /**
                                 * Contract Method 호출 Option 설정
                                 */
                                SendOptions sendOptions = new SendOptions();
                                // 컨트랙트 호출자
                                sendOptions.setFrom(executor.getAddress());
                                // 최대 Gas 사용할 량 설정
                                sendOptions.setGas(BigInteger.valueOf(400000));

                                /**
                                 * Method : transfer
                                 * Param1 : Token을 전달 받을 Address
                                 * Param2 : Token 량
                                 */
                                Object[] array = {"0xdA475CDcFcfCf91896C7FE79Bb9E0a7c5506aDBC", 100};
                                TransactionReceipt.TransactionReceiptData receipt = contract.getMethod("transfer").send(Arrays.asList(array), sendOptions);
                                Log.d(TAG, "receipt.getBlockHash:" + receipt.getBlockHash());
                                Log.d(TAG, "receipt.getBlockNumber:" + receipt.getBlockNumber());
                                Log.d(TAG, receipt.toString());
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
                            }

                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
        });

        setContentView(binding.getRoot());
    }
}