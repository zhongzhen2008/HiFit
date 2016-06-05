package com.hifit.zz.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hifit.zz.hifit.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class StepHistroyGraphActivity extends AppCompatActivity {

    private LineChartView mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_histroy_graph);

        initViews();
    }

    private void initViews() {
        mChart = (LineChartView) findViewById(R.id.step_chart);
        mChart.setOnValueTouchListener(new ValueTouchListener());

        mChart.setZoomEnabled(false);
        //mChart.setZoomType(ZoomType.HORIZONTAL);
        //mChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        mChart.setValueSelectionEnabled(true);

        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 4));
        values.add(new PointValue(4, 14));
        values.add(new PointValue(13, 24));
        values.add(new PointValue(14, 4));
        values.add(new PointValue(15, 14));
        values.add(new PointValue(20, 2));
        values.add(new PointValue(21, 4));
        values.add(new PointValue(22, 3));
        values.add(new PointValue(23, 4));
        values.add(new PointValue(24, 14));
        values.add(new PointValue(33, 24));
        values.add(new PointValue(34, 4));
        values.add(new PointValue(35, 14));

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        line.setHasLabelsOnlyForSelected(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (PointValue value : values) {
            AxisValue axisValue = new AxisValue(value.getX()).setLabel(value.getX() + "日");
            axisValues.add(axisValue);
        }
        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        resetViewport(values);

        //mChart.setZoomLevelWithAnimation(20, 50, 10);

        mChart.setLineChartData(data);

    }

    private void resetViewport(List<PointValue> values) {
        mChart.setViewportCalculationEnabled(false);

        float bottom = 0;
        float top = 0;
        float left = 0;
        float right = 0;
        for (PointValue value : values) {
            if (value.getX() < left) {
                left = value.getX();
            }
            if (value.getX() > right) {
                right = value.getX();
            }
            if (value.getY() < bottom) {
                bottom = value.getY();
            }
            if (value.getY() > top) {
                top = value.getY();
            }
        }

        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(mChart.getMaximumViewport());
        float pad = 5;
        v.bottom = (bottom - pad > 0) ? bottom - pad : 0;
        v.top = top + pad;
        v.left = (left - pad > 0) ? left - pad : 0;
        v.right = right;
        mChart.setMaximumViewport(v);

        float currentWidth = 20;
        v.left = v.right - currentWidth;
        mChart.setCurrentViewport(v);
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(StepHistroyGraphActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }
    @Override
    protected void onResume() {
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }
}
