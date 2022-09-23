package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.mygdx.game.Calculator.Calculator;

public class CoordinateAxis extends Widget {
    private ShapeRenderer renderer;
    float x;
    float y;
    Calculator calculator;
    private CoordinateArray coordinates;
    private double scale = 1;
    private TextField xCoordinateTextField;
    private TextField yCoordinateTextField;
    int halfHeight;
    int halfWidth;

    public CoordinateAxis(Calculator calculator , TextField xCoordinateLabel , TextField yCoordinateLabel) {
        super();
        halfWidth = Gdx.graphics.getWidth() / 2;
        halfHeight = Gdx.graphics.getHeight() / 2;
        renderer = new ShapeRenderer();
        this.calculator = calculator;
        transform(halfWidth , halfHeight);
        setBounds(0,0,1,1);
        setVisible(false);
        coordinates = new CoordinateArray(calculator , this);
        this.xCoordinateTextField = xCoordinateLabel;
        this.yCoordinateTextField = yCoordinateLabel;
    }

    public double getScale() {
        return scale;
    }

    public void recalculate() {
        coordinates.recalculate();
    }

    public void transform(float x, float y) {
        this.x += x;
        this.y += y;
        if (coordinates != null) {
            if (this.x > coordinates.yCoordinates.length / 2 - 1) this.x = coordinates.yCoordinates.length / 2 - 1;
            if (this.x - Gdx.graphics.getWidth() < -coordinates.yCoordinates.length / 2 + 1) this.x = -coordinates.yCoordinates.length / 2 + Gdx.graphics.getWidth() + 1;
        }
        setUpCoordinateTextFields();
    }

    public void setPosition(float x , float y) {
        this.x = x;
        this.y = y;
        if (coordinates != null) {
            if (this.x > coordinates.yCoordinates.length / 2) this.x = coordinates.yCoordinates.length / 2 - 1;
            if (this.x - Gdx.graphics.getWidth() < -coordinates.yCoordinates.length / 2) this.x = -coordinates.yCoordinates.length / 2 + Gdx.graphics.getWidth() + 1;
        }
        setUpCoordinateTextFields();
    }

    private void setUpCoordinateTextFields() {
        if (xCoordinateTextField != null && yCoordinateTextField != null) {
            String newX = Float.toString((-this.x + halfWidth) / (float) scale);
            String newY = Float.toString((-this.y + halfHeight) / (float) scale);

            if (!xCoordinateTextField.getText().equals(newX)) {
                int xCursorPosition = xCoordinateTextField.getCursorPosition();
                xCoordinateTextField.setText(newX);
                xCoordinateTextField.setCursorPosition(Math.min(xCursorPosition , xCoordinateTextField.getText().length()));
            }
            if (!yCoordinateTextField.getText().equals(newY)) {
                int yCursorPosition = yCoordinateTextField.getCursorPosition();
                yCoordinateTextField.setText(newY);
                yCoordinateTextField.setCursorPosition(Math.min(yCursorPosition , yCoordinateTextField.getText().length()));
            }
        }
    }

    public boolean setScale(double scale) {
        if (this.scale != scale && scale > 0) {
            this.scale = scale;
            recalculate();
            setUpCoordinateTextFields();
            return true;
        }
        return false;
    }

    public void translateToPoint() {
        float x1 = (halfWidth - x);
        float y1 = (float) -(coordinates.get((int) x1)) + halfHeight;
        setPosition(x , y1);
    }

    float x1;
    float y1;
    float x2;
    float y2;
    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.RED);
        for (int i = 0; i < Gdx.graphics.getWidth(); i++) {
            x1 = (i - x);
            y1 = (float) (coordinates.get((int) x1) + y);
            x2 = x1 + 1;
            y2 = (float) (coordinates.get((int) x2) + y);
            for (int j = -1; j <= 1; j++) {
                renderer.line(x1 + x + j, y1, x2 + x + j, y2);
            }
            for (int j = -1; j <= 1; j++) {
                renderer.line(x1 + x, y1 + j, x2 + x, y2 + j);
            }
        }
        renderer.setColor(Color.BLACK);
        for (int j = -1; j <= 1; j++) {
            renderer.line(0, y + j, Gdx.graphics.getWidth(), y + j);
            renderer.line(x + j, 0, x + j, Gdx.graphics.getHeight());
        }
        for (int i = 0; i <= 0; i++) {
            renderer.line(halfWidth + i, halfHeight - 10, halfWidth + i, halfHeight + 10);
            renderer.line(halfWidth - 10, halfHeight + i, halfWidth + 10, halfHeight + i);
        }
        renderer.end();
    }

    public static class CoordinateArray {
        Calculator calculator;
        double[] yCoordinates;
        Thread calculatingThead;
        private CoordinateAxis parent;
        StringBuilder calculatorTask;
        int threadNumber;

        private Thread getnewCalculatingThead() {
            if (Main.LOGGING) {
                System.out.println("getNewCalculatingThread number " + threadNumber++);
            }
            if (Main.FILE_LOGGING) {
                Main.logWriter.print("getNewCalculatingThread number " + threadNumber);
            }
            return new Thread() {
                int i;
                @Override
                public void run() {
                    for (i = 0; i < yCoordinates.length - 1; i++) {
                        try {
                            synchronized (this) {
                                if (i >= yCoordinates.length) return;
                                if (i % 2 == 0) {
                                    yCoordinates[i] = calculator.calculate((-(i / 2) / parent.getScale())) * parent.getScale();
                                } else {
                                    yCoordinates[i] = calculator.calculate(((i + 1) / 2 / parent.getScale())) * parent.getScale();
                                    if (yCoordinates[i] < -10000) {
                                        yCoordinates[i] = calculator.calculate(((i + 1) / 2 / parent.getScale())) * parent.getScale();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Main.printException(e);
                        }
                    }
                }

                @Override
                public synchronized void destroy() {
                    i = yCoordinates.length;
                }
            };
        }

        public CoordinateArray(Calculator calculator , CoordinateAxis parent) {
            this.calculator = calculator;
            int size = 15000;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                size = 150000;
            }
            yCoordinates = new double[size];
            this.parent = parent;
            calculatorTask = new StringBuilder();
        }

        public synchronized void recalculate() {
            if (calculatingThead != null) {
                calculatingThead.destroy();
            }
            if (!calculator.getStringTask().toString().equals(calculatorTask.toString())) {
                for (int i = 0; i < yCoordinates.length; i++) {
                    yCoordinates[i] = Double.NaN;
                }
                calculatorTask = new StringBuilder(calculator.getStringTask());
            }
            calculatingThead = getnewCalculatingThead();
            calculatingThead.start();
        }

        public double get(int x) {
            if (x > 0) {
                return yCoordinates[2*x - 1];
            }
            return yCoordinates[-2 * x];
        }
    }
}
