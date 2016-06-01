package es.ernesto.minicars;

import java.util.Random;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemy {
	final float CAMERA_WIDTH = GamePlayActivity.CAMERA_WIDTH;
	final float CAMERA_HEIGHT = GamePlayActivity.CAMERA_HEIGHT;
	final float meter = PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
	final float angle = 3.142f; //180deg in radians
	TiledSprite mEnemySprite;
	Body mEnemyBody;
	GamePlayActivity gpa;
	float range;
	float speed;
	Random random;

	public Enemy(Scene scene, PhysicsWorld mPhysicsWorld, float range,
			float speed, TiledTextureRegion texture,
			VertexBufferObjectManager VBOManager) {
		gpa = new GamePlayActivity();
		this.range = range;
		this.speed = speed;
		mEnemySprite = new TiledSprite(0, -200, texture, VBOManager);
		FixtureDef enemyFixtureDef = PhysicsFactory.createFixtureDef(0.1f,
				0.5f, 0.5f);
		mEnemyBody = PhysicsFactory.createBoxBody(mPhysicsWorld, mEnemySprite,
				BodyType.DynamicBody, enemyFixtureDef);
		mEnemyBody.setBullet(false);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mEnemySprite,
				mEnemyBody, true, true));
		generateEnemyPosition();
		scene.attachChild(mEnemySprite);
		scene.registerUpdateHandler(enemyHandler);
		random = new Random();
	}

	IUpdateHandler enemyHandler = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (GamePlayActivity.isPlayingStarted())
				mEnemyBody.setLinearVelocity(0,
						(float) GamePlayActivity.getSpeed());
			if (mEnemySprite.getY() > CAMERA_HEIGHT + 100) {
				generateEnemyPosition();
				mEnemyBody.setAngularVelocity(0);
				mEnemySprite.setCurrentTileIndex(random.nextInt(5));
			}
		}
	};

	void generateEnemyPosition() {
		float rX = (CAMERA_WIDTH - range + mEnemySprite.getWidthScaled()) / 2
				+ (range - mEnemySprite.getWidthScaled()) * (float) Math.random();
		if (rX > CAMERA_WIDTH / 2)
			mEnemyBody.setTransform(rX / meter, -100 / meter, 0);
		else
			mEnemyBody.setTransform(rX / meter, -100 / meter, angle);
	}

}
