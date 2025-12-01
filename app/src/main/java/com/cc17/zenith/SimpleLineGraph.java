package com.cc17.zenith;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class SimpleLineGraph extends View {
    private Paint linePaint, dotPaint, fillPaint, gridPaint;
    private List<Integer> dataPoints;

    public SimpleLineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 1. Line Paint - Added CornerPathEffect for rounded turns
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#29B6F6"));
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        // This rounds the sharp corners of the line
        linePaint.setPathEffect(new CornerPathEffect(60f));

        // 2. Dot Paint - White center
        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        // 3. Fill Paint - Light blue under the line
        fillPaint = new Paint();
        fillPaint.setColor(Color.parseColor("#E1F5FE"));
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setPathEffect(new CornerPathEffect(60f));

        // 4. Grid Paint - Dashed gray lines
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#E0E0E0")); // Light Gray
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(3f);
        // 10px on, 10px off dashed pattern
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
            float y = height - (i * (height / (float)gridLines));
            // Draw horizontal dashed line
            canvas.drawLine(0, y, width, y, gridPaint);
        }

        if (dataPoints == null || dataPoints.isEmpty()) return;

        // --- STEP 2: Calculate Path ---
        // Prevent divide by zero
        float xStep = dataPoints.size() > 1 ? width / (dataPoints.size() - 1) : 0;

        int max = 0;
        for (int val : dataPoints) if (val > max) max = val;
        if (max == 0) max = 1;

        Path path = new Path();
        Path fillPath = new Path();

        fillPath.moveTo(0, height);

        // We only construct the path here (Don't draw circles yet!)
        for (int i = 0; i < dataPoints.size(); i++) {
            float x = i * xStep;
            float y = height - ((dataPoints.get(i) / (float) max) * (height * 0.7f)) - 40; // 0.7f to leave room at top

            if (i == 0) {
                path.moveTo(x, y);
                fillPath.lineTo(x, y);
            } else {
                path.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
        }

        fillPath.lineTo(width, height);
        fillPath.close();

        // --- STEP 3: Draw Line & Fill ---
        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(path, linePaint);

        // --- STEP 4: Draw Circles (So they appear ON TOP of the line) ---
        for (int i = 0; i < dataPoints.size(); i++) {
            float x = i * xStep;
            float y = height - ((dataPoints.get(i) / (float) max) * (height * 0.7f)) - 40;

            // Draw Blue Ring (same paint as line)
            linePaint.setStyle(Paint.Style.FILL); // Switch to fill for the dot backing
            canvas.drawCircle(x, y, 14f, linePaint);

            // Restore line paint to Stroke for next redraw
            linePaint.setStyle(Paint.Style.STROKE);

            // Draw White Center
            canvas.drawCircle(x, y, 9f, dotPaint);
        }
    }
}