package com.creativedrewy.nfttime.watchface

import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.wear.watchface.*
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.MaterialManager
import org.rajawali3d.materials.textures.TextureManager
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Cube
import org.rajawali3d.util.RajLog
import org.rajawali3d.wear.EmptyGL10
import java.time.ZonedDateTime
import javax.microedition.khronos.opengles.GL10


class NFTimeWatchFaceService : WatchFaceService() {

    // Used by Watch Face APIs to construct user setting options and repository.
    override fun createUserStyleSchema(): UserStyleSchema = UserStyleSchema(listOf())

    // Creates all complication user settings and adds them to the existing user settings repository.
    override fun createComplicationSlotsManager(currentUserStyleRepository: CurrentUserStyleRepository): ComplicationSlotsManager =
        ComplicationSlotsManager(listOf(), currentUserStyleRepository)

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
//        val renderer = AnalogWatchCanvasRenderer(
//            context = applicationContext,
//            surfaceHolder = surfaceHolder,
//            watchState = watchState,
//            complicationSlotsManager = complicationSlotsManager,
//            currentUserStyleRepository = currentUserStyleRepository,
//            canvasType = CanvasType.HARDWARE
//        )

        val myCtx = this

        val renderer = object : Renderer.CanvasRenderer(
            surfaceHolder,
            currentUserStyleRepository,
            watchState,
            CanvasType.HARDWARE,
            16L
        ) {
            var backgroundBg = BitmapFactory.decodeResource(resources, R.drawable.watchface_service_bg)

            override fun render(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
                canvas.drawColor(Color.RED)

                val scale = bounds.width() / backgroundBg.width.toFloat()
//                backgroundBg = Bitmap.createScaledBitmap(backgroundBg, (bounds.width() * scale).toInt(), (bounds.height() * scale).toInt(), true)
//
                canvas.drawBitmap(backgroundBg, 0f, 0f, Paint())
            }

            override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime
            ) {

            }

        }

        class RajawaliRenderer : org.rajawali3d.renderer.Renderer(myCtx) {

            val cube = Cube(1f, false, false)

            override fun initScene() {
                RajLog.setDebugEnabled(true)

                //				Material material = new Material();
                //				material.addTexture(new Texture("earthColors",
                //												R.drawable.earthtruecolor_nasa_big));
                //				material.setColorInfluence(0);
                //				mSphere = new Sphere(1, 24, 24);
                //				mSphere.setMaterial(material);
                //				getCurrentScene().addChild(mSphere);

                val material = Material()
                material.colorInfluence = 1.0f
                material.color = Color.RED

                cube.material = material

                currentScene.addChild(cube)

                currentCamera.enableLookAt()
                currentCamera.setLookAt(0.0, 0.0, 0.0)
                currentCamera.z = 6.0
                currentCamera.orientation = currentCamera.orientation.inverse()

                //            getCurrentCamera().enableLookAt();
                //            getCurrentCamera().setLookAt(0, 0, 0);
                //            getCurrentCamera().setZ(6);
                //			getCurrentCamera().setOrientation(getCurrentCamera().getOrientation().inverse());
            }

            override fun onRenderFrame(gl: GL10?) {
                super.onRenderFrame(gl)

                cube.rotate(Vector3.Axis.Y, 1.0);
            }

            override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
                TODO("Not yet implemented")
            }

            override fun onTouchEvent(event: MotionEvent?) {
                TODO("Not yet implemented")
            }
        }

        val oglRenderer = object : Renderer.GlesRenderer(
            surfaceHolder,
            currentUserStyleRepository,
            watchState,
            16L
        ) {

            val rajawaliRenderer = RajawaliRenderer()
            var gl10: GL10 = EmptyGL10()

            override suspend fun onBackgroundThreadGlContextCreated() {
                super.onBackgroundThreadGlContextCreated()
            }

            override suspend fun onUiThreadGlSurfaceCreated(width: Int, height: Int) {
                super.onUiThreadGlSurfaceCreated(width, height)

                MaterialManager.getInstance().registerRenderer(rajawaliRenderer)
                TextureManager.getInstance().registerRenderer(rajawaliRenderer)

                rajawaliRenderer.onRenderSurfaceSizeChanged(gl10, width, height)
            }

            override fun render(zonedDateTime: ZonedDateTime) {
                rajawaliRenderer.onRenderFrame(gl10)
            }

            override fun renderHighlightLayer(zonedDateTime: ZonedDateTime) {
                TODO("Not yet implemented")
            }

        }

        // Creates the watch face.
        return WatchFace(
            watchFaceType = WatchFaceType.ANALOG,
            //renderer = renderer
            renderer = oglRenderer
        )
    }
}