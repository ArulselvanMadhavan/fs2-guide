package level8

import fs2._

object impl {

  /**
    * Maps the supplied stateful function over each element,
    * outputting the final state and the accumulated outputs
    */
  def evalMapAccumulate[F[_], S, O, O2](s1: Stream[F, O])(init: S)(
      f: (S, O) => F[(S, O2)]): Stream[F, (S, O2)] = {
    def go(s: S, in: Stream[F, O]): Pull[F, (S, O2), Unit] = {
      in.pull.uncons1.flatMap[F, (S, O2), Unit] {
        case None => Pull.done
        case Some((o, tl)) => {
          Pull.eval(f(s, o)).flatMap {
            case (nextState, o2) =>
              Pull.output1((nextState, o2)) >> go(nextState, tl)
          }
        }
      }
    }

    go(init, s1).stream
  }

  /**
    * Emits only inputs which match the supplied predicate
    */
  def filter[F[_], O](s: Stream[F, O])(f: O => Boolean): Stream[F, O] = {
    def go(s: Stream[F, O]): Pull[F, O, Unit] = {
      s.pull.uncons.flatMap[F, O, Unit] {
        case None           => Pull.done
        case Some((hd, tl)) => Pull.output(hd.filter(f)) >> go(tl)
      }
    }
    go(s).stream
  }
}
