package es.ernesto.minicars;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import android.content.Intent;
import android.graphics.Typeface;

public class LevelSelectActivity extends BaseGameActivity {
	public Camera mCamera;
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 854;
	private BitmapTextureAtlas mBackground, mButtonTexture;
	private TextureRegion mBackgroundTextureRegion, mButtonTextureRegion;
	private Sprite mBackButton, mBG, mLevel1Button, mLevel2Button,
			mLevel3Button, mLevelTestButton;
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	Text textBack, textLevel1, textLevel2, textLevel3, textLevelTest;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions mEngineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		return mEngineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		mBackground = new BitmapTextureAtlas(this.getTextureManager(), 1024,
				1024, TextureOptions.BILINEAR);
		mButtonTexture = new BitmapTextureAtlas(this.getTextureManager(), 512,
				256);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBackground, this, "menu/background.png", 0, 0);
		mButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mButtonTexture, this, "menu/menu_button.png",
						0, 0);
		mBackground.load();
		mButtonTexture.load();
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.getFontManager(), this.mFontTexture,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true,
				Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.getFontManager().loadFont(this.mFont);
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		final Scene scene = new Scene();
		scene.setOnAreaTouchTraversalFrontToBack();
		mBG = new Sprite(0, 0, mBackgroundTextureRegion,
				this.getVertexBufferObjectManager());
		scene.setBackground(new SpriteBackground(mBG));
		textLevel1 = new Text(CAMERA_WIDTH / 2 - 52, 90, this.mFont,
					"Nivel 1", this.getVertexBufferObjectManager());
		mLevel1Button = new Sprite((CAMERA_WIDTH / 2)
				- (mButtonTextureRegion.getWidth() / 2), 70,
				mButtonTextureRegion, this.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					GameSettings.setCurrentLevel(1);
					GameSettings.setMaxSpeed(15);
					Intent start = new Intent(LevelSelectActivity.this,
							GamePlayActivity.class);
					startActivity(start);
				}
				return true;
			}

		};
		textLevel2 = new Text(CAMERA_WIDTH / 2 - 52, 190, this.mFont,
					"Nivel 2", this.getVertexBufferObjectManager());
		mLevel2Button = new Sprite((CAMERA_WIDTH / 2)
				- (mButtonTextureRegion.getWidth() / 2), 170,
				mButtonTextureRegion, this.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					GameSettings.setCurrentLevel(2);
					GameSettings.setMaxSpeed(16);
					Intent start = new Intent(LevelSelectActivity.this,
							GamePlayActivity.class);
					startActivity(start);
				}
				return true;
			}

		};
		textLevel3 = new Text(CAMERA_WIDTH / 2 - 52, 290, this.mFont,
					"Nivel 3", this.getVertexBufferObjectManager());
		mLevel3Button = new Sprite((CAMERA_WIDTH / 2)
				- (mButtonTextureRegion.getWidth() / 2), 270,
				mButtonTextureRegion, this.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					GameSettings.setCurrentLevel(3);
					GameSettings.setMaxSpeed(17);
					Intent start = new Intent(LevelSelectActivity.this,
							GamePlayActivity.class);
					startActivity(start);
				}
				return true;
			}

		};

		textLevelTest = new Text(CAMERA_WIDTH / 2 - 52, 390, this.mFont,
				"TEST", this.getVertexBufferObjectManager());
		mLevelTestButton = new Sprite((CAMERA_WIDTH / 2)
				- (mButtonTextureRegion.getWidth() / 2), 370,
				mButtonTextureRegion, this.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
										 float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {


					Intent start = new Intent(LevelSelectActivity.this,
							RacerGameActivity.class);
					startActivity(start);
				}
				return true;
			}

		};


		textBack = new Text(CAMERA_WIDTH / 2 - 40, 490, this.mFont, "Volver",
					this.getVertexBufferObjectManager());
		mBackButton = new Sprite((CAMERA_WIDTH / 2)
				- (mButtonTextureRegion.getWidth() / 2), 470,
				mButtonTextureRegion, this.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					finish();
					Intent start = new Intent(LevelSelectActivity.this,
							MainMenuActivity.class);
					startActivity(start);
				}
				return true;
			}

		};
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setTouchAreaBindingOnActionMoveEnabled(true);
		scene.registerTouchArea(mLevel1Button);
		scene.registerTouchArea(mLevel2Button);
		scene.registerTouchArea(mLevel3Button);
		scene.registerTouchArea(mLevelTestButton);

		scene.registerTouchArea(mBackButton);
		scene.attachChild(mLevel1Button);
		scene.attachChild(mLevel2Button);
		scene.attachChild(mLevel3Button);
		scene.attachChild(mLevelTestButton);
		scene.attachChild(textLevel1);
		scene.attachChild(textLevel2);
		scene.attachChild(textLevel3);
		scene.attachChild(textLevelTest);

		scene.attachChild(mBackButton);
		scene.attachChild(textBack);
		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}
