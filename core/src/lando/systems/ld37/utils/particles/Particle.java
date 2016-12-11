package lando.systems.ld37.utils.particles;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import lando.systems.ld37.utils.Assets;

/**
 * Created by dsgraham on 12/10/16.
 */
 public class Particle implements Pool.Poolable {

        TextureRegion region;
        Vector2 pos;
        Vector2 vel;
        Vector2 acc;
        float acc_damping;
        Color startColor;
        Color endColor;
        MutableFloat scale;
        float timeToLive;
        float totalTTL;

        public Particle(){
            pos = new Vector2();
            vel = new Vector2();
            acc = new Vector2();
            acc_damping = 1;
            startColor = new Color();
            endColor = new Color();
            scale = new MutableFloat(.1f);
            region = new TextureRegion(Assets.whitePixel);
        }

        @Override
        public void reset() {
            timeToLive = -1;
            acc_damping = 1;
        }

        public void init(Vector2 p ,Vector2 v, Vector2 a, Color iC, Color fC, float s, float t) {
            init(p, v, a, iC, fC, s, t, new TextureRegion(Assets.whitePixel));
        }

        public void init(Vector2 p ,Vector2 v, Vector2 a, Color iC, Color fC, float s, float t, TextureRegion reg) {
            pos.set(p);
            vel.set(v);
            acc.set(a);
            startColor.set(iC);
            endColor.set(fC);
            scale.setValue(s);
            timeToLive = t;
            totalTTL = t;
            region = reg;
        }

        public void init(float px, float py,
                         float vx, float vy,
                         float ax, float ay, float ad,
                         float ir, float ig, float ib, float ia,
                         float fr, float fg, float fb, float fa,
                         float s,  float t){
            init(px, py, vx, vy, ax, ay, ad, ir, ig, ib, ia, fr, fg, fb, fa, s, t, new TextureRegion(Assets.whitePixel));
        }

        public void init(float px, float py,
                         float vx, float vy,
                         float ax, float ay, float ad,
                         float ir, float ig, float ib, float ia,
                         float fr, float fg, float fb, float fa,
                         float s,  float t,
                         TextureRegion reg) {
            pos.set(px, py);
            vel.set(vx, vy);
            acc.set(ax, ay);
            acc_damping = ad;
            startColor.set(ir, ig, ib, ia);
            endColor.set(fr, fg, fb, fa);
            scale.setValue(s);
            timeToLive = t;
            totalTTL = t;
            region.setRegion(reg);
        }


        public void update(float dt){
            timeToLive -= dt;
            vel.add(acc.x * dt, acc.y * dt);
            pos.add(vel.x * dt, vel.y * dt);

            acc.scl(acc_damping);
            if (acc.epsilonEquals(0.0f, 0.0f, 0.1f)) {
                acc.set(0f, 0f);
            }
        }

        public void render(SpriteBatch batch){
            // Equivalent to finalColor.cpy().lerp(initialColor, timeToLive / totalTTL)
            // but without the allocation for cpy()
            float t = timeToLive / totalTTL;

            float r = endColor.r + t * (startColor.r - endColor.r);
            float g = endColor.g + t * (startColor.g - endColor.g);
            float b = endColor.b + t * (startColor.b - endColor.b);
            float a = endColor.a + t * (startColor.a - endColor.a);

            r = (r < 0) ? 0 : (r > 1) ? 1 : r;
            g = (g < 0) ? 0 : (g > 1) ? 1 : g;
            b = (b < 0) ? 0 : (b > 1) ? 1 : b;
            a = (a < 0) ? 0 : (a > 1) ? 1 : a;

            batch.setColor(r, g, b, a);
            batch.draw(region, pos.x, pos.y, scale.floatValue(), scale.floatValue());
            batch.setColor(Color.WHITE);
            // TODO: rotation?
        }

    }
