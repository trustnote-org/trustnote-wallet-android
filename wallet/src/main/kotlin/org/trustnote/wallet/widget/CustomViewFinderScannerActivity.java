package org.trustnote.wallet.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.trustnote.wallet.R;
import org.trustnote.wallet.TApp;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CustomViewFinderScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    private boolean mFlash = false;

    private static Bitmap scanFrameBitmap = BitmapFactory.decodeResource(TApp.resources, R.drawable.img_scan_frame);
    private static String KEY_SCAN_RESULT = "KEY_SCAN_RESULT";

    private ImageView flashSwitcher;
    private TextView flashText;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_custom_view_finder_scanner);

        findViewById(R.id.toolbar_left_arrow_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);

        findViewById(R.id.area_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlash(v);
            }
        });

        flashSwitcher = (ImageView) findViewById(R.id.flash_switcher);
        flashText = (TextView) findViewById(R.id.flash_txt);

        mScannerView.setFlash(mFlash);

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    public void toggleFlash(View v) {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);

        flashSwitcher.setImageResource(!mFlash ? R.drawable.ic_scan_flash_off : R.drawable.ic_scan_flash_on);

        flashText.setText(mFlash ? R.string.scan_flash_off : R.string.scan_flash_on);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    public static String parseScanResult(Intent data) {
        if (data != null) {
            return data.getStringExtra(KEY_SCAN_RESULT);
        }
        return "";
    }

    @Override
    public void handleResult(Result rawResult) {
        //        Toast.makeText(this, "Contents = " + rawResult.getText() +
        //                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        if (!TextUtils.isEmpty(rawResult.getText())) {
            Intent data = new Intent();
            data.putExtra(KEY_SCAN_RESULT, rawResult.getText());
            setResult(RESULT_OK, data);
            onBackPressed();
        }

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(CustomViewFinderScannerActivity.this);
            }
        }, 2000);
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "ZXing";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }

        public void drawViewFinderBorder(Canvas canvas) {
            Rect framingRect = getFramingRect();

            int boderWidth = (int) TApp.resources.getDimension(R.dimen.line_gap_4);
            Rect newFramingRect = new Rect(framingRect.left - boderWidth, framingRect.top - boderWidth, framingRect.right + boderWidth, framingRect.bottom + boderWidth);

            canvas.drawBitmap(scanFrameBitmap, null, newFramingRect, new Paint());


            int distance = TApp.resources.getDimensionPixelOffset(R.dimen.line_gap_20);
            int textSize = TApp.resources.getDimensionPixelSize(R.dimen.text_14);
            r = new Rect(0, framingRect.bottom  + boderWidth + distance, getWidth(), framingRect.bottom  + boderWidth + distance + 200);

            String scanHint = TApp.resources.getString(R.string.scan_hint);

            drawCenter(canvas, new Paint(), scanHint, framingRect.bottom + boderWidth + distance +textSize);

        }

        private Rect r = new Rect();

        private void drawCenter(Canvas canvas, Paint paint, String text, float ypointRef) {
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setTextSize(TApp.resources.getDimensionPixelSize(R.dimen.text_14));

            paint.getTextBounds(text, 0, text.length(), r);
            float x = getWidth()/2f - r.width() / 2f;
            float y = ypointRef;
            canvas.drawText(text, x, y, paint);
        }


    }
}
