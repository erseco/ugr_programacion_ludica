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
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import android.content.Intent;
import android.graphics.Typeface;

public class MainMenuActivity extends BaseGameActivity {

	public Camera mCamera;
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 854;

	private BitmapTextureAtlas mBackground, mButtonTexture;
	private TextureRegion mBackgroundTextureRegion, mButtonTextureRegion;
	private Sprite mBG, mStartButton, mRecordsButton, mHelpButton, mExitButton;
	private BitmapTextureAtlas mFontTexture;
	private Font mFont, mCFont;
	Text textStart, textRecords, textHelp, textExit, textGameName;

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
		
		FontFactory.setAssetBasePath("font/");
		mCFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),
			    "exc.ttf", 46, true, android.graphics.Color.rgb(170, 10, 10));
	    mCFont.load();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		final Scene scene = new Scene();
		scene.setOnAreaTouchTraversalFrontToBack();
		String lang = java.util.Locale.getDefault().getDisplayName().substring(0, 7);
		mBG = new Sprite(0, 0, mBackgroundTextureRegion,
				this.getVertexBufferObjectManager());
		scene.setBackground(new SpriteBackground(mBG));
		textGameName = new Text(35, CAMERA_HEIGHT - 170, mCFont, "Mini", getVertexBufferObjectManager());
		scene.attachChild(textGameName);
		textGameName = new Text(70, CAMERA_HEIGHT - 125, mCFont, "Cars", getVertexBufferObjectManager());
		scene.attachChild(textGameName);
		textStart = new Text(CAMERA_WIDTH / 2 - 35, 90, this.mFont, "Jugar",
					this.getVertexBufferObjectManager());
		mStartButton = new Sprite((CAMERA_WIDTH / 2)
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
					finish();
					Intent start = new Intent(MainMenuActivity.this,
							LevelSelectActivity.class);
					startActivity(start);
				}
				return true;
			}

		};
		textRecords = new Text(CAMERA_WIDTH / 2 - 51, 190, this.mFont,
					"Records", this.getVertexBufferObjectManager());
		mRecordsButton = new Sprite((CAMERA_WIDTH / 2)
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
					finish();
					Intent start = new Intent(MainMenuActivity.this,
							RecordsMenuActivity.class);
					startActivity(start);
				}
				return true;
			}

		};
		textHelp = new Text(CAMERA_WIDTH / 2 - 30, 290, this.mFont, "Ayuda",
					this.getVertexBufferObjectManager());
		mHelpButton = new Sprite((CAMERA_WIDTH / 2)
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
					finish();
					Intent start = new Intent(MainMenuActivity.this,
							HelpMenuActivity.class);
					startActivity(start);
				}
				return true;
			}

		};
		textExit = new Text(CAMERA_WIDTH / 2 - 25, 390, this.mFont, "Salir",
					this.getVertexBufferObjectManager());
		mExitButton = new Sprite((CAMERA_WIDTH / 2)
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
					android.os.Process.killProcess(android.os.Process.myPid());
				}
				return true;
			}

		};
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setTouchAreaBindingOnActionMoveEnabled(true);
		scene.registerTouchArea(mStartButton);
		scene.registerTouchArea(mRecordsButton);
		scene.registerTouchArea(mHelpButton);
		scene.registerTouchArea(mExitButton);
		scene.attachChild(mStartButton);
		scene.attachChild(textStart);
		scene.attachChild(mRecordsButton);
		scene.attachChild(textRecords);
		scene.attachChild(mHelpButton);
		scene.attachChild(textHelp);
		scene.attachChild(mExitButton);
		scene.attachChild(textExit);
		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}
