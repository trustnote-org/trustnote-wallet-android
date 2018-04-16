package org.trustnote.wallet.settings;

import android.webkit.ValueCallback;

import org.trustnote.wallet.debugui.TestSocket;
import org.trustnote.wallet.js.JSApi;
import org.trustnote.wallet.pojo.Credential;
import org.trustnote.wallet.pojo.TProfile;
import org.trustnote.wallet.tttui.QRFragment;
import org.trustnote.wallet.util.Utils;
import org.trustnote.wallet.walletadmin.NewSeedActivity;
import org.trustnote.wallet.walletadmin.SimpleFragment;
import org.trustnote.wallet.walletadmin.SimpleFragmentActivity;
import org.trustnote.wallet.walletadmin.WalletModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsDataFactory {

    public static final String GROUP_TEST = "Test";
    public static final String GROUP_WALLET = "Wallet management";

    public static List<SettingGroup> makeSettings() {
        return Arrays.asList(makeTestGroup(),
                makeWalletGroup());
    }

    public static SettingGroup makeTestGroup() {
        return new SettingGroup(GROUP_TEST, makeTests());
    }

    public static List<SettingItem> makeTests() {

        SettingItem jsApi = new SettingItem("Test JS API: new mnomonic", false);
        jsApi.action = new Runnable() {
            @Override
            public void run() {
                new JSApi().mnemonic(new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        };

        SettingItem walletIdJSApi = new SettingItem("Test new wallet seed JS API: expected: LyzbDDiDedJh+fUHMFAXpWSiIw/Z1Tgve0J1+KOfT3w=", false);
        walletIdJSApi.action = new Runnable() {
            @Override
            public void run() {
                WalletModel.getInstance().testCreateWallet();
            }
        };


        SettingItem hubConnect = new SettingItem("Test hub connect", false);
        hubConnect.action = new Runnable() {
            @Override
            public void run() {
                TestSocket.test();
            }
        };

        SettingItem testJSSignWithDeviceMessageHash = new SettingItem("testJSSignWithDeviceMessageHash", false);
        testJSSignWithDeviceMessageHash.action = new Runnable() {
            @Override
            public void run() {

                String orignData = "{\"challenge\":\"C4q7l7jDyLVvFB9YDQu2M3J1N0PC/9NkNwbUNQLa\",\"pubkey\":\"A1woEiM/LdDHLvTYUvlTZpsTI+82AphGZAvHalie5Nbw\",\"signature\":\"cphmz+vksrdwMDAUlNNXUPo+1oL7fTaYiKoI0rQNflZpOyQBZpovA3s79HTByxvWrUo2Wy/NerrpsLzaaXm/2g==\"}";

                new JSApi().getDeviceMessageHashToSign(orignData, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String hashValue) {
                        Utils.debugLog(hashValue);


                        //fun sign(b64_hash: String, xPrivKey: String, path: String, cb: ValueCallback<String>) {

                        new JSApi().sign(hashValue, WalletModel.getInstance().getProfile().getXPrivKey(), "\"m/1'\"", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String signRes) {
                                Utils.debugLog(signRes);

                                // /r1gbvHPi8NLGpKoderkk1QJHbooOrDEi81rE2sXKtYCQFDHPxCdvmPPj17czehyptxL3T7dPKK2FqACbcdyiQ==

                            }
                        });

                    }
                });
            }
        };


        SettingItem testJSVerifySign = new SettingItem("testJSVerifySign", false);
        testJSVerifySign.action = new Runnable() {
            @Override
            public void run() {
                runTestJSVerifySign();
            }
        };

        SettingItem testQrCode = new SettingItem("Test QR CODE");
        testQrCode.action = new Runnable() {
            @Override
            public void run() {
                SimpleFragmentActivity.startMe(QRFragment.class.getCanonicalName());
            }
        };

        return Arrays.asList(jsApi, walletIdJSApi, hubConnect, testJSSignWithDeviceMessageHash, testJSVerifySign, testQrCode);
    }


    public static void runTestJSVerifySign() {

        String orignData = "\"32R6yukt9vRAL+FAWSbohdDpQhTcO8LcjRix6uBA0NY=\"";
        String sign = "\"cphmz+vksrdwMDAUlNNXUPo+1oL7fTaYiKoI0rQNflZpOyQBZpovA3s79HTByxvWrUo2Wy/NerrpsLzaaXm/2g==\"";

        String pubKey = "\"A1woEiM/LdDHLvTYUvlTZpsTI+82AphGZAvHalie5Nbw\"";
        new JSApi().verify(orignData, sign, pubKey, new ValueCallback<String>() {
            public void onReceiveValue(String signRes) {
                Utils.debugLog(signRes);
            }

        });

    }

    public static SettingGroup makeWalletGroup() {
        return new SettingGroup(GROUP_WALLET, makeWallets());
    }

    public static List<SettingItem> makeWallets() {
        List<SettingItem> res = new ArrayList();
        TProfile profile = WalletModel.getInstance().getProfile();
        if (profile != null) {
            for (Credential credential : profile.getCredentials()) {
                SettingItem oneWallet = new SettingItem(credential.getWalletName());
                res.add(oneWallet);
            }

            SettingItem newWallet = new SettingItem("+  New");
            newWallet.action = new Runnable() {
                @Override
                public void run() {
                    SimpleFragmentActivity.startMe(SimpleFragment.class.getCanonicalName());
                }
            };
            res.add(newWallet);
        } else {

            SettingItem newSeed = new SettingItem("create wallet from new seed", true);
            newSeed.action = new Runnable() {
                @Override
                public void run() {
                    NewSeedActivity.startMe();
                }
            };
            res.add(newSeed);
        }

        return res;
    }

}

