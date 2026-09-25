package lab.hadzha3.antistress

import android.content.Context
import android.graphics.*
import android.os.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.*
import kotlin.random.Random

class AntiStressView(context: Context) : View(context) {
    private val p=Paint(Paint.ANTI_ALIAS_FLAG)
    private val toys=listOf("WOOD","DART","SWITCH","LEAF","FISH","BALL","BOWL","BAMBOO","CHALK","CRADLE","DRINK","GEAR")
    private var game=-1
    private val state=BooleanArray(40)
    private val path=mutableListOf<PointF>()
    private val ripples=mutableListOf<Triple<Float,Float,Long>>()
    private var angle=0f; private var vel=0f; private var dragX=0f
    private val woodLight=Color.rgb(190,125,53); private val woodDark=Color.rgb(112,62,27)

    override fun onDraw(c:Canvas){
        if(game<0) shelf(c) else scene(c)
        if(game==4||game==5||game==9||game==11) postInvalidateOnAnimation()
    }

    private fun wood(c:Canvas){
        c.drawColor(Color.rgb(150,88,34))
        p.strokeWidth=3f
        for(x in 0..width step 42){
            p.color=if((x/42)%2==0) Color.argb(25,255,230,170) else Color.argb(20,50,20,0)
            c.drawLine(x.toFloat(),0f,x+sin(x.toFloat())*15,height.toFloat(),p)
        }
    }
    private fun shelf(c:Canvas){
        wood(c)
        val rows=4; val top=82f; val sh=(height-top)/rows
        p.color=Color.WHITE;p.textSize=30f;p.typeface=Typeface.DEFAULT_BOLD
        c.drawText("CALM TOYS",24f,50f,p);p.typeface=Typeface.DEFAULT
        for(r in 0 until rows){
            val y=top+r*sh
            p.color=woodDark;c.drawRect(0f,y+sh-17,width.toFloat(),y+sh,p)
            p.color=Color.rgb(218,153,72);c.drawRect(0f,y+sh-17,width.toFloat(),y+sh-10,p)
        }
        for(i in toys.indices){
            val col=i%3; val row=i/3
            val cx=width*(col+.5f)/3f; val cy=top+row*sh+sh*.47f
            drawToy(c,i,cx,cy,min(width/7f,sh*.28f))
        }
    }
    private fun drawToy(c:Canvas,i:Int,x:Float,y:Float,s:Float){
        p.style=Paint.Style.FILL
        when(i){
            0->{p.color=Color.rgb(121,76,45);c.drawRoundRect(x-s,y-s*.7f,x+s,y+s*.7f,16f,16f,p);shine(c,x-s*.45f,y-s*.35f,s*.35f)}
            1->{for(k in 3 downTo 1){p.color=listOf(Color.rgb(35,35,38),Color.rgb(235,224,190),Color.rgb(45,45,48))[k%3];c.drawCircle(x,y,s*k/3f,p)};p.color=Color.RED;c.drawCircle(x,y,s*.12f,p)}
            2->{p.color=Color.rgb(45,45,48);c.drawRoundRect(x-s*.8f,y-s*.75f,x+s*.8f,y+s*.75f,10f,10f,p);p.color=Color.rgb(180,40,32);c.drawRect(x-s*.45f,y-s*.5f,x+s*.45f,y+s*.5f,p);p.color=Color.WHITE;p.textSize=s*.55f;c.drawText("I",x-s*.08f,y-s*.08f,p)}
            3->{p.color=Color.rgb(50,137,69);c.drawOval(x-s,y-s*.55f,x+s,y+s*.55f,p);p.color=Color.rgb(25,95,42);c.drawLine(x,y,x+s*.75f,y-s*.25f,p)}
            4->{p.color=Color.rgb(57,160,210);c.drawOval(x-s,y-s*.55f,x+s*.75f,y+s*.55f,p);val q=Path();q.moveTo(x+s*.6f,y);q.lineTo(x+s*1.25f,y-s*.55f);q.lineTo(x+s*1.2f,y+s*.55f);q.close();c.drawPath(q,p);p.color=Color.WHITE;c.drawCircle(x-s*.45f,y-s*.15f,s*.13f,p)}
            5->{p.color=Color.rgb(70,72,76);c.drawCircle(x,y,s*.78f,p);p.color=Color.argb(120,255,255,255);c.drawCircle(x-s*.25f,y-s*.28f,s*.18f,p)}
            6->{p.color=Color.rgb(126,84,53);c.drawOval(x-s,y,x+s,y+s*.6f,p);p.color=Color.rgb(211,177,117);c.drawOval(x-s,y-s*.2f,x+s,y+s*.35f,p)}
            7->{for(k in -1..1){p.color=Color.rgb(75,137,75);c.drawRoundRect(x+k*s*.55f-s*.14f,y-s,x+k*s*.55f+s*.14f,y+s,8f,8f,p)}}
            8->{p.color=Color.WHITE;c.save();c.rotate(-18f,x,y);c.drawRoundRect(x-s*.2f,y-s,x+s*.2f,y+s,10f,10f,p);c.restore()}
            9->{p.color=Color.rgb(50,50,52);p.strokeWidth=4f;for(k in -2..2){val bx=x+k*s*.38f;c.drawLine(bx,y-s,bx,y,p);c.drawCircle(bx,y+s*.2f,s*.22f,p)}}
            10->{p.color=Color.argb(150,210,235,245);c.drawRoundRect(x-s*.65f,y-s,x+s*.65f,y+s,15f,15f,p);p.color=Color.rgb(130,70,155);c.drawRect(x-s*.58f,y,x+s*.58f,y+s*.85f,p)}
            11->{p.color=Color.rgb(75,78,82);for(k in 0..9){val a=k*PI/5;val xx=x+cos(a).toFloat()*s*.82f;val yy=y+sin(a).toFloat()*s*.82f;c.drawCircle(xx,yy,s*.22f,p)};c.drawCircle(x,y,s*.65f,p)}
        }
    }
    private fun shine(c:Canvas,x:Float,y:Float,r:Float){p.color=Color.argb(70,255,255,255);c.drawCircle(x,y,r,p)}
    private fun scene(c:Canvas){
        wood(c)
        p.color=Color.argb(160,0,0,0);c.drawCircle(42f,48f,27f,p);p.color=Color.WHITE;p.textSize=38f;c.drawText("‹",31f,61f,p)
        when(game){
            0->woodBlocks(c);1->dart(c);2->switches(c);3->leaf(c);4->fish(c);5->ball(c)
            6->bowl(c);7->bamboo(c);8->chalk(c);9->cradle(c);10->drink(c);11->gear(c)
        }
    }
    private fun woodBlocks(c:Canvas){for(i in 0..11){val x=45f+(i%3)*(width-90f)/2;val y=150f+(i/3)*115;p.color=if(state[i])Color.rgb(91,55,33) else Color.rgb(156,101,57);c.drawRoundRect(x-38,y-38,x+38,y+38,10f,10f,p)}}
    private fun dart(c:Canvas){val x=width/2f;val y=height/2f;for(k in 6 downTo 1){p.color=if(k%2==0)Color.rgb(230,220,190) else Color.rgb(35,35,38);c.drawCircle(x,y,k*35f,p)};p.color=Color.RED;c.drawCircle(x,y,25f,p)}
    private fun switches(c:Canvas){for(i in 0..7){val y=135f+i*70;p.color=Color.rgb(45,45,48);c.drawRoundRect(75f,y,width-75f,y+48,12f,12f,p);p.color=if(state[i])Color.rgb(200,45,35) else Color.rgb(90,90,92);val x=if(state[i])width-105f else 105f;c.drawCircle(x,y+24,20f,p)}}
    private fun leaf(c:Canvas){p.color=Color.rgb(38,125,60);c.drawOval(35f,170f,width-35f,height-100f,p);for(i in ripples.indices){val r=ripples[i];p.color=Color.argb(90,255,255,255);c.drawCircle(r.first,r.second,20f,p)}}
    private fun fish(c:Canvas){angle+=vel;vel*=.985f;val x=width/2f+sin(angle*.02f)*width*.3f;val y=height/2f+cos(angle*.017f)*180;p.color=Color.rgb(55,165,215);c.drawOval(x-90,y-55,x+70,y+55,p);p.color=Color.WHITE;c.drawCircle(x-45,y-14,10f,p)}
    private fun ball(c:Canvas){angle+=vel;vel*=.99f;val x=width/2f+sin(angle*.02f)*width*.32f;val y=height/2f+cos(angle*.023f)*220;p.color=Color.rgb(70,72,76);c.drawCircle(x,y,70f,p);shine(c,x-24,y-26,18f)}
    private fun bowl(c:Canvas){p.color=Color.rgb(205,162,91);c.drawOval(45f,height*.45f,width-45f,height*.68f,p);p.color=Color.rgb(115,75,45);c.drawOval(65f,height*.45f,width-65f,height*.57f,p)}
    private fun bamboo(c:Canvas){for(i in 0..3){val x=width*(i+1)/5f;p.color=Color.rgb(73,135,72);c.drawRoundRect(x-20,130f,x+20,height-100f,18f,18f,p)}}
    private fun chalk(c:Canvas){p.color=Color.rgb(44,58,48);c.drawRect(20f,105f,width-20f,height-25f,p);p.color=Color.WHITE;p.strokeWidth=8f;p.strokeCap=Paint.Cap.ROUND;for(i in 1 until path.size)c.drawLine(path[i-1].x,path[i-1].y,path[i].x,path[i].y,p)}
    private fun cradle(c:Canvas){val cx=width/2f;for(i in -2..2){val sway=if(i==-2)sin(System.currentTimeMillis()/180.0).toFloat()*55 else 0f;val x=cx+i*58+sway;p.color=Color.rgb(215,220,225);c.drawLine(cx+i*58,130f,x,390f,p);c.drawCircle(x,425f,31f,p)}}
    private fun drink(c:Canvas){p.color=Color.argb(130,220,240,245);c.drawRoundRect(70f,150f,width-70f,height-90f,25f,25f,p);p.color=Color.rgb(115,65,145);c.drawRect(78f,height*.5f,width-78f,height-98f,p)}
    private fun gear(c:Canvas){angle+=vel;vel*=.988f;c.save();c.rotate(angle,width/2f,height/2f);drawToy(c,11,width/2f,height/2f,150f);c.restore()}

