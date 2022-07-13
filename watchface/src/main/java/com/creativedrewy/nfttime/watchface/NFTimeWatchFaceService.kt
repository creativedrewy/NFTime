package com.creativedrewy.nfttime.watchface

import android.graphics.Color
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

        class RajawaliRenderer : org.rajawali3d.renderer.Renderer(this@NFTimeWatchFaceService) {

            override fun initScene() {
                RajLog.setDebugEnabled(true)

                val material = Material()
                material.colorInfluence = 1.0f
                material.color = Color.RED

                val emptyParent = Object3D()
                currentScene.addChild(emptyParent)

                val ball = Sphere(0.5f, 24, 24)
                ball.material = Material().apply {
                    color = Color.GREEN
                    colorInfluence = 1.0f
                }
                emptyParent.addChild(ball)

                val radius = 1.15
                val planeCount = 6
                val angleInterval = 360 / planeCount
                for (i in 0..planeCount) {
                    val angle = i * angleInterval

                    val plane = Plane(1f, 1f, 1, 1)
                    plane.isDoubleSided = true
                    plane.material = material
                    plane.z = radius * cos(Math.toRadians(angle.toDouble()))
                    plane.x = radius * sin(Math.toRadians(angle.toDouble()))
                    plane.rotY = -angle.toDouble()

                    emptyParent.addChild(plane)
                }

                currentCamera.enableLookAt()
                currentCamera.setLookAt(0.0, 0.0, 0.0)
                //currentCamera.z = 6.0
                currentCamera.z = 4.0

                val updownCamer = TranslateAnimation3D(Vector3(0.0, 6.0, 6.0), Vector3(0.0, 0.0, 6.0))
                updownCamer.transformable3D = currentCamera
                updownCamer.durationMilliseconds = 5000
                updownCamer.repeatMode = Animation.RepeatMode.REVERSE_INFINITE
                currentScene.registerAnimation(updownCamer)
                //updownCamer.play()

                val anim = RotateAnimation3D(Vector3(0.0, 359.9, 0.0))
                anim.transformable3D = emptyParent
                anim.durationMilliseconds = 50000
                anim.repeatMode = Animation.RepeatMode.INFINITE
                currentScene.registerAnimation(anim)
                anim.play()
            }

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