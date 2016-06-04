package es.ernesto.minicars;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2016 Ernesto Serrano
 *
 * @author Ernesto Serrano
 */
public class RacerGameActivity extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int RACETRACK_WIDTH = 64;

	private static final int OBSTACLE_SIZE = 16;
	private static final int CAR_SIZE = 16;

	private static final int CAMERA_WIDTH = RACETRACK_WIDTH * 5;
	private static final int CAMERA_HEIGHT = RACETRACK_WIDTH * 3;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private BitmapTextureAtlas mVehiclesTexture;
	private TiledTextureRegion mVehiclesTextureRegion;

	private BitmapTextureAtlas mBoxTexture;
	private ITextureRegion mBoxTextureRegion;

	private BitmapTextureAtlas mRacetrackTexture;
	private ITextureRegion mRacetrackStraightTextureRegion;
	private ITextureRegion mRacetrackCurveTextureRegion;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;

	private Body mCarBody;
	private TiledSprite mCar;

	private Body mCarBodyIA;
	private TiledSprite mCarIA;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mVehiclesTexture = new BitmapTextureAtlas(this.getTextureManager(), 128, 16, TextureOptions.BILINEAR);
		this.mVehiclesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mVehiclesTexture, this, "vehicles.png", 0, 0, 6, 1);
		this.mVehiclesTexture.load();

		this.mRacetrackTexture = new BitmapTextureAtlas(this.getTextureManager(), 128, 256, TextureOptions.REPEATING_NEAREST);
		this.mRacetrackStraightTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mRacetrackTexture, this, "racetrack_straight.png", 0, 0);
		this.mRacetrackCurveTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mRacetrackTexture, this, "racetrack_curve.png", 0, 128);
		this.mRacetrackTexture.load();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();

		this.mBoxTexture = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBoxTexture, this, "box.png", 0, 0);
		this.mBoxTexture.load();

	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);

		this.initRacetrack();
		this.initRacetrackBorders();
		this.initCar();
		this.initCar2();
		this.initObstacles();
		this.initOnScreenControls();

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		return this.mScene;
	}

	@Override
	public void onGameCreated() {


		this.mCarIA.setPosition(0,0);

	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void initOnScreenControls()
	{
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, (CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight()), this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.01f, this.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				final Body carBody = RacerGameActivity.this.mCarBody;

				final Vector2 velocity = Vector2Pool.obtain(pValueX * 5, pValueY * 5);
				carBody.setLinearVelocity(velocity);
				Vector2Pool.recycle(velocity);

				final float rotationInRad = (float)Math.atan2(-pValueX, pValueY);
				carBody.setTransform(carBody.getWorldCenter(), rotationInRad);

				RacerGameActivity.this.mCar.setRotation(MathUtils.radToDeg(rotationInRad));

			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {

				// Agregamos un obstaculo donde esté el coche
				addObstacle(mCar.getX(), mCar.getY());

			}
		});
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
				analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
				analogOnScreenControl.getControlBase().setScale(0.75f);
				analogOnScreenControl.getControlKnob().setScale(0.75f);
		analogOnScreenControl.refreshControlKnobPosition();

		this.mScene.setChildScene(analogOnScreenControl);



		HUD yourHud = new HUD();

		final Rectangle left = new Rectangle(20, 200, 60, 60,getVertexBufferObjectManager())
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				if (touchEvent.isActionUp())
				{
					// move player left
				}
				return true;
			};
		};

		final Rectangle right = new Rectangle(100, 200, 60, 60 ,getVertexBufferObjectManager())
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				if (touchEvent.isActionUp())
				{
					// move player right
				}
				return true;
			};
		};

		yourHud.registerTouchArea(left);
		yourHud.registerTouchArea(right);
		yourHud.attachChild(left);
		yourHud.attachChild(right);

		this.mCamera.setHUD(yourHud);



		final FPSCounter fpsCounter = new FPSCounter();
		this.mEngine.registerUpdateHandler(fpsCounter);

		Font mFont;
		BitmapTextureAtlas fontTextureAtlas;// = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        FontFactory.setAssetBasePath("font/");

		mFont = FontFactory.createFromAsset(mEngine.getFontManager(),
				mEngine.getTextureManager(), 256, 256, TextureOptions.BILINEAR,
				getAssets(), "exc.ttf", 12f, true,
				Color.YELLOW_ARGB_PACKED_INT);
		mFont.load();

		final Text mText = new Text(25, 25, mFont, "texto",getVertexBufferObjectManager());

		final Text fpsText = new Text(250, 240, mFont, "FPS:", "FPS: XXXXX".length(),getVertexBufferObjectManager());

		this.mScene.attachChild(fpsText);
        this.mScene.attachChild(mText);

		this.mScene.registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				//fpsText.setText("FPS: " + fpsCounter.getFPS());
                //System.out.println("hola");
                //mText.setText(String.valueOf(mCar.getX()));
                mCarIA.setPosition(mCarIA.getX()+1,mCarIA.getY());
			}
		}));


	}

	private void initCar() {
		this.mCar = new TiledSprite(20, 20, CAR_SIZE, CAR_SIZE, this.mVehiclesTextureRegion, this.getVertexBufferObjectManager());
		this.mCar.setCurrentTileIndex(0);

		final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		this.mCarBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, this.mCar, BodyType.DynamicBody, carFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mCar, this.mCarBody, true, false));

		this.mScene.attachChild(this.mCar);
	}

	private void initCar2() {


		this.mCarIA = new TiledSprite(20, 20, CAR_SIZE, CAR_SIZE, this.mVehiclesTextureRegion, this.getVertexBufferObjectManager());
		this.mCarIA.setCurrentTileIndex(0);

		final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		this.mCarBodyIA = PhysicsFactory.createBoxBody(this.mPhysicsWorld, this.mCarIA, BodyType.DynamicBody, carFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mCarIA, this.mCarBodyIA, true, false));

		this.mScene.attachChild(this.mCarIA);
	}


	private void initObstacles() {
		this.addObstacle(CAMERA_WIDTH / 2, RACETRACK_WIDTH / 2);
		this.addObstacle(CAMERA_WIDTH / 2, RACETRACK_WIDTH / 2);
		this.addObstacle(CAMERA_WIDTH / 2, CAMERA_HEIGHT - RACETRACK_WIDTH / 2);
		this.addObstacle(CAMERA_WIDTH / 2, CAMERA_HEIGHT - RACETRACK_WIDTH / 2);

		this.addObstacle(CAMERA_WIDTH / 2 +10, CAMERA_HEIGHT - RACETRACK_WIDTH / 2 +10);
	}

	private void addObstacle(final float pX, final float pY) {
		final Sprite box = new Sprite(pX, pY, OBSTACLE_SIZE, OBSTACLE_SIZE, this.mBoxTextureRegion, this.getVertexBufferObjectManager());

		final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0.1f, 0.5f, 0.5f);
		final Body boxBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, box, BodyType.DynamicBody, boxFixtureDef);
		boxBody.setLinearDamping(10);
		boxBody.setAngularDamping(10);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(box, boxBody, true, true));

		this.mScene.attachChild(box);
	}

	private void initRacetrack() {
		/* Straights. */
		{
			final ITextureRegion racetrackHorizontalStraightTextureRegion = this.mRacetrackStraightTextureRegion.deepCopy();
			racetrackHorizontalStraightTextureRegion.setTextureWidth(3 * this.mRacetrackStraightTextureRegion.getWidth());

			final ITextureRegion racetrackVerticalStraightTextureRegion = this.mRacetrackStraightTextureRegion;

			/* Top Straight */
			this.mScene.attachChild(new Sprite(RACETRACK_WIDTH, 0, 3 * RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackHorizontalStraightTextureRegion, this.getVertexBufferObjectManager()));
			/* Bottom Straight */
			this.mScene.attachChild(new Sprite(RACETRACK_WIDTH, CAMERA_HEIGHT - RACETRACK_WIDTH, 3 * RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackHorizontalStraightTextureRegion, this.getVertexBufferObjectManager()));

			/* Left Straight */
			final Sprite leftVerticalStraight = new Sprite(0, RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackVerticalStraightTextureRegion, this.getVertexBufferObjectManager());
			leftVerticalStraight.setRotation(90);
			this.mScene.attachChild(leftVerticalStraight);
			/* Right Straight */
			final Sprite rightVerticalStraight = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackVerticalStraightTextureRegion, this.getVertexBufferObjectManager());
			rightVerticalStraight.setRotation(90);
			this.mScene.attachChild(rightVerticalStraight);
		}

		/* Edges */
		{
			final ITextureRegion racetrackCurveTextureRegion = this.mRacetrackCurveTextureRegion;

			/* Upper Left */
			final Sprite upperLeftCurve = new Sprite(0, 0, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			upperLeftCurve.setRotation(90);
			this.mScene.attachChild(upperLeftCurve);

			/* Upper Right */
			final Sprite upperRightCurve = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, 0, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			upperRightCurve.setRotation(180);
			this.mScene.attachChild(upperRightCurve);

			/* Lower Right */
			final Sprite lowerRightCurve = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, CAMERA_HEIGHT - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			lowerRightCurve.setRotation(270);
			this.mScene.attachChild(lowerRightCurve);

			/* Lower Left */
			final Sprite lowerLeftCurve = new Sprite(0, CAMERA_HEIGHT - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			this.mScene.attachChild(lowerLeftCurve);
		}
	}


	private void initRacetrackBorders() {
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

		final Rectangle bottomOuter = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle topOuter = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle leftOuter = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle rightOuter = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		final Rectangle bottomInner = new Rectangle(RACETRACK_WIDTH, CAMERA_HEIGHT - 2 - RACETRACK_WIDTH, CAMERA_WIDTH - 2 * RACETRACK_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle topInner = new Rectangle(RACETRACK_WIDTH, RACETRACK_WIDTH, CAMERA_WIDTH - 2 * RACETRACK_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle leftInner = new Rectangle(RACETRACK_WIDTH, RACETRACK_WIDTH, 2, CAMERA_HEIGHT - 2 * RACETRACK_WIDTH, vertexBufferObjectManager);
		final Rectangle rightInner = new Rectangle(CAMERA_WIDTH - 2 - RACETRACK_WIDTH, RACETRACK_WIDTH, 2, CAMERA_HEIGHT - 2 * RACETRACK_WIDTH, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottomOuter, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, topOuter, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, leftOuter, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, rightOuter, BodyType.StaticBody, wallFixtureDef);

		PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottomInner, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, topInner, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, leftInner, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, rightInner, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(bottomOuter);
		this.mScene.attachChild(topOuter);
		this.mScene.attachChild(leftOuter);
		this.mScene.attachChild(rightOuter);

		this.mScene.attachChild(bottomInner);
		this.mScene.attachChild(topInner);
		this.mScene.attachChild(leftInner);
		this.mScene.attachChild(rightInner);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
