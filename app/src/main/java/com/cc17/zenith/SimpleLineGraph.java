package com.cc17.zenith;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class SimpleLineGraph extends View {
    private Paint linePaint, dotPaint, fillPaint;
    private List<Integer> dataPoints;

    public SimpleLineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#29B6F6")); // Light Blue
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setStrokeWidth(5f);

        fillPaint = new Paint();
        fillPaint.setColor(Color.parseColor("#E1F5FE")); // Very light blue fill
        fillPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(List<Integer> data) {
        this.dataPoints = data;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataPoints == null || dataPoints.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float xStep = width / (dataPoints.size() - 1);

        // Find max value to scale the graph
        int max = 0;
        for (int val : dataPoints) if (val > max) max = val;
        if (max == 0) max = 1;

        Path path = new Path();
        Path fillPath = new Path();

        fillPath.moveTo(0, height); // Start bottom left

        for (int i = 0; i < dataPoints.size(); i++) {
            float x = i * xStep;
            // Invert Y because canvas 0 is at top
            float y = height - ((dataPoints.get(i) / (float) max) * (height * 0.8f)) - 20;

            if (i == 0) {
                path.moveTo(x, y);
                fillPath.lineTo(x, y);
            } else {
                path.lineTo(x, y);
                fillPath.lineTo(x, y);
            }

            // Draw dot
            canvas.drawCircle(x, y, 12f, linePaint); // Ring
            canvas.drawCircle(x, y, 8f, dotPaint);   // White center
        }

        fillPath.lineTo(width, height);
        fillPath.close();

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(path, linePaint);
    }
}