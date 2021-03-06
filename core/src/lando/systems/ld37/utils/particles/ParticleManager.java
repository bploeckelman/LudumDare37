package lando.systems.ld37.utils.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld37.utils.Assets;

/**
 * Created by dsgraham on 12/10/16.
 */
public class ParticleManager {

    private final Array<Particle> activeParticles = new Array<Particle>();
    private final Pool<Particle> particlePool = Pools.get(Particle.class, 100);

    public ParticleManager() {

    }

    public void addBloodParticles(Rectangle bounds, int direction, int damage){
        int particles = 50 + damage * 30;
        for (int i = 0; i < particles; i++){

            Particle part = particlePool.obtain();

            float speed = 50 + MathUtils.random(200f);
            float dir = (45 + MathUtils.random(90f)) * direction;
            float px = bounds.x + MathUtils.random(bounds.width);
            float py = bounds.y + MathUtils.random(bounds.height);
            float vx = MathUtils.sinDeg(dir) * speed;
            float vy = MathUtils.cosDeg(dir) * speed;
            float scale = MathUtils.random(1, 4f);
            float ttl = MathUtils.random(0.5f, 2f);
            part.init(
                    px, py,
                    vx, vy,
                    -vx/2f, -200, 1,
                    1,0,0,1,
                    0.5f,0,0,0.5f,
                    scale, ttl);

            activeParticles.add(part);
        }

    }

    public void addSparkles(float x, float y){
        for (int i = 0; i < 5; i++) {
            Particle part = particlePool.obtain();
            float speed = 20 + MathUtils.random(20f);
            float dir = (MathUtils.random(360f));
            float px = x;
            float py = y;
            float vx = MathUtils.sinDeg(dir) * speed;
            float vy = MathUtils.cosDeg(dir) * speed;
            float scale = MathUtils.random(4, 6f);
            float ttl = MathUtils.random(1f, 4f);
            float r = MathUtils.random(.2f,1);
            float g = MathUtils.random(.2f,1);
            float b = MathUtils.random(.2f,1);
            TextureRegion tex = Assets.sparkles.get(MathUtils.random(Assets.sparkles.size -1));
            part.init(
                    px, py,
                    vx, vy,
                    0, -2, 1,
                    r,g,b,1,
                    r,g,b,0f,
                    scale, ttl, tex);

            activeParticles.add(part);
        }

    }

    public void addParticle(Rectangle bounds, Color c){
        int tiles = 10;
        float boundDx = bounds.width / tiles;
        for (int x = 0; x < tiles; x++){
            for (int y = 0; y < tiles; y++){
                Particle part = particlePool.obtain();

                float speed = MathUtils.random(300f);
                float dir = MathUtils.random(360f);
                float px = bounds.x + x * boundDx;
                float py = bounds.y + (y-1) * boundDx;
                float vx = MathUtils.sinDeg(dir) * speed;
                float vy = MathUtils.cosDeg(dir) * speed;
                float scale = MathUtils.random(boundDx, 4f * boundDx);
                float ttl = MathUtils.random(0.5f, 2f);
                part.init(
                        px, py,
                        vx, vy,
                        -vx, -vy, .5f,
                        c.r, c.g, c.b, c.a,
                        c.r, c.g, c.b, 0f,
                        scale, ttl,
                        new TextureRegion(Assets.whitePixel));

                activeParticles.add(part);
            }
        }
    }

    public void update(float dt){
        int len = activeParticles.size;
        for (int i = len -1; i >= 0; i--){
            Particle part = activeParticles.get(i);
            part.update(dt);
            if (part.timeToLive <= 0){
                activeParticles.removeIndex(i);
                particlePool.free(part);
            }
        }
    }

    public void render(SpriteBatch batch){
        for (Particle part : activeParticles){
            part.render(batch);
        }
    }


    public void clearParticles(){
        particlePool.freeAll(activeParticles);
        activeParticles.clear();
    }

}
