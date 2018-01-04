package ai.dragonfly.img.sprite

import java.net.URI
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import ai.dragonfly.distributed.Snowflake
import ai.dragonfly.img.{Img, ImgDOMUtils}
import org.scalajs.dom.raw.{Event, HTMLImageElement}

import scala.scalajs.js
import js.annotation._

/**
 * Created by clifton on 1/16/17.
 */

/*
 * Images load asynchronously.
 * Thundering herds.
 * Consistent lookup.
 */

trait ImgInfo {
  def id: Long
  def name: String
}

@JSExport
object LoadImgInfo {
  @JSExport def apply(src: URI, name: String): LoadImgInfo = LoadImgInfo(Snowflake(), src, name)
}

case class LoadImgInfo (override val id: Long, src: URI, name: String ) extends ImgInfo

@JSExport
object DerivedImgInfo {
  @JSExport def apply(name: String): DerivedImgInfo = DerivedImgInfo(Snowflake(), name)
}

case class DerivedImgInfo (override val id: Long, name: String ) extends ImgInfo

@JSExport
object LoadedImgRepository {

  private val repo = new ConcurrentHashMap[URI, ImageLoadStatus]()

  def get(src: URI, callback: (Img => Unit)): Unit = {
    repo.get(src) match {
      case loaded: LoadedImage => loaded(callback)
      case loading: LoadingImage => loading(callback)
      case _ =>
        val loading = new LoadingImage
        repo.put(src, loading)
        loading(callback)
        val imgElement: HTMLImageElement = ai.dragonfly.img.ImgDOMUtils.imageElement(src.toString)
        imgElement.onload = { evt: Event =>
          println(s"loaded image: $src ${imgElement.naturalWidth} x ${imgElement.naturalHeight}")
          val img = ImgDOMUtils.htmlImageElementToImg(imgElement)
          repo.put(src, new LoadedImage(img))
          loading.invokeCallbacks(img)
        }
    }
  }

  @JSExport
  def get(src:String, callback: js.Function1[Img, Unit]): Unit = {
    get(new URI(src), new Function1[Img, Unit] { def apply(img:Img) = callback(img) })
  }
}

trait ImageLoadStatus { def apply(callback: (Img => Unit)): Unit }

class LoadedImage(img: Img) extends ImageLoadStatus {
  override def apply(callback: (Img => Unit)): Unit = callback(img)
}

class LoadingImage extends ImageLoadStatus {
  private val callbackQueue = new ConcurrentLinkedQueue[(Img => Unit)]()

  override def apply(callback: (Img => Unit)): Unit = callbackQueue.add(callback)

  def invokeCallbacks(img: Img): Unit = while (!callbackQueue.isEmpty) callbackQueue.poll()(img)
}
