package es.ernesto.minicars;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bonus {

	AnimatedSprite mBonus;
	GamePlayActivity gpa;
	final float CAMERA_WIDTH = GamePlayActivity.CAMERA_WIDTH;
	final float CAMERA_HEIGHT = GamePlayActivity.CAMERA_HEIGHT;
	float range;

	public Bonus(Scene scene, float range, TiledTextureRegion texture,
			VertexBufferObjectManager VBOManager) {
		this.range = range;
		mBonus = new AnimatedSprite(0, 0, texture, VBOManager);
		mBonus.animate(100);
		scene.registerUpdateHandler(bonusHandler);
		showBonusAndGeneratePosition();
		scene.attachChild(mBonus);
	}

	IUpdateHandler bonusHandler = new IUpdateHandler() {

		@Override
		public void reset() {

		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (GamePlayActivity.isShowBonus()) {
				mBonus.setY(mBonus.getY() + (float) GamePlayActivity.getSpeed());
				if (mBonus.getY() > CAMERA_HEIGHT + 150) {
					GamePlayActivity.setShowBonus(false);
					showBonusAndGeneratePosition();
				}
			}
		}
	};

	void showBonusAndGeneratePosition() {
		mBonus.setVisible(true);
		float rX = (CAMERA_WIDTH - range) / 2 - 85 + (range / 2 + 85)
				* (float) Math.random();
		mBonus.setPosition(rX, -150);
	}

	public Shape getBonusShape() {
		return mBonus;
	}
}
