package com.cc17.zenith;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class SimpleLineGraph extends View {
    private Paint linePaint, dotPaint, fillPaint, gridPaint;
    private List<Integer> dataPoints;
    private Path path = new Path();
    private Path fillPath = new Path();

    public SimpleLineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 1. Line Paint
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#29B6F6"));
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        // REMOVED CornerPathEffect to prevent the line from missing the points

        // 2. Dot Paint
        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        // 3. Fill Paint
        fillPaint = new Paint();
        fillPaint.setColor(Color.parseColor("#E1F5FE"));
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        // REMOVED CornerPathEffect

        // 4. Grid Paint
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

        // --- STEP 1: Draw Background Grid ---
        int gridLines = 5;
        for (int i = 0; i < gridLines; i++) {
            float y = height - (i * (height / (float)gridLines));
            canvas.drawLine(0, y, width, y, gridPaint);
        }

        if (dataPoints == null || dataPoints.isEmpty()) return;

        float xStep = dataPoints.size() > 1 ? width / (dataPoints.size() - 1) : 0;

        int max = 0;
        for (int val : dataPoints) if (val > max) max = val;
        if (max == 0) max = 1;

        path.reset();
        fillPath.reset();

        fillPath.moveTo(0, height);

        float prevX = 0;
        float prevY = 0;

        // --- STEP 2: Calculate Smooth Path (Bezier) ---
        for (int i = 0; i < dataPoints.size(); i++) {
            float x = i * xStep;
            float y = height - ((dataPoints.get(i) / (float) max) * (height * 0.7f)) - 40;

            if (i == 0) {
                path.moveTo(x, y);
                fillPath.lineTo(x, y);
            } else {
                // Use Cubic Bezier to curve the line smoothly THROUGH the points
                // We use the midpoint X as the control point X for a horizontal-ish curve
                float cp1X = (prevX + x) / 2;
                float cp1Y = prevY;
                float cp2X = (prevX + x) / 2;
                float cp2Y = y;

                path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, x, y);
                fillPath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, x, y);
            }
            prevX = x;
            prevY = y;
        }

        fillPath.lineTo(width, height);
        fillPath.close();

        // --- STEP 3: Draw Line & Fill ---
        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(path, linePaint);

        // --- STEP 4: Draw Circles (On top) ---
        for (int i = 0; i < dataPoints.size(); i++) {
            float x = i * xStep;
            float y = height - ((dataPoints.get(i) / (float) max) * (height * 0.7f)) - 40;

            // Draw Blue Ring
            linePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, 14f, linePaint);

            // Restore Style
            linePaint.setStyle(Paint.Style.STROKE);

            // Draw White Center
            canvas.drawCircle(x, y, 9f, dotPaint);
        }
    }
}