package lab.hadzha3.antistress

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class AntiStressView(context: Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bg = Color.rgb(242, 239, 232)
    private val ink = Color.rgb(55, 58, 61)
    private val cards = listOf(
        "BUBBLES" to Color.rgb(235, 171, 171),
        "BUTTONS" to Color.rgb(164, 196, 181),
        "RIPPLES" to Color.rgb(145, 190, 210),
        "SPINNER" to Color.rgb(224, 190, 128),
        "DRAW" to Color.rgb(178, 166, 205),
        "SWITCHES" to Color.rgb(198, 183, 158)
    )
    private var game = -1
    private val popped = BooleanArray(24)
    private val toggles = BooleanArray(12)
    private val trails = mutableListOf<PointF>()
    private val ripples = mutableListOf<Triple<Float, Float, Long>>()
    private var spin = 0f
    private var spinVelocity = 0f
    private var lastX = 0f
    private var lastTime = System.currentTimeMillis()

    init {
        isFocusable = true
        p.typeface = Typeface.create("sans", Typeface.NORMAL)
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        c.drawColor(bg)
        if (game < 0) drawMenu(c) else {
            drawHeader(c)
            when (game) {
                0 -> drawBubbles(c)
                1 -> drawButtons(c)
                2 -> drawRipples(c)
                3 -> drawSpinner(c)
                4 -> drawSketch(c)
                5 -> drawSwitches(c)
            }
        }
        if (game == 2 || game == 3) postInvalidateOnAnimation()
    }

    private fun drawHeader(c: Canvas) {
        p.color = ink
        p.textSize = 42f
        p.typeface = Typeface.DEFAULT_BOLD
        c.drawText("‹", 28f, 62f, p)
        p.textSize = 22f
        c.drawText(cards[game].first, 82f, 55f, p)
        p.typeface = Typeface.DEFAULT
    }

    private fun drawMenu(c: Canvas) {
        p.color = ink
        p.textSize = 34f
        p.typeface = Typeface.DEFAULT_BOLD
        c.drawText("CALM LAB", 34f, 70f, p)
        p.typeface = Typeface.DEFAULT
        p.textSize = 16f
        p.color = Color.rgb(110,110,105)
        c.drawText("small things to touch, move and reset", 35f, 99f, p)

        val gap = 18f
        val margin = 28f
        val top = 135f
        val w = (width - margin * 2 - gap) / 2f
        val h = 170f
        cards.forEachIndexed { i, item ->
            val col = i % 2
            val row = i / 2
            val l = margin + col * (w + gap)
            val t = top + row * (h + gap)
            p.color = item.second
            c.drawRoundRect(l, t, l+w, t+h, 28f, 28f, p)
            p.color = Color.argb(70,255,255,255)
            c.drawCircle(l+w*.5f,t+h*.43f,42f,p)
            p.color = ink
            p.textSize = 17f
            p.typeface = Typeface.DEFAULT_BOLD
            c.drawText(item.first, l+18f, t+h-20f, p)
        }
        p.typeface = Typeface.DEFAULT
    }

    private fun drawBubbles(c: Canvas) {
        val cols=4; val r=min(width/10f,52f); val startY=145f
        for(i in 0 until 24){
            val x=width*(i%cols+1)/(cols+1f)
            val y=startY+(i/cols)*r*2.05f
            p.color=if(popped[i]) Color.rgb(218,210,198) else Color.rgb(235,171,171)
            c.drawCircle(x,y,if(popped[i]) r*.55f else r,p)
            if(!popped[i]) { p.color=Color.argb(90,255,255,255); c.drawCircle(x-r*.28f,y-r*.28f,r*.18f,p) }
        }
    }

    private fun drawButtons(c: Canvas) {
        val cols=3; val r=48f
        for(i in 0 until 12){
            val x=width*(i%cols+1)/(cols+1f); val y=160f+(i/cols)*125f
            p.color=if(toggles[i]) Color.rgb(95,135,116) else Color.rgb(164,196,181)
            c.drawCircle(x,y,r,p)
            p.style=Paint.Style.STROKE; p.strokeWidth=5f; p.color=Color.argb(90,0,0,0)
            c.drawCircle(x,y,r,p); p.style=Paint.Style.FILL
        }
    }

    private fun drawRipples(c: Canvas) {
        p.color=Color.rgb(145,190,210); c.drawRect(0f,100f,width.toFloat(),height.toFloat(),p)
        val now=System.currentTimeMillis()
        ripples.removeAll { now-it.third>1800 }
        ripples.forEach {
            val age=(now-it.third)/1800f
            p.style=Paint.Style.STROKE; p.strokeWidth=5f*(1-age)+1
            p.color=Color.argb((190*(1-age)).toInt(),255,255,255)
            c.drawCircle(it.first,it.second,age*260f,p)
        }
        p.style=Paint.Style.FILL
    }

    private fun drawSpinner(c: Canvas) {
        spin += spinVelocity
        spinVelocity *= .985f
        val cx=width/2f; val cy=height/2f
        c.save(); c.rotate(spin,cx,cy)
        p.color=Color.rgb(224,190,128)
        for(a in 0..2){
            val ang=Math.toRadians((a*120).toDouble())
            c.drawCircle(cx+cos(ang).toFloat()*110f,cy+sin(ang).toFloat()*110f,62f,p)
        }
        p.color=Color.rgb(105,91,72); c.drawCircle(cx,cy,43f,p)
        p.color=Color.rgb(240,224,190); c.drawCircle(cx,cy,17f,p)
        c.restore()
    }

    private fun drawSketch(c: Canvas) {
        p.color=Color.WHITE; c.drawRoundRect(18f,105f,width-18f,height-24f,28f,28f,p)
        p.color=Color.rgb(75,72,85); p.strokeWidth=7f; p.strokeCap=Paint.Cap.ROUND
        for(i in 1 until trails.size) c.drawLine(trails[i-1].x,trails[i-1].y,trails[i].x,trails[i].y,p)
    }

    private fun drawSwitches(c: Canvas) {
        for(i in 0 until 8){
            val y=145f+i*75f
            p.color=Color.rgb(210,201,184); c.drawRoundRect(70f,y, width-70f,y+48f,24f,24f,p)
            val on=toggles[i]
            p.color=if(on) Color.rgb(116,145,119) else Color.rgb(155,145,130)
            val x=if(on) width-96f else 96f
            c.drawCircle(x,y+24f,20f,p)
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if(e.action==MotionEvent.ACTION_DOWN && game>=0 && e.y<95f){
            game=-1; trails.clear(); invalidate(); return true
        }
        if(game<0 && e.action==MotionEvent.ACTION_DOWN){
            val margin=28f; val gap=18f; val top=135f
            val w=(width-margin*2-gap)/2f; val h=170f
            val col=if(e.x<width/2f)0 else 1
            val row=((e.y-top)/(h+gap)).toInt()
            val idx=row*2+col
            if(idx in cards.indices){ game=idx; vibrate(); invalidate() }
            return true
        }
        when(game){
            0 -> if(e.action==MotionEvent.ACTION_DOWN){
                val cols=4; val r=min(width/10f,52f); val startY=145f
                for(i in popped.indices){
                    val x=width*(i%cols+1)/(cols+1f); val y=startY+(i/cols)*r*2.05f
                    if(hypot(e.x-x,e.y-y)<r){ popped[i]=!popped[i]; vibrate(); invalidate(); break }
                }
            }
            1 -> if(e.action==MotionEvent.ACTION_DOWN){ toggleNearest(e.x,e.y,3,160f,125f,12); invalidate() }
            2 -> if(e.action==MotionEvent.ACTION_DOWN || e.action==MotionEvent.ACTION_MOVE){ ripples.add(Triple(e.x,e.y,System.currentTimeMillis())); invalidate() }
            3 -> {
                if(e.action==MotionEvent.ACTION_DOWN){ lastX=e.x; lastTime=System.currentTimeMillis() }
                if(e.action==MotionEvent.ACTION_MOVE){
                    val now=System.currentTimeMillis(); val dx=e.x-lastX
                    spinVelocity=(dx/max(1,now-lastTime).toFloat())*5f
                    spin+=dx*.5f; lastX=e.x; lastTime=now; invalidate()
                }
            }
            4 -> {
                if(e.action==MotionEvent.ACTION_DOWN) trails.clear()
                if(e.action==MotionEvent.ACTION_DOWN || e.action==MotionEvent.ACTION_MOVE){ trails.add(PointF(e.x,e.y)); invalidate() }
            }
            5 -> if(e.action==MotionEvent.ACTION_DOWN){
                val i=((e.y-145f)/75f).toInt()
                if(i in 0..7){ toggles[i]=!toggles[i]; vibrate(); invalidate() }
            }
        }
        return true
    }

    private fun toggleNearest(x:Float,y:Float,cols:Int,startY:Float,dy:Float,count:Int){
        var best=-1; var d=Float.MAX_VALUE
        for(i in 0 until count){
            val px=width*(i%cols+1)/(cols+1f); val py=startY+(i/cols)*dy
            val nd=hypot(x-px,y-py)
            if(nd<d){d=nd;best=i}
        }
        if(best>=0 && d<65f){toggles[best]=!toggles[best];vibrate()}
    }

    @Suppress("DEPRECATION")
    private fun vibrate(){
        val v=context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(Build.VERSION.SDK_INT>=26) v.vibrate(VibrationEffect.createOneShot(18,70)) else v.vibrate(18)
    }
}
