package com.telpo.tps550.api.demo.nfc;// NFCBoundingBoxView.java

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.common.demo.R;

public class NFCBoundingBoxView extends View {

    private Context context;

    public NFCBoundingBoxView(Context context) {
        super(context);

        this.context = context;
    }

    public NFCBoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 创建一个 Paint 对象用于设置画笔属性
        Paint paint = new Paint();
        paint.setColor(Color.RED); // 设置颜色为红色
        paint.setStyle(Paint.Style.STROKE); // 设置画笔样式为描边
        paint.setStrokeWidth(5); // 设置描边宽度

        // 获取方框的坐标和尺寸
        int left = getWidth() - 300;
        int top = getHeight() - 500;
        int right = getWidth() - 10;
        int bottom = getHeight() - 50;

        // 在 canvas 上绘制方框
        canvas.drawRect(left, top, right, bottom, paint);

        // 设置文本属性
        paint.setColor(Color.RED);
        paint.setTextSize(20);
        paint.setStrokeWidth(2); // 设置描边宽度

        // 获取文本的宽度和高度
        String text = context.getString(R.string.nfc_area_text);
        float textWidth = paint.measureText(text);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float textHeight = metrics.bottom - metrics.top;

        // 计算文本的坐标
        float x = ((right-left) - textWidth) / 2;
        float y = ((bottom - top) - textHeight) / 2;

        // 在 canvas 上绘制文本
        canvas.drawText(text, left + x, top + y, paint);
    }
}
