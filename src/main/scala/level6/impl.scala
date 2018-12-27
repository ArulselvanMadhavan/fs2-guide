package level6

import fs2._

object impl {

  /**
    * Emits the longest prefix for which all elements test true according to predicates
    */
  def takeWhile[F[_], O](s: Stream[F, O])(p: O => Boolean): Stream[F, O] = {
    def go(s: Stream[F, O]): Pull[F, O, Unit] = {
      s.pull.uncons.flatMap[F, O, Unit] {
        case Some((hd, tl)) => {
          hd.indexWhere(p(_) == false) match {
            case Some(idx) => Pull.output(hd.splitAt(idx)._1)
            case None      => Pull.output(hd) >> go(tl)
          }
        }
        case None => Pull.done
      }
    }
    go(s).stream
  }

  /**
    * Emits the separator between every pair of elements in the stream
    */
  def intersperse[F[_], O](s: Stream[F, O])(separator: O): Stream[F, O] = {
    def go(s: Stream[F, O]): Pull[F, O, Unit] =
      s.pull.uncons1.flatMap[F, O, Unit] {
        case Some((o, t)) => Pull.output(Chunk(separator, o)) >> go(t)
        case None         => Pull.done
      }
    go(s).stream.drop(1)
  }

  /**
    * Left fold which outputs all intermediate results
    */
  def scan[F[_], O, O2](s: Stream[F, O])(z: O2)(
      f: (O2, O) => O2): Stream[F, O2] = {
    def go(s: Stream[F, O], z: O2): Pull[F, O2, Unit] = {
      s.pull.uncons.flatMap[F, O2, Unit] {
        case Some((hd, tl)) => {
          val (c2, z2) = hd.scanLeftCarry(z)(f)
          Pull.output(c2) >> go(tl, z2)
        }
        case None => Pull.done
      }
    }
    Stream(z) ++ go(s, z).stream
  }
}
