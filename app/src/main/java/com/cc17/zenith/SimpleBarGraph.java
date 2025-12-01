package com.cc17.zenith;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class SimpleBarGraph extends View {
    private Paint barPaint, gridPaint;
    private List<Integer> dataPoints;
    private int[] colors = {Color.parseColor("#00BCD4"), Color.parseColor("#FF7043"), Color.parseColor("#006064")};

    public SimpleBarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Grid Paint setup
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(3f);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{20f, 20f}, 0));
    }

    public void setData(List<Integer> data) {
        this.dataPoints = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        // --- STEP 1: Draw Grid Lines (Background) ---
        int gridLines = 5;
        for (int i = 0; i < gridLines; i++) {
            // Draw lines distributed evenly
            float y = height - (i * (height / (float)gridLines));
            canvas.drawLine(0, y, width, y, gridPaint);
        }

        if (dataPoints == null || dataPoints.isEmpty()) return;

        // --- STEP 2: Draw Bars ---
        float barHeight = height / 5;

        int max = 0;
        for (int val : dataPoints) if (val > max) max = val;
        if (max == 0) max = 1;

        for (int i = 0; i < dataPoints.size(); i++) {
            if (i >= colors.length) break;

            barPaint.setColor(colors[i]);

            // Scale bar width relative to screen width
            float barWidth = (dataPoints.get(i) / (float) max) * (width * 0.9f); // 0.9f to keep some padding
            float top = i * (barHeight + 30) + 20;

            RectF rect = new RectF(0, top, barWidth, top + barHeight);
            canvas.drawRoundRect(rect, 10, 10, barPaint);
        }
    }
}