    override fun onTouchEvent(e:MotionEvent):Boolean{
        if(game>=0&&e.action==MotionEvent.ACTION_DOWN&&e.y<90){game=-1;path.clear();invalidate();return true}
        if(game<0&&e.action==MotionEvent.ACTION_DOWN){
            val top=82f;val sh=(height-top)/4f;val col=(e.x/(width/3f)).toInt().coerceIn(0,2);val row=((e.y-top)/sh).toInt();val i=row*3+col
            if(i in toys.indices){game=i;haptic();invalidate()};return true
        }
        if(e.action==MotionEvent.ACTION_DOWN){dragX=e.x;vel=0f}
        when(game){
            0->if(e.action==MotionEvent.ACTION_DOWN){val col=((e.x-7)/(width/3f)).toInt().coerceIn(0,2);val row=((e.y-112)/115).toInt();val i=row*3+col;if(i in 0..11){state[i]=!state[i];haptic();invalidate()}}
            2->if(e.action==MotionEvent.ACTION_DOWN){val i=((e.y-135)/70).toInt();if(i in 0..7){state[i]=!state[i];haptic();invalidate()}}
            3->if(e.action==MotionEvent.ACTION_DOWN||e.action==MotionEvent.ACTION_MOVE){ripples.add(Triple(e.x,e.y,System.currentTimeMillis()));if(ripples.size>45)ripples.removeAt(0);invalidate()}
            4,5,11->if(e.action==MotionEvent.ACTION_MOVE){vel+=(e.x-dragX)*.22f;dragX=e.x;invalidate()}
            8->if(e.action==MotionEvent.ACTION_DOWN||e.action==MotionEvent.ACTION_MOVE){path.add(PointF(e.x,e.y));invalidate()}
            6,7,9->if(e.action==MotionEvent.ACTION_DOWN){haptic();vel=8f;invalidate()}
        }
        return true
    }
    @Suppress("DEPRECATION") private fun haptic(){val v=context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;if(Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createOneShot(22,80))else v.vibrate(22)}
}
