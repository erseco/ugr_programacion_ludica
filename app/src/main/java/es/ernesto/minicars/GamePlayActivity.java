package es.ernesto.minicars;

import java.io.IOException;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import es.ernesto.minicars.extensions.AutoVerticalParallaxBackground;
import es.ernesto.minicars.extensions.InputText;
import es.ernesto.minicars.extensions.VerticalParallaxBackground.VerticalParallaxEntity;

public class GamePlayActivity extends PreloaderActivity implements
		IAccelerationListener, IOnSceneTouchListener {

	private Camera mCamera;
	static final int CAMERA_WIDTH = 480;
	static final int CAMERA_HEIGHT = 854;

	AutoVerticalParallaxBackground autoParallaxBackground;
	BitmapTextureAtlas mAutoParallaxGrassTexture, mAutoParallaxRoadTexture;
	ITextureRegion mParallaxLayerGrass, mParallaxLayerRoad;

	BitmapTextureAtlas mPlayerTextureAtlas, mSpeedTableTextureAtlas,
			mSpeedArrowTextureAtlas, mFuelTableTextureAtlas,
			mFuelArrowTextureAtlas, mDistanceTableTextureAtlas,
			mDistanceArrowTextureAtlas, mEnemyTextureAtlas,
			yesButtonTextureAtlas, noButtonTextureAtlas;
	ITextureRegion mPlayerTexture, mSpeedTableTexture, mSpeedArrowTexture,
			mFuelTableTexture, mFuelArrowTexture, mDistanceTableTexture,
			mDistanceArrowTexture, yesButtonTexture, noButtonTexture;

	Sprite car, speedTable, speedArrow, fuelTable, fuelArrow, distanceTable,
			distanceArrow, yesButton, noButton;
	private Body mCarBody;
	TiledTextureRegion mEnemyTexture;
	// texture and atlas for fuel bonus icon
	BitmapTextureAtlas fuelBonusAtlas;
	TiledTextureRegion fuelBonusTexture;
	double pWidth;
	private boolean winner = false;
	private int startPoint = 0;
	// speed of car
	float mParallaxSpeed = 1;
	private static double speed = 3;
	double a = 0.1;
	boolean accelerate = false;
	boolean step = false;
	boolean startDrivin = true, stopDriving = false;
	boolean isEmpty = false;
	private static boolean playingStarted = false;
	// sound effects
	private Sound offSound, onSound, stopSound, downSound, winSound, loseSound,
			bonusSound, crashSound;
	private Music music;
	// text font
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	// text labels
	Text dialogText, scoreText;
	Rectangle form;
	int scoreCount = 0;
	// physics interaction for cars
	private PhysicsWorld mPhysicsWorld;
	Enemy mEnemy;
	Bonus fuelBonus;
	private static boolean showBonus = false;
        // table of highscores
	RecordsTable recordsTable;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions mEngineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		mEngineOptions.getAudioOptions().setNeedsSound(true);
		mEngineOptions.getAudioOptions().setNeedsMusic(true);
		return mEngineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback arg0)
			throws Exception {
		super.onCreateResources(arg0);
		arg0.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback arg0) throws Exception {
		super.onCreateScene(arg0);
	}

	public Sprite getSprite(Scene scene, float pX, float pY,
			ITextureRegion texregion, float scale) {
		Sprite sprite = new Sprite(pX, pY, texregion,
				this.getVertexBufferObjectManager());
		sprite.setScale(scale);
		scene.attachChild(sprite);
		return sprite;
	}

	TimerHandler setFuel = new TimerHandler(0.5f, new ITimerCallback() {
		final int emptyValue = -95;

		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			if (isPlayingStarted()) {
				if (fuelArrow.getRotation() > emptyValue) {
					fuelArrow.setRotation(fuelArrow.getRotation() - 1f);
				} else {
					isEmpty = true;
				}
			}
			setFuel.reset();
		}
	}) {

	};

	TimerHandler setScore = new TimerHandler(1, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			if (isPlayingStarted() && !isEmpty) {
				scoreCount += (int) speed;
				scoreText.setText("Puntuacion: " + Integer.toString(scoreCount));
			}
			setScore.reset();
		}
	});

	IUpdateHandler gameHandler = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (isPlayingStarted()) {
				if (!isEmpty) {
					setCurrentSpeed();
					setSpeedometerData();
					checkCarPositionOnRoad();
					checkCurrentDistance();
					checkCollisionWithBonus();
					if (Math.abs(car.getY() - mEnemy.mEnemySprite.getY()) > 300) {
						collisionNotStarted = true;
					}
				} else {
					checkFuelLevel();
					setSpeedometerData();
				}
				setCurrentDistance();
			}
		}
	};

	public void setCurrentDistance() {
		if (distanceArrow.getY() > 153) {
			distanceArrow.setY(distanceArrow.getY()
					- (float) (0.001 * getSpeed()));
		} else {
			setSpeed(0);
			mCarBody.setLinearVelocity(0, 0);
			showWinMessage();
		}
	}

	public void setCurrentSpeed() {
		if ((accelerate == false) && (getSpeed() > 3 + a)) {
			setSpeed(getSpeed() - a);
		} else if ((accelerate == true)
				&& (getSpeed() <= GameSettings.getMaxSpeed())) {
			setSpeed(getSpeed() + a);
		}
	}

	public void checkFuelLevel() {
		if (!stopDriving) {
			onSound.stop();
			offSound.stop();
			stopSound.play();
			stopDriving = true;
		}
		if (getSpeed() >= a) {
			setSpeed(getSpeed() - a);
		} else {
			setSpeed(0);
			mCarBody.setLinearVelocity(0, 0);
			showGameOver();
		}
	}

	public void setSpeedometerData() {
		mParallaxSpeed += getSpeed();
		autoParallaxBackground.setParallaxValue(-mParallaxSpeed);
		if (getSpeed() != 0) {
			speedArrow.setRotation(-50
					+ (float) (16 * getSpeed() + Math.random() * 3));
		} else {
			speedArrow.setRotation(-50 + (float) (16 * getSpeed()));
		}
	}

	public void checkCarPositionOnRoad() {
		if ((car.getX() + 10 < (CAMERA_WIDTH - mParallaxLayerRoad.getWidth()) / 2)
				|| (car.getX() + car.getWidth() - 10 > CAMERA_WIDTH
						- (CAMERA_WIDTH - mParallaxLayerRoad.getWidth()) / 2)) {
			if (getSpeed() > GameSettings.getMaxSpeed() / 2) {
				setSpeed(getSpeed() - 2 * a);
			}
		}
	}

	public void checkCollisionWithBonus() {
		if (car.collidesWith(fuelBonus.getBonusShape())) {
			bonusSound.play();
			fuelBonus.getBonusShape().setVisible(false);
			fuelBonus.getBonusShape().setY(CAMERA_HEIGHT + 160);
			fuelArrow.setRotation(0);
		}
	}

	public void checkCurrentDistance() {
		/*
		 * for (int i = 1; i < 10; i++) { float currentY = 546 - 40 * i; if
		 * (Math.abs(distanceArrow.getY() - currentY) <= 0.01) {
		 * setShowBonus(true); break; } }
		 */
		if (startPoint - 35 == (int) distanceArrow.getY()) {
			startPoint = (int) distanceArrow.getY();
			setShowBonus(true);
		}
	}

	public void showGameOver() {
		loseSound.play();
		mEngine.getScene().clearUpdateHandlers();
		mEngine.clearUpdateHandlers();
		getSoundManager().remove(offSound);
		getSoundManager().remove(onSound);
		music.stop();
		autoParallaxBackground.setPaused(true);
		setPlayingStarted(false);
		car.setVisible(false);
		mEnemy.mEnemySprite.setVisible(false);
		form.setHeight(CAMERA_HEIGHT / 4);
		form.setVisible(true);
		dialogText.setVisible(true);
		yesButton.setVisible(true);
		noButton.setVisible(true);
		getEngine().getScene().registerTouchArea(yesButton);
		getEngine().getScene().registerTouchArea(noButton);
		dialogText.setX(dialogText.getX() - 40);
		dialogText.setText("Game Over. ¿Otra carrera?");
	}

	public void showWinMessage() {
		winSound.play();
		mEngine.getScene().clearUpdateHandlers();
		mEngine.clearUpdateHandlers();
		getSoundManager().remove(offSound);
		getSoundManager().remove(onSound);
		music.stop();
		autoParallaxBackground.setPaused(true);
		winner = true;
		setPlayingStarted(false);
		car.setVisible(false);
		mEnemy.mEnemySprite.setVisible(false);
		form.setHeight(CAMERA_HEIGHT / 4);
		form.setVisible(true);
		dialogText.setVisible(true);
		yesButton.setVisible(true);
		noButton.setVisible(true);
		getEngine().getScene().registerTouchArea(yesButton);
		getEngine().getScene().registerTouchArea(noButton);
		dialogText.setX(30);
		dialogText.setText("¡Ganaste! ¿Pasar al siguiente nivel?");
	}

	@Override
	public void onPopulateScene(Scene arg0, OnPopulateSceneCallback arg1)

	throws Exception {
		arg1.onPopulateSceneFinished();
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
            
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		if (isPlayingStarted()) {
			if (!isEmpty) {
				if (((car.getX() > 57) && (pAccelerationData.getX() < 0) || ((car
						.getX() + car.getWidth() < CAMERA_WIDTH - 57))
						&& (pAccelerationData.getX() > 0))) {
					mCarBody.setLinearVelocity(pAccelerationData.getX() * 3, 0);
				} else {
					mCarBody.setLinearVelocity(0, 0);
				}
			} else {
				car.setRotation(0);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setPlayingStarted(false);
			this.mEngine.getScene().clearUpdateHandlers();
			this.mEngine.clearUpdateHandlers();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			mCarBody.setLinearVelocity(mCarBody.getLinearVelocity().x - 0.5f, 0);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			mCarBody.setLinearVelocity(mCarBody.getLinearVelocity().x + 0.5f, 0);
		}
		return super.onKeyDown(keyCode, event);
	}
//io
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent e) {
		if (!isEmpty) {
			if (e.isActionMove()) {
				accelerate = true;
			} else if (e.isActionUp()) {
				onSound.stop();
				downSound.play();
				offSound.play();
				accelerate = false;
			} else if (e.isActionDown()) {
				if (!isPlayingStarted()) {
					setPlayingStarted(true);
					autoParallaxBackground.setPaused(false);
					dialogText.setVisible(false);
					form.setVisible(false);
					music.play();
				} else {
					accelerate = true;
				}
				offSound.stop();
				onSound.play();
			}
		}
		return true;
	}

	boolean collisionNotStarted;

	@Override
	protected Scene onAssetsLoaded() {
		collisionNotStarted = true;
		final Scene scene = new Scene();
		if (mEngine != null) {
			scene.setOnSceneTouchListener(this);
			this.enableAccelerationSensor(this);
			this.mEngine.registerUpdateHandler(gameHandler);
			this.mEngine.registerUpdateHandler(setFuel);
			this.mEngine.registerUpdateHandler(setScore);
		}
		recordsTable = new RecordsTable(this.getApplicationContext());
		autoParallaxBackground = new AutoVerticalParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground
				.attachVerticalParallaxEntity(new VerticalParallaxEntity(
						mParallaxSpeed, new Sprite(0, 0,
								this.mParallaxLayerGrass,
								getVertexBufferObjectManager())));
		autoParallaxBackground
				.attachVerticalParallaxEntity(new VerticalParallaxEntity(
						mParallaxSpeed,
						new Sprite((CAMERA_WIDTH - mParallaxLayerRoad
								.getWidth()) / 2, 0, this.mParallaxLayerRoad,
								getVertexBufferObjectManager())));
		autoParallaxBackground.setPaused(true);
		scene.setBackground(autoParallaxBackground);
		// create physics
		mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false,
				8, 1);
		scene.registerUpdateHandler(this.mPhysicsWorld);
		// init player's parameters
		final int playerX = (CAMERA_WIDTH - (int) this.mPlayerTexture
				.getWidth()) / 2 - 45;
		final int playerY = CAMERA_HEIGHT
				- (int) this.mPlayerTexture.getHeight() - 170;
		car = getSprite(scene, (float) playerX, (float) playerY,
				this.mPlayerTexture, 1f);
		car.setScaleCenterY(this.mPlayerTexture.getHeight());
		car.setRotationCenter(car.getWidth() / 2, car.getHeight() / 2);
		final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(100,
				0.5f, 0.5f);
		mCarBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, car,
				BodyType.DynamicBody, carFixtureDef);
		mCarBody.setBullet(false);
		// init enemies parameters
		mEnemy = new Enemy(scene, mPhysicsWorld, mParallaxLayerRoad.getWidth(),
				(float) getSpeed(), this.mEnemyTexture,
				getVertexBufferObjectManager());
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(car,
				mCarBody, true, false));
		// listener for contact between cars
		mPhysicsWorld.setContactListener(new ContactListener() {
			@Override
			public void beginContact(final Contact pContact) {
				if ((pContact.getFixtureA().getBody().equals(mCarBody) || pContact
						.getFixtureA().getBody().equals(mEnemy.mEnemyBody))
						&& (pContact.getFixtureB().getBody().equals(mCarBody) || pContact
								.getFixtureB().getBody()
								.equals(mEnemy.mEnemyBody))) {
					if (getSpeed() > GameSettings.getMaxSpeed() / 2) {
						setSpeed(GameSettings.getMaxSpeed() / 2);
						if (collisionNotStarted == true) {
							crashSound.play();
							collisionNotStarted = false;
						}
					}
				}
			}

			@Override
			public void endContact(final Contact pContact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub

			}
		});

		// speedometer work
		speedTable = getSprite(scene, CAMERA_WIDTH - 190, CAMERA_HEIGHT - 250,
				this.mSpeedTableTexture, 0.7f);
		speedArrow = getSprite(scene, CAMERA_WIDTH - 192, CAMERA_HEIGHT - 250,
				this.mSpeedArrowTexture, 0.7f);
		speedArrow.setRotation(-50);
		speedArrow.setRotationCenter(127, 89);
		// fuel-meter work
		fuelTable = getSprite(scene, -115, CAMERA_HEIGHT - 305,
				this.mFuelTableTexture, 0.35f);
		fuelArrow = getSprite(scene, -79, CAMERA_HEIGHT - 280,
				this.mFuelArrowTexture, 0.35f);
		fuelArrow.setRotationCenterX(fuelArrow.getRotationCenterX() - 6);
		// distance indicator
		distanceTable = getSprite(scene, -60, CAMERA_HEIGHT / 2 - 450,
				this.mDistanceTableTexture, 0.8f);
		distanceArrow = getSprite(scene, 15, CAMERA_HEIGHT / 2 + 117,
				this.mDistanceArrowTexture, 0.6f);
		startPoint = (int) distanceArrow.getY();
		// create bouns icon
		fuelBonus = new Bonus(scene, mParallaxLayerRoad.getWidth(),
				this.fuelBonusTexture, getVertexBufferObjectManager());
                //create dialog form
		form = new Rectangle(0, CAMERA_HEIGHT / 2 - 25, CAMERA_WIDTH,
				CAMERA_HEIGHT / 10, getVertexBufferObjectManager());
		form.setColor(0.05f, 0.05f, 0.05f, 0.7f);
		scene.attachChild(form);
		dialogText = new Text(140, CAMERA_HEIGHT / 2-50, this.mFont,
					"Pulse para acelerar", 150, this.getVertexBufferObjectManager());
		scene.attachChild(dialogText);
		scoreText = new Text(CAMERA_WIDTH / 2 - 80, 30,
					this.mFont, "Score: 0", 30,
					this.getVertexBufferObjectManager());
		scoreText.setColor(0.05f, 0.05f, 0.05f, 0.7f);
		scoreText.setScale(1.5f);
		scene.attachChild(scoreText);
		yesButton = new Sprite(CAMERA_WIDTH / 2 - 150, CAMERA_HEIGHT / 2 + 50,
				this.yesButtonTexture, getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					if (winner) {
						if (GameSettings.getCurrentLevel() < 3) {
							GameSettings.setCurrentLevel(GameSettings
									.getCurrentLevel() + 1);
							GameSettings
									.setMaxSpeed(GameSettings.getMaxSpeed() + 1);
						} else {
							GameSettings.setCurrentLevel(1);
							GameSettings.setMaxSpeed(15);
						}
					}
					finish();
					setPlayingStarted(false);
					Intent restart = new Intent(GamePlayActivity.this,
							GamePlayActivity.class);
					startActivity(restart);
				}
				return true;
			}
		};

		final InputText getName;
		getName = new InputText("!Nuevo record!", "Introduce tu nombre:", mFont,
					200, 200, getVertexBufferObjectManager(), this);
		noButton = new Sprite(CAMERA_WIDTH / 2 + 150
				- noButtonTexture.getWidth(), CAMERA_HEIGHT / 2 + 50,
				this.noButtonTexture, getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					if (recordsTable.isRecord(scoreCount)) {

						getName.showTextInput();
						if (getName.exit) {
							if (getName.getText() != null)
								recordsTable.addNewRecord(getName.getText(),
										scoreCount);
							setPlayingStarted(false);
							finish();
						}
					}
				}
				return true;
			}
		};
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setTouchAreaBindingOnActionMoveEnabled(true);
		scene.attachChild(yesButton);
		scene.attachChild(noButton);
		yesButton.setVisible(false);
		noButton.setVisible(false);
		return scene;
	}

	@Override
	protected void assetsToLoad() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mAutoParallaxGrassTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		this.mParallaxLayerGrass = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mAutoParallaxGrassTexture, this,
						"ingame/grass" + GameSettings.getCurrentLevel()
								+ ".png", 0, 0);
		this.mAutoParallaxGrassTexture.load();
		this.mAutoParallaxRoadTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		this.mParallaxLayerRoad = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mAutoParallaxRoadTexture, this,
						"ingame/road.png", 0, 0);
		this.mAutoParallaxRoadTexture.load();
		this.mPlayerTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				1024, 1024, TextureOptions.DEFAULT);
		this.mPlayerTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mPlayerTextureAtlas, this,
						"ingame/player.png", 0, 0);
		this.mPlayerTextureAtlas.load();
		this.mSpeedTableTextureAtlas = new BitmapTextureAtlas(
				getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		this.mSpeedTableTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mSpeedTableTextureAtlas, this,
						"ingame/speedtable.png", 0, 0);
		this.mSpeedTableTextureAtlas.load();
		this.mSpeedArrowTextureAtlas = new BitmapTextureAtlas(
				getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		this.mSpeedArrowTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mSpeedArrowTextureAtlas, this,
						"ingame/speedarrow.png", 0, 0);
		this.mSpeedArrowTextureAtlas.load();
		this.mFuelTableTextureAtlas = new BitmapTextureAtlas(
				getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		this.mFuelTableTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mFuelTableTextureAtlas, this,
						"ingame/fueltable.png", 0, 0);
		this.mFuelTableTextureAtlas.load();
		this.mFuelArrowTextureAtlas = new BitmapTextureAtlas(
				getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		this.mFuelArrowTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mFuelArrowTextureAtlas, this,
						"ingame/fuelarrow.png", 0, 0);
		this.mFuelArrowTextureAtlas.load();
		this.mDistanceTableTextureAtlas = new BitmapTextureAtlas(
				getTextureManager(), 512, 1024, TextureOptions.BILINEAR);
		this.mDistanceTableTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mDistanceTableTextureAtlas, this,
						"ingame/distance_table.png", 0, 0);
		this.mDistanceTableTextureAtlas.load();
		this.mDistanceArrowTextureAtlas = new BitmapTextureAtlas(
				getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		this.mDistanceArrowTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mDistanceArrowTextureAtlas, this,
						"ingame/distance_arrow.png", 0, 0);
		this.mDistanceArrowTextureAtlas.load();
		this.mEnemyTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				512, 512, TextureOptions.DEFAULT);
		this.mEnemyTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mEnemyTextureAtlas, this,
						"ingame/enemy.png", 0, 0, 5, 1);
		this.mEnemyTextureAtlas.load();
		this.fuelBonusAtlas = new BitmapTextureAtlas(getTextureManager(), 1024,
				1024, TextureOptions.DEFAULT);
		fuelBonusTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(fuelBonusAtlas, this,
						"ingame/fuel_bonus.png", 0, 0, 4, 4);
		this.fuelBonusAtlas.load();
		SoundFactory.setAssetBasePath("mfx/");
		MusicFactory.setAssetBasePath("mfx/");
		try {
			offSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "freelow.ogg");
			offSound.setLooping(true);
			offSound.setVolume(65);
			onSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "onlow.ogg");
			onSound.setLooping(true);
			onSound.setVolume(65);
			stopSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "stop.ogg");
			stopSound.setVolume(65);
			crashSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "strike.ogg");
			crashSound.setVolume(65);
			winSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "win.ogg");
			winSound.setVolume(65);
			loseSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "lose.ogg");
			loseSound.setVolume(65);
			bonusSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "bonus.ogg");
			bonusSound.setVolume(65);
			downSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "down.ogg");
			downSound.setVolume(500);
			music = MusicFactory.createMusicFromAsset(getMusicManager(), this,
					"mario.mid");
			music.setLooping(true);
            music.setVolume(100);
			System.out.print("mario.mid");
		} catch (final IOException e) {
			Debug.e(e);
		}
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.getFontManager(), this.mFontTexture,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, true,
				Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.getFontManager().loadFont(this.mFont);
		this.yesButtonTextureAtlas = new BitmapTextureAtlas(
				getTextureManager(), 128, 128, TextureOptions.DEFAULT);
		this.yesButtonTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.yesButtonTextureAtlas, this,
						"ingame/yes_button.png", 0, 0);
		this.yesButtonTextureAtlas.load();
		this.noButtonTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				128, 128, TextureOptions.DEFAULT);
		this.noButtonTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.noButtonTextureAtlas, this,
						"ingame/no_button.png", 0, 0);
		this.noButtonTextureAtlas.load();
	}

	public static boolean isPlayingStarted() {
		return playingStarted;
	}

	public static void setPlayingStarted(boolean playingStarted) {
		GamePlayActivity.playingStarted = playingStarted;
	}

	public static boolean isShowBonus() {
		return showBonus;
	}

	public static void setShowBonus(boolean showBonus) {
		GamePlayActivity.showBonus = showBonus;
	}

	public static double getSpeed() {
		return speed;
	}

	public static void setSpeed(double speed) {
		GamePlayActivity.speed = speed;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	@Override
	protected int getLayoutID() {
		// TODO Auto-generated method stub
		return R.layout.main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		// TODO Auto-generated method stub
		return R.id.xmllayoutRenderSurfaceView;
	}
}
