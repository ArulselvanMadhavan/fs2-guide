package level4

import fs2.{INothing, Stream}

object impl {

  /**
    * Repeats a stream indefinitely
    *
    * Implement me and others!!!
    */
  def repeat[F[_], O](s: Stream[F, O]): Stream[F, O] = s ++ repeat(s)

  /**
    * Strips all output from a stream
    */
  def drain[F[_], O](s: Stream[F, O]): Stream[F, INothing] = s >> Stream.empty

  /**
    * Runs an effect and ignores its output
    */
  def eval_[F[_], A](fa: F[A]): Stream[F, INothing] =
    Stream.eval(fa) >> Stream.empty

  /**
    * Catches any errors produced by a stream
    */
  def attempt[F[_], O](s: Stream[F, O]): Stream[F, Either[Throwable, O]] =
    s.map(Right(_)).handleErrorWith {
      case t: Throwable => Stream.emit(Left[Throwable, O](t))
    }
}
