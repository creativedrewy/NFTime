package com.creativedrewy.nfttime.watchface

import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.wear.watchface.*
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import org.rajawali3d.Object3D
import org.rajawali3d.animation.Animation
import org.rajawali3d.animation.RotateAnimation3D
import org.rajawali3d.animation.TranslateAnimation3D
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.MaterialManager
import org.rajawali3d.materials.textures.TextureManager
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Plane
import org.rajawali3d.primitives.Sphere
import org.rajawali3d.util.RajLog
import org.rajawali3d.wear.EmptyGL10
import java.time.ZonedDateTime
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin


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

            override fun initScene() {
                RajLog.setDebugEnabled(true)

                val material = Material()
                material.colorInfluence = 1.0f
                material.color = Color.RED

                val emptyParent = Object3D()
                currentScene.addChild(emptyParent)

                val ball = Sphere(0.75f, 24, 24)
                ball.material = Material().apply {
                    color = Color.GREEN
                    colorInfluence = 1.0f
                }
                emptyParent.addChild(ball)

                //val cube = Cube(1f, false, false)
                val plane = Plane(1f, 1f, 1, 1)
                plane.isDoubleSided = true
                plane.material = material
                plane.z = 2.0
                plane.x = 0.0
                emptyParent.addChild(plane)

                val plane2 = Plane(1f, 1f, 1, 1)
                plane2.isDoubleSided = true
                plane2.material = material
                plane2.z = 2 * cos(Math.toRadians(45.0))
                plane2.x = 2 * sin(Math.toRadians(45.0))
                plane2.rotY = -45.0
                emptyParent.addChild(plane2)

                val plane3 = Plane(1f, 1f, 1, 1)
                plane3.isDoubleSided = true
                plane3.material = material
                plane3.z = 0.0
                plane3.x = 2.0
                plane3.rotY = -90.0
                emptyParent.addChild(plane3)

                currentCamera.enableLookAt()
                currentCamera.setLookAt(0.0, 0.0, 0.0)
                currentCamera.y = 6.0
                currentCamera.z = 6.0

                val updownCamer = TranslateAnimation3D(Vector3(0.0, 6.0, 6.0), Vector3(0.0, 0.0, 6.0))
                updownCamer.transformable3D = currentCamera
                updownCamer.durationMilliseconds = 5000
                updownCamer.repeatMode = Animation.RepeatMode.REVERSE_INFINITE
                currentScene.registerAnimation(updownCamer)
                //updownCamer.play()

                val anim = RotateAnimation3D(Vector3(0.0, 359.0, 0.0))
                anim.transformable3D = emptyParent
                anim.durationMilliseconds = 20000
                anim.repeatMode = Animation.RepeatMode.REVERSE_INFINITE
                currentScene.registerAnimation(anim)
                anim.play()
            }

//            override fun onRenderFrame(gl: GL10?) {
//                super.onRenderFrame(gl)
//            }

            override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) { }
            override fun onTouchEvent(event: MotionEvent?) { }
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