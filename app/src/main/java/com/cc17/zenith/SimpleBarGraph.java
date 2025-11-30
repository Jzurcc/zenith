package com.cc17.zenith;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class SimpleBarGraph extends View {
    private Paint barPaint;
    private List<Integer> dataPoints;
    // Colors: Teal, Orange, Dark Teal
    private int[] colors = {Color.parseColor("#00BCD4"), Color.parseColor("#FF7043"), Color.parseColor("#006064")};

    public SimpleBarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setData(List<Integer> data) {
        this.dataPoints = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataPoints == null || dataPoints.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float barHeight = height / 5; // Spacing logic

        // Normalize max value
        int max = 0;
        for (int val : dataPoints) if (val > max) max = val;

        for (int i = 0; i < dataPoints.size(); i++) {
            if (i >= colors.length) break;

            barPaint.setColor(colors[i]);
            float barWidth = (dataPoints.get(i) / (float) max) * width;
            float top = i * (barHeight + 30) + 20; // 30 is vertical spacing

            RectF rect = new RectF(0, top, barWidth, top + barHeight);
            canvas.drawRoundRect(rect, 10, 10, barPaint);
        }
    }
}