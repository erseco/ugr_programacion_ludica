package es.ernesto.minicars;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;

import android.content.Intent;
import android.graphics.Typeface;

public class RecordsMenuActivity extends PreloaderActivity {

	public Camera mCamera;
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 854;
	private BitmapTextureAtlas mButtonTexture;
	private TextureRegion mButtonTextureRegion;
	private Sprite mBackButton;
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	Text textBack, textRecordsTitle, textNames, textScores;

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
		super.onCreateResources(pOnCreateResourcesCallback);
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		super.onCreateScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	protected Scene onAssetsLoaded() {
		final Scene scene = new Scene();
		scene.setOnAreaTouchTraversalFrontToBack();
		scene.setBackground(new Background(0.2f, 0.2f, 0.2f));
		textRecordsTitle = new Text(CAMERA_WIDTH / 2 - 65, 150, this.mFont,
					"Records", this.getVertexBufferObjectManager());
		textRecordsTitle.setScale(2);
		textNames = new Text(0, 0, this.mFont, "", 10000,
				this.getVertexBufferObjectManager());
		textScores = new Text(0, 0, this.mFont, "", 10000,
				this.getVertexBufferObjectManager());
		RecordsTable rt = new RecordsTable(getApplicationContext());
		if (rt.units.size() == 0)	 {
				textNames.setText("No records");
				textNames.setPosition(150, textRecordsTitle.getY() + 150);
		} else {
			for (int i = rt.units.size() - 1; i >= 0; i--) {
				textNames.setText(textNames.getText()
						+ Integer.toString(rt.units.size() - i) + " "
						+ rt.units.get(i).name + " \n");
				textScores.setText(textScores.getText()
						+ Integer.toString(rt.units.get(i).score) + " \n");
			}
			textNames.setPosition(70, textRecordsTitle.getY() + 150);
			textScores.setPosition(CAMERA_WIDTH - 150,
					textRecordsTitle.getY() + 150);
		}
		textBack = new Text(CAMERA_WIDTH / 2 - 40, 740, this.mFont, "Volver",
					this.getVertexBufferObjectManager());
		mBackButton = new Sprite((CAMERA_WIDTH / 2)
				- (mButtonTextureRegion.getWidth() / 2), 720,
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
					Intent start = new Intent(RecordsMenuActivity.this,
							MainMenuActivity.class);
					startActivity(start);
				}
				return true;
			}

		};
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setTouchAreaBindingOnActionMoveEnabled(true);
		scene.registerTouchArea(mBackButton);
		scene.attachChild(textRecordsTitle);
		scene.attachChild(textNames);
		scene.attachChild(textScores);
		scene.attachChild(mBackButton);
		scene.attachChild(textBack);
		return scene;
	}

	@Override
	protected void assetsToLoad() {
		mButtonTexture = new BitmapTextureAtlas(this.getTextureManager(), 512,
				256);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mButtonTexture, this, "menu/menu_button.png",
						0, 0);
		mButtonTexture.load();
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.getFontManager(), this.mFontTexture,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true,
				Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.getFontManager().loadFont(this.mFont);
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.xmllayoutRenderSurfaceView;
	}
}
