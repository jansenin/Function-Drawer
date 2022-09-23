package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.mygdx.game.Calculator.Calculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main extends ApplicationAdapter{
	public static final boolean LOGGING = false;
	public static final boolean FILE_LOGGING = false;
	enum ApplicationState {
		MAIN , HELP , TRANSLATIONS
	}
	public static PrintWriter logWriter;
	static {
		try {
			if (FILE_LOGGING) logWriter = new PrintWriter(getLogFile());
		} catch (FileNotFoundException e) {
			if (LOGGING) e.printStackTrace();
		}
	}
	private BitmapFont font;
	private Stage stage;
	private TextField textField;
	private TextField scaleTextField;
	private Button left , right;
	private TextButton toZeroZeroButton;
	private Calculator calculator;
	private Label statusBar;
	private Widget screenArea;
	private CoordinateAxis coordinateAxis;
	private Label scaleLabel;
	private Group scale;
	private Group helpStage;
	private Label[] functionLabels;
	private Button helpButton;
	private ApplicationState applicationState = ApplicationState.MAIN;
	private TextField xCoordinateTextField;
	private TextField yCoordinateTextField;
	private Label xCoordinateLabel;
	private Label yCoordinateLabel;
	private TextButton onFunctionLineButton;
	private Calculator calculatorForScaleTranslations;
	private Calculator calculatorForXTranslations;
	private Calculator calculatorForYTranslations;

	private TextButton translationButton;
	private Group translatGroup;
	private Label xTransletionLabel;
	private Label yTranslationLabel;
	private Label scaleTranslation;
	private Label goOnFunctionLine;
	private TextField xTranslationTextField;
	private TextField yTranslationTextField;
	private TextField scaleTranslationTextField;
	private TextButton goOnFunctionButton;
	private int scaleTime;

	private SpriteDrawable newDrawable(String file) {
		return new SpriteDrawable(new Sprite(new Texture(file)));
	}

	public static void printException(Exception e) {
		if (Main.FILE_LOGGING) {
			e.printStackTrace(logWriter);
		}
		if (Main.LOGGING) {
			e.printStackTrace();
		}
	}

	private static File getLogFile() {
		try {
			File file = null;
			if (FILE_LOGGING) {
				String defaultName = "Logs\\log";
				String defaultDimension = ".txt";
				int logNumber = 0;
				while (
						(file = new File(defaultName + logNumber++ + defaultDimension)).exists()
						) {
				}
			}
			return file;
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public void create () {
		font = new BitmapFont(Gdx.files.internal("Century Gothic.fnt"));
		stage = new Stage();
		calculator = new Calculator();
		calculatorForScaleTranslations = new Calculator();
		calculatorForXTranslations = new Calculator();
		calculatorForYTranslations = new Calculator();

		yCoordinateLabel = new Label("y : " , new Label.LabelStyle(font , new Color(1,1,1,1)));
		yCoordinateLabel.setPosition(20,20);

		xCoordinateLabel = new Label("x : " , new Label.LabelStyle(font , new Color(1,1,1,1)));
		xCoordinateLabel.setPosition(20 , yCoordinateLabel.getY() + yCoordinateLabel.getHeight() + 20);

		xCoordinateTextField = new TextField("0" , new TextField.TextFieldStyle(font , new Color(1,1,1,1) , newDrawable("cursor.png") , newDrawable("selected.png") , newDrawable("scaleTextFieldBackground.png")));
		yCoordinateTextField = new TextField("0" , new TextField.TextFieldStyle(font , new Color(1,1,1,1) , newDrawable("cursor.png") , newDrawable("selected.png") , newDrawable("scaleTextFieldBackground.png")));
		yCoordinateTextField.setPosition(yCoordinateLabel.getX() + yCoordinateLabel.getWidth() + 20 , yCoordinateLabel.getY());
		xCoordinateTextField.setPosition(xCoordinateLabel.getX() + xCoordinateLabel.getWidth() + 20 , xCoordinateLabel.getY());
		yCoordinateTextField.setAlignment(1);
		xCoordinateTextField.setAlignment(1);
		coordinateAxis = new CoordinateAxis(calculator , xCoordinateTextField , yCoordinateTextField);
		xCoordinateTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					int cursorPosition = xCoordinateTextField.getCursorPosition();
					String s = xCoordinateTextField.getText();
					double x = Double.parseDouble(s);
					xCoordinateTextField.setText(Double.toString(x));
					coordinateAxis.setPosition((float) -x * (float) coordinateAxis.getScale() + coordinateAxis.halfWidth , coordinateAxis.y);
					xCoordinateTextField.setCursorPosition(Math.min(cursorPosition , xCoordinateTextField.getText().length()));
				} catch (Exception e) {
					Main.printException(e);
				}
			}
		});
		yCoordinateTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					int cursorPosition = yCoordinateTextField.getCursorPosition();
					String s = yCoordinateTextField.getText();
					double y = Double.parseDouble(s);
					yCoordinateTextField.setText(Double.toString(y));
					coordinateAxis.setPosition(coordinateAxis.x, (float) -y * (float) coordinateAxis.getScale() + coordinateAxis.halfHeight);
					yCoordinateTextField.setCursorPosition(Math.min(cursorPosition , yCoordinateTextField.getText().length()));
				} catch (Exception e) {
					Main.printException(e);
				}
			}
		});

		screenArea = new Widget();
		screenArea.setSize(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
		screenArea.addListener(new ClickListener() {
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				coordinateAxis.transform(Gdx.input.getDeltaX() , -Gdx.input.getDeltaY());
			}
		});

		textField = new TextField("123" , new TextField.TextFieldStyle(font,new Color(1,1,1,1),null,new SpriteDrawable(new Sprite(new Texture("selected.png"))),new SpriteDrawable(new Sprite(new Texture("textFieldBackground.png")))));
		textField.setBounds(20,Gdx.graphics.getHeight() - 70,Gdx.graphics.getWidth() - 40,50);
		textField.setOrigin(10,10);
		textField.setAlignment(1);
		textField.getStyle().cursor = new SpriteDrawable(new Sprite(new Texture("cursor.png")));
		textField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					calculator.setTask(textField.getText());
					calculator.calculate(1);
					statusBar.setText("Ок");
					coordinateAxis.recalculate();
				} catch (Exception e) {
					statusBar.setText("Неправильная формула");
					Main.printException(e);
				}
			}
		});
		textField.setText("x");
		calculator.setTask("x");
		coordinateAxis.recalculate();

		toZeroZeroButton = new TextButton("В начало координат" , new TextButton.TextButtonStyle(new SpriteDrawable(new Sprite(new Texture("toZeroZeroButtonUp.png"))) , new SpriteDrawable(new Sprite(new Texture("toZeroZeroButtonDown.png"))) , null , font));
		toZeroZeroButton.setPosition(Gdx.graphics.getWidth() / 2 - toZeroZeroButton.getWidth() / 2,toZeroZeroButton.getY() + 20);
		toZeroZeroButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				coordinateAxis.transform(-coordinateAxis.x + Gdx.graphics.getWidth() / 2, -coordinateAxis.y + Gdx.graphics.getHeight() / 2);
			}
		});

		onFunctionLineButton = new TextButton("На линию графика" , new TextButton.TextButtonStyle(new SpriteDrawable(new Sprite(new Texture("toZeroZeroButtonUp.png"))) , new SpriteDrawable(new Sprite(new Texture("toZeroZeroButtonDown.png"))) , null , font));
		onFunctionLineButton.setPosition(Gdx.graphics.getWidth() / 2 - toZeroZeroButton.getWidth() / 2,toZeroZeroButton.getY() + toZeroZeroButton.getHeight() + 20);
		onFunctionLineButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				coordinateAxis.translateToPoint();
			}
		});

		statusBar = new Label("Неверная формула" , new Label.LabelStyle(font , new Color(0,0,0,1)));
		statusBar.setText("Ок");
		statusBar.setPosition(Gdx.graphics.getWidth() - statusBar.getWidth() - 60 , textField.getY() - 40 - statusBar.getHeight());
		statusBar.setAlignment(32);

		scaleLabel = new Label("Масштаб :" , new Label.LabelStyle(font , new Color(0,0,0,1)));
		scaleLabel.setPosition(20,statusBar.getY());
		scaleLabel.setAlignment(32);

		scaleTextField = new TextField("1" , new TextField.TextFieldStyle(font,new Color(1,1,1,1),null,new SpriteDrawable(new Sprite(new Texture("selected.png"))),new SpriteDrawable(new Sprite(new Texture("scaleTextFieldBackground.png")))));
		scaleTextField.setBounds(
				scaleLabel.getWidth() + 20 + 20,
				statusBar.getY(),
				200,
				50);
		scaleTextField.setOrigin(10,10);
		scaleTextField.setAlignment(1);
		scaleTextField.getStyle().cursor = new SpriteDrawable(new Sprite(new Texture("cursor.png")));
		scaleTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					coordinateAxis.setScale(Double.parseDouble(scaleTextField.getText()));
				} catch (Exception e) {
					Main.printException(e);
				}
			}
		});

		statusBar.setText("Ок");

		scale = new Group();
		scale.addActor(scaleLabel);
		scale.addActor(scaleTextField);

		left = new Button(new SpriteDrawable(new Sprite(new Texture("leftArrow.png"))) , new SpriteDrawable(new Sprite(new Texture("pressedLeftArrow.png"))));
		left.setPosition(20,scaleLabel.getY() - 40 - left.getHeight());
		left.setSize(90,60);
		left.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					textField.setCursorPosition(textField.getCursorPosition() - 1);
				} catch (Exception e) {
					Main.printException(e);
				}
			}
		});

		right = new Button(new SpriteDrawable(new Sprite(new Texture("rightArrow.png"))) , new SpriteDrawable(new Sprite(new Texture("pressedRightArrow.png"))));
		right.setSize(90,60);
		right.setPosition(Gdx.graphics.getWidth() -  20 - right.getWidth(),scaleLabel.getY() - 40 - left.getHeight());
		right.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					textField.setCursorPosition(textField.getCursorPosition() + 1);
				} catch (Exception e) {
					Main.printException(e);
				}
			}
		});

		translationButton = new TextButton("Преобразования" , new TextButton.TextButtonStyle(newDrawable("toZeroZeroButtonUp.png") , newDrawable("toZeroZeroButtonDown.png") , null , font));
		translationButton.setPosition(left.getX() , left.getY() - 20 - translationButton.getHeight());
		translationButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (applicationState == ApplicationState.TRANSLATIONS) {
					applicationState = ApplicationState.MAIN;
				} else {
					applicationState = ApplicationState.TRANSLATIONS;
				}
			}
		});

		boolean windows = System.getProperty("os.name").toLowerCase().contains("windows");

		Group group = new Group();
		group.addActor(screenArea);
		group.addActor(statusBar);
		group.addActor(textField);
		if (!windows) {
			group.addActor(left);
			group.addActor(right);
			statusBar.setPosition(Gdx.graphics.getWidth() / 2 - statusBar.getWidth() / 2 , left.getY());
			onFunctionLineButton.setX(Gdx.graphics.getWidth() - 20 - 50 - 20 - onFunctionLineButton.getWidth());
			toZeroZeroButton.setX(Gdx.graphics.getWidth() - 20 - 50 - 20 - toZeroZeroButton.getWidth());
		} else {
			left = null;
			right = null;
			translationButton.setPosition(scaleLabel.getX() , scaleLabel.getY() - 20 - translationButton.getHeight());
		}

		translatGroup = new Group();

		xTransletionLabel = new Label("x будет увеличиваться на " , new Label.LabelStyle(font , new Color(1,1,1,1)));
		xTransletionLabel.setPosition(translationButton.getX() + 50, translationButton.getY() - 20 - xTransletionLabel.getHeight());
		translatGroup.addActor(xTransletionLabel);

		xTranslationTextField = new TextField("0" , new TextField.TextFieldStyle(font , new Color(1,1,1,1) , newDrawable("cursor.png") , newDrawable("selected.png") , newDrawable("scaleTextFieldBackground.png")));
		if (windows) {
			xTranslationTextField.setPosition(xTransletionLabel.getX() + xTransletionLabel.getWidth() + 20, xTransletionLabel.getY());
		} else {
			xTranslationTextField.setPosition(xTransletionLabel.getX(), xTransletionLabel.getY() - 20 - xTranslationTextField.getHeight());
		}
		xTranslationTextField.setAlignment(1);
		translatGroup.addActor(xTranslationTextField);

		yTranslationLabel = new Label("y будет увеличиваться на " , new Label.LabelStyle(font , new Color(1,1,1,1)));
		yTranslationLabel.setPosition(xTransletionLabel.getX() , xTranslationTextField.getY() - 20 - yTranslationLabel.getHeight());
		translatGroup.addActor(yTranslationLabel);

		yTranslationTextField = new TextField("0" , new TextField.TextFieldStyle(font , new Color(1,1,1,1) , newDrawable("cursor.png") , newDrawable("selected.png") , newDrawable("scaleTextFieldBackground.png")));
		if (windows) {
			yTranslationTextField.setPosition(yTranslationLabel.getX() + yTranslationLabel.getWidth() + 20, yTranslationLabel.getY());
		} else {
			yTranslationTextField.setPosition(yTranslationLabel.getX(), yTranslationLabel.getY() - 20 - yTranslationTextField.getHeight());
		}
		yTranslationTextField.setAlignment(1);
		translatGroup.addActor(yTranslationTextField);

		scaleTranslation = new Label("Масштаб будет увеличиваться на " , new Label.LabelStyle(font , new Color(1,1,1,1)));
		scaleTranslation.setPosition(yTranslationLabel.getX() , yTranslationTextField.getY() - 20 - scaleTranslation.getHeight());
		translatGroup.addActor(scaleTranslation);

		scaleTranslationTextField = new TextField("0" , new TextField.TextFieldStyle(font , new Color(1,1,1,1) , newDrawable("cursor.png") , newDrawable("selected.png") , newDrawable("scaleTextFieldBackground.png")));
		if (windows) {
			scaleTranslationTextField.setPosition(scaleTranslation.getX() + scaleTranslation.getWidth() + 20, scaleTranslation.getY());
		} else {
			scaleTranslationTextField.setPosition(scaleTranslation.getX(), scaleTranslation.getY() - 20 - scaleTranslationTextField.getHeight());
		}
		scaleTranslationTextField.setAlignment(1);
		translatGroup.addActor(scaleTranslationTextField);
		scaleTranslationTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				coordinateAxis.recalculate();
			}
		});

		goOnFunctionLine = new Label("Следовать графику : " , new Label.LabelStyle(font , new Color(1,1,1,1)));
		goOnFunctionLine.setPosition(scaleTranslation.getX() , scaleTranslationTextField.getY() - 20 - goOnFunctionLine.getHeight());
		translatGroup.addActor(goOnFunctionLine);

		goOnFunctionButton = new TextButton("Нет" , new TextButton.TextButtonStyle(newDrawable("toZeroZeroButtonUp.png") , newDrawable("toZeroZeroButtonDown.png") , null , font));
		if (windows) {
			goOnFunctionButton.setPosition(goOnFunctionLine.getX() + goOnFunctionLine.getWidth() + 20, goOnFunctionLine.getY());
		} else {
			goOnFunctionButton.setPosition(goOnFunctionLine.getX(), goOnFunctionLine.getY() - 20 - goOnFunctionButton.getHeight());
		}
		translatGroup.addActor(goOnFunctionButton);
		goOnFunctionButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!goOnFunctionButton.getText().toString().equals("Да")) {
					goOnFunctionButton.setText("Да");
				} else {
					goOnFunctionButton.setText("Нет");
				}
			}
		});


		group.addActor(toZeroZeroButton);
		group.addActor(onFunctionLineButton);
		group.addActor(scaleLabel);
		group.addActor(scale);
		group.addActor(xCoordinateTextField);
		group.addActor(yCoordinateTextField);
		group.addActor(xCoordinateLabel);
		group.addActor(yCoordinateLabel);
		group.addActor(translationButton);
		group.addActor(translatGroup);

		helpButton = new Button(new SpriteDrawable(new Sprite(new Texture("helpButton.png"))) , new SpriteDrawable(new Sprite(new Texture("pressedHelpButton.png"))));
		helpButton.setPosition(Gdx.graphics.getWidth() - helpButton.getWidth() - 20 , 20);
		helpButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (applicationState == ApplicationState.MAIN) {
					applicationState = ApplicationState.HELP;
				} else if (applicationState == ApplicationState.HELP) {
					applicationState = ApplicationState.MAIN;
				}
				if (applicationState == ApplicationState.MAIN) {
					stage.clear();
					stage.addActor(group);
					stage.addActor(helpStage);
					stage.addActor(coordinateAxis);
					stage.addActor(helpButton);
				} else if (applicationState == ApplicationState.HELP){
					stage.clear();
					stage.addActor(helpStage);
					stage.addActor(helpButton);
				}
			}
		});
		group.addActor(helpButton);


		stage.addActor(group);

		coordinateAxis.calculator.setTask("x");
		coordinateAxis.recalculate();


		helpStage = new Group();

		int functionLabelCount = 11;
		functionLabels = new Label[functionLabelCount];
		Label.LabelStyle labelStyle = new Label.LabelStyle(font , new Color(0,0,0,1));
		functionLabels[0] = new Label("sin(1)  :  Синус единицы" , labelStyle);
		functionLabels[1] = new Label("cos(1)  :  Косинус единицы" , labelStyle);
		functionLabels[2] = new Label("tan(1) или tg(1)  :  Тангенс единицы" , labelStyle);
		functionLabels[3] = new Label("cot(1) или ctg(1)  :  Котангенс единицы" , labelStyle);
		functionLabels[4] = new Label("sqrt(1)  :  Корень из единицы" , labelStyle);
		functionLabels[5] = new Label("sqr(1)  :  Квадрат единицы" , labelStyle);
		functionLabels[6] = new Label("abs(1)  :  Модуль единицы" , labelStyle);
		functionLabels[7] = new Label("round(1)  :  Округление единицы" , labelStyle);
		functionLabels[8] = new Label("sign(1)  :  Знак единицы (+)" , labelStyle);
		functionLabels[9] = new Label("NaN  :  Не число" , labelStyle);
		functionLabels[10] = new Label("Infinity  :  Бесконечность" , labelStyle);


		functionLabels[0].setPosition(Gdx.graphics.getWidth() / 2 - functionLabels[0].getWidth() / 2,Gdx.graphics.getHeight() - functionLabels[0].getHeight() - 20);
		helpStage.addActor(functionLabels[0]);
		for (int i = 1;i < functionLabelCount;i++) {
			functionLabels[i].setPosition(Gdx.graphics.getWidth() / 2 - functionLabels[i].getWidth() / 2,functionLabels[i - 1].getY() - functionLabels[i].getHeight() - 20);
			helpStage.addActor(functionLabels[i]);
		}

		stage.addActor(helpStage);
		stage.addActor(coordinateAxis);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		if (!calculatorForScaleTranslations.getRawStringTask().equals(scaleTranslationTextField.getText())) {
			try {
				calculatorForScaleTranslations.setTask(scaleTranslationTextField.getText());
			} catch (Exception e) {
				Main.printException(e);
			}
		}
		if (!calculatorForXTranslations.getRawStringTask().equals(xTranslationTextField.getText())) {
			try {
				calculatorForXTranslations.setTask(xTranslationTextField.getText());
			} catch (Exception e) {
				Main.printException(e);
			}
		}
		if (!calculatorForYTranslations.getRawStringTask().equals(yTranslationTextField.getText())) {
			try {
				calculatorForYTranslations.setTask(yTranslationTextField.getText());
			} catch (Exception e) {
				Main.printException(e);
			}
		}


		if (scaleTime++ == 5) {
			try {
				if (coordinateAxis.setScale(coordinateAxis.getScale() + (float) calculatorForScaleTranslations.calculate(-(coordinateAxis.x - coordinateAxis.halfWidth) / coordinateAxis.getScale()))) {
					int cursorPosition = scaleTextField.getCursorPosition();
					scaleTextField.setText(Double.toString(coordinateAxis.getScale()));
					scaleTextField.setCursorPosition(Math.min(cursorPosition , scaleTextField.getText().length()));
				}
			} catch (Exception e) {
				Main.printException(e);
			}

			scaleTime = 0;
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (applicationState == ApplicationState.MAIN) {
			helpStage.setVisible(false);
			translatGroup.setVisible(false);
		} else if (applicationState == ApplicationState.HELP){
			helpStage.setVisible(true);
			translatGroup.setVisible(false);
		} else {
			translatGroup.setVisible(true);
			helpStage.setVisible(false);
		}
		stage.act();
		if (!calculatorForXTranslations.getRawStringTask().equals("")) {
			try {
				coordinateAxis.transform(-(float) calculatorForXTranslations.calculate(-(coordinateAxis.x - coordinateAxis.halfWidth) / coordinateAxis.getScale()) * (float) coordinateAxis.getScale(), 0);
			} catch (Exception e) {
				Main.printException(e);
			}
		}

		if (calculatorForYTranslations.getRawStringTask().equals("")) {
			try {
				coordinateAxis.transform(0, -(float) calculatorForYTranslations.calculate(-(coordinateAxis.x - coordinateAxis.halfWidth) / coordinateAxis.getScale()) * (float) coordinateAxis.getScale());
			} catch (Exception e) {
				printException(e);
			}
		}
		if (goOnFunctionButton.getText().toString().equals("Да")) {
			coordinateAxis.translateToPoint();
		}
		stage.getBatch().begin();
		coordinateAxis.draw(stage.getBatch() , 1);
		stage.getBatch().end();
		stage.draw();
	}
	
	@Override
	public void dispose () {
		this.stage.dispose();
		this.font.dispose();
	}
}
