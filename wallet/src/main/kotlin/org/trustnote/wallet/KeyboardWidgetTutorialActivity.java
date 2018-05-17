/*
 * Copyright (C) 2011 - Riccardo Ciovati
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustnote.wallet;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.trustnote.wallet.uiframework.BaseActivity;
import org.trustnote.wallet.widget.keyboard.BasicOnKeyboardActionListener;
import org.trustnote.wallet.widget.keyboard.CustomKeyboardView;

public class KeyboardWidgetTutorialActivity extends BaseActivity {

	private CustomKeyboardView mKeyboardView;
	private EditText mTargetView;
	private Keyboard mKeyboard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
//		mKeyboard = new Keyboard(this, R.xml.mnemonic);
		mTargetView = (EditText) findViewById(R.id.target);
		mTargetView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Dobbiamo intercettare l'evento onTouch in modo da aprire la
				// nostra tastiera e prevenire che venga aperta quella di
				// Android
				showKeyboardWithAnimation();
				return true;
			}
		});
//
//		mTargetView.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				System.out.println(s.toString());
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//
//			}
//		});
//
//		mTargetView.setText("TestFromMyCode");
//
//		mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
//		mKeyboardView.setKeyboard(mKeyboard);
//		mKeyboardView
//				.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(
//						this));
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event);
	}

	/***
	 * Mostra la tastiera a schermo con una animazione di slide dal basso
	 */
//	private void showKeyboardWithAnimation() {
//		if (mKeyboardView.getVisibility() == View.GONE) {
////			Animation animation = AnimationUtils
////					.loadAnimation(KeyboardWidgetTutorialActivity.this,
////							R.anim.slide_in_bottom);
////			mKeyboardView.showWithAnimation(animation);
//			mKeyboardView.setVisibility(View.VISIBLE);
//		}
//	}

	@Override
	public void injectDependencies(@NotNull TApplicationComponent graph) {

	}
}