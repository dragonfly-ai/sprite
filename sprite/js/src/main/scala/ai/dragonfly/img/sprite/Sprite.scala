package ai.dragonfly.img.sprite

import ai.dragonfly.img.Img

import org.scalajs.dom.CanvasRenderingContext2D

/**
 * Created by clifton on 1/15/17.
 */

trait Sprite {
  def draw (ctx: CanvasRenderingContext2D): Sprite
}

object LoadedSprite {
  def apply(img: Img, horizontalStride:Int = 1, verticalStride:Int = 1): Sprite = {
    val imgGrid: Array[Img] = new Array[Img](horizontalStride * verticalStride)
    val w: Int = img.width / horizontalStride
    val h: Int = img.height / verticalStride

    var k = 0
    for (i <- 0 until verticalStride; j <- 0 until horizontalStride) {
      imgGrid(k) = img.getSubImage(w * j, i * h, w, h).asInstanceOf[Img]
      k = k + 1
    }

    new LoadedSprite(imgGrid)
  }
}

class LoadedSprite(imgGrid: Array[Img]) extends Sprite {
  override def draw(ctx: CanvasRenderingContext2D): Sprite = {
    // stub
    this
  }
}

class LoadingSprite(w: Int, h: Int) extends Sprite {
  override def draw(ctx: CanvasRenderingContext2D): Sprite = {
    // stub
    this
  }
}