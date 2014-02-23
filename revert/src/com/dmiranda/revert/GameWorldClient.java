package com.dmiranda.revert;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Asteroid;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.Ship;
import com.dmiranda.revert.shared.Unit;
import com.dmiranda.revert.shared.bullet.Bullet;

public class GameWorldClient extends GameWorld {

    public Revert game;
    public static ParticleSystem particleSystem;
    public static RayHandler rayHandler;

    private EntityRenderer entityRenderer;
    private TextureRegion background;
    private World lightWorld;

    private int tickTime;

    public GameWorldClient(Revert game) {
        super();

        this.game = game;

        entityRenderer = new EntityRenderer(this);
    }

    public void create() {

        background = Revert.getLoadedTexture("bg-stars.png");
        entityRenderer.loadGraphics();

        particleSystem = new ParticleSystem();

        RayHandler.setGammaCorrection(true);

        lightWorld = new World(new Vector2(), true);
        rayHandler = new RayHandler(lightWorld);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(0.8f);
        rayHandler.setCulling(true);
        rayHandler.setBlurNum(1);

    }

    public void clientCreateAsteroid(int type, float x, float y, float r) {

        Asteroid asteroid = new Asteroid(type, x, y, r);
        entityManager.addLocalEntity(asteroid);

    }

    @Override
    public void entityDeath(Entity entity) {

        // TODO: Method needs to be removed

        // Shake nearby local player
        /*
        if(entity instanceof Unit){
            Unit unit = (Unit)entity;
            if(getLocalPlayer().ship != null){
                float distance = entity.getCenterPosition().dst(localPlayer.ship.getCenterPosition()) + 1;
                if(distance < 5000){
                    float amount = 5000 / distance * (unit.getMaxHealth() * 0.15f);
                    game.getCamera().shake(amount);
                }
            } else {
                game.getCamera().shake(250 * (unit.getMaxHealth() * 0.15f));
            }
        }
        */

    }

    @Override
    public void bulletCollision(Bullet bullet) {

    }

    @Override
    public void update(float delta) {

        entityManager.update(delta);

        lightWorld.step(delta, 8, 3);
        rayHandler.update();

        if (localPlayer != null && localPlayer.ship != null && localPlayer.ship.isAlive()) {

            Ship ship = localPlayer.ship;

            if (tickTime < 1) {

                Network.SingleUnitUpdate updater = new Network.SingleUnitUpdate();
                updater.playerid = ship.getOwnerPlayer().id;
                updater.latency = game.getClient().getLatency();
                updater.id = ship.getId();
                updater.rt = ship.getRotateTo();
                updater.x = ship.getPosition().x;
                updater.y = ship.getPosition().y;
                updater.xv = ship.getVelocity().x;
                updater.yv = ship.getVelocity().y;
                updater.w = ship.getW();
                updater.a = ship.getA();
                updater.s = ship.getS();
                updater.d = ship.getD();
                updater.shooting = ship.isShooting();

                game.getClient().getRawClient().sendUDP(updater);

            }
        }

        if (tickTime < 1) {
            tickTime = Network.CLIENT_SEND_INTERVAL;
        } else {
            tickTime--;
        }

    }

    public void render(SpriteBatch sb, Camera camera) {

        Camera bgCam = new Camera();
        bgCam.focusEntity(camera.getFocusEntity());
        bgCam.zoom = 1f;

        // Draw the background
        sb.disableBlending();
        sb.begin();
        sb.setProjectionMatrix(bgCam.calculateParallaxMatrix(0.04f, 0.04f));
        sb.draw(background, -bgCam.viewportWidth * 0.5f * bgCam.zoom, -bgCam.viewportHeight * 0.5f * bgCam.zoom);
        sb.end();

        // Draw world
        sb.enableBlending();
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        Entity[] entities = entityManager.getEntities();
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == null) continue;

            entities[i].render(sb);
            entityRenderer.render(sb, entities[i]);

        }

        Entity[] localEntities = entityManager.getLocalEntities();
        for (int i = 0; i < localEntities.length; i++) {
            if (localEntities[i] == null) continue;

            localEntities[i].render(sb);
        }

        particleSystem.render(sb, Gdx.graphics.getDeltaTime());

        sb.end();

        // Draw lights
        OrthographicCamera cam = game.getCamera();
        rayHandler.setCombinedMatrix(cam.combined, cam.position.x, cam.position.y, cam.viewportWidth * cam.zoom, cam.viewportHeight * cam.zoom);
        rayHandler.render();

    }

    public void dispose(){
        try{
            if(lightWorld != null) lightWorld.dispose();
            if(rayHandler != null) rayHandler.dispose();
        }catch(Exception e){
            // Gross & LAzy
            // Already disposed bug in light lib
        }

    }

    public void forceNextNetSend() {
        tickTime = -1;
    }

}
