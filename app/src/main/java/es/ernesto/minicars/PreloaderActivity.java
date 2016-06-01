package es.ernesto.minicars;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import es.ernesto.minicars.async.AsyncTaskLoader;
import es.ernesto.minicars.async.IAsyncCallback;

public abstract class PreloaderActivity extends LayoutGameActivity {

	private Camera mCamera;
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 854;

	BitmapTextureAtlas loadScreenBGTexture;
	ITextureRegion loadScreenBGRegion;
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;

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
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		loadScreenBGTexture = new BitmapTextureAtlas(this.getTextureManager(),
				1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		loadScreenBGRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(loadScreenBGTexture, this,
						"gfx/loading/background.png", 0, 0);//
		loadScreenBGTexture.load();
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.getFontManager(), this.mFontTexture,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 76, true,
				Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.getFontManager().loadFont(this.mFont);
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback arg0) throws Exception {
		Scene scene = new Scene();
		Sprite bg = new Sprite(0, 0, loadScreenBGRegion,
				this.getVertexBufferObjectManager());
		scene.setBackground(new SpriteBackground(bg));
		final Text textLoading = new Text(65, 200, this.mFont, "Cargando...", this.getVertexBufferObjectManager());
		scene.attachChild(textLoading);
		final IAsyncCallback callback = new IAsyncCallback() {

			@Override
			public void workToDo() {
				assetsToLoad();
			}

			@Override
			public void onComplete() {
				getEngine().setScene(onAssetsLoaded());
			}
		};

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AsyncTaskLoader().execute(callback);
			}
		});
		arg0.onCreateSceneFinished(scene);
	}

	/**
	 * This will be called after all of the asyc assets are loaded. The loader
	 * will be in charge of changing the scenes.
	 */
	protected abstract Scene onAssetsLoaded();

	/**
	 * This is called when assets need to be loaded in the background.
	 */
	protected abstract void assetsToLoad();

}