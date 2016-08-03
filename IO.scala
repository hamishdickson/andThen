package andThen

// let's have a think about IO
// previously, we talked about how anything from () => T or T => () was
// bad. but these are the signatures of readline and printline. So how do we
// reason about these fundamental ideas in FP?

// let's write a simple program and refactor it
import scala.io._

object Example {
  def main(args: Array[String]): Unit = {
    println("Hi, what's your name?")
    val n = StdIn.readLine()
    println(s"Hello, $n - do you like this program?")
    val a = StdIn.readLine()


    // ok, so that's quite simple, lets abstract those functions out so we can
    // play with them a little
    def ReadLine(): String = StdIn.readLine()
    def PrintLine(msg: String): Unit = println(msg)

    // the problem with these is they go () => T or T => ()
    // It would be better if we could abstract this out to something like this
    // IO[Unit] => T and T => IO[Unit]

    // so let's nievely try to create our own IO container
    trait IO[A]
    // print line is easy
    def PrintLineIO(msg: String): IO[Unit] = ???

    // but how can we do ReadLine? it's really not obvious, maybe we could try
    // something like this?
    def ReadLineIO(io: IO[Unit]): String = ???

    // no, that just feels wrong

    // what we really want to do with ReadLine is for it to run something (actually go
    // and read the line!) - if we make ReadLine an object, then run could be a function that runs
    object ReadLineObj {
      def run: String = StdIn.readLine()
    }

    // does this work?
    val o1 = ReadLineObj.run
    println(o1)

    // it does! awesome!
    // in fact we can do the same for println
    case class PrintLineObj(msg: String) {
      def run: Unit = println(msg)
    }

    // now we can do this
    PrintLineObj(ReadLineObj.run).run

    // and this works! Great! We've done ... absolutely nothing... well, not quite

    sealed trait IO2[A] {
      def run: A
      def andThen(f: A => IO2[B]): IO2[B]
    }
    // actually, this function isn't complete, but let's assume it always runs ok
    case object ReadLineIO2 extends IO2[String] {
      def run: String = StdIn.readLine()
    }
    case class PrintLineIO2(msg: String) extends IO2[Unit] {
      def run: Unit = println(msg)
    }

    // so what we want here is a function on IO2[A], which takes the output of ReadLineIO2 (A)
    // 
    ReadLineIO2.andThen(n => PrintLineIO2(s"Hi there $n"))
  }
}
